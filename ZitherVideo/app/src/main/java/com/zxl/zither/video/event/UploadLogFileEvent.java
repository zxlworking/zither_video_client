package com.zxl.zither.video.event;

/**
 * Created by zxl on 2018/9/6.
 */

public class UploadLogFileEvent {
    public long mTotal;
    public long mProgress;

    public UploadLogFileEvent(long total, long progress){
        mTotal = total;
        mProgress = progress;
    }
}
