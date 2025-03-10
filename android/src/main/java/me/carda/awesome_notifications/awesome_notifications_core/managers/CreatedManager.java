package me.carda.awesome_notifications.awesome_notifications_core.managers;

import android.content.Context;

import java.util.List;

import me.carda.awesome_notifications.awesome_notifications_core.Definitions;
import me.carda.awesome_notifications.awesome_notifications_core.models.returnedData.NotificationReceived;
import me.carda.awesome_notifications.awesome_notifications_core.utils.StringUtils;

public class CreatedManager {

    private static final SharedManager<NotificationReceived> shared
            = new SharedManager<>(
                    StringUtils.getInstance(),
                    "CreatedManager",
                    NotificationReceived.class,
                    "NotificationReceived");

    public static Boolean removeCreated(Context context, Integer id) {
        return shared.remove(context, Definitions.SHARED_CREATED, id.toString());
    }

    public static List<NotificationReceived> listCreated(Context context) {
        return shared.getAllObjects(context, Definitions.SHARED_CREATED);
    }

    public static void saveCreated(Context context, NotificationReceived received) {
        shared.set(context, Definitions.SHARED_CREATED, received.id.toString(), received);
    }

    public static NotificationReceived getCreatedByKey(Context context, Integer id){
        return shared.get(context, Definitions.SHARED_CREATED, id.toString());
    }

    public static void cancelAllCreated(Context context) {
        List<NotificationReceived> receivedList = shared.getAllObjects(context, Definitions.SHARED_CREATED);
        if (receivedList != null){
            for (NotificationReceived received : receivedList) {
                cancelCreated(context, received.id);
            }
        }
    }

    public static void cancelCreated(Context context, Integer id) {
        NotificationReceived received = getCreatedByKey(context, id);
        if(received !=null)
            removeCreated(context, received.id);
    }

    public static void commitChanges(Context context){
        shared.commit(context);
    }
}
