package com.hzu.zao.config;

import android.app.Application;
import android.content.Context;

import com.hzu.zao.model.MyUser;
import com.hzu.zao.utils.ImageHandlerUtils;
import com.hzu.zao.utils.StorageUtils;
import com.hzu.zao.utils.cache.ACache;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.File;

import cn.bmob.v3.BmobUser;


/**
 * 重写Application 配置呢ImageLoader
 *
 * Created by Nearby Yang on 2016-04-21.
 */
public class MyApplication extends Application {
    //单例模式
    private volatile static MyApplication instance;
    private  static Context context;
    @Override
    public void onCreate() {
        super.onCreate();

        init();

    }

    private void init() {
        initImageLoader();
        context =this;
    }

    /**
     * 单例的Application
     *
     * @return ApplicationConfig.this
     */
    public static MyApplication getInstance() {
        if (instance == null) {
            synchronized (MyApplication.class) {
                if (instance == null) {
                    instance = new MyApplication();
                }
            }
        }
        return instance;
    }

    /**
     * 获取缓存操作对象
     *
     * @return
     */
    public ACache getCacheInstance() {

        return ACache.get(com.hzu.zao.utils.StorageUtils.createCacheFile(context));
    }
    /**
     * 获取当前用户的信息，在用户登录之后进行保存
     *
     * @return
     */
    public MyUser getCUser() {
        MyUser cuser = BmobUser.getCurrentUser(context, MyUser.class);
        if (cuser != null) {
            return cuser;
        } else {
            return (MyUser) getCacheInstance().getAsObject(Contants.CURRENTUSER_KEY);
        }
    }



    public static Context getContext(){
        return context;
    }

    /**
     * 图片加载
     *
     */
    private void initImageLoader() {

        File cacheFile = StorageUtils.createImageFile(getApplicationContext());

        ImageLoaderConfiguration imConfig = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPoolSize(3) //线程池内线程的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator()) //将保存的时候的URI名称用MD5 加密
                .memoryCache(new UsingFreqLimitedMemoryCache(5 * 1024 * 1024))
                .memoryCacheSize( (int)Runtime.getRuntime().totalMemory()/ 8) // 内存缓存的最大值
                .diskCacheSize(50 * 1024 * 1024)  // SD卡缓存的最大值
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                // 由原先的discCache -> diskCache
                .diskCache(new UnlimitedDiskCache(cacheFile))//自定义缓存路径
                .imageDownloader(new BaseImageDownloader(getApplicationContext(), 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
                .defaultDisplayImageOptions(ImageHandlerUtils.imageloaderOption())
                .build();

        ImageLoader.getInstance().init(imConfig);
    }
}
