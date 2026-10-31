package com.android.support;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import com.android.support.PeerSync;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

/* loaded from: classes.dex */
final class PeerSyncControlBinder {
    private PeerSyncControlBinder() {
    }

    static void bind(View view, final Context context, final SharedPreferences sharedPreferences, final Supplier<SharedMemManager> supplier, final Consumer<Boolean> consumer, final Supplier<RankWarningView> supplier2) {
        Switch r8 = (Switch) view.findViewById(R.id.switchPeerSync);
        if (r8 == null) {
            return;
        }
        boolean z = sharedPreferences.getBoolean(FloatingWindowPrefsKeys.KEY_PEER_SYNC_ENABLED, false);
        r8.setChecked(z);
        consumer.accept(Boolean.valueOf(z));
        SharedMemManager sharedMemManager = supplier.get();
        if (sharedMemManager != null) {
            sharedMemManager.setPeerSyncEnabled(z);
        }
        PeerSync.getInstance(context).setListener(new PeerSync.PeerUpdateListener() { // from class: com.android.support.PeerSyncControlBinder$$ExternalSyntheticLambda0
            @Override // com.android.support.PeerSync.PeerUpdateListener
            public final void onPeerSeatsUpdated(Set set) {
                PeerSyncControlBinder.lambda$bind$0(supplier2, set);
            }
        });
        r8.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.support.PeerSyncControlBinder$$ExternalSyntheticLambda1
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z2) {
                PeerSyncControlBinder.lambda$bind$1(consumer, sharedPreferences, supplier, context, supplier2, compoundButton, z2);
            }
        });
    }

    static /* synthetic */ void lambda$bind$0(Supplier supplier, Set set) {
        RankWarningView rankWarningView = (RankWarningView) supplier.get();
        if (rankWarningView != null) {
            rankWarningView.setPeerSeats(set);
        }
    }

    static /* synthetic */ void lambda$bind$1(Consumer consumer, SharedPreferences sharedPreferences, Supplier supplier, Context context, Supplier supplier2, CompoundButton compoundButton, boolean z) {
        consumer.accept(Boolean.valueOf(z));
        sharedPreferences.edit().putBoolean(FloatingWindowPrefsKeys.KEY_PEER_SYNC_ENABLED, z).apply();
        SharedMemManager sharedMemManager = (SharedMemManager) supplier.get();
        if (sharedMemManager != null) {
            sharedMemManager.setPeerSyncEnabled(z);
        }
        if (!z) {
            PeerSync.getInstance(context).stop();
            RankWarningView rankWarningView = (RankWarningView) supplier2.get();
            if (rankWarningView != null) {
                rankWarningView.setPeerSeats(new HashSet());
            }
        }
        FloatingWindowService.safeToast(context, z ? "同行识别已开启" : "同行识别已关闭", 0);
    }
}
