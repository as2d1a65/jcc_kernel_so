package com.android.support;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;

/* loaded from: classes.dex */
public final class GameRegionContext extends ContextWrapper {
    public final GameRegion region;

    @Override // android.content.ContextWrapper, android.content.Context
    public Context getApplicationContext() {
        return this;
    }

    private GameRegionContext(Context context, GameRegion gameRegion) {
        super(context);
        this.region = gameRegion;
    }

    public static Context pin(Context context, GameRegion gameRegion) {
        if (context == null) {
            return null;
        }
        if (context instanceof GameRegionContext) {
            GameRegionContext gameRegionContext = (GameRegionContext) context;
            if (gameRegionContext.region == gameRegion) {
                return gameRegionContext;
            }
            context = gameRegionContext.getBaseContext();
        }
        Context applicationContext = context.getApplicationContext();
        if (applicationContext != null) {
            context = applicationContext;
        }
        return new GameRegionContext(context, gameRegion);
    }

    public static GameRegion regionOf(Context context) {
        return context instanceof GameRegionContext ? ((GameRegionContext) context).region : GameRegionState.current();
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public SharedPreferences getSharedPreferences(String str, int i) {
        String strPreferenceName = GameRegionState.preferenceName(str, this.region);
        Context baseContext = getBaseContext();
        if (baseContext instanceof JccApplication) {
            return ((JccApplication) baseContext).unscopedPreferences(strPreferenceName, i);
        }
        return baseContext.getSharedPreferences(strPreferenceName, i);
    }
}
