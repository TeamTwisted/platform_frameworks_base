/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.android.systemui.statusbar.phone;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.android.systemui.R;

public class StatusBarBgAnimationController {

    private ValueAnimator mValueAnimator;
    private ImageView mImageView;
    private FrameLayout mFrameLayout;
    private Context mContext;

    private float mDisplayWidth;
    private float mGlowWidth;
    private static final int ANIMATION_DURATION = 3000;
    private static final String ACTION_ONGOING_CALL = "com.android.systemui.ACTION_ONGOING_CALL";
    private static final String EXTRA_SHOW = "show";
    private static boolean mShowValueAnimator = false;
    private static boolean mIsScreenOn = true;
    private int mBarHeightKeyguard = -1;
    private int mBarHeight = -1;

    private final BroadcastReceiver mOngoingCallReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_ONGOING_CALL.equals(intent.getAction())) {
                mShowValueAnimator = intent.getBooleanExtra(EXTRA_SHOW, false);

                if (!mIsScreenOn) {
                    return;
                }

                if (mShowValueAnimator) {
                    mFrameLayout.setVisibility(View.VISIBLE);
                    if (!mValueAnimator.isRunning()) {
                        mValueAnimator.start();
                    }
                } else {
                    mFrameLayout.setVisibility(View.GONE);
                    mValueAnimator.cancel();
                }

            } else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
                mIsScreenOn = true;
                if (mShowValueAnimator) {
                    mFrameLayout.setVisibility(View.VISIBLE);
                    if (!mValueAnimator.isRunning()){
                        mValueAnimator.start();
                    }
                }
            } else if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                mIsScreenOn = false;
                if (mValueAnimator.isRunning()) {
                    mFrameLayout.setVisibility(View.GONE);
                    mValueAnimator.cancel();
                }
            }
        }
    };

    public StatusBarBgAnimationController(Context context,
            StatusBarWindowView statusBarWindowView) {

        mContext = context;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_ONGOING_CALL);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mContext.registerReceiverAsUser(mOngoingCallReceiver, UserHandle.ALL, intentFilter,
                Manifest.permission.CONTROL_INCALL_EXPERIENCE, null);

        Drawable drawable = mContext.getResources().getDrawable(
                R.drawable.ic_sysbar_lights_out_dot_large);
        mFrameLayout = (FrameLayout) statusBarWindowView
                .findViewById(R.id.ongoing_call_bg_parent_layout);

        mImageView = (ImageView) mFrameLayout.findViewById(R.id.ongoing_call_glow);
        mImageView.setImageDrawable(drawable);

        mGlowWidth = drawable.getIntrinsicWidth();
        mDisplayWidth = mContext.getResources().getDisplayMetrics().widthPixels;

        mValueAnimator = new ValueAnimator();
        if (mFrameLayout.isLayoutRtl()) {
            mValueAnimator.setFloatValues(1.0f, 0.0f);
        } else {
            mValueAnimator.setFloatValues(0.0f, 1.0f);
        }
        mValueAnimator.setRepeatMode(ValueAnimator.RESTART);
        mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mValueAnimator.setDuration(ANIMATION_DURATION);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float r = ((Float) animation.getAnimatedValue()).floatValue();
                float x = r * mDisplayWidth + (r - 1.0f) * mGlowWidth;
                mImageView.setTranslationX(x);
            }
        });
    }

    public void onConfigurationChanged(int direction) {
        mDisplayWidth = mContext.getResources().getDisplayMetrics().widthPixels;
        if (direction == View.LAYOUT_DIRECTION_RTL) {
            mValueAnimator.setFloatValues(1.0f, 0.0f);
        } else {
            mValueAnimator.setFloatValues(0.0f, 1.0f);
        }
    }

    public void destroy() {
        mContext.unregisterReceiver(mOngoingCallReceiver);
    }

    public void setKeyguardShowing(View statusBarView, boolean keyguardOn) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)statusBarView.getLayoutParams();
        final Resources res = mContext.getResources();
        if (keyguardOn) {
            // Keyguard size status bar
            if (mBarHeightKeyguard < 0) {
                mBarHeightKeyguard = res.getDimensionPixelSize(
                        R.dimen.status_bar_header_height_keyguard);
            }
            params.height = mBarHeightKeyguard;
        } else {
            // Standard size status bar
            if (mBarHeight < 0) {
                mBarHeight = res.getDimensionPixelSize(
                        com.android.internal.R.dimen.status_bar_height);
            }
            params.height = mBarHeight;
        }
        statusBarView.setLayoutParams(params);
    }
}
