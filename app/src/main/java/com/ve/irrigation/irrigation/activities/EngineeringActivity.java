package com.ve.irrigation.irrigation.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.ve.irrigation.datavalues.AppDataBase;
import com.ve.irrigation.datavalues.ConfiguratonModel;
import com.ve.irrigation.datavalues.ConnectionSourceData;
import com.ve.irrigation.datavalues.EngineeringData;
import com.ve.irrigation.datavalues.MySharedPreferences;
import com.ve.irrigation.irrigation.ConfigurationContractor;
import com.ve.irrigation.irrigation.ConfigurationPresenter;
import com.ve.irrigation.irrigation.ConnectLocalHost;
import com.ve.irrigation.irrigation.MakeHttpRequest;
import com.ve.irrigation.irrigation.R;
import com.ve.irrigation.utils.Constants;

import java.io.IOException;
import java.net.URL;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class EngineeringActivity extends BaseActivity implements ConfigurationContractor.ConfiguratioView {

    private GestureDetectorCompat mDetector;
    Button mButtonSave, mButtonConnectLocalHost;
    Spinner mSpinner, mSpinnerType, mSpinnerConfigLink;
    private ImageView mDemoMode, mTestMode;
    private int mDemo, mTest, mDebug;
    Button mButtonSaveConnection, mButtonSaveConnectionFinal;
    LinearLayout mLinearLayoutConnectionMain;
    private Animation animationUp;
    private Animation animationDown;

    EditText mEditTextName, mEditTextSSID, getmEditTextURL, mEditTextPassword;
    LinearLayout mLayoutSSID, mLayoutURL, mLayoutMain2;
    boolean mBooleanConnectionType;
    ImageView mImgSync;
    ConfigurationPresenter mConfigurationPresenter;
    ProgressBar mProgressBar;
    Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engineering);
        mDetector = new GestureDetectorCompat(this, new EngineeringActivity.MyGestureListner());
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER, 0, 0);

        initViews();

        setSpinnerValues();

        initListener();

        registerSpinerListener();

        getEngineeringData();


    }


    private void setSpinnerValues() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.debug_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);

        ArrayAdapter<CharSequence> adaptertype = ArrayAdapter.createFromResource(this,
                R.array.debug_type, android.R.layout.simple_spinner_item);
        adaptertype.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerType.setAdapter(adaptertype);

        ArrayAdapter<CharSequence> adapterConfigLink = ArrayAdapter.createFromResource(this,
                R.array.debug_configLink, android.R.layout.simple_spinner_item);
        adapterConfigLink.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerConfigLink.setAdapter(adapterConfigLink);

    }

    private void initViews() {
        mButtonSave = (Button) findViewById(R.id.btn_save);
        mButtonConnectLocalHost = (Button) findViewById(R.id.btn_connectlocalhost);
        mDemoMode = (ImageView) findViewById(R.id.img_demo_mode);
        mTestMode = (ImageView) findViewById(R.id.img_test_mode);
        mSpinner = (Spinner) findViewById(R.id.spinner_debug);
        mSpinnerType = (Spinner) findViewById(R.id.spinner_typeofconnection);
        mSpinnerConfigLink = (Spinner) findViewById(R.id.spinner_downloadlink);

        mButtonSaveConnection = (Button) findViewById(R.id.btn_saveconnection);
        mButtonSaveConnectionFinal = (Button) findViewById(R.id.btn_saveconnectionfinal);
        mLinearLayoutConnectionMain = (LinearLayout) findViewById(R.id.layout_connectiont_main);
        mLayoutMain2 = (LinearLayout) findViewById(R.id.layout_main2);
        mLinearLayoutConnectionMain.setVisibility(View.GONE);

        animationUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up_collapse);
        animationDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down_expand);

        mEditTextName = (EditText) findViewById(R.id.edittext_name);
        mEditTextSSID = (EditText) findViewById(R.id.edittext_ssid);
        getmEditTextURL = (EditText) findViewById(R.id.edittext_url);
        mEditTextPassword = (EditText) findViewById(R.id.edittext_password);

        mLayoutSSID = (LinearLayout) findViewById(R.id.layout_connectiont_ssid);
        mLayoutURL = (LinearLayout) findViewById(R.id.layout_connectiont_url);

        mImgSync = (ImageView) findViewById(R.id.img_sync);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar_sync);
    }


    private void initListener() {
        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    permanentChange();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        mDemoMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                demoClickAction();
                engineeringTest();


            }
        });

        mTestMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                testClickAction();
                engineeringTest();


            }
        });


        mButtonSaveConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //makeVisibleInvisibleForConnection();

                Intent intent = new Intent(EngineeringActivity.this, EditConnectionActivity.class);
                startActivity(intent);

            }
        });


        mButtonSaveConnectionFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveConnections();

            }
        });

        mImgSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDownloading();
            }
        });


        mButtonConnectLocalHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EngineeringActivity.this, ConnectLocalHost.class));
            }
        });

    }

    private void makeVisibleInvisibleForConnection() {

        if (!mLinearLayoutConnectionMain.isShown()) {

            mLinearLayoutConnectionMain.setVisibility(View.VISIBLE);
            mLinearLayoutConnectionMain.startAnimation(animationDown);
            mButtonSaveConnection.setText("Hide");
            mLayoutMain2.setVisibility(View.GONE);

        } else {
            mLinearLayoutConnectionMain.startAnimation(animationUp);

            animationUp.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mLinearLayoutConnectionMain.setVisibility(View.GONE);
                    mButtonSaveConnection.setText("Save Connection");
                    mLayoutMain2.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });


        }

    }

    private boolean checkConnectionData() {
        if (mEditTextName.getText().toString().length() == 0) {
            mEditTextName.setError("Name can not be black");
            return false;
        }


        if (!mBooleanConnectionType)
            if (mEditTextSSID.getText().toString().length() == 0) {
                mEditTextSSID.setError("SSID can not be black");
                return false;
            }

        if (mBooleanConnectionType)
            if (!isValid(getmEditTextURL.getText().toString().trim())) {
                getmEditTextURL.setError("Please enter a valid url");
                return false;
            }

        if (mEditTextPassword.getText().toString().length() == 0) {
            mEditTextPassword.setError("Password can not be black");
            return false;
        }
        return true;


    }

    /* Returns true if url is valid */
    public static boolean isValid(String url) {
        /* Try creating a valid URL */
        try {
            new URL(url).toURI();
            return true;
        }

        // If there was an Exception
        // while creating URL object
        catch (Exception e) {
            return false;
        }
    }

    private void saveConnections() {
        if (checkConnectionData()) {


            final ConnectionSourceData connectionSourceData = new ConnectionSourceData();
            connectionSourceData.setName(mEditTextName.getText().toString());
            connectionSourceData.setSSID(mEditTextSSID.getText().toString());
            connectionSourceData.setUrl(getmEditTextURL.getText().toString());
            connectionSourceData.setPassword(mEditTextPassword.getText().toString());
            connectionSourceData.setType(mSpinnerType.getSelectedItem().toString());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    AppDataBase.getAppDataBase(EngineeringActivity.this).connectionSourceDAO().insert(connectionSourceData);
                }
            }).start();

            makeVisibleInvisibleForConnection();

        }

    }

    private void demoClickAction() {
        if (mDemo == 0) {
            mDemoMode.setImageResource(R.mipmap.mode_on);
            mDemo = 1;
        } else {
            mDemoMode.setImageResource(R.mipmap.mode_off);
            mDemo = 0;
        }
    }


    private void testClickAction() {
        if (mTest == 0) {
            mTestMode.setImageResource(R.mipmap.mode_on);
            mTest = 1;
        } else {
            mTestMode.setImageResource(R.mipmap.mode_off);
            mTest = 0;
        }
    }


    private void engineeringTest() {
        makeEngineeringRequest();

    }


    private void makeEngineeringRequest() {

        MakeHttpRequest.getMakeHttpRequest().sendRequest(Constants.Url.BASE_URL+"rcom/tmode/" + mTest + "/" + mDemo).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.i("Engineering res: ", s);

            }
        });

    }


    private void permanentChange() throws IOException {
        MakeHttpRequest.getMakeHttpRequest().sendRequest(Constants.Url.BASE_URL+"rcom/dsav/").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.i("permanentChange res: ", s);

            }
        });
    }


    private void makeDebugRequest(final int index) throws IOException {
        MakeHttpRequest.getMakeHttpRequest().sendRequest(Constants.Url.BASE_URL+"rcom/debu/" + index).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.i("debug res: ", s);
                mDebug = index;

            }
        });
    }


    private void registerSpinerListener() {
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                try {
                    makeDebugRequest(i);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        mSpinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    mLayoutURL.setVisibility(View.GONE);
                    mLayoutSSID.setVisibility(View.VISIBLE);
                    mBooleanConnectionType = false;


                } else {
                    mLayoutURL.setVisibility(View.VISIBLE);
                    mLayoutSSID.setVisibility(View.GONE);
                    mBooleanConnectionType = true;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    public class MyGestureListner extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return super.onDown(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2,
                               float velocityX, float velocityY) {

            switch (getSlope(e1.getX(), e1.getY(), e2.getX(), e2.getY())) {
                case 1:

                    return true;
                case 2:

                    return true;
                case 3:
                    Log.d("Config", "down");
                    finish();
                    EngineeringActivity.this.overridePendingTransition(R.anim.slide_up2, R.anim.slide_down2);
                    return true;
                case 4:

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
        EngineeringActivity.this.overridePendingTransition(R.anim.slide_up2, R.anim.slide_down2);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EngineeringData engineeringData = new EngineeringData();
        engineeringData.setmDemo(mDemo);
        engineeringData.setmTest(mTest);
        engineeringData.setmDebug(mDebug);
        MySharedPreferences.getMySharedPreferences().saveEngineeringData(this, engineeringData);
    }

    private void getEngineeringData() {
        EngineeringData engineeringData = MySharedPreferences.getMySharedPreferences().getEngineeringData(this);
        if (engineeringData != null) {
            mDemo = engineeringData.getmDemo();
            mTest = engineeringData.getmTest();
            mDebug = engineeringData.getmDebug();


            if (mDemo == 0) {
                mDemoMode.setImageResource(R.mipmap.mode_off);
            } else {
                mDemoMode.setImageResource(R.mipmap.mode_on);
            }


            if (mTest == 0) {
                mTestMode.setImageResource(R.mipmap.mode_off);
            } else {
                mTestMode.setImageResource(R.mipmap.mode_on);
            }
            mSpinner.setSelection(mDebug);
        }


    }

    private void startDownloading() {

        mProgressBar.setVisibility(View.VISIBLE);
        mToast.setText("Syncing the config file");
        mToast.show();
        mConfigurationPresenter = new ConfigurationPresenter(EngineeringActivity.this);
        mConfigurationPresenter.requestServer();
    }

    @Override
    public void returnUpdate(ConfiguratonModel response) {
        mConfigurationPresenter.saveConnectionData(this, response);

    }

    @Override
    public void returnUpdateForNextScreen() {
        mProgressBar.setVisibility(View.GONE);
        mToast.setText("Syncing has been finished");
        mToast.show();

    }
}
