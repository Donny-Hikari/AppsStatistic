package com.donny.appstatistic;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Donny on 10/11/2016.
 */
public class AboutActivity extends AppCompatActivity {

    private TextView tvUserID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        tvUserID = (TextView) findViewById(R.id.about_userId);
        tvUserID.setText(CommonFunction.getUserInfo(this));

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                AboutActivity.this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
