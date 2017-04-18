package org.iml1s.messagecenter.wrapper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import org.iml1s.messagecenter.R;

public class WrapperActivity extends AppCompatActivity {

    private OrangeMessageCenter messageCenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrapper);

        messageCenter = (OrangeMessageCenter) ManagerFactory.getMessageManager(getApplicationContext());


        messageCenter.addListener("onclick", new IMessageManager.MainThreadEventListener() {
            @Override
            public void onEvent(IMessageManager.Message msg) {
                Toast.makeText(getApplicationContext(), "Onclick", Toast.LENGTH_LONG).show();
                Log.d("debug", "hello this thread is:" + Thread.currentThread().toString());
            }
        });


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            messageCenter.sendEmpty("onclick");
            return true;
        }
        return super.onTouchEvent(event);
    }
}
