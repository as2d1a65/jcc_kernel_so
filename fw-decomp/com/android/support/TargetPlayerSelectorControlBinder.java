package com.android.support;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.LinkedHashSet;
import java.util.Set;

/* loaded from: classes.dex */
final class TargetPlayerSelectorControlBinder {

    interface Host {
        void refreshTargetPlayerList();

        void restoreSelection(Set<Integer> set);

        String selectionSummary();

        void setTargetDropdownExpanded(boolean z);

        boolean targetDropdownExpanded();
    }

    private TargetPlayerSelectorControlBinder() {
    }

    static Set<Integer> parsePersistedIds(String str) {
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        if (str != null) {
            for (String str2 : str.split(",")) {
                String strTrim = str2.trim();
                if (!strTrim.isEmpty()) {
                    try {
                        linkedHashSet.add(Integer.valueOf(Integer.parseInt(strTrim)));
                    } catch (NumberFormatException unused) {
                    }
                }
            }
        }
        if (linkedHashSet.isEmpty()) {
            linkedHashSet.add(-1);
        }
        return linkedHashSet;
    }

    static String encodeIds(Set<Integer> set) {
        if (set == null || set.isEmpty()) {
            return "-1";
        }
        StringBuilder sb = new StringBuilder();
        for (Integer num : set) {
            if (num != null) {
                if (sb.length() > 0) {
                    sb.append(',');
                }
                sb.append(num.intValue());
            }
        }
        return sb.length() == 0 ? "-1" : sb.toString();
    }

    static void bind(View view, SharedPreferences sharedPreferences, final Host host) {
        TextView textView = (TextView) view.findViewById(R.id.tvSelectedTargets);
        final LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.llTargetPlayerList);
        if (textView == null || linearLayout == null) {
            return;
        }
        host.restoreSelection(parsePersistedIds(sharedPreferences.getString(FloatingWindowPrefsKeys.KEY_TARGET_PLAYER_IDS, "-1")));
        textView.setText(host.selectionSummary());
        textView.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.TargetPlayerSelectorControlBinder$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                TargetPlayerSelectorControlBinder.lambda$bind$0(host, linearLayout, view2);
            }
        });
    }

    static /* synthetic */ void lambda$bind$0(Host host, LinearLayout linearLayout, View view) {
        boolean zTargetDropdownExpanded = host.targetDropdownExpanded();
        host.setTargetDropdownExpanded(!zTargetDropdownExpanded);
        linearLayout.setVisibility(!zTargetDropdownExpanded ? 0 : 8);
        if (zTargetDropdownExpanded) {
            return;
        }
        host.refreshTargetPlayerList();
    }
}
