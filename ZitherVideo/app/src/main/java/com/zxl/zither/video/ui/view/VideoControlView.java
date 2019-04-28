package com.zxl.zither.video.ui.view;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zxl.zither.video.R;

/**
 * Created by zhangxiaolong on 19-4-28.
 */
public class VideoControlView extends LinearLayout {
    private static final String TAG = "VideoControlView";

    private Context mContext;

    private ImageView mPlayPauseImg;

    public VideoControlView(Context context) {
        super(context);
        init(context);
    }

    public VideoControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VideoControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.video_control_view,this);
        mPlayPauseImg = findViewById(R.id.play_pause_img);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable = mPlayPauseImg.getDrawable();
                if (drawable instanceof Animatable) {
                    ((Animatable) drawable).start();
                }
            }
        });
    }
}
