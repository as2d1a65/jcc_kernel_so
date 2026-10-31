package com.android.support;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
final class ConfigPreferenceNamespaces {
    static final Namespace APP_SETTINGS;
    private static final List<Namespace> CUSTOMER_CONFIGS;
    private static final List<ExcludedNamespace> NON_CUSTOMER_STATE;

    static {
        Namespace namespace = new Namespace("app_settings", "_app_settings");
        APP_SETTINGS = namespace;
        CUSTOMER_CONFIGS = Collections.unmodifiableList(Arrays.asList(new Namespace(EmulatorShortcutService.PREF_NAME, "_shortcut_config"), new Namespace("lineup_auto_apply", "_lineup_auto_apply"), new Namespace("lineup_hud_state", "_lineup_hud_state"), new Namespace("lineup_favorites", "_lineup_favorites"), new Namespace("lineup_cdn", "_lineup_cdn"), new Namespace("jcc_specified_hex", "_specified_hex"), namespace));
        NON_CUSTOMER_STATE = Collections.unmodifiableList(Arrays.asList(new ExcludedNamespace("wig_verify_prefs", "credential_and_device_identity"), new ExcludedNamespace("jcc_presence_prefs", "presence_generation"), new ExcludedNamespace("ipc_v2_config_revision", "transport_revision"), new ExcludedNamespace("injection_runtime_state", "injection_runtime"), new ExcludedNamespace("main_announcement", "announcement_seen_state"), new ExcludedNamespace(UiSurfaceControl.PREFERENCES_NAME, "server_ui_surface_policy")));
    }

    static final class Namespace {
        final String jsonKey;
        final String preferencesName;

        Namespace(String str, String str2) {
            this.preferencesName = str;
            this.jsonKey = str2;
        }
    }

    static final class ExcludedNamespace {
        final String preferencesName;
        final String reason;

        ExcludedNamespace(String str, String str2) {
            this.preferencesName = str;
            this.reason = str2;
        }
    }

    private ConfigPreferenceNamespaces() {
    }

    static List<Namespace> customerConfigs() {
        return CUSTOMER_CONFIGS;
    }

    static List<ExcludedNamespace> nonCustomerState() {
        return NON_CUSTOMER_STATE;
    }
}
