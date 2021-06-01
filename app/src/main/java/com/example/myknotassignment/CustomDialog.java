package com.example.myknotassignment;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.bumptech.glide.Glide;

public class CustomDialog {
    private static CustomDialog instance;
    View v;
    ConstraintLayout rootLayout;
    ImageView imageView;
    TextView textView;
    Button successButton;
    Context context;
    AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();

    CustomDialog(Context context, ConstraintLayout rootLayout) {
        this.rootLayout = rootLayout;
        this.context = context;
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = vi.inflate(R.layout.post_data_dialog,null);
    }

    public static synchronized CustomDialog getInstance(Context context,ConstraintLayout rootLayout) {
        if (instance == null) {
            instance = new CustomDialog(context,rootLayout);
        }
        return instance;
    }

    void builder() {
        imageView = v.findViewById(R.id.imageView);
        textView = v.findViewById(R.id.titleTextView);
        successButton = v.findViewById(R.id.successButton);
        final GestureDetector gdt = new GestureDetector(new GestureListener());
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                gdt.onTouchEvent(event);
                return true;
            }
        });
    }

    void show() {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        rootLayout.addView(v, p);
    }

    void setData(String title, String imageUrl, String successUrl) {
        textView.setText(title);
        Glide.with(context).load(Uri.parse(imageUrl)).into(imageView);
        successButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(context, Uri.parse(successUrl));
            }
        });
    }
    private static final int SWIPE_MIN_DISTANCE = 50;
    private static final int SWIPE_THRESHOLD_VELOCITY = 10;

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        void animate(int xMul, int yMul, int rotateMul){
            v.animate().translationY(yMul*(rootLayout.getY()+rootLayout.getHeight()/2.0f)).translationX(xMul*(rootLayout.getX()+rootLayout.getWidth()/2.0f)).scaleX(0).scaleY(0).rotation(rotateMul*90).alpha(0.0f).setInterpolator(interpolator).setDuration(1000).start();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((ViewGroup) v.getParent()).removeView(v);
                }
            },1000);
        }
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                Log.i("Swipe","Right->left");
                animate(-1,-1,-1);

                return false; // Right to left
            }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                Log.i("Swipe","Right<-left");
                animate(1,-1,1);
                return false; // Left to right
            }
            return false;
        }
    }
}
