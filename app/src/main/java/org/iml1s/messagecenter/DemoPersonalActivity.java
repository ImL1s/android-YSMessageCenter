package org.iml1s.messagecenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.jsontech.inc.MessageCenter;

public class DemoPersonalActivity extends AppCompatActivity implements MessageCenter.MainThreadEventListener {

    public static final String MSG_ON_CHANGE_NAME = "onChangeName";
    private TextView tv_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_personal);
        tv_name = (TextView) findViewById(R.id.tv_name);

        MessageCenter.getInstance().addListener(MSG_ON_CHANGE_NAME, this);
    }

    public void onClick(View view) {
        startActivity(new Intent(DemoPersonalActivity.this, DemoPersonalNameActivity.class));
    }

    @Override
    public void onEvent(MessageCenter.Message msg) {
        String name = msg.getContent(String.class);
        tv_name.setText(name);
    }

    @Override
    protected void onDestroy() {
        MessageCenter.getInstance().removeListener("onChangeName", this);
        super.onDestroy();
    }
}
