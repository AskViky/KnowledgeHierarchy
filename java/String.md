
## String
1、

## String的不可变性   

## 对象的不可变性   
如果一个对象，在它创建完成之后，不能再改变它的状态，那么这个对象就是不可变的。   
不能改变状态的意思是，不能改变对象内的成员变量，包括基本数据类型的值不能改变，     
引用类型的变量不能指向其他的对象，引用类型指向的对象的状态也不能改变。     

## 区分对象和对象的引用
1、对象在内存中是一块内存区，成员变量越多，这块内存区占的空间越大；     
2、引用只是一个4字节的数据，里面存放了它所指向的对象的地址，通过这个地址可以访问对象；       

## 参考文献   
1、[Java中的String为什么是不可变的？ -- String源码分析](https://blog.csdn.net/zhangjg_blog/article/details/18319521)      
2、[为什么Java字符串是不可变对象？](http://www.codeceo.com/article/why-java-string-class-static.html)      

