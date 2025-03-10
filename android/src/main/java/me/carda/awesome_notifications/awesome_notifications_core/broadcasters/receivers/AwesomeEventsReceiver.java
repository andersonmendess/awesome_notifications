package me.carda.awesome_notifications.awesome_notifications_core.broadcasters.receivers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import me.carda.awesome_notifications.awesome_notifications_core.AwesomeNotifications;
import me.carda.awesome_notifications.awesome_notifications_core.Definitions;
import me.carda.awesome_notifications.awesome_notifications_core.listeners.AwesomeActionEventListener;
import me.carda.awesome_notifications.awesome_notifications_core.listeners.AwesomeNotificationEventListener;
import me.carda.awesome_notifications.awesome_notifications_core.exceptions.AwesomeNotificationsException;
import me.carda.awesome_notifications.awesome_notifications_core.models.returnedData.ActionReceived;
import me.carda.awesome_notifications.awesome_notifications_core.models.returnedData.NotificationReceived;
import me.carda.awesome_notifications.awesome_notifications_core.utils.StringUtils;

public class AwesomeEventsReceiver {

    public static String TAG = "AwesomeEventsReceiver";

    // ************** SINGLETON PATTERN ***********************

    private static AwesomeEventsReceiver instance;

    private AwesomeEventsReceiver(StringUtils stringUtils){
        this.stringUtils = stringUtils;
    }

    public static AwesomeEventsReceiver getInstance() {
        if (instance == null)
            instance = new AwesomeEventsReceiver(
                    StringUtils.getInstance());
        return instance;
    }

    public boolean isEmpty(){
        return notificationActionListeners.isEmpty();
    }

    // ********************************************************

    protected final StringUtils stringUtils;

    /// **************  OBSERVER PATTERN  *********************

    static List<AwesomeNotificationEventListener> notificationEventListeners = new ArrayList<>();
    public AwesomeEventsReceiver subscribeOnNotificationEvents(AwesomeNotificationEventListener listener) {
        notificationEventListeners.add(listener);

        if(AwesomeNotifications.debug)
            Log.d(TAG, listener.getClass().getSimpleName() + " subscribed to receive notification events");

        return this;
    }
    public AwesomeEventsReceiver unsubscribeOnNotificationEvents(AwesomeNotificationEventListener listener) {
        notificationEventListeners.remove(listener);

        if(AwesomeNotifications.debug)
            Log.d(TAG, listener.getClass().getSimpleName() + " unsubscribed from notification events");

        return this;
    }
    private void notifyNotificationEvent(String eventName, NotificationReceived notificationReceived) {

        if(AwesomeNotifications.debug && notificationEventListeners.isEmpty())
            Log.e(TAG, "New event "+eventName+" ignored, as there is no listeners waiting for new notification events");

        for (AwesomeNotificationEventListener listener : notificationEventListeners)
            listener.onNewNotificationReceived(eventName, notificationReceived);
    }

    // ********************************************************

    static List<AwesomeActionEventListener> notificationActionListeners = new ArrayList<>();
    public AwesomeEventsReceiver subscribeOnActionEvents(AwesomeActionEventListener listener) {
        notificationActionListeners.add(listener);

        if(AwesomeNotifications.debug)
            Log.d(TAG, listener.getClass().getSimpleName() + " subscribed to receive action events");

        return this;
    }
    public AwesomeEventsReceiver unsubscribeOnActionEvents(AwesomeActionEventListener listener) {
        notificationActionListeners.remove(listener);

        if(AwesomeNotifications.debug)
            Log.d(TAG, listener.getClass().getSimpleName() + " unsubscribed from action events");

        return this;
    }
    private void notifyActionEvent(String eventName, ActionReceived actionReceived) {

        if(AwesomeNotifications.debug && notificationEventListeners.isEmpty())
            Log.e(TAG, "New event "+eventName+" ignored, as there is no listeners waiting for new action events");

        for (AwesomeActionEventListener listener : notificationActionListeners)
            listener.onNewActionReceived(eventName, actionReceived);
    }

    // ********************************************************

    public void addNotificationEvent(Context context, String eventName, NotificationReceived notificationReceived){

        if(notificationEventListeners.isEmpty()) {
            if(AwesomeNotifications.debug)
                Log.e(TAG, "New event "+eventName+" ignored, as there is no listeners waiting for new notification events");
            return;
        }

        try {
            switch (eventName) {

                case Definitions.BROADCAST_CREATED_NOTIFICATION:
                    onBroadcastNotificationCreated(context, notificationReceived);
                    return;

                case Definitions.BROADCAST_DISPLAYED_NOTIFICATION:
                    onBroadcastNotificationDisplayed(context, notificationReceived);
                    return;

                default:
                    if (AwesomeNotifications.debug)
                        Log.d(TAG, "Received unknown notification event: " + (
                                stringUtils.isNullOrEmpty(eventName) ? "empty" : eventName));

            }
        } catch (Exception e) {
            if (AwesomeNotifications.debug)
                Log.d(TAG, String.format("%s", e.getMessage()));
            e.printStackTrace();
        }
    }

    public void addAwesomeActionEvent(Context context, String eventName, ActionReceived actionReceived){

        if(notificationEventListeners.isEmpty()) {
            if(AwesomeNotifications.debug)
                Log.e(TAG, "New event "+eventName+" ignored, as there is no listeners waiting for new action events");
            return;
        }

        try {
            switch (eventName) {

                case Definitions.BROADCAST_DEFAULT_ACTION:
                    onBroadcastDefaultActionNotification(context, actionReceived);
                    return;

                case Definitions.BROADCAST_DISMISSED_NOTIFICATION:
                    onBroadcastNotificationDismissed(context, actionReceived);
                    return;

                case Definitions.BROADCAST_SILENT_ACTION:
                    onBroadcastSilentActionNotification(context, actionReceived);
                    return;

                case Definitions.BROADCAST_BACKGROUND_ACTION:
                    onBroadcastBackgroundActionNotification(context, actionReceived);
                    return;

                default:
                    if (AwesomeNotifications.debug)
                        Log.d(TAG, "Received unknown action event: " + (
                                stringUtils.isNullOrEmpty(eventName) ? "empty" : eventName));

            }
        } catch (Exception e) {
            if (AwesomeNotifications.debug)
                Log.d(TAG, String.format("%s", e.getMessage()));
            e.printStackTrace();
        }
    }

    /// ***********************************

    private void onBroadcastNotificationCreated(Context context, NotificationReceived notificationReceived) throws AwesomeNotificationsException {
        try {
            notificationReceived.validate(context);

            if (AwesomeNotifications.debug)
                Log.d(TAG, "New notification creation event");

            notifyNotificationEvent(Definitions.EVENT_NOTIFICATION_CREATED, notificationReceived);

        } catch (Exception e) {
            throw new AwesomeNotificationsException(e.getMessage());
        }
    }

    private void onBroadcastNotificationDisplayed(Context context, NotificationReceived notificationReceived) throws AwesomeNotificationsException {
        try {
            notificationReceived.validate(context);

            if (AwesomeNotifications.debug)
                Log.d(TAG, "New notification display event");

            notifyNotificationEvent(Definitions.EVENT_NOTIFICATION_DISPLAYED, notificationReceived);

        } catch (Exception e) {
            throw new AwesomeNotificationsException(e.getMessage());
        }
    }

    private void onBroadcastDefaultActionNotification(Context context, ActionReceived actionReceived) {
        try {
            actionReceived.validate(context);

            if(AwesomeNotifications.debug)
                Log.d(TAG, "New notification action event");

            notifyActionEvent(Definitions.EVENT_DEFAULT_ACTION, actionReceived);

        } catch (Exception e) {
            if(AwesomeNotifications.debug)
                Log.d(TAG, String.format("%s", e.getMessage()));
            e.printStackTrace();
        }
    }

    private void onBroadcastNotificationDismissed(Context context, ActionReceived actionReceived) throws AwesomeNotificationsException {
        try {
            actionReceived.validate(context);

            if (AwesomeNotifications.debug)
                Log.d(TAG, "New notification dismiss event");

            notifyActionEvent(Definitions.EVENT_NOTIFICATION_DISMISSED, actionReceived);

        } catch (Exception e) {
            throw new AwesomeNotificationsException(e.getMessage());
        }
    }

    private void onBroadcastSilentActionNotification(Context context, ActionReceived actionReceived) {
        try {
            actionReceived.validate(context);

            if(AwesomeNotifications.debug)
                Log.d(TAG, "New silent action event");

            notifyActionEvent(Definitions.EVENT_SILENT_ACTION, actionReceived);

        } catch (Exception e) {
            if(AwesomeNotifications.debug)
                Log.d(TAG, String.format("%s", e.getMessage()));
            e.printStackTrace();
        }
    }

    private void onBroadcastBackgroundActionNotification(Context context, ActionReceived actionReceived) throws AwesomeNotificationsException {
        try {
            actionReceived.validate(context);

            if (AwesomeNotifications.debug)
                Log.d(TAG, "New background silent action event");

            notifyActionEvent(Definitions.EVENT_SILENT_ACTION, actionReceived);

        } catch (Exception e) {
            throw new AwesomeNotificationsException(e.getMessage());
        }
    }
}
