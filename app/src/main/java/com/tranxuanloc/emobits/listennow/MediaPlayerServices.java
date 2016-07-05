package com.tranxuanloc.emobits.listennow;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tranxuanloc.emobits.R;
import com.tranxuanloc.emobits.main.MainActivity;

import java.io.IOException;

public class MediaPlayerServices extends Service implements MediaPlayer.OnErrorListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener, MusicFocusable {
    public static final String ACTION_PLAY = "com.tranxuanloc.emobits.action.PLAY";
    public static final String ACTION_TOGGLE_PLAYBACK = "com.tranxuanloc.emobits.action.ACTION_TOGGLE_PLAYBACK";
    public static final String ACTION_PAUSE = "com.tranxuanloc.emobits.action.PAUSE";
    public static final String ACTION_STOP = "com.tranxuanloc.emobits.action.STOP";
    public static final String ACTION_RESUME = "com.tranxuanloc.emobits.action.RESUME";
    public static final String ACTION_NEXT = "com.tranxuanloc.emobits.action.NEXT";
    public static final String ACTION_PREVIOUS = "com.tranxuanloc.emobits.action.PREVIOUS";

    public static final String START_TRACKING = "com.tranxuanloc.emobits.action.START_TRACKING";
    public static final String STOP_TRACKING = "com.tranxuanloc.emobits.action.STOP_TRACKING";
    public static final String BROADCAST_UPDATE = "com.tranxuanloc.emobits.action.BROADCAST_UPDATE";

    public static final String CURRENT_POSITION = "com.tranxuanloc.emobits.CURRENT_POSITION";
    public static final String DURATION = "com.tranxuanloc.emobits.DURATION";
    public static final String STATUS = "com.tranxuanloc.emobits.STATUS";
    public static final String IS_PLAYING = "com.tranxuanloc.emobits.IS_PLAYING";
    public static final String SECOND_POSITION = "Scom.tranxuanloc.emobits.ECOND_POSITION";
    public static final String IS_COMPLETION = "com.tranxuanloc.emobits.IS_COMPLETION";
    public static final String SONG_URL = "com.tranxuanloc.emobits.SONG_URL";
    public static final String SONG_NAME = "com.tranxuanloc.emobits.SONG_NAME";
    public static final String SONG_THUMBNAIL = "com.tranxuanloc.emobits.SONG_THUMBNAIL";
    public static final String SONG_DESC = "com.tranxuanloc.emobits.SONG_DESC";
    private final static String TAG = MediaPlayerServices.class.getSimpleName();
    private int delayMillis = 1000;
    private MediaPlayer mediaPlayer = null;
    private String sourcePath, thumbnailUrl, songName, songDescription, action;
    private Handler handler = new Handler();
    private Intent intentBroadCast;
    private RemoteViews views;
    private Notification status;
    private SharedPreferences preferences;
    private int secondaryProgress;
    private int forward_rewind = 20000;
    private NotificationCompat.Builder builder;
    private Runnable updateTimerTask = new Runnable() {
        @Override
        public void run() {
            intentBroadCast.putExtra(STATUS, action);
            intentBroadCast.putExtra(CURRENT_POSITION, mediaPlayer.getCurrentPosition());
            intentBroadCast.putExtra(DURATION, mediaPlayer.getDuration());
            intentBroadCast.putExtra(IS_PLAYING, mediaPlayer.isPlaying());
            intentBroadCast.putExtra(IS_COMPLETION, false);
            intentBroadCast.putExtra(SECOND_POSITION, secondaryProgress);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intentBroadCast);
            handler.postDelayed(this, delayMillis);
        }
    };
    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            builder.setLargeIcon(bitmap);
            status = builder.build();
            startForeground(1411, status);
            Log.d(TAG, "onBitmapLoaded: ");
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
            status = builder.build();
            startForeground(1411, status);
            Log.d(TAG, "onBitmapFailed: ");
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            Log.d(TAG, "onPrepareLoad: ");
        }
    };
    private State mState = State.Retrieving;
    private AudioFocus mAudioFocus = AudioFocus.NoFocusNoDuck;
    private AudioManager mAudioManager;
    private AudioFocusHelper mAudioFocusHelper;
    private AudioManager.OnAudioFocusChangeListener mAudioFocusListenter;
    private WifiManager.WifiLock wifiLock;

    public MediaPlayerServices() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: ");
        preferences = getSharedPreferences("song_pref", Context.MODE_PRIVATE);
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, "emobits");
        wifiLock.acquire();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mAudioFocusHelper = new AudioFocusHelper(getApplicationContext(), this);
    }

    private void updateNotification() {
        Intent intentPrevious = new Intent(this, MediaPlayerServices.class);
        intentPrevious.setAction(ACTION_PREVIOUS);
        PendingIntent pendingIntentPrevious = PendingIntent.getService(this, 0, intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentToggle = new Intent(this, MediaPlayerServices.class);
        intentToggle.setAction(ACTION_TOGGLE_PLAYBACK);
        PendingIntent pendingIntentToggle = PendingIntent.getService(this, 0, intentToggle, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentNext = new Intent(this, MediaPlayerServices.class);
        intentNext.setAction(ACTION_NEXT);
        PendingIntent pendingIntentNext = PendingIntent.getService(this, 0, intentNext, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentStop = new Intent(this, MediaPlayerServices.class);
        intentStop.setAction(ACTION_STOP);
        PendingIntent pendingIntentStop = PendingIntent.getService(this, 0, intentStop, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.M) {
            builder.setStyle(
                    new NotificationCompat.MediaStyle()
                            .setShowCancelButton(true)
                            .setCancelButtonIntent(pendingIntentStop));
        }
        builder.mActions.clear();
        builder.addAction(
                new android.support.v4.app.NotificationCompat.Action(
                        R.drawable.notification_ic_av_fast_rewind, "Rewind", pendingIntentPrevious)
        );
        builder.addAction(
                new android.support.v4.app.NotificationCompat.Action(
                        mState == State.Playing ? R.drawable.notification_ic_av_pause
                                : R.drawable.notification_ic_av_play_arrow, "Play/Pause", pendingIntentToggle)
        );
        builder.addAction(
                new android.support.v4.app.NotificationCompat.Action(
                        R.drawable.notification_ic_av_fast_forward, "Forward", pendingIntentNext)
        );

        status = builder.build();
        status.flags |= NotificationCompat.PRIORITY_HIGH;
        status.flags |= NotificationCompat.FLAG_ONGOING_EVENT;
        status.flags |= NotificationCompat.FLAG_NO_CLEAR;
        startForeground(1411, status);
    }

    private void showNotification() {
       /* views = new RemoteViews(getPackageName(), R.layout.ongoing_notification);
        views.setTextViewText(R.id.notification_tv_song_title, songName);
        views.setTextViewText(R.id.notification_tv_song_description, songDescription);

        views.setOnClickPendingIntent(R.id.notification_iv_listen_pause_start, pendingIntentPause);

        views.setOnClickPendingIntent(R.id.notification_iv_listen_forward, pendingIntentNext);

        views.setOnClickPendingIntent(R.id.notification_iv_listen_rewind, pendingIntentPrevious);
*/
        Intent intentPrevious = new Intent(this, MediaPlayerServices.class);
        intentPrevious.setAction(ACTION_PREVIOUS);
        PendingIntent pendingIntentPrevious = PendingIntent.getService(this, 0, intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentToggle = new Intent(this, MediaPlayerServices.class);
        intentToggle.setAction(ACTION_TOGGLE_PLAYBACK);
        PendingIntent pendingIntentToggle = PendingIntent.getService(this, 0, intentToggle, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentNext = new Intent(this, MediaPlayerServices.class);
        intentNext.setAction(ACTION_NEXT);
        PendingIntent pendingIntentNext = PendingIntent.getService(this, 0, intentNext, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentStop = new Intent(this, MediaPlayerServices.class);
        intentStop.setAction(ACTION_STOP);
        PendingIntent pendingIntentStop = PendingIntent.getService(this, 0, intentStop, PendingIntent.FLAG_UPDATE_CURRENT);


        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("IS_NOTIFY", "");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder = new NotificationCompat.Builder(this);
        builder.setStyle(new NotificationCompat.MediaStyle().setShowCancelButton(true));
        builder.setSmallIcon(R.drawable.ic_launcher_small_icon);
        builder.setContentTitle(songName);
        builder.setWhen(0);
        builder.setColor(ContextCompat.getColor(this, R.color.colorNotifyBackground));
        builder.setContentText(songDescription);
        builder.setContentIntent(pendingIntent);
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.M) {
            builder.setStyle(
                    new NotificationCompat.MediaStyle()/*
                            .setShowActionsInCompactView(0)*/
                            .setShowCancelButton(true)
                            .setCancelButtonIntent(pendingIntentStop));
        }
        builder.mActions.clear();
        builder.addAction(
                new android.support.v4.app.NotificationCompat.Action(
                        R.drawable.notification_ic_av_fast_rewind, "Rewind", pendingIntentPrevious)
        );
        builder.addAction(
                new android.support.v4.app.NotificationCompat.Action(
                        mState == State.Playing ? R.drawable.notification_ic_av_pause
                                : R.drawable.notification_ic_av_play_arrow, "Play/Pause", pendingIntentToggle)
        );
        builder.addAction(
                new android.support.v4.app.NotificationCompat.Action(
                        R.drawable.notification_ic_av_fast_forward, "Forward", pendingIntentNext)
        );

        status = builder.build();
        status.flags |= NotificationCompat.PRIORITY_HIGH;
        status.flags |= NotificationCompat.FLAG_ONGOING_EVENT;
        status.flags |= NotificationCompat.FLAG_NO_CLEAR;
        startForeground(1411, status);
        /*ComponentName componentName = new ComponentName(this, MusicIntentReceiver.class);
        MediaSessionCompat sessionCompat = new MediaSessionCompat(this, "session", componentName, null);
        MediaControllerCompat controller = sessionCompat.getController();*/
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand: ");
        String action = intent.getAction();
        if (action.equals(ACTION_TOGGLE_PLAYBACK)) processToggleRequest();
        else if (action.equals(ACTION_PLAY)) processPlayRequest(intent);
        else if (action.equals(ACTION_PAUSE)) processPauseRequest();
        else if (action.equals(ACTION_RESUME)) processResumeRequest();
        else if (action.equals(ACTION_STOP)) processStopRequest();
        else if (action.equals(START_TRACKING)) {
            Log.e(TAG, "onStartCommand: START_TRACKING");
            this.action = START_TRACKING;
            handler.removeCallbacks(updateTimerTask);
        } else if (action.equals(STOP_TRACKING)) {
            Log.e(TAG, "onStartCommand: STOP_TRACKING");
            this.action = STOP_TRACKING;
            int currentPosition = intent.getIntExtra(CURRENT_POSITION, 0);
            mediaPlayer.seekTo(currentPosition);
            handler.postDelayed(updateTimerTask, delayMillis);
        } else if (action.equals(ACTION_NEXT)) {
            Log.d(TAG, "onStartCommand: ACTION_NEXT");
            this.action = ACTION_NEXT;
            int msec = mediaPlayer.getCurrentPosition() + forward_rewind;
            if (msec < mediaPlayer.getDuration())
                mediaPlayer.seekTo(msec);
            else mediaPlayer.seekTo(mediaPlayer.getDuration());
        } else if (action.equals(ACTION_PREVIOUS)) {
            this.action = ACTION_PREVIOUS;
            Log.d(TAG, "onStartCommand: ACTION_PREVIOUS");
            int msec = mediaPlayer.getCurrentPosition() - forward_rewind;
            if (msec > 0)
                mediaPlayer.seekTo(msec);
            else
                mediaPlayer.seekTo(0);
        }
        return START_NOT_STICKY;
    }

    private void processResumeRequest() {
        Log.e(TAG, "onStartCommand: RESUME");
        mState = State.Playing;
        tryToGetAudioFocus();
        mediaPlayer.start();
        updateNotification();
    }

    private void processToggleRequest() {
        if (mState == State.Paused)
            processResumeRequest();
        else
            processPauseRequest();
    }

    private void processPauseRequest() {
        Log.e(TAG, "onStartCommand: PAUSE");
        mState = State.Paused;
        this.action = ACTION_PAUSE;
        mediaPlayer.pause();
        updateNotification();
    }

    private void processPlayRequest(Intent intent) {
        Log.e(TAG, "onStartCommand: PLAY");
        mState = State.Playing;
        handler.removeCallbacks(updateTimerTask);
        this.action = ACTION_PLAY;
        sourcePath = intent.getStringExtra(SONG_URL);
        songName = intent.getStringExtra(SONG_NAME);
        thumbnailUrl = intent.getStringExtra(SONG_THUMBNAIL);
        songDescription = intent.getStringExtra(SONG_DESC);
        playSong();
        showNotification();
        Picasso.with(this).load(thumbnailUrl).into(target);
    }

    private void tryToGetAudioFocus() {
        if (mAudioFocus != AudioFocus.Focused && mAudioFocusHelper != null
                && mAudioFocusHelper.requestFocus())
            mAudioFocus = AudioFocus.Focused;
    }

    private void playSong() {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(sourcePath);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(TAG, "onError: " + what + " " + extra);
        mState = State.Stopped;
        processStopRequest();
        return true;
    }

    @Override
    public void onPrepared(final MediaPlayer mp) {
        Log.e(TAG, "onPrepared: ");
        tryToGetAudioFocus();
        mp.start();
        intentBroadCast = new Intent();
        intentBroadCast.setAction(BROADCAST_UPDATE);
        handler.post(updateTimerTask);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean("IS_FIRST_PLAY", false);
        edit.apply();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        // int mpCurrent = (mp.getCurrentPosition() / mp.getDuration()) * 100;
        //  Log.d(TAG, "onBufferingUpdate() returned: " + mpCurrent + " " + percent);
        secondaryProgress = percent * getResources().getInteger(R.integer.max_progress) / 100;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.e(TAG, "onCompletion: ");
        processStopRequest();
    }


    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy: ");
        processStopRequest();
    }

    private void processStopRequest() {
        Log.e(TAG, "onStartCommand: STOP");
        stopForeground(true);
        handler.removeCallbacks(updateTimerTask);
        if (mediaPlayer != null) {
            mState = State.Stopped;
            intentBroadCast = new Intent();
            intentBroadCast.setAction(BROADCAST_UPDATE);
            intentBroadCast.putExtra(IS_PLAYING, false);
            intentBroadCast.putExtra(IS_COMPLETION, true);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intentBroadCast);
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean("IS_FIRST_PLAY", true);
        edit.putBoolean("IS_PLAYING", false);
        edit.apply();
        if (wifiLock.isHeld())
            wifiLock.release();
        giveUpAudioFocus();
        stopSelf();
    }

    private void giveUpAudioFocus() {
        if (mAudioFocus == AudioFocus.Focused && mAudioFocusHelper != null
                && mAudioFocusHelper.abandonFocus())
            mAudioFocus = AudioFocus.NoFocusNoDuck;
    }

    @Override
    public void onGainedAudioFocus() {

    }

    @Override
    public void onLostAudioFocus(boolean canDuck) {

    }

    enum State {
        Retrieving,
        Stopped,
        Preparing,
        Playing,
        Paused
    }

    enum PauseReason {
        UserRequest,
        FocusLoss,
    }

    enum AudioFocus {
        NoFocusNoDuck,
        NoFocusCanDuck,
        Focused
    }
}
