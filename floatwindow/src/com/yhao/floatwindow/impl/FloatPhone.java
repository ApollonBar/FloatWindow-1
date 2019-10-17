package com.yhao.floatwindow.impl;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

import com.yhao.floatwindow.FloatActivity;
import com.yhao.floatwindow.interfaces.BaseFloatView;
import com.yhao.floatwindow.permission.PermissionListener;
import com.yhao.floatwindow.utils.L;
import com.yhao.floatwindow.utils.Miui;

/**
 * @Copyright © 2017 Analysys Inc. All rights reserved.
 * @Description:
 * @Version: 1.0.9
 * @Create: 2017-11-14 17:15:35
 * @Author: yhao
 */
public class FloatPhone extends BaseFloatView {
    private final Context mContext;

    private final WindowManager mWindowManager;
    private final WindowManager.LayoutParams mLayoutParams;
    private View mView;
    private int mX, mY;
    private boolean isRemove = false;
    private PermissionListener mPermissionListener;

    public FloatPhone(Context applicationContext, PermissionListener permissionListener) {
        mContext = applicationContext;
        mPermissionListener = permissionListener;
        mWindowManager = (WindowManager) applicationContext.getSystemService(Context.WINDOW_SERVICE);
        mLayoutParams = new WindowManager.LayoutParams();
        // mLayoutParams.format = PixelFormat.RGBA_8888;
        // mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        // | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

        //PixelFormat.RGBA_8888
        mLayoutParams.format = 1;
        // WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL: 0x00000020
        // WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE: 0x00000008
        // WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS: 0x00000200
        // WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN: 0x00000100
        // WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR: 0x00010000
        mLayoutParams.flags = 0x00000020 | 0x00000008 | 0x00000200 | 0x00000100 | 0x00010000;
        mLayoutParams.windowAnimations = 0;
    }

    @Override
    public void setSize(int width, int height) {
        mLayoutParams.width = width;
        mLayoutParams.height = height;
    }

    @Override
    public void setView(View view) {
        mView = view;
    }

    @Override
    public void setGravity(int gravity, int xOffset, int yOffset) {
        mLayoutParams.gravity = gravity;
        mLayoutParams.x = mX = xOffset;
        mLayoutParams.y = mY = yOffset;
    }

    @Override
    public void init() {
        if (Build.VERSION.SDK_INT >= 25) {
            req();
        } else if (Miui.rom()) {
            if (Build.VERSION.SDK_INT >= 23) {
                req();
            } else {
                // 避免方法警告
                // WindowManager.LayoutParams.TYPE_PHONE: 2002
                mLayoutParams.type = 2002;
                Miui.requestPermission(mContext, new PermissionListener() {
                    @Override
                    public void onSuccess() {
                        mWindowManager.addView(mView, mLayoutParams);
                        if (mPermissionListener != null) {
                            mPermissionListener.onSuccess();
                        }
                    }

                    @Override
                    public void onFail() {
                        if (mPermissionListener != null) {
                            mPermissionListener.onFail();
                        }
                    }
                });
            }
        } else {
            try {
                // 避免过时方法警告
                // WindowManager.LayoutParams.TYPE_TOAST:2005
                mLayoutParams.type = 2005;
                mWindowManager.addView(mView, mLayoutParams);
            } catch (Exception e) {
                mWindowManager.removeView(mView);
                L.e("TYPE_TOAST 失败");
                req();
            }
        }
    }

    private void req() {
        //Build.VERSION_CODES.O: 26
        if (Build.VERSION.SDK_INT >= 26) {
            //WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY: 2038
            mLayoutParams.type = 2038;
        } else {
            //WindowManager.LayoutParams.TYPE_PHONE: 2002
            mLayoutParams.type = 2002;
        }
        FloatActivity.request(mContext, new PermissionListener() {
            @Override
            public void onSuccess() {
                mWindowManager.addView(mView, mLayoutParams);
                if (mPermissionListener != null) {
                    mPermissionListener.onSuccess();
                }
            }

            @Override
            public void onFail() {
                if (mPermissionListener != null) {
                    mPermissionListener.onFail();
                }
            }
        });
    }

    @Override
    public void dismiss() {
        isRemove = true;
        mWindowManager.removeView(mView);
    }

    @Override
    public void updateXY(int x, int y) {
        if (isRemove) {
            return;
        }
        mLayoutParams.x = mX = x;
        mLayoutParams.y = mY = y;
        mWindowManager.updateViewLayout(mView, mLayoutParams);
    }

    @Override
    public void updateX(int x) {
        if (isRemove) {
            return;
        }
        mLayoutParams.x = mX = x;
        mWindowManager.updateViewLayout(mView, mLayoutParams);
    }

    @Override
    public void updateY(int y) {
        if (isRemove) {
            return;
        }
        mLayoutParams.y = mY = y;
        mWindowManager.updateViewLayout(mView, mLayoutParams);
    }

    @Override
    public int getX() {
        return mX;
    }

    @Override
    public int getY() {
        return mY;
    }

}
