package me.carda.awesome_notifications.awesome_notifications_core.models;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import me.carda.awesome_notifications.awesome_notifications_core.Definitions;
import me.carda.awesome_notifications.awesome_notifications_core.TestUtils;
import me.carda.awesome_notifications.awesome_notifications_core.exceptions.AwesomeNotificationsException;

import static org.junit.Assert.*;

public class NotificationIntervalModelTest {

    @Before
    public void setUp() throws Exception {
        AbstractModel
                .defaultValues
                .putAll(Definitions.initialValues);
    }

    @After
    public void tearDown() throws Exception {
        Definitions
                .initialValues
                .clear();
    }

    @Test
    public void fromJsonAndToJson() {
        NotificationIntervalModel intervalModel = new NotificationIntervalModel();

        TestUtils.testModelField(intervalModel, Definitions.NOTIFICATION_SCHEDULE_INTERVAL);
    }

    @Test
    public void validate() throws AwesomeNotificationsException {
        NotificationIntervalModel intervalModel = new NotificationIntervalModel();

        assertThrows("Must throw exception case the required files are empty",
                AwesomeNotificationsException.class,() -> {
                    intervalModel.validate(null);
                });

        intervalModel.repeats = false;
        TestUtils.testValidateDateComponentField(intervalModel, Definitions.NOTIFICATION_SCHEDULE_INTERVAL, 0, null);

        intervalModel.repeats = true;
        TestUtils.testValidateDateComponentField(intervalModel, Definitions.NOTIFICATION_SCHEDULE_INTERVAL, 60, null);
    }

    @Test
    public void getNextValidDate() {
    }
}