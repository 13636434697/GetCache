package com.xu.getcache;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.os.Bundle;
import android.app.Activity;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.text.format.Formatter;
import android.util.Log;

/*
* 获取游览器的缓存大小，aidl文件多案例
* 里面有反射的案例
* */
public class MainActivity extends Activity {

    protected static final String tag = "MainActivity";
    private PackageManager mPm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPm = getPackageManager();
        //获取主线程的id(每个线程都有唯一性的id)
        Log.i(tag, "主线程id = "+Thread.currentThread().getId());

        // 创建了一个IPackageStatsObserver.Stub子类的对象,并且实现了onGetStatsCompleted方法
        IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {

            public void onGetStatsCompleted(PackageStats stats,boolean succeeded) {
                // 缓存大小的过程,子线程中代码,不能去处理UI
                String str = Formatter.formatFileSize(getApplicationContext(), stats.cacheSize);
                Log.i(tag, "==========================================浏览器缓存大小 = "+str);
                Log.i(tag, "线程id = "+Thread.currentThread().getId());
            }
        };

        // 获取mStatsObserver对象,然后调用onGetStatsCompleted()方法,调用此方法时可以获取,缓存大小

        // 参数1:获取缓存应用的包名,参数2:aidl文件指向类,对应的对象
        // 问题:PackageManager中getPackageSizeInfo方法隐藏方法不能被调用,需要反射
        // mPm.getPackageSizeInfo("com.android.browser", mStatsObserver);

        //1.获取指定类的字节码文件
        try {
            Class<?> clazz = Class.forName("android.content.pm.PackageManager");
            //2.获取调用方法对象
            Method method = clazz.getMethod("getPackageSizeInfo", String.class,IPackageStatsObserver.class);
            //3.获取对象调用方法
            method.invoke(mPm, "com.android.browser",mStatsObserver);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
