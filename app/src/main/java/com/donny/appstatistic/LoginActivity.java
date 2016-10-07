package com.donny.appstatistic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static com.donny.appstatistic.CommonFunction.IsUserAlreadyLogin;
import static com.donny.appstatistic.CommonFunction.setUserInfo;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    Button btnConfirm;
    EditText etIDNum;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etIDNum = (EditText) findViewById(R.id.idnumber);
        btnConfirm = (Button) findViewById(R.id.confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();
                String sID = etIDNum.getText().toString();
                if (sID.isEmpty()) {
                    Toast.makeText(context, "Please enter the id number.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!IsUserAlreadyLogin(context)) {
                    setUserInfo(context, sID);
                }
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }
        });
    }


}

