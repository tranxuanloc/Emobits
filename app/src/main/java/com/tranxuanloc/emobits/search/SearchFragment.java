package com.tranxuanloc.emobits.search;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tranxuanloc.emobits.R;
import com.tranxuanloc.emobits.main.RecyclerTouchListener;
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
public class SearchFragment extends Fragment {
    private static final String TAG = SearchFragment.class.getSimpleName();
    private static final String ARG_SECTION_NUMBER = "section_number";
    @Bind(R.id.recyclerViewNewSession)
    RecyclerView recyclerViewNewSession;
    @Bind(R.id.recyclerViewBestSession)
    RecyclerView recyclerViewBestSession;
    @Bind(R.id.nsv_content_search)
    NestedScrollView contentSearch;
    @Bind(R.id.progress_bar)
    ProgressBar bar;
    @Bind(R.id.tv_error)
    TextView tvError;
    @Bind(R.id.content_load)
    RelativeLayout contentLoad;
    @Bind(R.id.labelSlogan)
    TextView tvSlogan;
    @Bind(R.id.ivBanner)
    ImageView ivBanner;
    private Context context;
    private Utilities util = new Utilities();
    private ArrayList<SessionInfo.ListSessionInfo> arrayList = new ArrayList<>();
    private SessionAdapter adapterBest, adapterNew;
    private View.OnClickListener action;
    private boolean isVisibleToUser, isCreatedView;
    private OnSessionSelectedListener mListener;

    public static SearchFragment newInstance(int sectionNumber) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnSessionSelectedListener) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException(context.toString() + " must implement OnSessionSelectedListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        context = getContext();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        isCreatedView = true;
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, rootView);
        action = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getListSession();
            }
        };
        tvSlogan.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, (util.getWidthPixel(context) / 2 - tvSlogan.getHeight()) / 2, 0, 0);
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                tvSlogan.setLayoutParams(params);
            }
        });
        ivBanner.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, util.getWidthPixel(context) / 2));

        LinearLayoutManager newSessionLayoutManager = new LinearLayoutManager(getContext());
        newSessionLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewNewSession.setHasFixedSize(false);
        recyclerViewNewSession.setLayoutManager(newSessionLayoutManager);
        adapterNew = new SessionAdapter(context, arrayList);
        recyclerViewNewSession.setAdapter(adapterNew);
        recyclerViewNewSession.addOnItemTouchListener(new RecyclerTouchListener(context, recyclerViewNewSession, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                mListener.onSessionSelected(arrayList.get(position));
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        LinearLayoutManager bestSessionLayoutManager = new LinearLayoutManager(getContext());
        bestSessionLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewBestSession.setHasFixedSize(false);
        recyclerViewBestSession.setLayoutManager(bestSessionLayoutManager);
        adapterBest = new SessionAdapter(context, arrayList);
        recyclerViewBestSession.setAdapter(adapterBest);
        recyclerViewBestSession.addOnItemTouchListener(new RecyclerTouchListener(context, recyclerViewBestSession, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                mListener.onSessionSelected(arrayList.get(position));
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
       // getListSession();
        arrayList.clear();
        arrayList.addAll(new SessionInfo().getData());
        adapterBest.notifyDataSetChanged();
        adapterNew.notifyDataSetChanged();
        updateUI(true);
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
       /* this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser && isCreatedView)
            getListSession();*/
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /*if (isVisibleToUser)
            getListSession();*/
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        isCreatedView = false;
    }

    private void getListSession() {
        updateUI(false);
        showLoad();
        if (!Utilities.isConnected(getContext()))
            updateUIError(getString(R.string.no_internet), new NoInternet());
        else
            MyRetrofit.initRequest(context).getListSession(util.getAndroidID(context)).enqueue(new Callback<SessionInfo>() {
                @Override
                public void onResponse(Response<SessionInfo> response, Retrofit retrofit) {
                    Log.d(TAG, "onResponse: " + new Gson().toJson(response.body()));
                    if (response.isSuccess() && response.body() != null) {
                        SessionInfo info = response.body();
                        if (info.getStatus() == 1) {
                            arrayList.clear();
                            arrayList.addAll(info.getData());
                            adapterBest.notifyDataSetChanged();
                            adapterNew.notifyDataSetChanged();
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
            contentSearch.setVisibility(View.VISIBLE);
        } else {
            contentLoad.setVisibility(View.VISIBLE);
            contentSearch.setVisibility(View.GONE);
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


    public interface OnSessionSelectedListener {
        void onSessionSelected(SessionInfo.ListSessionInfo info);
    }
}
