package com.tranxuanloc.emobits.listennow;

/**
 * Created by tranxuanloc on 4/30/2016.
 */
public interface MusicFocusable {
    void onGainedAudioFocus();

    void onLostAudioFocus(boolean canDuck);

}
