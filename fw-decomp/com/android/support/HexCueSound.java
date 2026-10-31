package com.android.support;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.Log;

/* loaded from: classes.dex */
final class HexCueSound implements AutoCloseable {
    private static final String TAG = "HexPredict";
    private final String label;
    private volatile boolean loaded;
    private SoundPool pool;
    private int soundId;
    private volatile boolean unusable;

    static float volumeForSlider(int i) {
        if (i < 0) {
            i = 0;
        } else if (i > 100) {
            i = 100;
        }
        return i / 100.0f;
    }

    HexCueSound(Context context) {
        this(context, R.raw.hex_hit_ding, "指定海克斯");
    }

    HexCueSound(Context context, int i, String str) {
        str = str == null ? "提示音" : str;
        this.label = str;
        if (context == null) {
            this.unusable = true;
            return;
        }
        try {
            SoundPool soundPoolBuild = new SoundPool.Builder().setMaxStreams(2).setAudioAttributes(new AudioAttributes.Builder().setUsage(1).setContentType(4).build()).build();
            this.pool = soundPoolBuild;
            soundPoolBuild.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() { // from class: com.android.support.HexCueSound$$ExternalSyntheticLambda0
                @Override // android.media.SoundPool.OnLoadCompleteListener
                public final void onLoadComplete(SoundPool soundPool, int i2, int i3) {
                    this.f$0.m1203lambda$new$0$comandroidsupportHexCueSound(soundPool, i2, i3);
                }
            });
            int iLoad = this.pool.load(context, i, 1);
            this.soundId = iLoad;
            if (iLoad == 0) {
                this.unusable = true;
                Log.w(TAG, "[" + str + "] 提示音资源加载返回 0");
            }
        } catch (Throwable th) {
            this.unusable = true;
            Log.w(TAG, "[" + this.label + "] 提示音初始化失败", th);
        }
    }

    /* renamed from: lambda$new$0$com-android-support-HexCueSound, reason: not valid java name */
    /* synthetic */ void m1203lambda$new$0$comandroidsupportHexCueSound(SoundPool soundPool, int i, int i2) {
        if (i2 == 0) {
            this.loaded = true;
        } else {
            this.unusable = true;
            Log.w(TAG, "[" + this.label + "] 提示音加载失败：状态=" + i2);
        }
    }

    boolean play(float f) {
        SoundPool soundPool;
        if (!this.unusable && this.loaded && (soundPool = this.pool) != null && f > 0.0f) {
            float f2 = f > 1.0f ? 1.0f : f;
            try {
                if (soundPool.play(this.soundId, f2, f2, 1, 0, 1.0f) == 0) {
                    Log.w(TAG, "[" + this.label + "] 播放未获得音轨");
                    return false;
                }
                Dbg.i(TAG, "[" + this.label + "] 已播放：音量=" + f2);
                return true;
            } catch (Throwable th) {
                Log.w(TAG, "[" + this.label + "] 播放失败", th);
            }
        }
        return false;
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        SoundPool soundPool = this.pool;
        this.pool = null;
        this.loaded = false;
        this.unusable = true;
        if (soundPool == null) {
            return;
        }
        try {
            soundPool.release();
        } catch (Throwable unused) {
        }
    }
}
