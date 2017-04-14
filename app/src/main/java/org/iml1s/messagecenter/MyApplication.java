package org.iml1s.messagecenter;

import android.app.Application;


/**
 * Created by ImL1s on 2017/4/14.
 * <p>
 * DESC:
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MessageCenter.init(this, MessageCenter.PollingMode.BACKGROUND);
    }
}
