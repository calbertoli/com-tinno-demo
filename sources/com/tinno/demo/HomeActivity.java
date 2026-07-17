package com.tinno.demo;

import android.content.ComponentName;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.tinno.demo.util.ActivityUtils;

/* JADX INFO: loaded from: classes2.dex */
public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "HomeActivity";

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ActivityUtils.keepScreenOn(this);
        setContentView(R.layout.activity_home);
        ActivityUtils.setTransparentStatusBar(this);
        ActivityUtils.setStatusBarIconVisibility(this, true);
        ActivityUtils.adjustContentForSystemBars(this, findViewById(R.id.ll_content), 20);
        enableAccessibilityService(this);
        init();
    }

    /* JADX WARN: Removed duplicated region for block: B:17:0x0036  */
    /* JADX WARN: Removed duplicated region for block: B:19:? A[RETURN, SYNTHETIC] */
    @Override // android.view.View.OnClickListener
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onClick(android.view.View r3) {
        /*
            r2 = this;
            int r3 = r3.getId()
            r0 = 2131361894(0x7f0a0066, float:1.8343553E38)
            r1 = -1
            if (r3 == r0) goto L29
            switch(r3) {
                case 2131362088: goto L27;
                case 2131362089: goto L25;
                case 2131362090: goto L23;
                case 2131362091: goto L21;
                case 2131362092: goto L1f;
                default: goto Ld;
            }
        Ld:
            switch(r3) {
                case 2131362360: goto L18;
                case 2131362361: goto L11;
                default: goto L10;
            }
        L10:
            goto L33
        L11:
            r3 = 2131558449(0x7f0d0031, float:1.8742214E38)
            r2.setupDialog(r3)
            goto L33
        L18:
            r3 = 2131558448(0x7f0d0030, float:1.8742212E38)
            r2.setupDialog(r3)
            goto L33
        L1f:
            r3 = 4
            goto L34
        L21:
            r3 = 3
            goto L34
        L23:
            r3 = 2
            goto L34
        L25:
            r3 = 1
            goto L34
        L27:
            r3 = 0
            goto L34
        L29:
            android.content.Intent r3 = new android.content.Intent
            java.lang.Class<com.tinno.demo.ModelsActivity> r0 = com.tinno.demo.ModelsActivity.class
            r3.<init>(r2, r0)
            r2.startActivity(r3)
        L33:
            r3 = r1
        L34:
            if (r3 == r1) goto L59
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            java.lang.String r1 = "start selling point:"
            r0.<init>(r1)
            java.lang.StringBuilder r0 = r0.append(r3)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "HomeActivity"
            com.tinno.demo.util.LogUtils.d(r1, r0)
            android.content.Intent r0 = new android.content.Intent
            java.lang.Class<com.tinno.demo.SellingPointActivity> r1 = com.tinno.demo.SellingPointActivity.class
            r0.<init>(r2, r1)
            java.lang.String r1 = "key_selling_point"
            r0.putExtra(r1, r3)
            r2.startActivity(r0)
        L59:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.tinno.demo.HomeActivity.onClick(android.view.View):void");
    }

    private void init() {
        findViewById(R.id.btn_models).setOnClickListener(this);
        findViewById(R.id.iv_selling_point_1).setOnClickListener(this);
        findViewById(R.id.iv_selling_point_2).setOnClickListener(this);
        findViewById(R.id.iv_selling_point_3).setOnClickListener(this);
        findViewById(R.id.iv_selling_point_4).setOnClickListener(this);
        findViewById(R.id.iv_selling_point_5).setOnClickListener(this);
        TextView textView = (TextView) findViewById(R.id.tv_footer_disclaimer);
        setTextUnderline(textView, getString(R.string.home_footer_disclaimer));
        textView.setOnClickListener(this);
        TextView textView2 = (TextView) findViewById(R.id.tv_footer_terms_conditions);
        setTextUnderline(textView2, getString(R.string.home_footer_terms_conditions));
        textView2.setOnClickListener(this);
    }

    private void setTextUnderline(TextView textView, String str) {
        SpannableString spannableString = new SpannableString(str);
        spannableString.setSpan(new UnderlineSpan(), 0, str.length(), 33);
        textView.setText(spannableString);
    }

    private void setupDialog(int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View viewInflate = getLayoutInflater().inflate(i, (ViewGroup) null);
        builder.setView(viewInflate);
        final AlertDialog alertDialogCreate = builder.create();
        Window window = alertDialogCreate.getWindow();
        if (window != null) {
            window.setGravity(17);
            alertDialogCreate.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialogCreate.show();
        viewInflate.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() { // from class: com.tinno.demo.HomeActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                alertDialogCreate.dismiss();
            }
        });
    }

    private void enableAccessibilityService(Context context) {
        try {
            String strFlattenToString = new ComponentName(context.getPackageName(), InactivityMonitorService.class.getName()).flattenToString();
            String string = Settings.Secure.getString(context.getContentResolver(), "enabled_accessibility_services");
            if (string == null || !string.contains(strFlattenToString)) {
                if (string != null && !string.isEmpty()) {
                    strFlattenToString = string + ":" + strFlattenToString;
                }
                Settings.Secure.putString(context.getContentResolver(), "enabled_accessibility_services", strFlattenToString);
                Settings.Secure.putInt(context.getContentResolver(), "accessibility_enabled", 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
