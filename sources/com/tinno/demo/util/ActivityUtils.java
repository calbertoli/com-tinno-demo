package com.tinno.demo.util;

import android.R;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/* JADX INFO: loaded from: classes2.dex */
public class ActivityUtils {
    private static final String PRODUCT_SNAKE = "snake";
    private static final String VIDEO_ACTIVITY_NAME = "VideoActivity";

    public static void keepScreenOn(Activity activity) {
        activity.getWindow().addFlags(6815872);
    }

    public static void hideSystemBars(Activity activity) {
        if (Build.VERSION.SDK_INT >= 30) {
            activity.getWindow().setDecorFitsSystemWindows(false);
            WindowInsetsController insetsController = activity.getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                insetsController.setSystemBarsBehavior(2);
                return;
            }
            return;
        }
        activity.getWindow().getDecorView().setSystemUiVisibility(5382);
    }

    public static void setTransparentStatusBar(Activity activity) {
        activity.getWindow().getDecorView().setSystemUiVisibility(1280);
        activity.getWindow().setStatusBarColor(0);
    }

    public static void adjustContentForSystemBars(final Activity activity, final int i) {
        View viewFindViewById = activity.findViewById(R.id.content);
        if (viewFindViewById != null) {
            ViewCompat.setOnApplyWindowInsetsListener(viewFindViewById, new OnApplyWindowInsetsListener() { // from class: com.tinno.demo.util.ActivityUtils$$ExternalSyntheticLambda0
                @Override // androidx.core.view.OnApplyWindowInsetsListener
                public final WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat) {
                    return ActivityUtils.lambda$adjustContentForSystemBars$0(activity, i, view, windowInsetsCompat);
                }
            });
        }
    }

    static /* synthetic */ WindowInsetsCompat lambda$adjustContentForSystemBars$0(Activity activity, int i, View view, WindowInsetsCompat windowInsetsCompat) {
        Insets insets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars());
        float f = i;
        view.setPadding(dpToPx(activity, f), insets.top, dpToPx(activity, f), insets.bottom);
        return WindowInsetsCompat.CONSUMED;
    }

    public static void adjustContentForSystemBars(final Activity activity, View view, final int i) {
        if (view != null) {
            ViewCompat.setOnApplyWindowInsetsListener(view, new OnApplyWindowInsetsListener() { // from class: com.tinno.demo.util.ActivityUtils$$ExternalSyntheticLambda1
                @Override // androidx.core.view.OnApplyWindowInsetsListener
                public final WindowInsetsCompat onApplyWindowInsets(View view2, WindowInsetsCompat windowInsetsCompat) {
                    return ActivityUtils.lambda$adjustContentForSystemBars$1(activity, i, view2, windowInsetsCompat);
                }
            });
        }
    }

    static /* synthetic */ WindowInsetsCompat lambda$adjustContentForSystemBars$1(Activity activity, int i, View view, WindowInsetsCompat windowInsetsCompat) {
        Insets insets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars());
        float f = i;
        view.setPadding(dpToPx(activity, f), insets.top, dpToPx(activity, f), insets.bottom);
        return WindowInsetsCompat.CONSUMED;
    }

    public static void setStatusBarIconVisibility(Activity activity, boolean z) {
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= 30) {
            WindowInsetsController insetsController = window.getInsetsController();
            if (insetsController != null) {
                insetsController.setSystemBarsAppearance(z ? 8 : 0, 8);
                return;
            }
            return;
        }
        View decorView = window.getDecorView();
        int systemUiVisibility = decorView.getSystemUiVisibility();
        decorView.setSystemUiVisibility(z ? systemUiVisibility | 8192 : systemUiVisibility & (-8193));
    }

    public static int dpToPx(Context context, float f) {
        return (int) ((f * context.getResources().getDisplayMetrics().density) + 0.5f);
    }
}
