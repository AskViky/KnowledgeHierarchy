

## fragment在开发中遇到的问题
1、fragment getActivity()为空

如何解决：[Fragment中getActivity()或getContext()返回null的问题](https://blog.csdn.net/setsail_z/article/details/77989769)     
getActivity()为null是因为此时fragment已经跟activity
解除关联，故而获取不到，通过在onAttache中用引用关联
activity，可避免null出现。

2、fragment activity 状态丢失

## 参考文献   
1、[Fragment实际开发中的总结(一)](https://blog.csdn.net/johnnyz1234/article/details/45919907)      
2、
