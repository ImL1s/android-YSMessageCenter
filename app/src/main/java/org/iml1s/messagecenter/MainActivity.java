package org.iml1s.messagecenter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.widget.Toast;

import com.jsontech.inc.MessageCenter;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        testPostingMsgCenter();
//        testBackgroundMsgCenter();

        testStickyMsgCenter();

    }

    private void testStickyMsgCenter() {
        MessageCenter.init(getApplicationContext(), MessageCenter.PollingMode.BACKGROUND);
        MessageCenter.getInstance().sendEmptySticky("onclick");
//        MessageCenter.getInstance().clearStickyEvent();
    }

    private void testPostingMsgCenter() {
        MessageCenter.init(getApplicationContext());

//        MessageCenter.getInstance().addListener("onclick", new MessageCenter.MessageEventListener() {
//            @Override
//            public void onEvent(MessageCenter.Message msg) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(MainActivity.this, "onclick", Toast.LENGTH_LONG).show();
//                    }
//                });
//            }
//        });

        MessageCenter.getInstance().addListener("onclick", new MessageCenter.MainThreadEventListener() {
            @Override
            public void onEvent(MessageCenter.Message msg) {
                Toast.makeText(MainActivity.this, "onclick on " + Thread.currentThread().toString(), Toast.LENGTH_LONG).show();
            }
        });

//        MessageCenter.getInstance().addListener("onclick", new MessageCenter.NewThreadEventListener() {
//            @Override
//            public void onEvent(MessageCenter.Message msg) {
//                Toast.makeText(MainActivity.this, "onclick on " + Thread.currentThread().toString(), Toast.LENGTH_LONG).show();
//            }
//        });
    }

    private void testBackgroundMsgCenter() {
        MessageCenter.init(getApplicationContext(), MessageCenter.PollingMode.BACKGROUND);

        MessageCenter.getInstance().addListener("onclick", new MessageCenter.MessageEventListener() {
            @Override
            public void onEvent(MessageCenter.Message msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "onclick", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            /** testBackgroundMsgCenter & testPostingMsgCenter */
//            MessageCenter.getInstance().sendEmpty("onclick");

            /** testStickyMsgCenter */
            MessageCenter.getInstance().addListener("onclick", new MessageCenter.MainThreadEventListener() {
                @Override
                public void onEvent(MessageCenter.Message msg) {
                    Toast.makeText(MainActivity.this, "onclick", Toast.LENGTH_LONG).show();
                }
            }, true);

            return true;
        } else
            return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {


        super.onDestroy();
    }
}
