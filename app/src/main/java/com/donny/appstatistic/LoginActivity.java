package com.donny.appstatistic;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
                if (etIDNum.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter the id number.", Toast.LENGTH_LONG).show();
                    return;
                }
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("请确认您的ID").setMessage(etIDNum.getText().toString())
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!IsUserAlreadyLogin(LoginActivity.this)) {
                                    setUserInfo(LoginActivity.this, etIDNum.getText().toString());
                                }
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                LoginActivity.this.finish();
                            }
                        })
                        .setNegativeButton("取消", null).show();
            }
        });
    }


}

