package org.iml1s.messagecenter;

import android.content.Context;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Toast;

import org.iml1s.messagecenter.wrapper.IMessageManager;
import org.iml1s.messagecenter.wrapper.ManagerFactory;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by ImL1s on 2017/4/19.
 * <p>
 * DESC:
 */

@RunWith(AndroidJUnit4.class)
public class MessageCenterTest {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);
    String value = "a";

    @Test
    public void testMessageCenter() {

        final Context context = InstrumentationRegistry.getContext();
        IMessageManager orangeMessageCenter = ManagerFactory.getMessageManager(context);

        orangeMessageCenter.addListener("onclick", new IMessageManager.MessageEventListener() {
            @Override
            public void onEvent(IMessageManager.Message msg) {
//                Toast.makeText(context,"onClick",Toast.LENGTH_LONG).show();
                value = "b";
            }
        });

        SystemClock.sleep(2000);

        orangeMessageCenter.sendEmpty("onclick");

        SystemClock.sleep(2000);

        Assert.assertEquals("b",value);
    }
}
