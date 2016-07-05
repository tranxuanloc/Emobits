package com.tranxuanloc.emobits.listennow;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tranxuanloc.emobits.R;
import com.tranxuanloc.emobits.search.SessionInfo;
import com.tranxuanloc.emobits.utilities.Utilities;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by tranxuanloc on 4/6/2016.
 */
public class ListenNowFragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener {
    private static final String TAG = ListenNowFragment.class.getSimpleName();
    @Bind(R.id.iv_listen_pause_start)
    ImageView ivPauseStart;
    @Bind(R.id.seekBar)
    SeekBar seekBar;
    @Bind(R.id.tv_listen_current_time)
    TextView tvCurrentTime;
    @Bind(R.id.tv_listen_total_time)
    TextView tvTotalTime;
    @Bind(R.id.tv_listen_name)
    TextView tvSongName;
    @Bind(R.id.tv_listen_album)
    TextView tvSongDescription;
    @Bind(R.id.iv_listen_ask)
    TextView tvAsk;
    private BroadcastReceiver receiver;
    private int currentPosition, duration, progress;
    private Utilities util = new Utilities();
    private boolean isFirstPlay, isPlaying, isCompletion;
    private String songUrl;
    private String songName;
    private String songThumbnail;
    private String songDescription;
    private SharedPreferences preferences;
    private String currentTime, totalTime;

    public static ListenNowFragment newInstance() {
        ListenNowFragment fragment = new ListenNowFragment();
        Bundle args = new Bundle();
        args.putString("1", "");
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getActivity().getSharedPreferences("song_pref", Context.MODE_PRIVATE);
        Log.d(TAG, "onCreate: " + (savedInstanceState != null));
        songName = preferences.getString("SONG_NAME", "");
        songDescription = preferences.getString("SONG_DESCRIPTION", "");
        songUrl = preferences.getString("SONG_URL", "");
        songThumbnail = preferences.getString("SONG_THUMBNAIL", "");
        isFirstPlay = preferences.getBoolean("IS_FIRST_PLAY", true);
        isPlaying = preferences.getBoolean("IS_PLAYING", false);
        progress = preferences.getInt("PROGRESS", 0);
        currentTime = preferences.getString("CURRENT_TIME", "00:00");
        totalTime = preferences.getString("TOTAL_TIME", "00:00");
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: " + (savedInstanceState != null));
        View rootView = inflater.inflate(R.layout.fragment_listen_now, container, false);
        ButterKnife.bind(this, rootView);
        seekBar.setPadding(0, 0, 0, 0);
        seekBar.setThumbOffset((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, getResources().getDisplayMetrics()));
        if (isPlaying)
            ivPauseStart.setImageResource(R.drawable.ic_av_pause_selector);
        else ivPauseStart.setImageResource(R.drawable.ic_av_play_arrow_selector);
        if (!isFirstPlay) {
            seekBar.setProgress(progress);
            tvCurrentTime.setText(currentTime);
            tvTotalTime.setText(totalTime);
        }
        tvSongName.setText(songName);
        tvSongDescription.setText(songDescription);
        if (songDescription.length() > 0)
            tvAsk.setText(String.format("Do you fell %s?", songDescription.split(" - ")[1]));
        seekBar.setEnabled(false);
        seekBar.setOnSeekBarChangeListener(this);
        ivPauseStart.setOnClickListener(this);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String status = intent.getStringExtra(MediaPlayerServices.STATUS);
                isCompletion = intent.getBooleanExtra(MediaPlayerServices.IS_COMPLETION, false);
                isPlaying = intent.getBooleanExtra(MediaPlayerServices.IS_PLAYING, false);
                preferences.edit().putBoolean("IS_PLAYING", isPlaying).apply();
                int secondProgress = intent.getIntExtra(MediaPlayerServices.SECOND_POSITION, 0);
                seekBar.setSecondaryProgress(secondProgress);
                if (isPlaying) {
                    seekBar.setEnabled(true);
                    duration = intent.getIntExtra(MediaPlayerServices.DURATION, 0);
                    currentPosition = intent.getIntExtra(MediaPlayerServices.CURRENT_POSITION, 0);
                    currentTime = util.milliSecondsToTimer(currentPosition);
                    tvCurrentTime.setText(currentTime);
                    totalTime = util.milliSecondsToTimer(duration);
                    tvTotalTime.setText(totalTime);
                    ivPauseStart.setImageResource(R.drawable.ic_av_pause_selector);
                    int progress = util.getProgressPercentage(currentPosition, duration);
                    preferences.edit().putInt("PROGRESS", progress).apply();
                    preferences.edit().putString("CURRENT_TIME", currentTime).apply();
                    preferences.edit().putString("TOTAL_TIME", totalTime).apply();
                    seekBar.setProgress(progress);
                } else if (isCompletion) {
                    preferences.edit().putBoolean("IS_FIRST_PLAY", true);
                    preferences.edit().putBoolean("IS_PLAYING", false).apply();
                    isFirstPlay = true;
                    isPlaying = false;
                    seekBar.setEnabled(false);
                    seekBar.setProgress(0);
                    ivPauseStart.setImageResource(R.drawable.ic_av_play_arrow_selector);
                    tvCurrentTime.setText(R.string.zero_zero);
                } else {
                    duration = intent.getIntExtra(MediaPlayerServices.DURATION, 0);
                    currentPosition = intent.getIntExtra(MediaPlayerServices.CURRENT_POSITION, 0);
                    currentTime = util.milliSecondsToTimer(currentPosition);
                    tvCurrentTime.setText(currentTime);
                    int progress = util.getProgressPercentage(currentPosition, duration);
                    seekBar.setProgress(progress);
                    ivPauseStart.setImageResource(R.drawable.ic_av_play_arrow_selector);
                }
            }
        };
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void updateUI(SessionInfo.ListSessionInfo info) {
        SharedPreferences.Editor edit = preferences.edit();
        String djName = info.getDJName();
        String djStyle = info.getDJStyle();
        songDescription = String.format("%s - %s", djName, djStyle);
        songName = info.getSongName();
        songUrl = info.getSongURL()/*String.format("%s%s", API.BASE_URL, info.getSongURL())*/;
        songThumbnail = info.getThumbnailURL();
        edit.putString("SONG_NAME", songName);
        edit.putString("SONG_DESCRIPTION", songDescription);
        edit.putString("SONG_URL", songUrl);
        edit.putString("SONG_THUMBNAIL", songThumbnail);
        edit.putBoolean("IS_FIRST_PLAY", true);
        edit.apply();
        tvSongName.setText(songName);
        tvSongDescription.setText(songDescription);
        tvCurrentTime.setText(getString(R.string.zero_zero));
        tvTotalTime.setText(getString(R.string.zero_zero));
        if (songDescription.length() > 0)
            tvAsk.setText(String.format("Do you fell %s?", songDescription.split(" - ")[1]));
        seekBar.setProgress(0);
        seekBar.setSecondaryProgress(0);

        isPlaying = false;
        isFirstPlay = true;
        ivPauseStart.performClick();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, new IntentFilter(MediaPlayerServices.BROADCAST_UPDATE));
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
    }

    @OnClick(R.id.iv_listen_forward)
    public void forwardClick() {
        Intent intent = new Intent(getContext(), MediaPlayerServices.class);
        intent.setAction(MediaPlayerServices.ACTION_NEXT);
        getContext().startService(intent);
    }

    @OnClick(R.id.iv_listen_rewind)
    public void rewindClick() {
        Intent intent = new Intent(getContext(), MediaPlayerServices.class);
        intent.setAction(MediaPlayerServices.ACTION_PREVIOUS);
        getContext().startService(intent);
    }

    @Override
    public void onClick(View v) {
        if (v == ivPauseStart) {
            Intent intent = new Intent(getContext(), MediaPlayerServices.class);
            if (!isPlaying) {
                ivPauseStart.setImageResource(R.drawable.ic_av_pause_selector);
                if (isFirstPlay) {
                    intent.setAction(MediaPlayerServices.ACTION_PLAY);
                    intent.putExtra(MediaPlayerServices.SONG_URL, songUrl);
                    intent.putExtra(MediaPlayerServices.SONG_NAME, songName);
                    intent.putExtra(MediaPlayerServices.SONG_THUMBNAIL, songThumbnail);
                    intent.putExtra(MediaPlayerServices.SONG_DESC, songDescription);
                    isFirstPlay = false;
                } else
                    intent.setAction(MediaPlayerServices.ACTION_RESUME);
            } else {
                ivPauseStart.setImageResource(R.drawable.ic_av_play_arrow_selector);
                intent.setAction(MediaPlayerServices.ACTION_PAUSE);
            }
            getContext().startService(intent);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        tvCurrentTime.setText(util.milliSecondsToTimer(util.progressToTimer(progress, duration)));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Intent intent = new Intent(getContext(), MediaPlayerServices.class);
        intent.setAction(MediaPlayerServices.START_TRACKING);
        getContext().startService(intent);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Intent intent = new Intent(getContext(), MediaPlayerServices.class);
        intent.setAction(MediaPlayerServices.STOP_TRACKING);
        intent.putExtra(MediaPlayerServices.CURRENT_POSITION, util.progressToTimer(seekBar.getProgress(), duration));
        getContext().startService(intent);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }
}
