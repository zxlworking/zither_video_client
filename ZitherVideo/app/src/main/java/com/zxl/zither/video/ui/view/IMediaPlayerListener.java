package com.zxl.zither.video.ui.view;

import android.media.MediaPlayer;

public interface IMediaPlayerListener {
    public abstract void onPrepared(MediaPlayer mp);
    public abstract void onBufferingUpdate(MediaPlayer mp, int percent);
    public abstract boolean onInfo(MediaPlayer mp, int what, int extra);
    public abstract void onCompletion(MediaPlayer mp);
}
