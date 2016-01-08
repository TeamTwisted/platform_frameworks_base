/*
 * Copyright (C) 2014 The Android Open Source Project
 * Copyright (C) 2012-2015 The CyanogenMod Project
 * Copyright 2014-2015 The Euphoria-OS Project
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
 * limitations under the License.
 */

package com.android.systemui.qs.tiles;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;

import com.android.systemui.R;
import com.android.systemui.qs.QSTile;
import com.android.internal.logging.MetricsLogger;

/** Quick settings tile: Twisted Settings **/
public class TwistedTile extends QSTile<QSTile.BooleanState> {

    private static final Intent TWISTED_SETTINGS = new Intent().setComponent(new ComponentName(
            "com.android.settings", "com.android.settings.Settings$TwistedSettingsActivity"));
    private static final Intent TWISTED_PROJECT = new Intent().setComponent(new ComponentName(
            "com.android.twistedproject", "com.android.twistedproject.TwistedActivity"));

    public TwistedTile(Host host) {
        super(host);
    }

    @Override
    protected BooleanState newTileState() {
        return new BooleanState();
    }

    @Override
    public void setListening(boolean listening) {
    }

    @Override
    protected void handleClick() {
        mHost.startActivityDismissingKeyguard(TWISTED_SETTINGS);
    }

    @Override
    protected void handleLongClick() {
        mHost.startActivityDismissingKeyguard(TWISTED_PROJECT);
    }

    private void updateState() {
    }

    @Override
    public int getMetricsCategory() {
        return MetricsLogger.TWISTED_METRIC;
    }

    @Override
    protected void handleUpdateState(BooleanState state, Object arg) {
        state.visible = true;
        state.label = mContext.getString(R.string.quick_settings_twisted_label);
        state.icon = ResourceIcon.get(R.drawable.ic_qs_twisted);
    }

}
