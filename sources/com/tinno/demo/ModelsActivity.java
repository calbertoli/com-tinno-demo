package com.tinno.demo;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.tinno.demo.util.ActivityUtils;

/* JADX INFO: loaded from: classes2.dex */
public class ModelsActivity extends AppCompatActivity {
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ActivityUtils.keepScreenOn(this);
        setContentView(R.layout.activity_models);
        ActivityUtils.setTransparentStatusBar(this);
        ActivityUtils.setStatusBarIconVisibility(this, true);
        init();
    }

    private void init() {
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() { // from class: com.tinno.demo.ModelsActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (view.getId() == R.id.iv_back) {
                    ModelsActivity.this.finish();
                }
            }
        });
    }
}
