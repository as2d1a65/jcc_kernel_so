package com.android.support;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Handler;
import android.os.Looper;
import com.android.support.CloudTtsClient;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/* loaded from: classes.dex */
public final class VoiceAlertPlayer implements AutoCloseable {
    private final AtomicBoolean closeRequested;
    private Session current;
    private final Listener listener;
    private final Handler mainHandler;

    public interface Callback {
        void onFailure(Failure failure);

        void onSuccess();
    }

    public enum FailureKind {
        REPLACED,
        CANCELLED,
        CLOSED,
        SETUP,
        DATA_SOURCE,
        PREPARE,
        START,
        PLAYBACK
    }

    public interface Listener {
        void onPlaybackCompleted(VoiceAlertRequest voiceAlertRequest);

        void onPlaybackFailed(VoiceAlertRequest voiceAlertRequest, PlaybackError playbackError);

        void onPlaybackStarted(VoiceAlertRequest voiceAlertRequest);
    }

    public interface PlayHandle {
        void cancel();

        boolean isDone();
    }

    public static final class Failure {
        private final FailureKind kind;
        private final String message;

        private Failure(FailureKind failureKind, String str) {
            this.kind = failureKind;
            this.message = str;
        }

        public FailureKind getKind() {
            return this.kind;
        }

        public String getMessage() {
            return this.message;
        }
    }

    public static final class PlaybackError {
        private final FailureKind kind;
        private final String message;

        private PlaybackError(Failure failure) {
            this.kind = failure.getKind();
            this.message = failure.getMessage();
        }

        public FailureKind getKind() {
            return this.kind;
        }

        public String getMessage() {
            return this.message;
        }
    }

    public VoiceAlertPlayer() {
        this(null);
    }

    public VoiceAlertPlayer(Listener listener) {
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.closeRequested = new AtomicBoolean();
        this.listener = listener;
    }

    public PlayHandle play(CloudTtsClient.AudioFile audioFile, int i, int i2, int i3, Callback callback) {
        return playInternal(null, audioFile, i, i2, i3, callback);
    }

    public PlayHandle play(VoiceAlertRequest voiceAlertRequest, CloudTtsClient.AudioFile audioFile, int i, int i2, int i3, Callback callback) {
        if (voiceAlertRequest == null) {
            throw new IllegalArgumentException("request must not be null");
        }
        return playInternal(voiceAlertRequest, audioFile, i, i2, i3, callback);
    }

    private PlayHandle playInternal(VoiceAlertRequest voiceAlertRequest, CloudTtsClient.AudioFile audioFile, int i, int i2, int i3, Callback callback) {
        if (audioFile == null) {
            throw new IllegalArgumentException("audioFile must not be null");
        }
        if (callback == null) {
            throw new IllegalArgumentException("callback must not be null");
        }
        final Session session = new Session(voiceAlertRequest, audioFile.retain(), i, i2, i3, callback);
        try {
            runOnMain(new Runnable() { // from class: com.android.support.VoiceAlertPlayer$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() throws IllegalStateException, IOException, SecurityException, IllegalArgumentException {
                    this.f$0.m1606lambda$playInternal$0$comandroidsupportVoiceAlertPlayer(session);
                }
            });
            return session;
        } catch (RuntimeException e) {
            releaseAudioPin(session);
            throw e;
        }
    }

    public void cancel() {
        runOnMain(new Runnable() { // from class: com.android.support.VoiceAlertPlayer$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() throws IllegalStateException {
                this.f$0.m1604lambda$cancel$1$comandroidsupportVoiceAlertPlayer();
            }
        });
    }

    /* renamed from: lambda$cancel$1$com-android-support-VoiceAlertPlayer, reason: not valid java name */
    /* synthetic */ void m1604lambda$cancel$1$comandroidsupportVoiceAlertPlayer() throws IllegalStateException {
        Session session = this.current;
        if (session != null) {
            finishFailure(session, failure(FailureKind.CANCELLED, "Playback was cancelled"));
        }
    }

    public static float speedForSlider(int i) {
        return CloudTtsWire.speedForSlider(i);
    }

    public static float pitchForSlider(int i) {
        return CloudTtsWire.pitchForSlider(i);
    }

    public static float volumeForSlider(int i) {
        return CloudTtsWire.volumeForSlider(i);
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        if (this.closeRequested.compareAndSet(false, true)) {
            runOnMain(new Runnable() { // from class: com.android.support.VoiceAlertPlayer$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() throws IllegalStateException {
                    this.f$0.m1605lambda$close$2$comandroidsupportVoiceAlertPlayer();
                }
            });
        }
    }

    /* renamed from: lambda$close$2$com-android-support-VoiceAlertPlayer, reason: not valid java name */
    /* synthetic */ void m1605lambda$close$2$comandroidsupportVoiceAlertPlayer() throws IllegalStateException {
        Session session = this.current;
        if (session != null) {
            finishFailure(session, failure(FailureKind.CLOSED, "Player is closed"));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: startSession, reason: merged with bridge method [inline-methods] */
    public void m1606lambda$playInternal$0$comandroidsupportVoiceAlertPlayer(final Session session) throws IllegalStateException, IOException, SecurityException, IllegalArgumentException {
        if (session.terminal.get()) {
            return;
        }
        if (session.cancelRequested) {
            finishFailure(session, failure(FailureKind.CANCELLED, "Playback was cancelled"));
            return;
        }
        if (this.closeRequested.get()) {
            finishFailure(session, failure(FailureKind.CLOSED, "Player is closed"));
            return;
        }
        Session session2 = this.current;
        this.current = session;
        if (session2 != null) {
            finishFailure(session2, failure(FailureKind.REPLACED, "Playback was replaced"));
        }
        if (session.terminal.get() || this.current != session) {
            return;
        }
        if (session.cancelRequested) {
            finishFailure(session, failure(FailureKind.CANCELLED, "Playback was cancelled"));
            return;
        }
        if (this.closeRequested.get()) {
            finishFailure(session, failure(FailureKind.CLOSED, "Player is closed"));
            return;
        }
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            session.player = mediaPlayer;
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setUsage(11).setContentType(1).build());
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { // from class: com.android.support.VoiceAlertPlayer$$ExternalSyntheticLambda2
                @Override // android.media.MediaPlayer.OnPreparedListener
                public final void onPrepared(MediaPlayer mediaPlayer2) throws IllegalStateException {
                    this.f$0.m1607lambda$startSession$3$comandroidsupportVoiceAlertPlayer(session, mediaPlayer2);
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // from class: com.android.support.VoiceAlertPlayer$$ExternalSyntheticLambda3
                @Override // android.media.MediaPlayer.OnCompletionListener
                public final void onCompletion(MediaPlayer mediaPlayer2) throws IllegalStateException {
                    this.f$0.m1608lambda$startSession$4$comandroidsupportVoiceAlertPlayer(session, mediaPlayer2);
                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() { // from class: com.android.support.VoiceAlertPlayer$$ExternalSyntheticLambda4
                @Override // android.media.MediaPlayer.OnErrorListener
                public final boolean onError(MediaPlayer mediaPlayer2, int i, int i2) {
                    return this.f$0.m1609lambda$startSession$5$comandroidsupportVoiceAlertPlayer(session, mediaPlayer2, i, i2);
                }
            });
            CloudTtsClient.AudioFile audioFile = session.audioFile;
            if (audioFile == null) {
                finishFailure(session, failure(FailureKind.DATA_SOURCE, "Audio file is missing"));
                return;
            }
            try {
                mediaPlayer.setDataSource(audioFile.getFile().getAbsolutePath());
                releaseAudioPin(session);
                try {
                    mediaPlayer.prepareAsync();
                } catch (RuntimeException unused) {
                    finishFailure(session, failure(FailureKind.PREPARE, "Audio preparation failed"));
                }
            } catch (IOException | RuntimeException unused2) {
                finishFailure(session, failure(FailureKind.DATA_SOURCE, "Audio data source failed"));
            }
        } catch (RuntimeException unused3) {
            finishFailure(session, failure(FailureKind.SETUP, "Media player setup failed"));
        }
    }

    /* renamed from: lambda$startSession$4$com-android-support-VoiceAlertPlayer, reason: not valid java name */
    /* synthetic */ void m1608lambda$startSession$4$comandroidsupportVoiceAlertPlayer(Session session, MediaPlayer mediaPlayer) throws IllegalStateException {
        onCompletion(session);
    }

    /* renamed from: lambda$startSession$5$com-android-support-VoiceAlertPlayer, reason: not valid java name */
    /* synthetic */ boolean m1609lambda$startSession$5$comandroidsupportVoiceAlertPlayer(Session session, MediaPlayer mediaPlayer, int i, int i2) {
        return onPlaybackError(session);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onPrepared, reason: merged with bridge method [inline-methods] */
    public void m1607lambda$startSession$3$comandroidsupportVoiceAlertPlayer(Session session, MediaPlayer mediaPlayer) throws IllegalStateException {
        if (session.terminal.get() || this.current != session) {
            return;
        }
        if (session.cancelRequested) {
            finishFailure(session, failure(FailureKind.CANCELLED, "Playback was cancelled"));
            return;
        }
        if (this.closeRequested.get()) {
            finishFailure(session, failure(FailureKind.CLOSED, "Player is closed"));
            return;
        }
        applyPlaybackParamsWithFallback(mediaPlayer, session.speedSlider, session.pitchSlider);
        try {
            float fVolumeForSlider = volumeForSlider(session.volumeSlider);
            mediaPlayer.setVolume(fVolumeForSlider, fVolumeForSlider);
            mediaPlayer.start();
            notifyPlaybackStarted(session);
        } catch (RuntimeException unused) {
            finishFailure(session, failure(FailureKind.START, "Audio playback could not start"));
        }
    }

    private void onCompletion(Session session) throws IllegalStateException {
        if (session.cancelRequested) {
            finishFailure(session, failure(FailureKind.CANCELLED, "Playback was cancelled"));
        } else if (this.closeRequested.get()) {
            finishFailure(session, failure(FailureKind.CLOSED, "Player is closed"));
        } else {
            finishSuccess(session);
        }
    }

    private boolean onPlaybackError(Session session) throws IllegalStateException {
        finishFailure(session, failure(FailureKind.PLAYBACK, "Audio playback failed"));
        return true;
    }

    private void applyPlaybackParamsWithFallback(MediaPlayer mediaPlayer, int i, int i2) {
        try {
            try {
                mediaPlayer.setPlaybackParams(new PlaybackParams().setSpeed(speedForSlider(i)).setPitch(pitchForSlider(i2)));
            } catch (RuntimeException unused) {
                mediaPlayer.setPlaybackParams(new PlaybackParams().setSpeed(1.0f).setPitch(1.0f));
            }
        } catch (RuntimeException unused2) {
        }
    }

    private void finishSuccess(Session session) throws IllegalStateException {
        if (session.terminal.compareAndSet(false, true)) {
            Callback callbackClearAndRelease = clearAndRelease(session, false);
            notifyPlaybackCompleted(session);
            try {
                callbackClearAndRelease.onSuccess();
            } catch (RuntimeException unused) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finishFailure(Session session, Failure failure) throws IllegalStateException {
        if (session.terminal.compareAndSet(false, true)) {
            Callback callbackClearAndRelease = clearAndRelease(session, true);
            notifyPlaybackFailed(session, failure);
            try {
                callbackClearAndRelease.onFailure(failure);
            } catch (RuntimeException unused) {
            }
        }
    }

    private Callback clearAndRelease(Session session, boolean z) throws IllegalStateException {
        if (this.current == session) {
            this.current = null;
        }
        MediaPlayer mediaPlayer = session.player;
        session.player = null;
        releaseAudioPin(session);
        Callback callback = session.callback;
        session.callback = null;
        if (mediaPlayer != null) {
            try {
                mediaPlayer.setOnPreparedListener(null);
                mediaPlayer.setOnCompletionListener(null);
                mediaPlayer.setOnErrorListener(null);
            } catch (RuntimeException unused) {
            }
            if (z) {
                try {
                    mediaPlayer.stop();
                } catch (RuntimeException unused2) {
                }
            }
            try {
                mediaPlayer.release();
            } catch (RuntimeException unused3) {
            }
        }
        return callback;
    }

    private void notifyPlaybackStarted(Session session) {
        VoiceAlertRequest voiceAlertRequest = session.request;
        if (voiceAlertRequest == null || this.listener == null || session.started || session.terminal.get() || this.current != session) {
            return;
        }
        session.started = true;
        try {
            this.listener.onPlaybackStarted(voiceAlertRequest);
        } catch (RuntimeException unused) {
        }
    }

    private void notifyPlaybackCompleted(Session session) {
        Listener listener;
        VoiceAlertRequest voiceAlertRequest = session.request;
        if (voiceAlertRequest == null || (listener = this.listener) == null) {
            return;
        }
        try {
            listener.onPlaybackCompleted(voiceAlertRequest);
        } catch (RuntimeException unused) {
        }
    }

    private void notifyPlaybackFailed(Session session, Failure failure) {
        Listener listener;
        VoiceAlertRequest voiceAlertRequest = session.request;
        if (voiceAlertRequest == null || (listener = this.listener) == null) {
            return;
        }
        try {
            listener.onPlaybackFailed(voiceAlertRequest, new PlaybackError(failure));
        } catch (RuntimeException unused) {
        }
    }

    private void releaseAudioPin(Session session) {
        CloudTtsClient.AudioFile audioFile = session.audioFile;
        session.audioFile = null;
        if (audioFile != null) {
            audioFile.close();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void runOnMain(Runnable runnable) {
        if (Looper.myLooper() == this.mainHandler.getLooper()) {
            runnable.run();
        } else {
            this.mainHandler.post(runnable);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Failure failure(FailureKind failureKind, String str) {
        return new Failure(failureKind, str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    final class Session implements PlayHandle {
        private CloudTtsClient.AudioFile audioFile;
        private Callback callback;
        private volatile boolean cancelRequested;
        private final int pitchSlider;
        private MediaPlayer player;
        private final VoiceAlertRequest request;
        private final int speedSlider;
        private boolean started;
        private final AtomicBoolean terminal = new AtomicBoolean();
        private final int volumeSlider;

        Session(VoiceAlertRequest voiceAlertRequest, CloudTtsClient.AudioFile audioFile, int i, int i2, int i3, Callback callback) {
            this.request = voiceAlertRequest;
            this.audioFile = audioFile;
            this.speedSlider = i;
            this.pitchSlider = i2;
            this.volumeSlider = i3;
            this.callback = callback;
        }

        @Override // com.android.support.VoiceAlertPlayer.PlayHandle
        public void cancel() {
            if (this.terminal.get()) {
                return;
            }
            this.cancelRequested = true;
            VoiceAlertPlayer.this.runOnMain(new Runnable() { // from class: com.android.support.VoiceAlertPlayer$Session$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() throws IllegalStateException {
                    this.f$0.m1624lambda$cancel$0$comandroidsupportVoiceAlertPlayer$Session();
                }
            });
        }

        /* renamed from: lambda$cancel$0$com-android-support-VoiceAlertPlayer$Session, reason: not valid java name */
        /* synthetic */ void m1624lambda$cancel$0$comandroidsupportVoiceAlertPlayer$Session() throws IllegalStateException {
            VoiceAlertPlayer.this.finishFailure(this, VoiceAlertPlayer.failure(FailureKind.CANCELLED, "Playback was cancelled"));
        }

        @Override // com.android.support.VoiceAlertPlayer.PlayHandle
        public boolean isDone() {
            return this.terminal.get();
        }
    }
}
