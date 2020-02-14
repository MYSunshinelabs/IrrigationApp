package com.ve.irrigation.irrigation;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.ve.irrigation.datavalues.HeartBeat;
import com.ve.irrigation.datavalues.MySharedPreferences;
import com.ve.irrigation.utils.Constants;

import java.io.IOException;
import java.util.Calendar;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class DetailedModeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    NumberPicker mTarget;
    NumberPicker mActual;


    View mainView;
    Button mButton;
    boolean mButtonState;
    long mFlingTime;
    Handler mHandler;
    Runnable mRunnable;

    TextView mTextTarget, mTextActual;


    public DetailedModeFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static DetailedModeFragment newInstance(String param1, String param2) {
        DetailedModeFragment fragment = new DetailedModeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_detailed_mode, container, false);
        initViews();
        return mainView;
    }


    private void initViews() {

        mTextTarget = (TextView) mainView.findViewById(R.id.text_detail_target);
        mTextActual = (TextView) mainView.findViewById(R.id.text_detail_actual);

        mTarget = (NumberPicker) mainView.findViewById(R.id.np_detailed_target);
        mActual = (NumberPicker) mainView.findViewById(R.id.np_detailed_actual);
        mButton = (Button) mainView.findViewById(R.id.btn_detailed_startstop);

        mTarget.setMinValue(1);
        mActual.setMinValue(1);

        mTarget.setMaxValue(10000);
        mActual.setMaxValue(10000);


        mTarget.setValue(3000);
        mActual.setValue(4000);

        mTarget.setWrapSelectorWheel(true);
        mActual.setWrapSelectorWheel(true);

        mTarget.setOnValueChangedListener(onValueChangeListenerTarget);
        mActual.setOnValueChangedListener(onValueChangeListenerActual);

        mTarget.setOnScrollListener(onScrollListenerTarget);
        mActual.setOnScrollListener(onScrollListenerActual);

        mButton.setOnClickListener(onClickListener);


        mTextTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTarget.setVisibility(View.VISIBLE);
            }
        });

        mTextActual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mActual.setVisibility(View.VISIBLE);
            }
        });


    }


    public  void setValueFromHeartBeat(HeartBeat heartBeat) {
        if(mTextTarget!=null) {
            mTextTarget.setText("" + heartBeat.getMetric().getRvol() + "L");
            mTarget.setValue(Integer.parseInt(heartBeat.getMetric().getRvol()));
            mTextActual.setText("" + heartBeat.getMetric().getAvol() + "L");
        }

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    NumberPicker.OnValueChangeListener onValueChangeListenerTarget =
            new NumberPicker.OnValueChangeListener() {

                @Override
                public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                    mTarget.setValue(i1);
                }
            };

    NumberPicker.OnValueChangeListener onValueChangeListenerActual =
            new NumberPicker.OnValueChangeListener() {

                @Override
                public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                    mActual.setValue(i1);
                }
            };


    NumberPicker.OnScrollListener onScrollListenerTarget = new NumberPicker.OnScrollListener() {
        @Override
        public void onScrollStateChange(NumberPicker numberPicker, final int i) {


            if (i == SCROLL_STATE_IDLE) {

                mHandler = new Handler();
                mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (Calendar.getInstance().getTimeInMillis() - mFlingTime > 1000) {

                            mTarget.setVisibility(View.GONE);
                            mTextTarget.setText("" + mTarget.getValue() + "L");
                            MySharedPreferences.getMySharedPreferences().saveWaterTarget(getActivity(), mTarget.getValue());
                            try {
                                saveWaterTarget(mTarget.getValue());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else {
                            mHandler.removeCallbacks(mRunnable);
                        }
                    }
                };

                mHandler.postDelayed(mRunnable, 1000);

            } else if (i == SCROLL_STATE_TOUCH_SCROLL) {

                mFlingTime = Calendar.getInstance().getTimeInMillis();

            }
        }
    };


    NumberPicker.OnScrollListener onScrollListenerActual = new NumberPicker.OnScrollListener() {
        @Override
        public void onScrollStateChange(NumberPicker numberPicker, final int i) {


            if (i == SCROLL_STATE_IDLE) {

                mHandler = new Handler();
                mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (Calendar.getInstance().getTimeInMillis() - mFlingTime > 1000) {

                            mActual.setVisibility(View.GONE);
                            mTextActual.setText("" + mActual.getValue() + "L");
                            MySharedPreferences.getMySharedPreferences().saveWaterActual(getActivity(), mActual.getValue());
                        } else {
                            mHandler.removeCallbacks(mRunnable);
                        }
                    }
                };

                mHandler.postDelayed(mRunnable, 1000);

            } else if (i == SCROLL_STATE_TOUCH_SCROLL) {

                mFlingTime = Calendar.getInstance().getTimeInMillis();

            }
        }
    };


    Button.OnClickListener onClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!mButtonState) {
                mButton.setText("Stop");
            } else {
                mButton.setText("Start");
            }
            mButtonState = !mButtonState;
        }
    };


    private void saveWaterTarget(int val) throws IOException {


        MakeHttpRequest.getMakeHttpRequest().sendRequest(Constants.Url.BASE_URL+"rqdt/0/" + val).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.i("Fragment res: ", s);
            }
        });
    }
}
