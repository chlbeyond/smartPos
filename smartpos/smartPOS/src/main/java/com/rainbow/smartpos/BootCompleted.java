package com.rainbow.smartpos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompleted extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
//			Intent i = new Intent(context, MainScreenActivity.class);
//			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//			context.startActivity(i);
        }
    }
}