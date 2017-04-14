package org.iml1s.messagecenter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class DemoPersonalNameActivity extends AppCompatActivity {

    private EditText ed_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_personal_name);
        ed_name = (EditText) findViewById(R.id.ed_name);

        findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageCenter.getInstance().send(DemoPersonalActivity.MSG_ON_CHANGE_NAME, this, ed_name.getText().toString(), null, null);
                finish();
            }
        });
    }
}
