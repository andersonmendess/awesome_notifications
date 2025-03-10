package me.carda.awesome_notifications.awesome_notifications_core.models;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import me.carda.awesome_notifications.awesome_notifications_core.Definitions;
import me.carda.awesome_notifications.awesome_notifications_core.enumerators.ActionType;
import me.carda.awesome_notifications.awesome_notifications_core.enumerators.MediaSource;
import me.carda.awesome_notifications.awesome_notifications_core.enumerators.NotificationCategory;
import me.carda.awesome_notifications.awesome_notifications_core.enumerators.NotificationLayout;
import me.carda.awesome_notifications.awesome_notifications_core.enumerators.NotificationLifeCycle;
import me.carda.awesome_notifications.awesome_notifications_core.enumerators.NotificationPrivacy;
import me.carda.awesome_notifications.awesome_notifications_core.enumerators.NotificationSource;
import me.carda.awesome_notifications.awesome_notifications_core.exceptions.AwesomeNotificationsException;
import me.carda.awesome_notifications.awesome_notifications_core.managers.ChannelManager;
import me.carda.awesome_notifications.awesome_notifications_core.utils.BitmapUtils;
import me.carda.awesome_notifications.awesome_notifications_core.utils.CalendarUtils;
import me.carda.awesome_notifications.awesome_notifications_core.utils.ListUtils;
import me.carda.awesome_notifications.awesome_notifications_core.utils.MapUtils;
import me.carda.awesome_notifications.awesome_notifications_core.utils.StringUtils;

@SuppressWarnings("unchecked")
public class NotificationContentModel extends AbstractModel {

    public boolean isRefreshNotification = false;
    public boolean isRandomId = false;

    public Integer id;
    public String channelKey;
    public String title;
    public String body;
    public String summary;
    public Boolean showWhen;
    public List<NotificationMessageModel> messages;
    public Map<String, String> payload;
    public String groupKey;
    public String customSound;
    public Boolean playSound;
    public String icon;
    public String largeIcon;
    public Boolean locked;
    public String bigPicture;
    public Boolean wakeUpScreen;
    public Boolean fullScreenIntent;
    public Boolean hideLargeIconOnExpand;
    public Boolean autoDismissible;
    public Boolean displayOnForeground;
    public Boolean displayOnBackground;
    public Long color;
    public Long backgroundColor;
    public Integer progress;
    public String ticker;

    public Boolean roundedLargeIcon;
    public Boolean roundedBigPicture;

    public ActionType actionType;

    public NotificationPrivacy privacy;
    public String privateMessage;

    public NotificationLayout notificationLayout;

    public NotificationSource createdSource;
    public NotificationLifeCycle createdLifeCycle;
    public Calendar createdDate;

    public NotificationLifeCycle displayedLifeCycle;
    public Calendar displayedDate;

    public NotificationCategory category;

    public NotificationContentModel(){
        super(StringUtils.getInstance());
    }

    public boolean registerCreatedEvent(NotificationLifeCycle lifeCycle, NotificationSource createdSource){

        // Creation register can only happen once
        if(this.createdSource == null){

            this.createdDate = CalendarUtils.getInstance().getCurrentCalendar();
            this.createdLifeCycle = lifeCycle;
            this.createdSource = createdSource;

            return true;
        }
        return false;
    }

    public boolean registerDisplayedEvent(NotificationLifeCycle lifeCycle){
        this.displayedDate = CalendarUtils.getInstance().getCurrentCalendar();
        this.displayedLifeCycle = lifeCycle;
        return true;
    }

    @Override
    public NotificationContentModel fromMap(Map<String, Object> arguments) {
        if(arguments == null || arguments.isEmpty())
            return null;

        processRetroCompatibility(arguments);

        id = getValueOrDefault(arguments, Definitions.NOTIFICATION_ID, Integer.class);

        actionType = getEnumValueOrDefault(arguments, Definitions.NOTIFICATION_ACTION_TYPE,
                ActionType.class, ActionType.values());

        createdDate = MapUtils.extractValue(arguments, Definitions.NOTIFICATION_CREATED_DATE, Calendar.class)
                            .orNull();

        displayedDate = MapUtils.extractValue(arguments, Definitions.NOTIFICATION_DISPLAYED_DATE, Calendar.class)
                            .orNull();

        createdLifeCycle = getEnumValueOrDefault(arguments, Definitions.NOTIFICATION_CREATED_LIFECYCLE,
                NotificationLifeCycle.class, NotificationLifeCycle.values());

        displayedLifeCycle = getEnumValueOrDefault(arguments, Definitions.NOTIFICATION_DISPLAYED_LIFECYCLE,
                NotificationLifeCycle.class, NotificationLifeCycle.values());

        createdSource = getEnumValueOrDefault(arguments, Definitions.NOTIFICATION_CREATED_SOURCE,
                NotificationSource.class, NotificationSource.values());

        channelKey = getValueOrDefault(arguments, Definitions.NOTIFICATION_CHANNEL_KEY, String.class);
        color = getValueOrDefault(arguments, Definitions.NOTIFICATION_COLOR, Long.class);
        backgroundColor = getValueOrDefault(arguments, Definitions.NOTIFICATION_BACKGROUND_COLOR, Long.class);


        title = getValueOrDefault(arguments, Definitions.NOTIFICATION_TITLE, String.class);
        body  = getValueOrDefault(arguments, Definitions.NOTIFICATION_BODY, String.class);
        summary = getValueOrDefault(arguments, Definitions.NOTIFICATION_SUMMARY, String.class);

        playSound = getValueOrDefault(arguments, Definitions.NOTIFICATION_PLAY_SOUND, Boolean.class);
        customSound = getValueOrDefault(arguments, Definitions.NOTIFICATION_CUSTOM_SOUND, String.class);

        wakeUpScreen = getValueOrDefault(arguments, Definitions.NOTIFICATION_WAKE_UP_SCREEN, Boolean.class);
        fullScreenIntent = getValueOrDefault(arguments, Definitions.NOTIFICATION_FULL_SCREEN_INTENT, Boolean.class);

        showWhen = getValueOrDefault(arguments, Definitions.NOTIFICATION_SHOW_WHEN, Boolean.class);
        locked = getValueOrDefault(arguments, Definitions.NOTIFICATION_LOCKED, Boolean.class);

        displayOnForeground = getValueOrDefault(arguments, Definitions.NOTIFICATION_DISPLAY_ON_FOREGROUND, Boolean.class);
        displayOnBackground = getValueOrDefault(arguments, Definitions.NOTIFICATION_DISPLAY_ON_BACKGROUND, Boolean.class);

        hideLargeIconOnExpand = getValueOrDefault(arguments, Definitions.NOTIFICATION_HIDE_LARGE_ICON_ON_EXPAND, Boolean.class);

        notificationLayout =
                getEnumValueOrDefault(arguments, Definitions.NOTIFICATION_LAYOUT, NotificationLayout.class, NotificationLayout.values());

        privacy =
                getEnumValueOrDefault(arguments, Definitions.NOTIFICATION_PRIVACY, NotificationPrivacy.class, NotificationPrivacy.values());

        category =
                getEnumValueOrDefault(arguments, Definitions.NOTIFICATION_CATEGORY, NotificationCategory.class, NotificationCategory.values());

        privateMessage = getValueOrDefault(arguments, Definitions.NOTIFICATION_PRIVATE_MESSAGE, String.class);

        icon  = getValueOrDefault(arguments, Definitions.NOTIFICATION_ICON, String.class);
        largeIcon  = getValueOrDefault(arguments, Definitions.NOTIFICATION_LARGE_ICON, String.class);
        bigPicture = getValueOrDefault(arguments, Definitions.NOTIFICATION_BIG_PICTURE, String.class);

        payload = getValueOrDefault(arguments, Definitions.NOTIFICATION_PAYLOAD, Map.class);

        autoDismissible = getValueOrDefault(arguments, Definitions.NOTIFICATION_AUTO_DISMISSIBLE, Boolean.class);

        progress    = getValueOrDefault(arguments, Definitions.NOTIFICATION_PROGRESS, Integer.class);

        groupKey = getValueOrDefault(arguments, Definitions.NOTIFICATION_GROUP_KEY, String.class);

        ticker = getValueOrDefault(arguments, Definitions.NOTIFICATION_TICKER, String.class);

        messages = mapToMessages(getValueOrDefault(arguments, Definitions.NOTIFICATION_MESSAGES, List.class));

        roundedLargeIcon = getValueOrDefault(arguments, Definitions.NOTIFICATION_ROUNDED_LARGE_ICON, Boolean.class);
        roundedBigPicture = getValueOrDefault(arguments, Definitions.NOTIFICATION_ROUNDED_BIG_PICTURE, Boolean.class);

        return this;
    }

    // Retro-compatibility with 0.6.X
    public void processRetroCompatibility(Map<String, Object> arguments){

        if (arguments.containsKey("autoCancel")) {
            Log.i("AwesomeNotifications", "autoCancel is deprecated. Please use autoDismissible instead.");
            autoDismissible   = getValueOrDefault(arguments, "autoCancel", Boolean.class);
        }
    }

    @Override
    public Map<String, Object> toMap(){
        Map<String, Object> returnedObject = new HashMap<>();

        CalendarUtils calendarUtils = CalendarUtils.getInstance();

        putDataOnMapObject(Definitions.NOTIFICATION_ID, returnedObject, this.id);
        putDataOnMapObject(Definitions.NOTIFICATION_ID, returnedObject, this.id);
        putDataOnMapObject(Definitions.NOTIFICATION_RANDOM_ID, returnedObject, this.isRandomId);
        putDataOnMapObject(Definitions.NOTIFICATION_TITLE, returnedObject, this.title);
        putDataOnMapObject(Definitions.NOTIFICATION_BODY, returnedObject, this.body);
        putDataOnMapObject(Definitions.NOTIFICATION_SUMMARY, returnedObject, this.summary);
        putDataOnMapObject(Definitions.NOTIFICATION_SHOW_WHEN, returnedObject, this.showWhen);
        putDataOnMapObject(Definitions.NOTIFICATION_WAKE_UP_SCREEN, returnedObject, this.wakeUpScreen);
        putDataOnMapObject(Definitions.NOTIFICATION_FULL_SCREEN_INTENT, returnedObject, this.fullScreenIntent);
        putDataOnMapObject(Definitions.NOTIFICATION_ACTION_TYPE, returnedObject, this.actionType);
        putDataOnMapObject(Definitions.NOTIFICATION_LOCKED, returnedObject, this.locked);
        putDataOnMapObject(Definitions.NOTIFICATION_PLAY_SOUND, returnedObject, this.playSound);
        putDataOnMapObject(Definitions.NOTIFICATION_CUSTOM_SOUND, returnedObject, this.customSound);
        putDataOnMapObject(Definitions.NOTIFICATION_TICKER, returnedObject, this.ticker);
        putDataOnMapObject(Definitions.NOTIFICATION_PAYLOAD, returnedObject, this.payload);
        putDataOnMapObject(Definitions.NOTIFICATION_AUTO_DISMISSIBLE, returnedObject, this.autoDismissible);
        putDataOnMapObject(Definitions.NOTIFICATION_LAYOUT, returnedObject, this.notificationLayout);
        putDataOnMapObject(Definitions.NOTIFICATION_CREATED_SOURCE, returnedObject, this.createdSource);
        putDataOnMapObject(Definitions.NOTIFICATION_CREATED_LIFECYCLE, returnedObject, this.createdLifeCycle);
        putDataOnMapObject(Definitions.NOTIFICATION_DISPLAYED_LIFECYCLE, returnedObject, this.displayedLifeCycle);
        putDataOnMapObject(Definitions.NOTIFICATION_DISPLAYED_DATE, returnedObject, calendarUtils.calendarToString(this.displayedDate));
        putDataOnMapObject(Definitions.NOTIFICATION_CREATED_DATE, returnedObject, calendarUtils.calendarToString(this.createdDate));
        putDataOnMapObject(Definitions.NOTIFICATION_CHANNEL_KEY, returnedObject, this.channelKey);
        putDataOnMapObject(Definitions.NOTIFICATION_CATEGORY, returnedObject, this.category);
        putDataOnMapObject(Definitions.NOTIFICATION_AUTO_DISMISSIBLE, returnedObject, this.autoDismissible);
        putDataOnMapObject(Definitions.NOTIFICATION_DISPLAY_ON_FOREGROUND, returnedObject, this.displayOnForeground);
        putDataOnMapObject(Definitions.NOTIFICATION_DISPLAY_ON_BACKGROUND, returnedObject, this.displayOnBackground);
        putDataOnMapObject(Definitions.NOTIFICATION_COLOR, returnedObject, this.color);
        putDataOnMapObject(Definitions.NOTIFICATION_BACKGROUND_COLOR, returnedObject, this.backgroundColor);
        putDataOnMapObject(Definitions.NOTIFICATION_ICON, returnedObject, this.icon);
        putDataOnMapObject(Definitions.NOTIFICATION_LARGE_ICON, returnedObject, this.largeIcon);
        putDataOnMapObject(Definitions.NOTIFICATION_BIG_PICTURE, returnedObject, this.bigPicture);
        putDataOnMapObject(Definitions.NOTIFICATION_PROGRESS, returnedObject, this.progress);
        putDataOnMapObject(Definitions.NOTIFICATION_GROUP_KEY, returnedObject, this.groupKey);
        putDataOnMapObject(Definitions.NOTIFICATION_PRIVACY, returnedObject, this.privacy);
        putDataOnMapObject(Definitions.NOTIFICATION_PRIVATE_MESSAGE, returnedObject, this.privateMessage);

        putDataOnMapObject(Definitions.NOTIFICATION_MESSAGES, returnedObject, messagesToMap(this.messages));

        putDataOnMapObject(Definitions.NOTIFICATION_ROUNDED_LARGE_ICON, returnedObject, this.roundedLargeIcon);
        putDataOnMapObject(Definitions.NOTIFICATION_ROUNDED_BIG_PICTURE, returnedObject, this.roundedBigPicture);

        return returnedObject;
    }

    public static List<Map> messagesToMap(List<NotificationMessageModel> messages){
        List<Map> returnedMessages = new ArrayList<>();
        if(!ListUtils.isNullOrEmpty(messages)){
            for (NotificationMessageModel messageModel : messages) {
                returnedMessages.add(messageModel.toMap());
            }
        }
        return returnedMessages;
    }

    public static List<NotificationMessageModel> mapToMessages(List<Map> messagesData){
        List<NotificationMessageModel> messages = new ArrayList<>();
        if(!ListUtils.isNullOrEmpty(messagesData))
            for(Map<String, Object> messageData : messagesData){
                NotificationMessageModel messageModel =
                        new NotificationMessageModel().fromMap(messageData);
                messages.add(messageModel);
            }
        return messages;
    }

    @Override
    public String toJson() {
        return templateToJson();
    }

    @Override
    public NotificationContentModel fromJson(String json){
        return (NotificationContentModel) super.templateFromJson(json);
    }

    @Override
    public void validate(Context context) throws AwesomeNotificationsException {

        if(ChannelManager
                .getInstance()
                .getChannelByKey(context, channelKey) == null)
            throw new AwesomeNotificationsException("Notification channel '"+channelKey+"' does not exist.");

        validateIcon(context);

        if (notificationLayout == NotificationLayout.BigPicture)
            validateBigPicture(context);

        validateLargeIcon(context);

    }

    private void validateIcon(Context context) throws AwesomeNotificationsException {

        if(!stringUtils.isNullOrEmpty(icon)){
            if(
                BitmapUtils.getInstance().getMediaSourceType(icon) != MediaSource.Resource ||
                !BitmapUtils.getInstance().isValidBitmap(context, icon)
            ){
                throw new AwesomeNotificationsException("Small icon ('"+icon+"') must be a valid media native resource type.");
            }
        }
    }

    private void validateBigPicture(Context context) throws AwesomeNotificationsException {
        if(
            (stringUtils.isNullOrEmpty(largeIcon) && stringUtils.isNullOrEmpty(bigPicture)) ||
            (!stringUtils.isNullOrEmpty(largeIcon) && !BitmapUtils.getInstance().isValidBitmap(context, largeIcon)) ||
            (!stringUtils.isNullOrEmpty(bigPicture) && !BitmapUtils.getInstance().isValidBitmap(context, bigPicture))
        ){
            throw new AwesomeNotificationsException("Invalid big picture '"+bigPicture+"' or large icon '"+largeIcon+"'");
        }
    }

    private void validateLargeIcon(Context context) throws AwesomeNotificationsException {
        if(
            (!stringUtils.isNullOrEmpty(largeIcon) && !BitmapUtils.getInstance().isValidBitmap(context, largeIcon))
        )
            throw new AwesomeNotificationsException("Invalid large icon '"+largeIcon+"'");
    }
}
