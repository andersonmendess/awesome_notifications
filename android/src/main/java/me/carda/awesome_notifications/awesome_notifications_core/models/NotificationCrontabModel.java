package me.carda.awesome_notifications.awesome_notifications_core.models;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.Nullable;

import me.carda.awesome_notifications.awesome_notifications_core.Definitions;
import me.carda.awesome_notifications.awesome_notifications_core.externalLibs.CronExpression;
import me.carda.awesome_notifications.awesome_notifications_core.exceptions.AwesomeNotificationsException;
import me.carda.awesome_notifications.awesome_notifications_core.utils.CalendarUtils;
import me.carda.awesome_notifications.awesome_notifications_core.utils.CronUtils;
import me.carda.awesome_notifications.awesome_notifications_core.utils.ListUtils;
import me.carda.awesome_notifications.awesome_notifications_core.utils.StringUtils;

public class NotificationCrontabModel extends NotificationScheduleModel {

    public Calendar initialDateTime;
    public Calendar expirationDateTime;
    public String crontabExpression;
    public List<Calendar> preciseSchedules;

    @Override
    @SuppressWarnings("unchecked")
    public NotificationCrontabModel fromMap(Map<String, Object> arguments) {
        super.fromMap(arguments);

        initialDateTime = getValueOrDefault(arguments, Definitions.NOTIFICATION_INITIAL_DATE_TIME, Calendar.class);
        expirationDateTime = getValueOrDefault(arguments, Definitions.NOTIFICATION_EXPIRATION_DATE_TIME, Calendar.class);
        crontabExpression = getValueOrDefault(arguments, Definitions.NOTIFICATION_CRONTAB_EXPRESSION, String.class);
        preciseSchedules = getValueOrDefault(arguments, Definitions.NOTIFICATION_PRECISE_SCHEDULES, List.class);

        return this;
    }

    @Override
    public Map<String, Object> toMap(){
        Map<String, Object> returnedObject = super.toMap();
        CalendarUtils calendarUtils = CalendarUtils.getInstance();

        returnedObject.put(Definitions.NOTIFICATION_INITIAL_DATE_TIME, calendarUtils.calendarToString(initialDateTime));
        returnedObject.put(Definitions.NOTIFICATION_EXPIRATION_DATE_TIME, calendarUtils.calendarToString(expirationDateTime));
        returnedObject.put(Definitions.NOTIFICATION_CRONTAB_EXPRESSION, crontabExpression);
        returnedObject.put(Definitions.NOTIFICATION_PRECISE_SCHEDULES, preciseSchedules);

        return returnedObject;
    }

    @Override
    public String toJson() {
        return templateToJson();
    }

    @Override
    public NotificationCalendarModel fromJson(String json){
        return (NotificationCalendarModel) super.templateFromJson(json);
    }

    @Override
    public void validate(Context context) throws AwesomeNotificationsException {

        if(
            stringUtils.isNullOrEmpty(crontabExpression) &&
            ListUtils.isNullOrEmpty(preciseSchedules)
        )
            throw new AwesomeNotificationsException("At least one schedule parameter is required");

        try {
            if(initialDateTime != null && expirationDateTime != null){
                if(
                    initialDateTime.equals(expirationDateTime) ||
                    initialDateTime.after(expirationDateTime)
                )
                    throw new AwesomeNotificationsException("Expiration date must be greater than initial date");
            }

            if(crontabExpression != null && !CronExpression.isValidExpression(crontabExpression))
                throw new AwesomeNotificationsException("Schedule cron expression is invalid");

        } catch (AwesomeNotificationsException e){
            throw e;
        } catch (Exception e){
            throw new AwesomeNotificationsException("Schedule time is invalid");
        }
    }

    @Override
    public Calendar getNextValidDate(@NonNull Calendar fixedNowDate) throws AwesomeNotificationsException {

        try {
            CalendarUtils calendarUtils = CalendarUtils.getInstance();

            if (fixedNowDate == null)
                fixedNowDate = calendarUtils.getCurrentCalendar(timeZone);

            if (
                expirationDateTime != null &&
                fixedNowDate.after(expirationDateTime) ||
                fixedNowDate.equals(expirationDateTime)
            ) return null;

            Calendar preciseCalendar = null, crontabCalendar = null;

            if (!ListUtils.isNullOrEmpty(preciseSchedules)){
                for (Calendar preciseSchedule : preciseSchedules) {
                    if(
                        initialDateTime != null &&
                        preciseSchedule.before(preciseSchedule)
                    )
                        continue;

                    if(preciseSchedule.before(fixedNowDate))
                        continue;

                    if(
                        preciseCalendar == null ||
                        preciseCalendar.after(preciseSchedule)
                    )
                        preciseCalendar = preciseSchedule;
                }
            }

            if(!stringUtils.isNullOrEmpty(crontabExpression))
                crontabCalendar = CronUtils
                        .getNextCalendar(
                                initialDateTime != null ?
                                        initialDateTime : fixedNowDate,
                                crontabExpression,
                                timeZone);

            if (preciseCalendar == null)
                return crontabCalendar;

            if (crontabCalendar == null)
                return preciseCalendar;

            if (preciseCalendar.before(crontabCalendar))
                return preciseCalendar;

            return crontabCalendar;

        } catch (AwesomeNotificationsException e){
            throw e;
        } catch (Exception e){
            throw new AwesomeNotificationsException("Schedule time is invalid");
        }
    }
}
