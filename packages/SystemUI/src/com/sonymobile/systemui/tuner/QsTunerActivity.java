/*
 * Copyright (C) 2015 Sony Mobile Communications Inc.
 * All rights, including trade secret rights, reserved.
 */

package com.sonymobile.systemui.tuner;

import android.app.Activity;
import android.os.Bundle;

import com.android.systemui.tuner.QsTuner;

/**
 * A activity that provides Quick Settings edit function.
 */
public class QsTunerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new QsTuner(), "QsTuner").commit();
    }
}