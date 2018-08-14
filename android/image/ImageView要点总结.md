

## ImageView要点总结         

### 一、ImageView加载图片的方式比对                             
* void setImageBitmap(Bitmap bm)                
* void setImageDrawable(Drawable drawable)               
* void setImageIcon(Icon icon)                 
* void setImageResource(int resId)    
* void setImageURI(Uri uri)

#### 1、setImageBitmap 
实际上setImageBitmap做的事情就是把Bitmap对象封装成Drawable对象,然后调用setImageDrawable来设置图片。
因此源码里面才写上了建议,如果需要频繁调用这个方法的话最好自己封装个固定的Drawable对象,
直接调用setImageDrawable,这样可以减少Drawable对象。
因为每次调用setImageBitmap方法都会对Bitmap对象new出一个Drawable。

#### 2、setImageDrawable
参数是Drawable,也是可以接受不同来源的图片,方法中所做的事情就是更新ImageView的图片。

#### 3、setImageIcon
从源码可以看到,setImageIcon从Icon参数中获取到Drawable对象,
然后调用setImageDrawable方法

#### 4、setImageResource
对图片进行读取和解析的,不过是在UI主线程中进行的,所以有可能对一个Activity的启动造成延迟。
所以顾虑到这个官方建议用setImageDrawable和setImageBitmap来代替。

#### 5、setImageURI
从app本地资源的uri中加载图片到imageview，试过效果不好，不建议使用

#### ImageView加载图片小结         
综合来看setImageDrawable是最省内存高效的,如果担心图片过大或者图片过多影响内存和加载效率,         
可以自己解析图片然后通过调用setImageDrawable方法进行设置。         

### 二、ImageView的ScaleType属性         
该属性指定了你想让ImageView如何显示图片，包括是否进行缩放、等比缩放、缩放后展示位置等         

#### 八个ScaleType，可以分为三个类型：        
* 以FIT_开头的4种，它们的共同点是都会对图片进行缩放       
* 以CENTER_开头的3种，它们的共同点是居中显示，图片的中心点会与ImageView的中心点重叠       
* ScaleType.MATRIX             

#### 1、ScaleType.FIT_CENTER 默认         
在该模式下，图片会被等比缩放到能够填充控件大小，并居中展示         

#### 2、ScaleType.FIT_START             
图片等比缩放到控件大小，并放置在控件的上边或左边展示

#### 3、ScaleType.FIT_END             
图片等比缩放到控件大小，并放置在控件的下边或右边展示

#### 4、ScaleType.FIT_XY             
图片缩放到控件大小，完全填充控件大小展示。注意，此模式不是等比缩放

#### 5、ScaleType.CENTER
不使用缩放，ImageView会展示图片的中心部分，即图片的中心点和ImageView的中心点重叠

#### 6、ScaleType.CENTER_CROP
在该模式下，图片会被等比缩放直到完全填充整个ImageView，并居中显示

#### 7、ScaleType.CENTER_INSIDE
此模式以完全展示图片的内容为目的

#### 8、ScaleType.MATRIX        
该模式需要用于指定一个变换矩阵用于指定图片如何展示。          
其实前面的7种模式都是通过ImageView在内部生成了相应的变换矩阵，           
等于是提供了该模式的一种特定值，使用这个模式只要传入相应矩阵，               
也就能实现上述七种显示效果。           

## 参考文献
1、[android imageview研究总结](http://crazyandcoder.tech/2016/04/16/android%20imageview%E7%A0%94%E7%A9%B6%E6%80%BB%E7%BB%93/)   2、[Android ImageView 的scaleType 属性图解](https://www.jianshu.com/p/32e335d5b842)             


