package me.carda.awesome_notifications.awesome_notifications_core.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import me.carda.awesome_notifications.awesome_notifications_core.enumerators.MediaSource;
import me.carda.awesome_notifications.awesome_notifications_core.managers.ChannelManager;

import static me.carda.awesome_notifications.awesome_notifications_core.Definitions.MEDIA_VALID_ASSET;
import static me.carda.awesome_notifications.awesome_notifications_core.Definitions.MEDIA_VALID_FILE;
import static me.carda.awesome_notifications.awesome_notifications_core.Definitions.MEDIA_VALID_NETWORK;
import static me.carda.awesome_notifications.awesome_notifications_core.Definitions.MEDIA_VALID_RESOURCE;

public class AudioUtils extends MediaUtils {

    // ************** SINGLETON PATTERN ***********************

    protected static AudioUtils instance;

    protected AudioUtils(){
    }

    public static AudioUtils getInstance() {
        if (instance == null)
            instance = new AudioUtils();
        return instance;
    }

    // ********************************************************

    public int getAudioResourceId(Context context, String audioReference){
        audioReference = this.cleanMediaPath(audioReference);
        String[] reference = audioReference.split("\\/");

        try {
            int resId = 0;

            String type = reference[0];
            String label = reference[1];

            // Resources protected from obfuscation
            // https://developer.android.com/studio/build/shrink-code#strict-reference-checks
            String name = String.format("res_%1s", label);
            resId = context.getResources().getIdentifier(name, type, context.getPackageName());

            if(resId == 0){
                resId = context.getResources().getIdentifier(label, type, context.getPackageName());
            }

            return resId;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public Boolean isValidAudio(Context context, String mediaPath) {

        if (mediaPath != null) {

            if (matchMediaType(MEDIA_VALID_RESOURCE, mediaPath)) {
                return isValidAudioResource(context, mediaPath);
            }

            if (matchMediaType(MEDIA_VALID_NETWORK, mediaPath, false)) {
                // TODO MISSING IMPLEMENTATION
                return false;
            }

            if (matchMediaType(MEDIA_VALID_FILE, mediaPath)) {
                // TODO MISSING IMPLEMENTATION
                return false;
            }

            if (matchMediaType(MEDIA_VALID_ASSET, mediaPath)) {
                // TODO MISSING IMPLEMENTATION
                return false;
            }

        }
        return false;
    }

    private Boolean isValidAudioResource(Context context, String name) {
        if(name != null){
            int resourceId = getAudioResourceId(context, name);
            return resourceId > 0;
        }
        return false;
    }
}
