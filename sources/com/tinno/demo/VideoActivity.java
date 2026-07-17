package com.tinno.demo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerView;
import com.tinno.demo.util.ActivityUtils;
import com.tinno.demo.util.LogUtils;

/* JADX INFO: loaded from: classes2.dex */
public class VideoActivity extends AppCompatActivity {
    private final String TAG = "VideoActivity";
    private PlayerView mDemoVideo;
    private ExoPlayer mPlayer;

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ActivityUtils.keepScreenOn(this);
        setContentView(R.layout.activity_video);
        ActivityUtils.hideSystemBars(this);
        PlayerView playerView = (PlayerView) findViewById(R.id.pv_demo);
        this.mDemoVideo = playerView;
        playerView.setUseController(false);
        this.mDemoVideo.setOnClickListener(new View.OnClickListener() { // from class: com.tinno.demo.VideoActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                VideoActivity.this.startHomeActivity();
            }
        });
    }

    @Override // android.app.Activity
    public boolean onTouchEvent(MotionEvent motionEvent) {
        LogUtils.d("VideoActivity", "touch");
        startHomeActivity();
        return super.onTouchEvent(motionEvent);
    }

    @Override // androidx.appcompat.app.AppCompatActivity, android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        LogUtils.d("VideoActivity", "key down");
        startHomeActivity();
        return super.onKeyDown(i, keyEvent);
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onStart() {
        super.onStart();
        LogUtils.d("VideoActivity", "onStart");
        playVideo();
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onStop() {
        super.onStop();
        LogUtils.d("VideoActivity", "onStop");
        releasePlayer();
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.d("VideoActivity", "onDestroy");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startHomeActivity() {
        LogUtils.d("VideoActivity", "start home activity");
        finish();
        startActivity(new Intent(this, (Class<?>) HomeActivity.class));
    }

    private void playVideo() {
        if (this.mPlayer != null) {
            releasePlayer();
        }
        ExoPlayer exoPlayerBuild = new ExoPlayer.Builder(this).build();
        this.mPlayer = exoPlayerBuild;
        this.mDemoVideo.setPlayer(exoPlayerBuild);
        this.mPlayer.setMediaItem(MediaItem.fromUri(Uri.parse("android.resource://" + getPackageName() + "/2131755008")));
        this.mPlayer.setPlayWhenReady(true);
        this.mPlayer.seekTo(0, 0L);
        this.mPlayer.prepare();
        this.mPlayer.setVolume(0.0f);
        this.mPlayer.addListener(new Player.Listener() { // from class: com.tinno.demo.VideoActivity.2
            @Override // com.google.android.exoplayer2.Player.Listener
            public void onPlaybackStateChanged(int i) {
                if (i != 4) {
                    return;
                }
                VideoActivity.this.restartPlayer();
            }

            @Override // com.google.android.exoplayer2.Player.Listener
            public void onPlayerError(PlaybackException playbackException) {
                playbackException.printStackTrace();
            }
        });
    }

    private void releasePlayer() {
        ExoPlayer exoPlayer = this.mPlayer;
        if (exoPlayer != null) {
            exoPlayer.release();
            this.mPlayer = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void restartPlayer() {
        ExoPlayer exoPlayer = this.mPlayer;
        if (exoPlayer != null) {
            exoPlayer.seekTo(0L);
            this.mPlayer.setPlayWhenReady(true);
        }
    }
}
