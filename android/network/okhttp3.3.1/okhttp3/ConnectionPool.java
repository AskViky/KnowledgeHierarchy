/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package okhttp3;

import java.lang.ref.Reference;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import okhttp3.ConnectionPool.1;
import okhttp3.internal.Platform;
import okhttp3.internal.RouteDatabase;
import okhttp3.internal.Util;
import okhttp3.internal.http.StreamAllocation;
import okhttp3.internal.io.RealConnection;

/**
 * Manages reuse of HTTP and SPDY connections for reduced network latency. HTTP requests that share
 * the same {@link Address} may share a {@link Connection}. This class implements the policy of
 * which connections to keep open for future use.
 * 管理HTTP和SPDY连接的重用来减少网络延迟。那些分享同一个地址的HTTP请求可能会分享同一个连接。这个类实现了那个
 * 连接来为未来的使用保持打开的策略。
 */
public final class ConnectionPool {

    private static final Executor executor;
    /** The maximum number of idle connections for each address. */
    /** 每个地址的最大空闲连接数量 */
    private final int maxIdleConnections;
    private final long keepAliveDurationNs;
    private final Runnable cleanupRunnable;
    private final Deque<RealConnection> connections;
    final RouteDatabase routeDatabase;
    boolean cleanupRunning;

    /**
     * Create a new connection pool with tuning parameters appropriate for a single-user application.
     * The tuning parameters in this pool are subject to change in future OkHttp releases. Currently
     * this pool holds up to 5 idle connections which will be evicted after 5 minutes of inactivity.
     * 创建一个新的连接池用调整参数适当的为了当用户应用。这个连接池中的调整参数是可变的在未来的OkHttp版本中。
     * 当前的这个连接池持有5个空闲的连接，来收回在5分钟之后的不互动。
     */
    public ConnectionPool() {
        this(5, 5L, TimeUnit.MINUTES);
    }

    public ConnectionPool(int maxIdleConnections, long keepAliveDuration, TimeUnit timeUnit) {
        this.cleanupRunnable = new 1(this);
        this.connections = new ArrayDeque();
        this.routeDatabase = new RouteDatabase();
        this.maxIdleConnections = maxIdleConnections;
        this.keepAliveDurationNs = timeUnit.toNanos(keepAliveDuration);
        if(keepAliveDuration <= 0L) {
            throw new IllegalArgumentException("keepAliveDuration <= 0: " + keepAliveDuration);
        }
    }

    public synchronized int idleConnectionCount() {
        int total = 0;
        Iterator var2 = this.connections.iterator();

        while(var2.hasNext()) {
            RealConnection connection = (RealConnection)var2.next();
            if(connection.allocations.isEmpty()) {
                ++total;
            }
        }

        return total;
    }

    public synchronized int connectionCount() {
        return this.connections.size();
    }

    RealConnection get(Address address, StreamAllocation streamAllocation) {
        assert Thread.holdsLock(this);

        Iterator var3 = this.connections.iterator();

        RealConnection connection;
        do {
            if(!var3.hasNext()) {
                return null;
            }

            connection = (RealConnection)var3.next();
        } while(connection.allocations.size() >= connection.allocationLimit || !address.equals(connection.route().address) || connection.noNewStreams);

        streamAllocation.acquire(connection);
        return connection;
    }

    void put(RealConnection connection) {
        assert Thread.holdsLock(this);

        if(!this.cleanupRunning) {
            this.cleanupRunning = true;
            executor.execute(this.cleanupRunnable);
        }

        this.connections.add(connection);
    }

    boolean connectionBecameIdle(RealConnection connection) {
        assert Thread.holdsLock(this);

        if(!connection.noNewStreams && this.maxIdleConnections != 0) {
            this.notifyAll();
            return false;
        } else {
            this.connections.remove(connection);
            return true;
        }
    }

    public void evictAll() {
        List<RealConnection> evictedConnections = new ArrayList();
        synchronized(this) {
            Iterator i = this.connections.iterator();

            while(true) {
                if(!i.hasNext()) {
                    break;
                }

                RealConnection connection = (RealConnection)i.next();
                if(connection.allocations.isEmpty()) {
                    connection.noNewStreams = true;
                    evictedConnections.add(connection);
                    i.remove();
                }
            }
        }

        Iterator var2 = evictedConnections.iterator();

        while(var2.hasNext()) {
            RealConnection connection = (RealConnection)var2.next();
            Util.closeQuietly(connection.socket());
        }

    }

    long cleanup(long now) {
        int inUseConnectionCount = 0;
        int idleConnectionCount = 0;
        RealConnection longestIdleConnection = null;
        long longestIdleDurationNs = -9223372036854775808L;
        synchronized(this) {
            Iterator i = this.connections.iterator();

            while(i.hasNext()) {
                RealConnection connection = (RealConnection)i.next();
                if(this.pruneAndGetAllocationCount(connection, now) > 0) {
                    ++inUseConnectionCount;
                } else {
                    ++idleConnectionCount;
                    long idleDurationNs = now - connection.idleAtNanos;
                    if(idleDurationNs > longestIdleDurationNs) {
                        longestIdleDurationNs = idleDurationNs;
                        longestIdleConnection = connection;
                    }
                }
            }

            if(longestIdleDurationNs < this.keepAliveDurationNs && idleConnectionCount <= this.maxIdleConnections) {
                if(idleConnectionCount > 0) {
                    return this.keepAliveDurationNs - longestIdleDurationNs;
                }

                if(inUseConnectionCount > 0) {
                    return this.keepAliveDurationNs;
                }

                this.cleanupRunning = false;
                return -1L;
            }

            this.connections.remove(longestIdleConnection);
        }

        Util.closeQuietly(longestIdleConnection.socket());
        return 0L;
    }

    private int pruneAndGetAllocationCount(RealConnection connection, long now) {
        List<Reference<StreamAllocation>> references = connection.allocations;
        int i = 0;

        while(i < references.size()) {
            Reference<StreamAllocation> reference = (Reference)references.get(i);
            if(reference.get() != null) {
                ++i;
            } else {
                Platform.get().log(5, "A connection to " + connection.route().address().url() + " was leaked. Did you forget to close a response body?", (Throwable)null);
                references.remove(i);
                connection.noNewStreams = true;
                if(references.isEmpty()) {
                    connection.idleAtNanos = now - this.keepAliveDurationNs;
                    return 0;
                }
            }
        }

        return references.size();
    }

    /**
     * Background threads are used to cleanup expired connections. There will be at most a single
     * thread running per connection pool. The thread pool executor permits the pool itself to be
     * garbage collected.
     * 背景线程来使用去清理一个超时连接。这会最多一个线程运行每个线程池。这个线程池执行器允许这个池本身来进行
     * 垃圾回收。
     */
    static {
        executor = new ThreadPoolExecutor(0, 2147483647, 60L, TimeUnit.SECONDS, new SynchronousQueue(), Util.threadFactory("OkHttp ConnectionPool", true));
    }
}
