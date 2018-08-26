

## Glide


## Glide特点
1、Glide的这种默认的缓存机制有一个优点，就是它可以加快图片加载的速度      
（可以理解为以空间换时间)       
2、Glide的一个明显的优点就是它可以加载gif图片             


## Glide使用
        //（1）加载网络图片
        tvGlide1.setText("（1）加载网络图片");
        Glide.with(this).load("http://img1.imgtn.bdimg.com/it/u=2615772929,948758168&fm=21&gp=0.jpg").into(ivGlide1);
 
        //（2）加载资源图片
        tvGlide2.setText("（2）加载资源图片");
        Glide.with(this).load(R.drawable.atguigu_logo).into(ivGlide2);
 
        //（3）加载本地图片
        tvGlide3.setText("（3）加载本地图片");
        String path = Environment.getExternalStorageDirectory() + "/meinv1.jpg";
        File file = new File(path);
        Uri uri = Uri.fromFile(file);
        Glide.with(this).load(uri).into(ivGlide3);
 
        // （4）加载网络gif
        tvGlide4.setText("（4）加载网络gif");
        String gifUrl = "http://b.hiphotos.baidu.com/zhidao/pic/item/faedab64034f78f066abccc57b310a55b3191c67.jpg";
        Glide.with(this).load(gifUrl).placeholder(R.mipmap.ic_launcher).into(ivGlide4);
 
        // （5）加载资源gif
        tvGlide5.setText("（5）加载资源gif");
        Glide.with(this).load(R.drawable.loading).asGif().placeholder(R.mipmap.ic_launcher).into(ivGlide5);
 
        //（6）加载本地gif
        tvGlide6.setText("（6）加载本地gif");
        String gifPath = Environment.getExternalStorageDirectory() + "/meinv2.jpg";
        File gifFile = new File(gifPath);
        Glide.with(this).load(gifFile).placeholder(R.mipmap.ic_launcher).into(ivGlide6);
 
        //（7）加载本地小视频和快照
        tvGlide7.setText("（7）加载本地小视频和快照");
        String videoPath = Environment.getExternalStorageDirectory() + "/video.mp4";
        File videoFile = new File(videoPath);
        Glide.with(this).load(Uri.fromFile(videoFile)).placeholder(R.mipmap.ic_launcher).into(ivGlide7);
 
        //（8）设置缩略图比例,然后，先加载缩略图，再加载原图
        tvGlide8.setText("（8）设置缩略图比例,然后，先加载缩略图，再加载原图");
        String urlPath = Environment.getExternalStorageDirectory() + "/meinv1.jpg";
        Glide.with(this).load(new File(urlPath)).thumbnail(0.1f).centerCrop().placeholder(R.mipmap.ic_launcher).into(ivGlide8);
 
        //（9）先建立一个缩略图对象，然后，先加载缩略图，再加载原图
        tvGlide9.setText("（9）先建立一个缩略图对象，然后，先加载缩略图，再加载原图");
        DrawableRequestBuilder thumbnailRequest = Glide.with(this).load(new File(urlPath));
        Glide.with(this).load(Uri.fromFile(videoFile)).thumbnail(thumbnailRequest).centerCrop().placeholder(R.mipmap.ic_launcher).into(ivGlide9);
        
         // 显示数据
                int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, mContext.getResources().getDisplayMetrics());
                int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200f, mContext.getResources().getDisplayMetrics());
         
                Glide.with(mContext)
                        .load(mDatas[position])
                        .placeholder(R.mipmap.ic_launcher) //占位图
                        .error(R.mipmap.ic_launcher)  //出错的占位图
                        .override(width, height) //图片显示的分辨率 ，像素值 可以转化为DP再设置
                        .animate(R.anim.glide_anim)
                        .centerCrop()
                        .fitCenter()
                        .into(holder.image);
                        
1、[Android实践 -- Android开源框架Glide的使用](https://www.jianshu.com/p/299c8332aca6)       
        
## Glide基本原理
2、[Glide使用及原理概述](https://blog.csdn.net/jinhuoxingkong/article/details/75944220)

## Glide源码
1、[Glide 源码学习,了解 Glide 图片加载原理](https://www.jianshu.com/p/9d8aeaa5a329)

## 参考文献
1、[Glide - 系列综述](https://mrfu.me/2016/02/28/Glide_Series_Roundup/?hmsr=toutiao.io&utm_medium=toutiao.io&utm_source=toutiao.io)      
 
