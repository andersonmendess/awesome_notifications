package me.carda.awesome_notifications.awesome_notifications_core.models;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import me.carda.awesome_notifications.awesome_notifications_core.Definitions;
import me.carda.awesome_notifications.awesome_notifications_core.enumerators.ActionType;
import me.carda.awesome_notifications.awesome_notifications_core.exceptions.AwesomeNotificationsException;
import me.carda.awesome_notifications.awesome_notifications_core.utils.StringUtils;

public class NotificationButtonModel extends AbstractModel {

    public String key;
    public String icon;
    public String label;
    public Integer color;
    public Boolean enabled;
    public Boolean requireInputText = false;
    public Boolean autoDismissible;
    public Boolean showInCompactView;
    public Boolean isDangerousOption;
    public ActionType actionType;

    public NotificationButtonModel(){
        super(StringUtils.getInstance());
    }

    @Override
    public NotificationButtonModel fromMap(Map<String, Object> arguments) {

        processRetroCompatibility(arguments);

        key        = getValueOrDefault(arguments, Definitions.NOTIFICATION_BUTTON_KEY, String.class);
        icon       = getValueOrDefault(arguments, Definitions.NOTIFICATION_BUTTON_ICON, String.class);
        label      = getValueOrDefault(arguments, Definitions.NOTIFICATION_BUTTON_LABEL, String.class);
        color      = getValueOrDefault(arguments, Definitions.NOTIFICATION_COLOR, Integer.class);

        actionType = getEnumValueOrDefault(arguments, Definitions.NOTIFICATION_ACTION_TYPE,
                ActionType.class, ActionType.values());

        enabled    = getValueOrDefault(arguments, Definitions.NOTIFICATION_ENABLED, Boolean.class);
        requireInputText  = getValueOrDefault(arguments, Definitions.NOTIFICATION_REQUIRE_INPUT_TEXT, Boolean.class);
        isDangerousOption = getValueOrDefault(arguments, Definitions.NOTIFICATION_IS_DANGEROUS_OPTION, Boolean.class);
        autoDismissible   = getValueOrDefault(arguments, Definitions.NOTIFICATION_AUTO_DISMISSIBLE, Boolean.class);
        showInCompactView = getValueOrDefault(arguments, Definitions.NOTIFICATION_SHOW_IN_COMPACT_VIEW, Boolean.class);

        return this;
    }

    // Retro-compatibility with 0.6.X
    private void processRetroCompatibility(Map<String, Object> arguments){

        if (arguments.containsKey("autoCancel")) {
            Log.d("AwesomeNotifications", "autoCancel is deprecated. Please use autoDismissible instead.");
            autoDismissible   = getValueOrDefault(arguments, "autoCancel", Boolean.class);
        }

        if (arguments.containsKey("buttonType")){
            Log.d("AwesomeNotifications", "buttonType is deprecated. Please use actionType instead.");
            actionType = getEnumValueOrDefault(arguments, "buttonType",
                    ActionType.class, ActionType.values());
        }

        adaptInputFieldToRequireText();
    }

    // Retro-compatibility with 0.6.X
    private void adaptInputFieldToRequireText(){
        if (actionType == ActionType.InputField) {
            Log.d("AwesomeNotifications", "InputField is deprecated. Please use requireInputText instead.");
            actionType = ActionType.SilentAction;
            requireInputText = true;
        }
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> returnedObject = new HashMap<>();

        returnedObject.put(Definitions.NOTIFICATION_BUTTON_KEY, key);
        returnedObject.put(Definitions.NOTIFICATION_BUTTON_ICON, icon);
        returnedObject.put(Definitions.NOTIFICATION_BUTTON_LABEL, label);
        returnedObject.put(Definitions.NOTIFICATION_COLOR, color);

        returnedObject.put(Definitions.NOTIFICATION_ACTION_TYPE,
                this.actionType != null ? this.actionType.toString() : ActionType.Default.toString());

        returnedObject.put(Definitions.NOTIFICATION_ENABLED, enabled);
        returnedObject.put(Definitions.NOTIFICATION_REQUIRE_INPUT_TEXT, requireInputText);
        returnedObject.put(Definitions.NOTIFICATION_AUTO_DISMISSIBLE, autoDismissible);
        returnedObject.put(Definitions.NOTIFICATION_SHOW_IN_COMPACT_VIEW, showInCompactView);
        returnedObject.put(Definitions.NOTIFICATION_IS_DANGEROUS_OPTION, isDangerousOption);

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
        if(stringUtils.isNullOrEmpty(key))
            throw new AwesomeNotificationsException("Button action key cannot be null or empty");

        if(stringUtils.isNullOrEmpty(label))
            throw new AwesomeNotificationsException("Button label cannot be null or empty");
    }
}
