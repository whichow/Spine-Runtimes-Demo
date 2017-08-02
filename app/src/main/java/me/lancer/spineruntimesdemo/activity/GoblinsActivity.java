package me.lancer.spineruntimesdemo.activity;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AppActivity;

import me.lancer.spineruntimesdemo.R;
import me.lancer.spineruntimesdemo.model.Dragon;
import me.lancer.spineruntimesdemo.model.Goblins;

public class GoblinsActivity extends AppActivity {

    Goblins dragon;
    View dragonView;

    long startTime;
    int tag = 0;
    int oldOffsetX;
    int oldOffsetY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank);

        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.r = cfg.g = cfg.b = cfg.a = 8;
        dragon = new Goblins();
        dragonView = initializeForView(dragon, cfg);
        if (dragonView instanceof SurfaceView) {
            SurfaceView glView = (SurfaceView) dragonView;
            glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
            glView.setZOrderOnTop(true);
        }
        addDragon();
    }

    public void addDragon() {
        final WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        final WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        dragonView.setOnTouchListener(new View.OnTouchListener() {
            float lastX, lastY;

            public boolean onTouch(View v, MotionEvent event) {
                final int action = event.getAction();
                float x = event.getX();
                float y = event.getY();
                if (tag == 0) {
                    oldOffsetX = layoutParams.x;
                    oldOffsetY = layoutParams.y;
                }
                if (action == MotionEvent.ACTION_DOWN) {
                    lastX = x;
                    lastY = y;
                    startTime = System.currentTimeMillis();
                } else if (action == MotionEvent.ACTION_MOVE) {
                    layoutParams.x += (int) (x - lastX);
                    layoutParams.y += (int) (y - lastY);
                    tag = 1;
                    windowManager.updateViewLayout(dragonView, layoutParams);
                } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                    int newOffsetX = layoutParams.x;
                    int newOffsetY = layoutParams.y;
                    long endTime = System.currentTimeMillis();
                    if ((Math.abs(oldOffsetX - newOffsetX) + Math.abs(oldOffsetY - newOffsetY)) < 10) {
                        if (endTime - startTime > 500) {
                            if (dragonView.getTag() == null) {
                                dragon.zoomBig();
                                dragonView.setTag("");
                                layoutParams.width = dp2Px(160);
                                layoutParams.height = dp2Px(280);
                                windowManager.updateViewLayout(dragonView, layoutParams);
                            } else {
                                dragon.zoomSmall();
                                dragonView.setTag(null);
                                layoutParams.width = dp2Px(80);
                                layoutParams.height = dp2Px(140);
                                windowManager.updateViewLayout(dragonView, layoutParams);
                            }

                        } else {
                            dragon.animate();
                        }
                    } else {
                        tag = 0;
                        dragon.animate();
                    }
                }
                return true;
            }
        });
        int type = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            type = WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParams.type = type;
        layoutParams.flags = 40;
        layoutParams.width = dp2Px(144);
        layoutParams.height = dp2Px(144);
        layoutParams.format = -3;
        windowManager.addView(dragonView, layoutParams);
    }

    public int dp2Px(float value) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (value * scale + 0.5f);
    }

    @Override
    protected void onDestroy() {
        getWindowManager().removeView(dragonView);
        super.onDestroy();
    }
}