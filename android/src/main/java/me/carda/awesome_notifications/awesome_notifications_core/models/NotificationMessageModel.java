package me.carda.awesome_notifications.awesome_notifications_core.models;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import me.carda.awesome_notifications.awesome_notifications_core.Definitions;
import me.carda.awesome_notifications.awesome_notifications_core.exceptions.AwesomeNotificationsException;
import me.carda.awesome_notifications.awesome_notifications_core.utils.StringUtils;

public class NotificationMessageModel extends AbstractModel {

    public String title;
    public String message;
    public String largeIcon;
    public Long timestamp;

    public NotificationMessageModel(){
        super(StringUtils.getInstance());
    }

    public NotificationMessageModel(String title, String message, String largeIcon){
        super(StringUtils.getInstance());
        this.title = title;
        this.message = message;
        this.largeIcon = largeIcon;
        this.timestamp = System.currentTimeMillis();
    }

    @Override
    public NotificationMessageModel fromMap(Map<String, Object> arguments) {

        title   = getValueOrDefault(arguments, Definitions.NOTIFICATION_TITLE, String.class);
        message = getValueOrDefault(arguments, Definitions.NOTIFICATION_MESSAGES, String.class);
        largeIcon = getValueOrDefault(arguments, Definitions.NOTIFICATION_LARGE_ICON, String.class);
        timestamp = getValueOrDefault(arguments, Definitions.NOTIFICATION_TIMESTAMP, Long.class);

        return this;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> returnedObject = new HashMap<>();

        returnedObject.put(Definitions.NOTIFICATION_TITLE, title);
        returnedObject.put(Definitions.NOTIFICATION_MESSAGES, message);
        returnedObject.put(Definitions.NOTIFICATION_LARGE_ICON, largeIcon);
        returnedObject.put(Definitions.NOTIFICATION_TIMESTAMP, timestamp);

        return returnedObject;
    }

    @Override
    public String toJson() {
        return templateToJson();
    }

    @Override
    public NotificationButtonModel fromJson(String json){
        return (NotificationButtonModel) super.templateFromJson(json);
    }

    @Override
    public void validate(Context context) throws AwesomeNotificationsException {
    }

}
