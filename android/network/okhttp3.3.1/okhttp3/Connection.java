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

import java.net.socket;

/**
 * The sockets and streams of an HTTP, HTTPS, or HTTPS+SPDY connection. May be used for multiple
 * HTTP request/response exchanges. Connections may be direct to the origin server or via a proxy.
 * 一个HTTP，HTTPS，或者HTTPS+SPDY连接的各个socket和各个stream。可能用于多个HTTP请求/相应交换。
 * 连接可能直连到服务器，或者通过代理服务器连接。
 *
 * <p>Typically instances of this class are created, connected and exercised automatically by the
 * HTTP client. Applications may use this class to monitor HTTP connections as members of a
 * {@linkplain ConnectionPool connection pool}.
 * 这个接口的典型实例是通过HTTP客户端自动创建、连接、执行。应用可能使用这个接口来检测HTTP连接池的成员。
 *
 * <p>Do not confuse this class with the misnamed {@code HttpURLConnection}, which isn't so much a
 * connection as a single request/response exchange.
 * 不要把这个接口跟误称的HttpURLConnection混淆了，HttpURLConnection不是那么多的作为单个请求/响应的交换连接。
 *
 * <h3>Modern TLS</h3>
 * 现代的TLS
 *
 * <p>There are tradeoffs when selecting which options to include when negotiating a secure
 * connection to a remote host. Newer TLS options are quite useful:
 * 当选择哪个选项来包含代表一个到远方服务器连接是有折衷方案的，更新的TLS选项是相当有用的：
 *
 * <ul>
 *     <li>Server Name Indication (SNI) enables one IP address to negotiate secure connections for
 *         multiple domain names.
 *         服务器名表明可以使一个IP地址来协商多个域名的安全连接。
 *     <li>Application Layer Protocol Negotiation (ALPN) enables the HTTPS port (443) to be used for
 *         different HTTP and SPDY protocols.
 *         应用称协议协商（ALPN）使得HTTPS端口443来用于不同的HTTP和SPDY协议。
 * </ul>
 *
 * <p>Unfortunately, older HTTPS servers refuse to connect when such options are presented. Rather
 * than avoiding these options entirely, this class allows a connection to be attempted with modern
 * options and then retried without them should the attempt fail.
 * 不幸的是，老的HTTPS服务器拒绝连接当有这样的选项存在。与其全面避免这些选项，这个接口允许一个连接来尝试现代的
 * 选项，然后不用它们来重试看看这个尝试是否失败。
 *
 * <h3>Connection Reuse</h3>
 * 连接重用
 *
 * <p>Each connection can carry a varying number streams, depending on the underlying protocol being
 * used. HTTP/1.x connections can carry either zero or one streams. HTTP/2 connections can carry any
 * number of streams, dynamically configured with {@code SETTINGS_MAX_CONCURRENT_STREAMS}. A
 * connection currently carrying zero streams is an idle stream. We keep it alive because reusing an
 * existing connection is typically faster than establishing a new one.
 * 每个连接可以承载一个可变数量的流，依赖于所使用的底层协议。HTTP/1.x连接可以承载零个或者一个流。HTTP/2连接可以
 * 承载任意数量的流，通过SETTINGS_MAX_CONCURRENT_STREAMS来动态配置。一个连接当前承载零个流是一个空闲的流。
 * 我们让它活着是以为重用一个存在的流是典型的比创建一个新的流更快。
 *
 * <p>When a single logical call requires multiple streams due to redirects or authorization
 * challenges, we prefer to use the same physical connection for all streams in the sequence. There
 * are potential performance and behavior consequences to this preference. To support this feature,
 * this class separates <i>allocations</i> from <i>streams</i>. An allocation is created by a call,
 * used for one or more streams, and then released. An allocated connection won't be stolen by other
 * calls while a redirect or authorization challenge is being handled.
 * 当一个逻辑调用需要多个流由于重定向或者授权挑战，我们更偏向于为一个序列中的多个流使用同一个物理连接。这个特例
 * 有潜在的性能和行为序列。为了支持这个特性，这个接口为各个流分开分配。一个分配是给一个调用所创建的，为一个或多
 * 个流所用，然后释放。一个分配连接不会被其它调用所窃取，当一个重定向或者授权挑战被处理时。
 *
 * <p>When the maximum concurrent streams limit is reduced, some allocations will be rescinded.
 * Attempting to create new streams on these allocations will fail.
 * 当最大并发流显示减少后，一些分配会被废止。尝试去创建一个新的流在那些分配上会失败。
 *
 * <p>Note that an allocation may be released before its stream is completed. This is intended to
 * make bookkeeping easier for the caller: releasing the allocation as soon as the terminal stream
 * has been found. But only complete the stream once its data stream has been exhausted.
 * 注意，一个分配可能释放在他的流完成之前。这是尝试去薄记更容易的为了调用者：释放这个分配快到就像终端流被发现那样。
 * 但是只有完成的流曾经它的数据流被消耗尽。
 */
public interface Connection {
    /** Returns the route used by this connection. */
    /** 返回这个连接所用的路由 */
    Route route();

    /**
     * Returns the socket that this connection is using. Returns an {@linkplain
     * javax.net.ssl.SSLSocket SSL socket} if this connection is HTTPS. If this is an HTTP/2 or SPDY
     * connection the socket may be shared by multiple concurrent calls.
     * 返回这个连接所用的socket。返回一个SSLSocket如果这个连接是HTTPS。如果是一个HTTP/2或者SPDY连接这个socket
     * 可能为多个并发调用所分享。
     */
    Socket socket();

    /**
     * Returns the TLS handshake used to establish this connection, or null if the connection is not
     * HTTPS.
     * 返回用来建立这个连接的TLS握手，后者null如果这个连接不是HTTPS。
     */
    Handshake handshake();

    /**
     * Returns the protocol negotiated by this connection, or {@link Protocol#HTTP_1_1} if no protocol
     * has been negotiated. This method returns {@link Protocol#HTTP_1_1} even if the remote peer is
     * using {@link Protocol#HTTP_1_0}.
     * 返回协议的协商通过增额连接，或者HTTP1.1如果没有协议被协商。这个方法返回HTTP1.1即使远程的端口是使用HTTP1.0
     */
    Protocol protocol();
}
