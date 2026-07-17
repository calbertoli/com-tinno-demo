package androidx.media.app;

import android.app.Notification;
import android.app.PendingIntent;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.media.session.MediaSessionCompat;
import android.widget.RemoteViews;
import androidx.core.app.NotificationBuilderWithBuilderAccessor;
import androidx.core.app.NotificationCompat;
import androidx.media.R;

/* JADX INFO: loaded from: classes.dex */
public class NotificationCompat {
    private NotificationCompat() {
    }

    public static class MediaStyle extends NotificationCompat.Style {
        private static final int MAX_MEDIA_BUTTONS = 5;
        private static final int MAX_MEDIA_BUTTONS_IN_COMPACT = 3;
        int[] mActionsToShowInCompact = null;
        PendingIntent mCancelButtonIntent;
        boolean mShowCancelButton;
        MediaSessionCompat.Token mToken;

        @Override // androidx.core.app.NotificationCompat.Style
        public RemoteViews makeBigContentView(NotificationBuilderWithBuilderAccessor notificationBuilderWithBuilderAccessor) {
            return null;
        }

        @Override // androidx.core.app.NotificationCompat.Style
        public RemoteViews makeContentView(NotificationBuilderWithBuilderAccessor notificationBuilderWithBuilderAccessor) {
            return null;
        }

        public MediaStyle setCancelButtonIntent(PendingIntent pendingIntent) {
            this.mCancelButtonIntent = pendingIntent;
            return this;
        }

        public MediaStyle setMediaSession(MediaSessionCompat.Token token) {
            this.mToken = token;
            return this;
        }

        public MediaStyle setShowActionsInCompactView(int... iArr) {
            this.mActionsToShowInCompact = iArr;
            return this;
        }

        public MediaStyle setShowCancelButton(boolean z) {
            return this;
        }

        public static MediaSessionCompat.Token getMediaSession(Notification notification) {
            Parcelable parcelable;
            Bundle extras = androidx.core.app.NotificationCompat.getExtras(notification);
            if (extras == null || (parcelable = extras.getParcelable(androidx.core.app.NotificationCompat.EXTRA_MEDIA_SESSION)) == null) {
                return null;
            }
            return MediaSessionCompat.Token.fromToken(parcelable);
        }

        public MediaStyle() {
        }

        public MediaStyle(NotificationCompat.Builder builder) {
            setBuilder(builder);
        }

        @Override // androidx.core.app.NotificationCompat.Style
        public void apply(NotificationBuilderWithBuilderAccessor notificationBuilderWithBuilderAccessor) {
            Api21Impl.setMediaStyle(notificationBuilderWithBuilderAccessor.getBuilder(), Api21Impl.fillInMediaStyle(Api21Impl.createMediaStyle(), this.mActionsToShowInCompact, this.mToken));
        }

        RemoteViews generateContentView() {
            RemoteViews remoteViewsApplyStandardTemplate = applyStandardTemplate(false, getContentViewLayoutResource(), true);
            int size = this.mBuilder.mActions.size();
            int[] iArr = this.mActionsToShowInCompact;
            int iMin = iArr == null ? 0 : Math.min(iArr.length, 3);
            remoteViewsApplyStandardTemplate.removeAllViews(R.id.media_actions);
            if (iMin > 0) {
                for (int i = 0; i < iMin; i++) {
                    if (i >= size) {
                        throw new IllegalArgumentException(String.format("setShowActionsInCompactView: action %d out of bounds (max %d)", Integer.valueOf(i), Integer.valueOf(size - 1)));
                    }
                    remoteViewsApplyStandardTemplate.addView(R.id.media_actions, generateMediaActionButton(this.mBuilder.mActions.get(this.mActionsToShowInCompact[i])));
                }
            }
            if (this.mShowCancelButton) {
                remoteViewsApplyStandardTemplate.setViewVisibility(R.id.end_padder, 8);
                remoteViewsApplyStandardTemplate.setViewVisibility(R.id.cancel_action, 0);
                remoteViewsApplyStandardTemplate.setOnClickPendingIntent(R.id.cancel_action, this.mCancelButtonIntent);
                remoteViewsApplyStandardTemplate.setInt(R.id.cancel_action, "setAlpha", this.mBuilder.mContext.getResources().getInteger(R.integer.cancel_button_image_alpha));
            } else {
                remoteViewsApplyStandardTemplate.setViewVisibility(R.id.end_padder, 0);
                remoteViewsApplyStandardTemplate.setViewVisibility(R.id.cancel_action, 8);
            }
            return remoteViewsApplyStandardTemplate;
        }

        private RemoteViews generateMediaActionButton(NotificationCompat.Action action) {
            boolean z = action.getActionIntent() == null;
            RemoteViews remoteViews = new RemoteViews(this.mBuilder.mContext.getPackageName(), R.layout.notification_media_action);
            remoteViews.setImageViewResource(R.id.action0, action.getIcon());
            if (!z) {
                remoteViews.setOnClickPendingIntent(R.id.action0, action.getActionIntent());
            }
            Api15Impl.setContentDescription(remoteViews, R.id.action0, action.getTitle());
            return remoteViews;
        }

        int getContentViewLayoutResource() {
            return R.layout.notification_template_media;
        }

        RemoteViews generateBigContentView() {
            int iMin = Math.min(this.mBuilder.mActions.size(), 5);
            RemoteViews remoteViewsApplyStandardTemplate = applyStandardTemplate(false, getBigContentViewLayoutResource(iMin), false);
            remoteViewsApplyStandardTemplate.removeAllViews(R.id.media_actions);
            if (iMin > 0) {
                for (int i = 0; i < iMin; i++) {
                    remoteViewsApplyStandardTemplate.addView(R.id.media_actions, generateMediaActionButton(this.mBuilder.mActions.get(i)));
                }
            }
            if (this.mShowCancelButton) {
                remoteViewsApplyStandardTemplate.setViewVisibility(R.id.cancel_action, 0);
                remoteViewsApplyStandardTemplate.setInt(R.id.cancel_action, "setAlpha", this.mBuilder.mContext.getResources().getInteger(R.integer.cancel_button_image_alpha));
                remoteViewsApplyStandardTemplate.setOnClickPendingIntent(R.id.cancel_action, this.mCancelButtonIntent);
            } else {
                remoteViewsApplyStandardTemplate.setViewVisibility(R.id.cancel_action, 8);
            }
            return remoteViewsApplyStandardTemplate;
        }

        int getBigContentViewLayoutResource(int i) {
            if (i <= 3) {
                return R.layout.notification_template_big_media_narrow;
            }
            return R.layout.notification_template_big_media;
        }
    }

    public static class DecoratedMediaCustomViewStyle extends MediaStyle {
        @Override // androidx.media.app.NotificationCompat.MediaStyle, androidx.core.app.NotificationCompat.Style
        public RemoteViews makeBigContentView(NotificationBuilderWithBuilderAccessor notificationBuilderWithBuilderAccessor) {
            return null;
        }

        @Override // androidx.media.app.NotificationCompat.MediaStyle, androidx.core.app.NotificationCompat.Style
        public RemoteViews makeContentView(NotificationBuilderWithBuilderAccessor notificationBuilderWithBuilderAccessor) {
            return null;
        }

        @Override // androidx.core.app.NotificationCompat.Style
        public RemoteViews makeHeadsUpContentView(NotificationBuilderWithBuilderAccessor notificationBuilderWithBuilderAccessor) {
            return null;
        }

        @Override // androidx.media.app.NotificationCompat.MediaStyle, androidx.core.app.NotificationCompat.Style
        public void apply(NotificationBuilderWithBuilderAccessor notificationBuilderWithBuilderAccessor) {
            Api21Impl.setMediaStyle(notificationBuilderWithBuilderAccessor.getBuilder(), Api21Impl.fillInMediaStyle(Api24Impl.createDecoratedMediaCustomViewStyle(), this.mActionsToShowInCompact, this.mToken));
        }

        @Override // androidx.media.app.NotificationCompat.MediaStyle
        int getContentViewLayoutResource() {
            if (this.mBuilder.getContentView() != null) {
                return R.layout.notification_template_media_custom;
            }
            return super.getContentViewLayoutResource();
        }

        @Override // androidx.media.app.NotificationCompat.MediaStyle
        int getBigContentViewLayoutResource(int i) {
            if (i <= 3) {
                return R.layout.notification_template_big_media_narrow_custom;
            }
            return R.layout.notification_template_big_media_custom;
        }

        private void setBackgroundColor(RemoteViews remoteViews) {
            int color;
            if (this.mBuilder.getColor() != 0) {
                color = this.mBuilder.getColor();
            } else {
                color = this.mBuilder.mContext.getResources().getColor(R.color.notification_material_background_media_default_color);
            }
            remoteViews.setInt(R.id.status_bar_latest_event_content, "setBackgroundColor", color);
        }
    }

    private static class Api15Impl {
        private Api15Impl() {
        }

        static void setContentDescription(RemoteViews remoteViews, int i, CharSequence charSequence) {
            remoteViews.setContentDescription(i, charSequence);
        }
    }

    private static class Api21Impl {
        private Api21Impl() {
        }

        static void setMediaStyle(Notification.Builder builder, Notification.MediaStyle mediaStyle) {
            builder.setStyle(mediaStyle);
        }

        static Notification.MediaStyle createMediaStyle() {
            return new Notification.MediaStyle();
        }

        static Notification.MediaStyle fillInMediaStyle(Notification.MediaStyle mediaStyle, int[] iArr, MediaSessionCompat.Token token) {
            if (iArr != null) {
                setShowActionsInCompactView(mediaStyle, iArr);
            }
            if (token != null) {
                setMediaSession(mediaStyle, (MediaSession.Token) token.getToken());
            }
            return mediaStyle;
        }

        static void setShowActionsInCompactView(Notification.MediaStyle mediaStyle, int... iArr) {
            mediaStyle.setShowActionsInCompactView(iArr);
        }

        static void setMediaSession(Notification.MediaStyle mediaStyle, MediaSession.Token token) {
            mediaStyle.setMediaSession(token);
        }
    }

    private static class Api24Impl {
        private Api24Impl() {
        }

        static Notification.DecoratedMediaCustomViewStyle createDecoratedMediaCustomViewStyle() {
            return new Notification.DecoratedMediaCustomViewStyle();
        }
    }
}
