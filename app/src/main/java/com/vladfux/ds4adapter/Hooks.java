package com.vladfux.ds4adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class Hooks implements IXposedHookLoadPackage {
    private static boolean mServiceStarted = false;

    private void handlePsRemote(final LoadPackageParam lpparam) {
        XposedBridge.log("Remoteplay app");

        findAndHookMethod("android.content.ContextWrapper", lpparam.classLoader, "bindService",
                "android.content.Intent", "android.content.ServiceConnection", int.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Intent intent = (Intent)param.args[0];

                        String package_name = intent.getComponent().getPackageName();
                        String class_name = intent.getComponent().getClassName();

                        XposedBridge.log("IntentPackage: " + package_name);
                        XposedBridge.log("IntentClass: " + class_name);

                        if (package_name.equals("com.android.bluetooth")
                                && class_name.equals("com.sonymobile.ds4.HidSonyDS4Service")) {
                            XposedBridge.log("Redirect bind on com.vladfux.ds4adapter");

                            Intent new_intent = new Intent();
                            new_intent.setClassName("com.vladfux.ds4adapter", class_name);
                            param.args[0] = new_intent;
                        }
                    }
                });

        findAndHookMethod("android.app.Activity", lpparam.classLoader, "dispatchGenericMotionEvent",
                MotionEvent.class,  new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        MotionEvent event = (MotionEvent) param.args[0];
                        Intent intent = new Intent();
                        intent.setAction("com.vladfux.ds4adapter.action.MOTION_EVENT");
                        intent.putExtra("com.vladfux.ds4adapter.extra.MOTION_EVENT", event);
                        ((Activity)(param.thisObject)).sendBroadcast(intent);
                    }
                });


        findAndHookMethod("android.app.Activity", lpparam.classLoader, "dispatchKeyEvent",
                KeyEvent.class,  new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        KeyEvent event = (KeyEvent) param.args[0];
                        Intent intent = new Intent();
                        intent.setAction("com.vladfux.ds4adapter.action.KEY_EVENT");
                        intent.putExtra("com.vladfux.ds4adapter.extra.KEY_EVENT", event);
                        ((Activity)(param.thisObject)).sendBroadcast(intent);

                        if (event.getKeyCode() == KeyEvent.KEYCODE_BUTTON_MODE) {
                            XposedBridge.log("KEY_EVENT: " + event.toString());
                            KeyEvent new_even = new KeyEvent(event.getDownTime(), event.getEventTime(),
                                    event.getAction(), 3, event.getRepeatCount(),
                                    event.getMetaState(), event.getDeviceId(), event.getScanCode(),
                                    event.getFlags(), event.getSource());
                            param.args[0] = new_even;
                            XposedBridge.log("Changed to KEY_EVENT: " + new_even.toString());
                        }
                    }
                });
    }

    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals("com.playstation.remoteplay")) {
            handlePsRemote(lpparam);
        }
    }
}
