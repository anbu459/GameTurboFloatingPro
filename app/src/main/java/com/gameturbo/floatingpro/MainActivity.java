package com.gameturbo.floatingpro;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;

public class MainActivity extends Activity {
    private int cyan = Color.rgb(0, 220, 210);
    private int bg = Color.rgb(10, 13, 19);

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(bg);
        getWindow().setNavigationBarColor(bg);
        buildScreen();
    }

    private TextView text(String s, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(s); t.setTextSize(size); t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }

    private GradientDrawable shape(int color, int stroke, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color); d.setCornerRadius(radius);
        if (stroke != 0) d.setStroke(2, stroke);
        return d;
    }

    private void buildScreen() {
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(bg);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(22, 22, 22, 28);
        scroll.addView(root);

        TextView brand = text("◉  GAME TURBO", 13, cyan, true);
        root.addView(brand);
        TextView title = text("Gaming space", 29, Color.WHITE, true);
        LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(-1, -2);
        tp.topMargin = 8; root.addView(title, tp);
        TextView sub = text("V5  •  Floating gaming tools", 13, 0xff8d98a8, false);
        root.addView(sub);

        LinearLayout hero = new LinearLayout(this);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setPadding(18, 18, 18, 18);
        hero.setBackground(shape(0xff151d27, cyan, 24));
        LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(-1, -2);
        hp.topMargin = 24; root.addView(hero, hp);
        hero.addView(text("FREE FIRE", 12, cyan, true));
        TextView heroTitle = text("Ready for battle?", 23, Color.WHITE, true);
        LinearLayout.LayoutParams htp = new LinearLayout.LayoutParams(-1, -2);
        htp.topMargin = 8; hero.addView(heroTitle, htp);
        TextView hint = text("Open your game, then use the floating side panel.", 13, 0xffaab5c4, false);
        LinearLayout.LayoutParams hnp = new LinearLayout.LayoutParams(-1, -2);
        hnp.topMargin = 7; hero.addView(hint, hnp);

        Button overlay = new Button(this);
        overlay.setText("ENABLE FLOATING SIDEBAR");
        overlay.setTextColor(Color.BLACK);
        overlay.setAllCaps(false);
        overlay.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        overlay.setBackground(shape(cyan, 0, 16));
        LinearLayout.LayoutParams op = new LinearLayout.LayoutParams(-1, 54);
        op.topMargin = 18; hero.addView(overlay, op);
        overlay.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
                Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
                startActivity(i);
            } else {
                startService(new Intent(this, OverlayService.class));
                Toast.makeText(this, "Floating sidebar enabled", Toast.LENGTH_SHORT).show();
            }
        });

        Button launch = new Button(this);
        launch.setText("OPEN FREE FIRE");
        launch.setAllCaps(false);
        launch.setTextColor(Color.WHITE);
        launch.setBackground(shape(0xff242d39, 0xff3c4b5c, 16));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, 52);
        lp.topMargin = 10; hero.addView(launch, lp);
        launch.setOnClickListener(v -> {
            Intent i = getPackageManager().getLaunchIntentForPackage("com.dts.freefireth");
            if (i == null) i = getPackageManager().getLaunchIntentForPackage("com.dts.freefiremax");
            if (i != null) startActivity(i);
            else Toast.makeText(this, "Free Fire not found. Open it manually.", Toast.LENGTH_LONG).show();
        });

        TextView tools = text("QUICK TOOLS", 12, 0xff8794a5, true);
        LinearLayout.LayoutParams toolp = new LinearLayout.LayoutParams(-1, -2);
        toolp.topMargin = 25; root.addView(tools, toolp);
        root.addView(tile("◉   Dual-edge sidebar", "Swipe inward from the left or right screen edge to open the panel"));
        root.addView(tile("⌁   Game network monitor", "Reads Free Fire UID traffic when Android exposes it"));
        root.addView(tile("▣   Battery status", "Battery percentage and temperature"));
        root.addView(tile("✦   Performance checklist", "Enable Do Not Disturb, disable Battery Saver while gaming, and avoid charging if the phone gets hot."));
        root.addView(tile("🎯   60 FPS TARGET", "Use Smooth graphics, close background apps, keep the phone cool. This is a target, not a live FPS reading."));

        TextView foot = text("Note: This is an independent app, not Xiaomi's system Game Turbo. Some system-only features cannot be duplicated by a normal APK.", 12, 0xff7e8a99, false);
        LinearLayout.LayoutParams fp = new LinearLayout.LayoutParams(-1, -2);
        fp.topMargin = 18; root.addView(foot, fp);
        setContentView(scroll);
    }

    private View tile(String title, String desc) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(16, 14, 16, 14);
        box.setBackground(shape(0xff141a23, 0xff283340, 18));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.topMargin = 10;
        box.setLayoutParams(p);
        box.addView(text(title, 15, Color.WHITE, true));
        TextView d = text(desc, 12, 0xff8f9bab, false);
        LinearLayout.LayoutParams dp = new LinearLayout.LayoutParams(-1, -2);
        dp.topMargin = 5; box.addView(d, dp);
        return box;
    }
}
