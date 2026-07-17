package com.google.android.exoplayer2.text.span;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public final class RubySpan implements LanguageFeatureSpan {
    public final int position;
    public final String rubyText;

    public RubySpan(String str, int i) {
        this.rubyText = str;
        this.position = i;
    }
}
