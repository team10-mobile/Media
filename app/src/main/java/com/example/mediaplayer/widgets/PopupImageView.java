package com.example.mediaplayer.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.widget.ImageView;

public class PopupImageView extends android.support.v7.widget.AppCompatImageView {

    public PopupImageView(Context context) {
        super(context);
    }

    public PopupImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        tint();
    }

    public PopupImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        tint();
    }

    @TargetApi(21)
    public PopupImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context,attrs,defStyleAttr);
        tint();
    }

    private void tint() {
        ImageView imageView = this;
        if (PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("dark_theme", false)) {
            imageView.setColorFilter(Color.parseColor("#eeeeee"), PorterDuff.Mode.SRC_ATOP);
        } else  {
            imageView.setColorFilter(Color.parseColor("#434343"), PorterDuff.Mode.SRC_ATOP);
        }
    }
}
