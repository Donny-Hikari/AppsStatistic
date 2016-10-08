package com.donny.appstatistic;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuestionFragment extends Fragment {

    private RelativeLayout rootView;
    private TextView tvQuestion;
    private RadioButton[] rbOptions = new RadioButton[4];

    private class OptionListener implements View.OnClickListener {

        private int nMyIndex = 0;

        public OptionListener(int index) {
            nMyIndex = index;
        }

        @Override
        public void onClick(View v) {
            FragmentActivity activity = getActivity();
            if (activity instanceof CommonFunction.FragmentListener) {
                ((CommonFunction.FragmentListener) activity).movetoNextQuestion(nMyIndex);
            }
        }

    }

    ;

    public QuestionFragment() {
        // Required empty public constructor
    }

    public boolean setQuestion(String sQuestion) {
        if (tvQuestion == null) return false;
        tvQuestion.setText(sQuestion);
        Log.d("Survey", "Question changed.");
        return true;
    }

    public boolean clearSelection() {
        for (int i = 0; i < 4; ++i) {
            if (rbOptions[i] != null)
                rbOptions[i].setChecked(false);
        }
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = (RelativeLayout) inflater.inflate(R.layout.fragment_question, null);
            tvQuestion = (TextView) rootView.findViewById(R.id.survey_question);
            rbOptions[0] = (RadioButton) rootView.findViewById(R.id.option1);
            rbOptions[1] = (RadioButton) rootView.findViewById(R.id.option2);
            rbOptions[2] = (RadioButton) rootView.findViewById(R.id.option3);
            rbOptions[3] = (RadioButton) rootView.findViewById(R.id.option4);
            for (int i = 0; i < 4; ++i) {
                rbOptions[i].setOnClickListener(new OptionListener(i));
            }

            FragmentActivity activity = getActivity();
            if (activity instanceof CommonFunction.FragmentListener) {
                setQuestion(((CommonFunction.FragmentListener) activity).getFirstQuestion());
            }
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    }

}
