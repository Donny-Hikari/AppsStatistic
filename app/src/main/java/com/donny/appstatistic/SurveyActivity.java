package com.donny.appstatistic;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class SurveyActivity extends AppCompatActivity implements CommonFunction.FragmentListener {

    private TextView tvSchedule;
    private IntroductionFragment introFragm = new IntroductionFragment();
    private QuestionFragment questFragm = new QuestionFragment();
    private int nProcess = 0;

    private int[] selections = new int[21];

    private String[] questionsTable = new String[21];

    public void LoadQuestionnaire() {
        InputStream input = getResources().openRawResource(R.raw.questionnaire);
        Scanner indata = new Scanner(input, "UTF-8");
        for (int i = 0; i < 21 && indata.hasNext(); ++i) {
            questionsTable[i] = indata.nextLine();
            Log.d("Survey", questionsTable[i]);
        }
        try {
            indata.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoadQuestionnaire();
        setContentView(R.layout.activity_survey);

        tvSchedule = (TextView) findViewById(R.id.survey_schedule);
        getSupportFragmentManager().beginTransaction().replace(R.id.id_questionnaire, introFragm).commit();
    }

    /*
    * Fragment Listener method
    */

    @Override
    public void movetoFirstQuestion() {
        getSupportFragmentManager().beginTransaction().replace(R.id.id_questionnaire, questFragm).commit();
        tvSchedule.setText(String.format("%d/21", ++nProcess));
        tvSchedule.setVisibility(TextView.VISIBLE);
    }

    private Handler handlerNext = new Handler();

    @Override
    public void movetoNextQuestion(int selectedIndex) {
        selections[nProcess - 1] = 3 - selectedIndex;
        if (nProcess >= 21) {
            CommonFunction.SaveSurveyData(this, selections);
            CommonFunction.SetSurveyRecorded(this);
            new AlertDialog.Builder(this).setTitle("Survey").setMessage("恭喜您完成问卷！").
                    setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SurveyActivity.this.finish();
                        }
                    }).setCancelable(false).show();
        } else {
            ++nProcess;
            handlerNext.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (nProcess > 21) return;
                    questFragm.clearSelection();
                    questFragm.setQuestion(questionsTable[nProcess - 1]);
                    tvSchedule.setText(String.format("%d/21", nProcess - 1));
                }
            }, 400);
        }
    }

    @Override
    public String getFirstQuestion() {
        if (questionsTable == null) return null;
        return questionsTable[0];
    }
}
