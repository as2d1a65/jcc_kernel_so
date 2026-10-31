package com.android.support;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.android.support.GameRegionPolicy;
import java.util.function.BooleanSupplier;

/* loaded from: classes.dex */
final class GameRegionSelector implements Runnable {
    static final String[] LABELS = {"功能", "牌库与商店", "指定", "玩家数据", "语音", "快捷键", "设置", "关于", "自动拿牌", "记忆模式", "玩家详情", "对手预测", "三星预警", "调试悬浮窗", "功能介绍", "卖牌与换位", "一键梭哈", "指定海克斯", "指定商店", "指定重铸"};
    private final Activity activity;
    private final BooleanSupplier canSwitch;
    private final Runnable changed;

    GameRegionSelector(Activity activity, BooleanSupplier booleanSupplier, Runnable runnable) {
        this.activity = activity;
        this.canSwitch = booleanSupplier;
        this.changed = runnable;
        activity.findViewById(R.id.btnRegionCn).setOnClickListener(new View.OnClickListener() { // from class: com.android.support.GameRegionSelector$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m1200lambda$new$0$comandroidsupportGameRegionSelector(view);
            }
        });
        activity.findViewById(R.id.btnRegionSea).setOnClickListener(new View.OnClickListener() { // from class: com.android.support.GameRegionSelector$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m1201lambda$new$1$comandroidsupportGameRegionSelector(view);
            }
        });
        activity.findViewById(R.id.btnRegionFeatures).setOnClickListener(new View.OnClickListener() { // from class: com.android.support.GameRegionSelector$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m1202lambda$new$2$comandroidsupportGameRegionSelector(view);
            }
        });
        run();
    }

    /* renamed from: lambda$new$0$com-android-support-GameRegionSelector, reason: not valid java name */
    /* synthetic */ void m1200lambda$new$0$comandroidsupportGameRegionSelector(View view) {
        select(GameRegion.CN);
    }

    /* renamed from: lambda$new$1$com-android-support-GameRegionSelector, reason: not valid java name */
    /* synthetic */ void m1201lambda$new$1$comandroidsupportGameRegionSelector(View view) {
        select(GameRegion.SEA);
    }

    /* renamed from: lambda$new$2$com-android-support-GameRegionSelector, reason: not valid java name */
    /* synthetic */ void m1202lambda$new$2$comandroidsupportGameRegionSelector(View view) {
        showFeatures();
    }

    private void select(GameRegion gameRegion) {
        if (gameRegion != GameRegionState.current() && GameRegionControl.visible(gameRegion)) {
            boolean asBoolean = this.canSwitch.getAsBoolean();
            Activity activity = this.activity;
            if (!asBoolean) {
                Toast.makeText(activity, "正在连接游戏，请等本次连接结束后再切换游戏服", 1).show();
            } else if (!GameRegionState.select(activity, gameRegion)) {
                Toast.makeText(this.activity, "游戏已连接，请先在悬浮面板里断开与游戏的连接，再切换游戏服", 1).show();
            } else {
                this.changed.run();
                run();
            }
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        if (this.activity.isFinishing() || this.activity.isDestroyed()) {
            return;
        }
        GameRegion gameRegionCurrent = GameRegionState.current();
        GameRegion[] gameRegionArrValues = GameRegion.values();
        int length = gameRegionArrValues.length;
        for (int i = 0; i < length; i++) {
            GameRegion gameRegion = gameRegionArrValues[i];
            Button button = (Button) this.activity.findViewById(gameRegion == GameRegion.CN ? R.id.btnRegionCn : R.id.btnRegionSea);
            button.setVisibility(GameRegionControl.visible(gameRegion) ? 0 : 8);
            button.setText(gameRegion.label);
            button.setSelected(gameRegion == gameRegionCurrent);
            button.setAlpha(1.0f);
            button.setTextColor(this.activity.getColor(gameRegion == gameRegionCurrent ? R.color.ds_brand : R.color.ds_text_2));
            button.setContentDescription(gameRegion.label + (gameRegion == gameRegionCurrent ? "，当前选择" : "，点击切换"));
        }
        TextView textView = (TextView) this.activity.findViewById(R.id.tvGameBrand);
        if (textView != null) {
            textView.setText(gameRegionCurrent == GameRegion.CN ? "金铲铲助手" : "金铲铲国际服助手");
        }
        String strUnavailableReason = GameRegionControl.unavailableReason(gameRegionCurrent);
        TextView textView2 = (TextView) this.activity.findViewById(R.id.tvGameRegionStatus);
        GameRegionPolicy.Entry entry = GameRegionControl.entry(gameRegionCurrent);
        String str = "";
        String str2 = entry == null ? "" : entry.message;
        StringBuilder sbAppend = new StringBuilder().append(gameRegionCurrent.gameName).append(" · ").append(strUnavailableReason.isEmpty() ? "已选择" : strUnavailableReason);
        if (strUnavailableReason.isEmpty() && !str2.isEmpty()) {
            str = "\n" + str2;
        }
        textView2.setText(sbAppend.append(str).toString());
        ((TextView) this.activity.findViewById(R.id.tvGameRegionPackage)).setText(gameRegionCurrent.packageName);
    }

    private void showFeatures() {
        GameRegion gameRegionCurrent = GameRegionState.current();
        UiSurfacePolicy uiSurfacePolicySurfaces = GameRegionControl.surfaces(gameRegionCurrent);
        StringBuilder sbAppend = new StringBuilder(gameRegionCurrent.gameName).append("\n\n");
        for (int i = 0; i < UiSurfaceRegistry.size(); i++) {
            String strIdAt = UiSurfaceRegistry.idAt(i);
            String str = !gameRegionCurrent.supportsSurface(strIdAt) ? "适配准备中" : "已接入";
            if (!gameRegionCurrent.compiledSurface(strIdAt)) {
                str = "已禁用";
            }
            if (!GameRegionControl.visible(gameRegionCurrent) || !uiSurfacePolicySurfaces.visible(strIdAt, true)) {
                str = "已隐藏";
            } else if (!GameRegionControl.enabled(gameRegionCurrent) || !uiSurfacePolicySurfaces.enabled(strIdAt, true)) {
                str = "维护中";
            }
            String[] strArr = LABELS;
            if (i < strArr.length) {
                strIdAt = strArr[i];
            }
            sbAppend.append(strIdAt).append("：").append(str).append('\n');
        }
        new AlertDialog.Builder(this.activity).setTitle(gameRegionCurrent.label + "功能状态").setMessage(sbAppend.toString()).setPositiveButton("知道了", (DialogInterface.OnClickListener) null).show();
    }
}
