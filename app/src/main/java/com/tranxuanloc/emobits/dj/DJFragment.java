package com.tranxuanloc.emobits.dj;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tranxuanloc.emobits.R;
import com.tranxuanloc.emobits.retrofit.MyRetrofit;
import com.tranxuanloc.emobits.retrofit.NoInternet;
import com.tranxuanloc.emobits.retrofit.RetrofitError;
import com.tranxuanloc.emobits.utilities.Utilities;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by tranxuanloc on 4/6/2016.
 */
public class DJFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = DJFragment.class.getSimpleName();
    @Bind(R.id.listView)
    ListView listView;
    @Bind(R.id.dj_line_bottom)
    View vLineBottom;
    @Bind(R.id.rl_content_dj)
    RelativeLayout contentDJ;
    @Bind(R.id.progress_bar)
    ProgressBar bar;
    @Bind(R.id.tv_error)
    TextView tvError;
    @Bind(R.id.content_load)
    RelativeLayout contentLoad;
    private DJAdapter adapter;
    private View.OnClickListener action;
    private Context context;
    private Utilities util = new Utilities();
    private boolean isVisibleToUser, isCreatedView;

    public static DJFragment newInstance(int sectionNumber) {
        DJFragment fragment = new DJFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        isCreatedView = true;
        View rootView = inflater.inflate(R.layout.fragment_dj, container, false);
        ButterKnife.bind(this, rootView);
        action = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getDJScore();
            }
        };
        adapter = new DJAdapter(getContext(), new ArrayList<DJInfo.ListDJInfo>());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.post(new Runnable() {
            @Override
            public void run() {
                int lastVisiblePosition = listView.getLastVisiblePosition();
                int firstVisiblePosition = listView.getFirstVisiblePosition();
                int numberItemVisible = lastVisiblePosition - firstVisiblePosition + 1;

                if (numberItemVisible < listView.getAdapter().getCount())
                    vLineBottom.setVisibility(View.GONE);
                else
                    vLineBottom.setVisibility(View.VISIBLE);
            }
        });
        //getDJScore();

        adapter.clear();
        adapter.addAll(new DJInfo().getData());
        updateUI(true);
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        /*this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser && isCreatedView)
            getDJScore();*/
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /*if (isVisibleToUser)
            getDJScore();*/
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // isCreatedView = false;
    }

    private void getDJScore() {
        Log.d(TAG, "getDJScore: ");
        updateUI(false);
        showLoad();
        if (!Utilities.isConnected(getContext()))
            updateUIError(getString(R.string.no_internet), new NoInternet());
        else
            MyRetrofit.initRequest(context).getDJScore(util.getAndroidID(context)).enqueue(new Callback<DJInfo>() {
                @Override
                public void onResponse(Response<DJInfo> response, Retrofit retrofit) {
                    Log.d(TAG, "onResponse: " + new Gson().toJson(response.body()));
                    if (response.isSuccess() && response.body() != null) {
                        DJInfo info = response.body();
                        if (info.getStatus() == 1) {
                            adapter.clear();
                            adapter.addAll(info.getData());
                            updateUI(true);
                        } else {
                            updateUI(false);
                            updateUIError(getString(R.string.error_system), new Throwable());
                        }
                    } else {
                        updateUI(false);
                        updateUIError(getString(R.string.error_system), new Throwable());
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    updateUI(false);
                    updateUIError(RetrofitError.getErrorMessage(context, t), t);
                }
            });

    }

    private void updateUI(boolean isSuccess) {
        if (isSuccess) {
            contentLoad.setVisibility(View.GONE);
            contentDJ.setVisibility(View.VISIBLE);
        } else {
            contentLoad.setVisibility(View.VISIBLE);
            contentDJ.setVisibility(View.GONE);
        }
    }

    private void updateUIError(String string, Throwable error) {
        assert bar != null;
        bar.setVisibility(View.INVISIBLE);
        assert tvError != null;
        tvError.setText(string);
        tvError.setVisibility(View.VISIBLE);
        RetrofitError.errorWithAction(context, error, TAG, bar, action);
    }

    private void showLoad() {
        assert bar != null;
        bar.setVisibility(View.VISIBLE);
        assert tvError != null;
        tvError.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
