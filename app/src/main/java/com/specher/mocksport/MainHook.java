package com.specher.mocksport;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.Random;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class MainHook implements IXposedHookLoadPackage, IXposedHookZygoteInit {
    public final String SETTING_CHANGED = "com.specher.mocksport.SETTING_CHANGED";
    private static final String YUEDONG = "com.yuedong.sport";
    private static final String CHUNYU = "me.chunyu.Pedometer";
    private static final String LEDONG = "cn.ledongli.ldl";
    private static final int UPTATE_INTERVAL_TIME = 100; // 两次的时间间隔
    private int m=10;
    private long lastUpdateTime,lastUpdateTime1; // 上次检测时间
    static boolean  isChunyu, isYuedong,isLedong,isAll,isLocked;
    XSharedPreferences sharedPreferences;

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        Log.w("msg", "mshooked:"+loadPackageParam.packageName);
        IntentFilter intentFilter = new IntentFilter();
        final Object activityThread = XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", null), "currentActivityThread");
        final Context systemContext = (Context) XposedHelpers.callMethod(activityThread, "getSystemContext");
        intentFilter.addAction(SETTING_CHANGED);
        systemContext.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                isAll = intent.getExtras().getBoolean("all", false);
                isChunyu = intent.getExtras().getBoolean("chunyu", true);
                isYuedong = intent.getExtras().getBoolean("yuedong", true);
                m = Integer.valueOf(intent.getExtras().getString("magnification", "10"));
                isLedong = intent.getExtras().getBoolean("ledong", true);
                isLocked =intent.getExtras().getBoolean("lock", true);
            }
        }, intentFilter);

        //if ( loadPackageParam.packageName.equals(YUEDONG) || loadPackageParam.packageName.equals(CHUNYU)|| loadPackageParam.packageName.equals(LEDONG) || isAll) {

            getKey();
            final Class<?> sensorEL = XposedHelpers.findClass("android.hardware.SystemSensorManager$SensorEventQueue", loadPackageParam.classLoader);
            XposedBridge.hookAllMethods(sensorEL, "dispatchSensorEvent", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                    if (isLocked) {

                        ((float[]) param.args[1])[0] = 0;
                            ((float[]) param.args[1])[2] =0;
                            ((float[]) param.args[1])[1] =0;

                    }else
                    {
                        if (isChunyu && loadPackageParam.packageName.equals(CHUNYU)) {
                            ((float[]) param.args[1])[0] = (125.0F + 1200.0F * new Random().nextFloat());
                        }
                        if (isYuedong && loadPackageParam.packageName.equals(YUEDONG)) {
                            ((float[]) param.args[1])[0] = (125.0F + 1200.0F * new Random().nextFloat());
                        } if (isLedong && loadPackageParam.packageName.equals(LEDONG)) {
                        ((float[]) param.args[1])[0] = (125.0F + 1200.0F * new Random().nextFloat());

                    }
                        if (isAll ) {

                            long currentUpdateTime1 = System.currentTimeMillis();
                            long timeInterval1 = currentUpdateTime1 - lastUpdateTime1;

                            if (timeInterval1 < m*1000)
                                return;
                                // 现在检测时间
                            long currentUpdateTime = System.currentTimeMillis();

                                // 两次检测的时间间隔
                            long timeInterval = currentUpdateTime - lastUpdateTime;

                            ((float[]) param.args[1])[0] = (10.0F + 1000.0F * new Random().nextFloat());
                            ((float[]) param.args[1])[2] = (10.0F + 1000.0F * new Random().nextFloat());
                            ((float[]) param.args[1])[1] = (10.0F + 1000.0F * new Random().nextFloat());

                                // 判断是否达到了检测时间间隔
                                if (timeInterval < UPTATE_INTERVAL_TIME)
                                    return;
                                // 现在的时间变成last时间
                                lastUpdateTime = currentUpdateTime;
                              lastUpdateTime1 = currentUpdateTime1;
                            ((float[]) param.args[1])[0] = (0.0F + 1000.0F * new Random().nextFloat());
                            ((float[]) param.args[1])[2] = (0.0F + 1000.0F * new Random().nextFloat());
                            ((float[]) param.args[1])[1] = (0.0F + 1000.0F * new Random().nextFloat());

                        }


                    }
                }
            });
        }
    //}

    private void getKey() {
        sharedPreferences.reload();
        isAll = sharedPreferences.getBoolean("all", false);
        isLocked =sharedPreferences.getBoolean("lock", true);
        m = Integer.valueOf(sharedPreferences.getString("magnification", "10"));
        isChunyu = sharedPreferences.getBoolean("chunyu", true);
        isYuedong = sharedPreferences.getBoolean("yuedong", true);
        isLedong = sharedPreferences.getBoolean("ledong", true);
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        sharedPreferences = new XSharedPreferences(BuildConfig.APPLICATION_ID);
    }
}
