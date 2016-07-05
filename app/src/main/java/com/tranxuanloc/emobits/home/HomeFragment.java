package com.tranxuanloc.emobits.home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tranxuanloc.emobits.R;
import com.tranxuanloc.emobits.utilities.Utilities;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by tranxuanloc on 4/6/2016.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = HomeFragment.class.getSimpleName();
    @Bind(R.id.tv_home_emotion1)
    TextView tvEmotion1;
    @Bind(R.id.tv_home_emotion2)
    TextView tvEmotion2;
    @Bind(R.id.tv_home_emotion3)
    TextView tvEmotion3;
    @Bind(R.id.tv_home_emotion4)
    TextView tvEmotion4;
    @Bind(R.id.tv_home_emotion5)
    TextView tvEmotion5;
    @Bind(R.id.tv_home_emotion6)
    TextView tvEmotion6;
    @Bind(R.id.tv_home_emotion7)
    TextView tvEmotion7;
    private Utilities util = new Utilities();

    public static HomeFragment newInstance(int sectionNumber) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, rootView);
        RelativeLayout rlMain = (RelativeLayout) rootView.findViewById(R.id.rl_main_main);
        rlMain.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, util.getWidthPixel(getContext())));
        tvEmotion1.setOnClickListener(this);
        tvEmotion2.setOnClickListener(this);
        tvEmotion3.setOnClickListener(this);
        tvEmotion4.setOnClickListener(this);
        tvEmotion5.setOnClickListener(this);
        tvEmotion6.setOnClickListener(this);
        tvEmotion7.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {

    }
}
