package com.donny.appstatistic;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class IntroductionFragment extends Fragment {

    private RelativeLayout rootView;
    private Button btnStart;

    public IntroductionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = (RelativeLayout) inflater.inflate(R.layout.fragment_introduction, null);
            btnStart = (Button)rootView.findViewById(R.id.btn_start);
            btnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentActivity activity = getActivity();
                    if (activity instanceof CommonFunction.FragmentListener) {
                        ((CommonFunction.FragmentListener) activity).movetoFirstQuestion();
                    }
                }
            });
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    }

}
