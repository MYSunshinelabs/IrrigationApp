package com.ve.irrigation.irrigation.activities;

import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.ve.irrigation.datavalues.ConfigurationDDGET;
import com.ve.irrigation.datavalues.MySharedPreferences;
import com.ve.irrigation.customview.CustomTextView;
import com.ve.irrigation.irrigation.MakeHttpRequest;
import com.ve.irrigation.irrigation.R;
import com.ve.irrigation.utils.Constants;

import java.io.IOException;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SwipeLeftActivity extends BaseActivity {

    private GestureDetectorCompat mDetector;
    private ConfigurationDDGET configurationDDGET;

    CustomTextView mTextViewTarget, mTextViewPlanned, mTextViewActual, mTextViewStart1, mTextViewEnd1, mTextViewStart2, mTextViewEnd2, mTextViewStart3, mTextViewEnd3, mTextViewStart4, mTextViewEnd4, mTextViewVol1, mTextViewVol2, mTextViewVol3, mTextViewVol4;
    ImageView mImageView1, mImageView2, mImageView3, mImageView4;

    boolean mbool1, mbool2, mbool3, mbool4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_left);
        mDetector = new GestureDetectorCompat(this, new SwipeLeftActivity.MyGestureListner());
        initViews();
        initListener();
        try {
            makeDDGET();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void initListener() {

        mImageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mbool1) {
                    mImageView1.setImageResource(R.mipmap.mode_on);

                } else {
                    mImageView1.setImageResource(R.mipmap.mode_off);

                }
                mbool1 = !mbool1;
            }
        });


        mImageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mbool2) {
                    mImageView2.setImageResource(R.mipmap.mode_on);

                } else {
                    mImageView2.setImageResource(R.mipmap.mode_off);

                }
                mbool2 = !mbool2;
            }
        });

        mImageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mbool3) {
                    mImageView3.setImageResource(R.mipmap.mode_on);

                } else {
                    mImageView3.setImageResource(R.mipmap.mode_off);

                }
                mbool3 = !mbool3;
            }
        });


        mImageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mbool4) {
                    mImageView4.setImageResource(R.mipmap.mode_on);

                } else {
                    mImageView4.setImageResource(R.mipmap.mode_off);

                }
                mbool4 = !mbool4;
            }
        });


    }


    private void initViews() {

        mTextViewTarget =  findViewById(R.id.text_watertarget);
        mTextViewPlanned =  findViewById(R.id.text_waterplanned);
        mTextViewActual =  findViewById(R.id.text_wateractual);

        mTextViewStart1 =  findViewById(R.id.textview_start1);
        mTextViewStart2 =  findViewById(R.id.textview_start2);
        mTextViewStart3 =  findViewById(R.id.textview_start3);
        mTextViewStart4 =  findViewById(R.id.textview_start4);

        mTextViewEnd1 =  findViewById(R.id.textview_end1);
        mTextViewEnd2 =  findViewById(R.id.textview_end2);
        mTextViewEnd3 =  findViewById(R.id.textview_end3);
        mTextViewEnd4 =  findViewById(R.id.textview_end4);


        mImageView1 =  findViewById(R.id.img_pump1);
        mImageView2 = findViewById(R.id.img_pump2);
        mImageView3 = findViewById(R.id.img_pump3);
        mImageView4 = findViewById(R.id.img_pump4);


        mTextViewVol1 =  findViewById(R.id.textview_vol1);
        mTextViewVol2 =  findViewById(R.id.textview_vol2);
        mTextViewVol3 =  findViewById(R.id.textview_vol3);
        mTextViewVol4 =  findViewById(R.id.textview_vol4);


    }


    private void makeDDGET() throws IOException {
        MakeHttpRequest.getMakeHttpRequest().sendRequest(Constants.Url.BASE_URL+"ddget/0").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).onErrorResumeNext(new Func1<Throwable, Observable<? extends String>>() {
            @Override
            public Observable<? extends String> call(Throwable throwable) {
                return null;
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.i("DDGET res: ", s);
                if (s != null && s.length() > 0) {
                    configurationDDGET = new Gson().fromJson(s, ConfigurationDDGET.class);
                    setValuesFromDDGET(configurationDDGET);
                }
            }
        });
    }


    private void setValuesFromDDGET(ConfigurationDDGET configurationDDGET) {
        mTextViewTarget.setText(configurationDDGET.getRqdvol());
        mTextViewPlanned.setText(configurationDDGET.getV1vol());
        mTextViewActual.setText(configurationDDGET.getTtvol());


        mTextViewStart1.setText((MySharedPreferences.getMySharedPreferences().convertSecondtoTime(Integer.parseInt(configurationDDGET.getP1start()))));
        mTextViewEnd1.setText((MySharedPreferences.getMySharedPreferences().convertSecondtoTime(Integer.parseInt(configurationDDGET.getP1stop()))));
        mTextViewVol1.setText(configurationDDGET.getP1vol());

        mTextViewStart2.setText(MySharedPreferences.getMySharedPreferences().convertSecondtoTime(Integer.parseInt(configurationDDGET.getP2start())));
        mTextViewEnd2.setText((MySharedPreferences.getMySharedPreferences().convertSecondtoTime(Integer.parseInt(configurationDDGET.getP2stop()))));
        mTextViewVol2.setText(configurationDDGET.getP2vol());


        mTextViewStart3.setText((MySharedPreferences.getMySharedPreferences().convertSecondtoTime(Integer.parseInt(configurationDDGET.getP3start()))));
        mTextViewEnd3.setText((MySharedPreferences.getMySharedPreferences().convertSecondtoTime(Integer.parseInt(configurationDDGET.getP3stop()))));
        mTextViewVol3.setText(configurationDDGET.getP3vol());

        mTextViewStart4.setText((MySharedPreferences.getMySharedPreferences().convertSecondtoTime(Integer.parseInt(configurationDDGET.getP4start()))));
        mTextViewEnd4.setText((MySharedPreferences.getMySharedPreferences().convertSecondtoTime(Integer.parseInt(configurationDDGET.getP4stop()))));
        mTextViewVol4.setText(configurationDDGET.getP4vol());


    }


    public class MyGestureListner extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return super.onDown(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2,float velocityX, float velocityY) {
            switch (getSlope(e1.getX(), e1.getY(), e2.getX(), e2.getY())) {
                case 1:
                    return true;
                case 2:
                    return true;
                case 3:
                    return true;
                case 4:
                    finish();
                    SwipeLeftActivity.this.overridePendingTransition(R.anim.slide_left2, R.anim.slide_right2);
                    return true;
            }
            return false;
        }

        private int getSlope(float x1, float y1, float x2, float y2) {
            Double angle = Math.toDegrees(Math.atan2(y1 - y2, x2 - x1));
            if (angle > 45 && angle <= 135)
                // top
                return 1;
            if (angle >= 135 && angle < 180 || angle < -135 && angle > -180)
                // left
                return 2;
            if (angle < -45 && angle >= -135)
                // down
                return 3;
            if (angle > -45 && angle <= 45)
                // right
                return 4;
            return 0;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        SwipeLeftActivity.this.overridePendingTransition(R.anim.slide_left2, R.anim.slide_right2);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
