

## ImageView要点总结         

### ImageView加载图片的方式比对                             
* void setImageBitmap(Bitmap bm)                
* void setImageDrawable(Drawable drawable)               
* void setImageIcon(Icon icon)                 
* void setImageResource(int resId)    
* void setImageURI(Uri uri)

#### setImageBitmap 
实际上setImageBitmap做的事情就是把Bitmap对象封装成Drawable对象,然后调用setImageDrawable来设置图片。
因此源码里面才写上了建议,如果需要频繁调用这个方法的话最好自己封装个固定的Drawable对象,
直接调用setImageDrawable,这样可以减少Drawable对象。
因为每次调用setImageBitmap方法都会对Bitmap对象new出一个Drawable。

#### setImageDrawable
参数是Drawable,也是可以接受不同来源的图片,方法中所做的事情就是更新ImageView的图片。

#### setImageIcon
从源码可以看到,setImageIcon从Icon参数中获取到Drawable对象,
然后调用setImageDrawable方法

#### setImageResource
对图片进行读取和解析的,不过是在UI主线程中进行的,所以有可能对一个Activity的启动造成延迟。
所以顾虑到这个官方建议用setImageDrawable和setImageBitmap来代替。

#### setImageURI
从app本地资源的uri中加载图片到imageview，试过效果不好，不建议使用

#### ImageView加载图片小结
综合来看setImageDrawable是最省内存高效的,如果担心图片过大或者图片过多影响内存和加载效率,
可以自己解析图片然后通过调用setImageDrawable方法进行设置。

### 参考文献
1、[android imageview研究总结](http://crazyandcoder.tech/2016/04/16/android%20imageview%E7%A0%94%E7%A9%B6%E6%80%BB%E7%BB%93/)         

