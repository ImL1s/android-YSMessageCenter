package org.iml1s.messagecenter.wrapper;

import android.content.Context;

/**
 * Created by ImL1s on 2017/4/18.
 * <p>
 * DESC:
 */

public class ManagerFactory {

    private static IMessageManager messageManager = null;

    public static IMessageManager getMessageManager(Context appContext) {
        if (messageManager == null) {
            messageManager = new OrangeMessageCenter();
            messageManager.init(appContext, IMessageManager.PollingMode.BACKGROUND);
        }

        return messageManager;
    }
}
