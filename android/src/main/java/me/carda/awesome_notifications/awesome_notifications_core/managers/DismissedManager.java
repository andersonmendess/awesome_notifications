package me.carda.awesome_notifications.awesome_notifications_core.managers;

import android.content.Context;

import java.util.List;

import me.carda.awesome_notifications.awesome_notifications_core.Definitions;
import me.carda.awesome_notifications.awesome_notifications_core.models.returnedData.ActionReceived;
import me.carda.awesome_notifications.awesome_notifications_core.utils.StringUtils;

public class DismissedManager {

    private static final SharedManager<ActionReceived> shared
            = new SharedManager<>(
                    StringUtils.getInstance(),
                    "DismissedManager",
                    ActionReceived.class,
                    "ActionReceived");

    public static Boolean removeDismissed(Context context, Integer id) {
        return shared.remove(context, Definitions.SHARED_DISMISSED, id.toString());
    }

    public static List<ActionReceived> listDismisses(Context context) {
        return shared.getAllObjects(context, Definitions.SHARED_DISMISSED);
    }

    public static void saveDismissed(Context context, ActionReceived received) {
        shared.set(context, Definitions.SHARED_DISMISSED, received.id.toString(), received);
    }

    public static ActionReceived getDismissedByKey(Context context, Integer id){
        return shared.get(context, Definitions.SHARED_DISMISSED, id.toString());
    }

    public static void clearAllDismissed(Context context) {
        List<ActionReceived> receivedList = shared.getAllObjects(context, Definitions.SHARED_DISMISSED);
        if (receivedList != null){
            for (ActionReceived received : receivedList) {
                cancelDismissed(context, received.id);
            }
        }
    }

    public static void cancelDismissed(Context context, Integer id) {
        ActionReceived received = getDismissedByKey(context, id);
        if(received !=null)
            removeDismissed(context, received.id);
    }

    public static void commitChanges(Context context){
        shared.commit(context);
    }
}
