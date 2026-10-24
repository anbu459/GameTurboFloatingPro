package com.gameturbo.floatingpro;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.net.TrafficStats;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class OverlayService extends Service {
    private WindowManager wm;
    private TextView leftHandle, rightHandle;
    private View panel;
    private WindowManager.LayoutParams leftParams, rightParams, panelParams;
    private boolean panelOpen = false;
    private boolean panelOnLeft = false;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private long lastRx, lastTx, lastGameRx = -1, lastGameTx = -1;
    private int gameUid = -1;
    private TextView netLabel, batteryLabel;
    private Runnable updater;

    private GradientDrawable shape(int color, int stroke, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(radius);
        if (stroke != 0) d.setStroke(2, stroke);
        return d;
    }

    private TextView label(String s, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }

    @Override public void onCreate() {
        super.onCreate();
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        int type = Build.VERSION.SDK_INT >= 26
                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                : WindowManager.LayoutParams.TYPE_PHONE;

        leftParams = new WindowManager.LayoutParams(
                dp(22), dp(76), type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);
        leftParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        leftParams.x = 0;
        leftParams.y = 0;

        rightParams = new WindowManager.LayoutParams(
                dp(22), dp(76), type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);
        rightParams.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
        rightParams.x = 0;
        rightParams.y = 0;

        leftHandle = makeHandle("›", true);
        rightHandle = makeHandle("‹", false);
        try {
            wm.addView(leftHandle, leftParams);
            wm.addView(rightHandle, rightParams);
        } catch (Exception e) {
            Toast.makeText(this, "Allow Display over other apps, then enable again", Toast.LENGTH_LONG).show();
            stopSelf();
            return;
        }

        lastRx = TrafficStats.getTotalRxBytes();
        lastTx = TrafficStats.getTotalTxBytes();
        gameUid = findFreeFireUid();
        if (gameUid >= 0) {
            lastGameRx = TrafficStats.getUidRxBytes(gameUid);
            lastGameTx = TrafficStats.getUidTxBytes(gameUid);
        }
        updater = new Runnable() {
            @Override public void run() {
                updateStats();
                handler.postDelayed(this, 2000);
            }
        };
        handler.post(updater);
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private TextView makeHandle(String arrow, boolean isLeft) {
        TextView h = label(arrow, 24, Color.WHITE, true);
        h.setBackground(shape(0xee101820, 0xff00ded2, 16));
        h.setOnTouchListener(new View.OnTouchListener() {
            float downX, downY;
            @Override public boolean onTouch(View v, MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    downX = e.getRawX();
                    downY = e.getRawY();
                    return true;
                }
                if (e.getAction() == MotionEvent.ACTION_UP) {
                    float dx = e.getRawX() - downX;
                    float dy = e.getRawY() - downY;
                    boolean inwardSwipe = isLeft ? dx > dp(28) : dx < -dp(28);
                    if (inwardSwipe || Math.abs(dx) < dp(12) && Math.abs(dy) < dp(18)) {
                        openPanel(isLeft);
                    }
                    return true;
                }
                return true;
            }
        });
        return h;
    }

    private void openPanel(boolean fromLeft) {
        panelOnLeft = fromLeft;
        if (panelOpen) {
            closePanel();
        }
        panel = createPanel();
        int type = Build.VERSION.SDK_INT >= 26
                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                : WindowManager.LayoutParams.TYPE_PHONE;
        panelParams = new WindowManager.LayoutParams(
                dp(184), WindowManager.LayoutParams.WRAP_CONTENT, type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);
        panelParams.gravity = (fromLeft ? Gravity.LEFT : Gravity.RIGHT) | Gravity.CENTER_VERTICAL;
        panelParams.x = dp(24);
        panelParams.y = 0;
        try {
            wm.addView(panel, panelParams);
            panelOpen = true;
            if (leftHandle != null) leftHandle.setText(fromLeft ? "‹" : "›");
            if (rightHandle != null) rightHandle.setText(fromLeft ? "›" : "‹");
        } catch (Exception e) {
            Toast.makeText(this, "Could not open floating panel", Toast.LENGTH_SHORT).show();
        }
    }

    private View createPanel() {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(12), dp(12), dp(12), dp(12));
        box.setBackground(shape(0xf210151d, 0xff00ded2, dp(22)));
        box.setElevation(dp(8));

        TextView title = label("✦  GAME TURBO", 13, 0xff00ded2, true);
        box.addView(title, new LinearLayout.LayoutParams(-1, dp(34)));

        netLabel = label("GAME NET\nChecking…", 12, Color.WHITE, true);
        netLabel.setBackground(shape(0xff202a35, 0, dp(12)));
        LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(-1, dp(58));
        p1.topMargin = dp(6);
        box.addView(netLabel, p1);

        batteryLabel = label("BATTERY\nChecking…", 12, Color.WHITE, true);
        batteryLabel.setBackground(shape(0xff202a35, 0, dp(12)));
        LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(-1, dp(58));
        p2.topMargin = dp(7);
        box.addView(batteryLabel, p2);

        TextView fps = label("FPS  •  N/A", 12, Color.WHITE, true);
        fps.setBackground(shape(0xff202a35, 0, dp(12)));
        LinearLayout.LayoutParams p3 = new LinearLayout.LayoutParams(-1, dp(44));
        p3.topMargin = dp(7);
        box.addView(fps, p3);

        TextView close = label("CLOSE  ×", 12, 0xffff8090, true);
        close.setBackground(shape(0xff2a1b25, 0, dp(12)));
        LinearLayout.LayoutParams p4 = new LinearLayout.LayoutParams(-1, dp(40));
        p4.topMargin = dp(7);
        box.addView(close, p4);
        close.setOnClickListener(v -> closePanel());

        TextView note = label("Independent overlay • not Xiaomi system Game Turbo", 9, 0xff9aa7b5, false);
        note.setPadding(dp(2), dp(8), dp(2), dp(2));
        box.addView(note, new LinearLayout.LayoutParams(-1, -2));
        return box;
    }

    private void closePanel() {
        if (panel != null && wm != null) {
            try { wm.removeView(panel); } catch (Exception ignored) {}
        }
        panel = null;
        panelOpen = false;
    }

    private void updateStats() {
        if (wm == null) return;
        long rx = TrafficStats.getTotalRxBytes();
        long tx = TrafficStats.getTotalTxBytes();
        long deviceBytes = Math.max(0, rx - lastRx) + Math.max(0, tx - lastTx);
        lastRx = rx;
        lastTx = tx;

        if (netLabel != null) {
            if (gameUid >= 0) {
                long gameRx = TrafficStats.getUidRxBytes(gameUid);
                long gameTx = TrafficStats.getUidTxBytes(gameUid);
                if (gameRx >= 0 && gameTx >= 0 && lastGameRx >= 0 && lastGameTx >= 0) {
                    long bytes = Math.max(0, gameRx - lastGameRx) + Math.max(0, gameTx - lastGameTx);
                    long kbps = bytes * 8L / 2000L;
                    netLabel.setText("FREE FIRE NET\n" + kbps + " kbit/s");
                } else {
                    netLabel.setText("FREE FIRE NET\nN/A");
                }
                lastGameRx = gameRx;
                lastGameTx = gameTx;
            } else {
                netLabel.setText("DEVICE NET\n" + (deviceBytes / 2L) + " B/s");
            }
        }

        Intent status = registerReceiver(null, new android.content.IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (status != null && batteryLabel != null) {
            int level = status.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = status.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int pct = (level >= 0 && scale > 0) ? level * 100 / scale : -1;
            batteryLabel.setText("BATTERY\n" + (pct >= 0 ? pct + "%" : "N/A"));
        }
    }

    private int findFreeFireUid() {
        String[] packages = { "com.dts.freefireth", "com.dts.freefiremax" };
        for (String packageName : packages) {
            try {
                android.content.pm.ApplicationInfo info = getPackageManager().getApplicationInfo(packageName, 0);
                return info.uid;
            } catch (Exception ignored) {}
        }
        return -1;
    }

    @Override public void onDestroy() {
        if (updater != null) handler.removeCallbacks(updater);
        closePanel();
        if (wm != null && leftHandle != null) try { wm.removeView(leftHandle); } catch (Exception ignored) {}
        if (wm != null && rightHandle != null) try { wm.removeView(rightHandle); } catch (Exception ignored) {}
        super.onDestroy();
    }

    @Override public IBinder onBind(Intent intent) { return null; }
}
