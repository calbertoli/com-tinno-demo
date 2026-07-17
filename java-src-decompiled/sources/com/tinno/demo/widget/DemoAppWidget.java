package com.tinno.demo.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import androidx.core.view.accessibility.AccessibilityEventCompat;
import com.tinno.demo.HomeActivity;
import com.tinno.demo.R;

/* JADX INFO: loaded from: classes2.dex */
public class DemoAppWidget extends AppWidgetProvider {
    @Override // android.appwidget.AppWidgetProvider
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] iArr) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_demo);
        remoteViews.setOnClickPendingIntent(R.id.ll_widget_demo, PendingIntent.getActivity(context, 0, new Intent(context, (Class<?>) HomeActivity.class), AccessibilityEventCompat.TYPE_VIEW_TARGETED_BY_SCROLL));
        appWidgetManager.updateAppWidget(iArr, remoteViews);
    }
}
