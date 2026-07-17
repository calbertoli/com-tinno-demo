package com.tinno.demo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.tinno.demo.HomeActivity;

/* JADX INFO: loaded from: classes2.dex */
public class BootReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent intent2 = new Intent(context, (Class<?>) HomeActivity.class);
            intent2.addFlags(268435456);
            context.startActivity(intent2);
        }
    }
}
