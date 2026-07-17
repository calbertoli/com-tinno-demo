package com.google.android.exoplayer2.ui;

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;
import android.widget.FrameLayout;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.ttml.TtmlNode;
import com.google.android.exoplayer2.ui.SubtitleView;
import com.google.android.exoplayer2.util.Util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
final class WebViewSubtitleOutput extends FrameLayout implements SubtitleView.Output {
    private static final float CSS_LINE_HEIGHT = 1.2f;
    private static final String DEFAULT_BACKGROUND_CSS_CLASS = "default_bg";
    private float bottomPaddingFraction;
    private final CanvasSubtitleOutput canvasSubtitleOutput;
    private float defaultTextSize;
    private int defaultTextSizeType;
    private CaptionStyleCompat style;
    private List<Cue> textCues;
    private final WebView webView;

    private static int anchorTypeToTranslatePercent(int i) {
        if (i != 1) {
            return i != 2 ? 0 : -100;
        }
        return -50;
    }

    private static String convertVerticalTypeToCss(int i) {
        return i != 1 ? i != 2 ? "horizontal-tb" : "vertical-lr" : "vertical-rl";
    }

    public WebViewSubtitleOutput(Context context) {
        this(context, null);
    }

    public WebViewSubtitleOutput(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.textCues = Collections.emptyList();
        this.style = CaptionStyleCompat.DEFAULT;
        this.defaultTextSize = 0.0533f;
        this.defaultTextSizeType = 0;
        this.bottomPaddingFraction = 0.08f;
        CanvasSubtitleOutput canvasSubtitleOutput = new CanvasSubtitleOutput(context, attributeSet);
        this.canvasSubtitleOutput = canvasSubtitleOutput;
        WebView webView = new WebView(context, attributeSet) { // from class: com.google.android.exoplayer2.ui.WebViewSubtitleOutput.1
            @Override // android.webkit.WebView, android.view.View
            public boolean onTouchEvent(MotionEvent motionEvent) {
                super.onTouchEvent(motionEvent);
                return false;
            }

            @Override // android.view.View
            public boolean performClick() {
                super.performClick();
                return false;
            }
        };
        this.webView = webView;
        webView.setBackgroundColor(0);
        addView(canvasSubtitleOutput);
        addView(webView);
    }

    @Override // com.google.android.exoplayer2.ui.SubtitleView.Output
    public void update(List<Cue> list, CaptionStyleCompat captionStyleCompat, float f, int i, float f2) {
        this.style = captionStyleCompat;
        this.defaultTextSize = f;
        this.defaultTextSizeType = i;
        this.bottomPaddingFraction = f2;
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        for (int i2 = 0; i2 < list.size(); i2++) {
            Cue cue = list.get(i2);
            if (cue.bitmap != null) {
                arrayList.add(cue);
            } else {
                arrayList2.add(cue);
            }
        }
        if (!this.textCues.isEmpty() || !arrayList2.isEmpty()) {
            this.textCues = arrayList2;
            updateWebView();
        }
        this.canvasSubtitleOutput.update(arrayList, captionStyleCompat, f, i, f2);
        invalidate();
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (!z || this.textCues.isEmpty()) {
            return;
        }
        updateWebView();
    }

    public void destroy() {
        this.webView.destroy();
    }

    /* JADX WARN: Removed duplicated region for block: B:26:0x00f8  */
    /* JADX WARN: Removed duplicated region for block: B:27:0x0108  */
    /* JADX WARN: Removed duplicated region for block: B:30:0x0124  */
    /* JADX WARN: Removed duplicated region for block: B:31:0x0127  */
    /* JADX WARN: Removed duplicated region for block: B:34:0x013b  */
    /* JADX WARN: Removed duplicated region for block: B:40:0x0149  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x014b  */
    /* JADX WARN: Removed duplicated region for block: B:49:0x015f  */
    /* JADX WARN: Removed duplicated region for block: B:53:0x018a  */
    /* JADX WARN: Removed duplicated region for block: B:63:0x01e3  */
    /* JADX WARN: Removed duplicated region for block: B:64:0x0203  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void updateWebView() {
        /*
            Method dump skipped, instruction units count: 627
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.ui.WebViewSubtitleOutput.updateWebView():void");
    }

    private static String getBlockShearTransformFunction(Cue cue) {
        if (cue.shearDegrees != 0.0f) {
            return Util.formatInvariant("%s(%.2fdeg)", (cue.verticalType == 2 || cue.verticalType == 1) ? "skewY" : "skewX", Float.valueOf(cue.shearDegrees));
        }
        return "";
    }

    private String convertTextSizeToCss(int i, float f) {
        float fResolveTextSize = SubtitleViewUtils.resolveTextSize(i, f, getHeight(), (getHeight() - getPaddingTop()) - getPaddingBottom());
        return fResolveTextSize == -3.4028235E38f ? "unset" : Util.formatInvariant("%.2fpx", Float.valueOf(fResolveTextSize / getContext().getResources().getDisplayMetrics().density));
    }

    private static String convertCaptionStyleToCssTextShadow(CaptionStyleCompat captionStyleCompat) {
        int i = captionStyleCompat.edgeType;
        if (i == 1) {
            return Util.formatInvariant("1px 1px 0 %1$s, 1px -1px 0 %1$s, -1px 1px 0 %1$s, -1px -1px 0 %1$s", HtmlUtils.toCssRgba(captionStyleCompat.edgeColor));
        }
        if (i == 2) {
            return Util.formatInvariant("0.1em 0.12em 0.15em %s", HtmlUtils.toCssRgba(captionStyleCompat.edgeColor));
        }
        if (i != 3) {
            return i != 4 ? "unset" : Util.formatInvariant("-0.05em -0.05em 0.15em %s", HtmlUtils.toCssRgba(captionStyleCompat.edgeColor));
        }
        return Util.formatInvariant("0.06em 0.08em 0.15em %s", HtmlUtils.toCssRgba(captionStyleCompat.edgeColor));
    }

    /* JADX INFO: renamed from: com.google.android.exoplayer2.ui.WebViewSubtitleOutput$2, reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$android$text$Layout$Alignment;

        static {
            int[] iArr = new int[Layout.Alignment.values().length];
            $SwitchMap$android$text$Layout$Alignment = iArr;
            try {
                iArr[Layout.Alignment.ALIGN_NORMAL.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$android$text$Layout$Alignment[Layout.Alignment.ALIGN_OPPOSITE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$android$text$Layout$Alignment[Layout.Alignment.ALIGN_CENTER.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    private static String convertAlignmentToCss(Layout.Alignment alignment) {
        if (alignment == null) {
            return TtmlNode.CENTER;
        }
        int i = AnonymousClass2.$SwitchMap$android$text$Layout$Alignment[alignment.ordinal()];
        return i != 1 ? i != 2 ? TtmlNode.CENTER : TtmlNode.END : TtmlNode.START;
    }
}
