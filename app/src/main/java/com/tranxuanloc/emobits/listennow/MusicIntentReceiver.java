package com.tranxuanloc.emobits.listennow;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.view.KeyEvent;

public class MusicIntentReceiver extends BroadcastReceiver {
    public MusicIntentReceiver() {
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
            Intent service = new Intent(context, MediaPlayerServices.class);
            service.setAction(MediaPlayerServices.ACTION_PAUSE);
            context.startService(service);
        } else if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
            KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
            assert keyEvent != null;
            if (keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                return;
            Intent service = new Intent(context, MediaPlayerServices.class);
            switch (keyEvent.getKeyCode()) {
                case KeyEvent.KEYCODE_HEADSETHOOK:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    service.setAction(MediaPlayerServices.ACTION_TOGGLE_PLAYBACK);
                    context.startService(service);
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    service.setAction(MediaPlayerServices.ACTION_PLAY);
                    context.startService(service);
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    service.setAction(MediaPlayerServices.ACTION_PAUSE);
                    context.startService(service);
                    break;
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    service.setAction(MediaPlayerServices.ACTION_STOP);
                    context.startService(service);
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    service.setAction(MediaPlayerServices.ACTION_NEXT);
                    context.startService(service);
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    service.setAction(MediaPlayerServices.ACTION_PREVIOUS);
                    context.startService(service);
                    break;
            }
        }
    }
}
