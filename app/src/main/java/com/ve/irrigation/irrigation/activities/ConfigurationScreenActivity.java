package com.ve.irrigation.irrigation.activities;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.view.GestureDetectorCompat;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.gson.Gson;

import com.ve.irrigation.datavalues.ConfigurationDDGET;
import com.ve.irrigation.datavalues.ConfiguratonModel;
import com.ve.irrigation.datavalues.MySharedPreferences;
import com.ve.irrigation.irrigation.MakeHttpRequest;
import com.ve.irrigation.irrigation.R;
import com.ve.irrigation.irrigation.listners.SwipeListener;
import com.ve.irrigation.utils.Constants;
import com.ve.irrigation.utils.IrrigationGestureDetector;
import com.ve.irrigation.utils.Preferences;
import com.ve.irrigation.utils.Utils;
import com.ve.irrigation.widget.IrrigationWidgetProvider;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ConfigurationScreenActivity extends BaseActivity implements View.OnClickListener, SwipeListener {

    private static final String TAG = ConfigurationScreenActivity.class.getSimpleName();
    TextView mTextViewNotBeforeTime, mTextViewNotAfterTime;
    EditText mEditTextPumpCapacity, mEditTextNumberofDevice;
    Spinner mSpinnerLanguage, mSpinnerMode;
    Button mButtonSave;
    Toast mToast;
    private GestureDetectorCompat mDetector;
    ConfigurationDDGET configurationDDGET;
    int mNotBefore, mNotAfter;
    boolean notAfter;
    ImageView mImageViewLock;
    boolean mLock;
    private RadioButton radioLock;
    private Button btnSwitchLang;
    private ImageView imgEditableValve;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_configuration_screen);

        mDetector = new GestureDetectorCompat(this, new IrrigationGestureDetector(this));

        initViews();

        perfomClick();

        registerEditTextListener();

        updateEditableSwitch();

    }

    private void updateEditableSwitch() {
        if (Preferences.getEditableValveAssignment(this)) {
            imgEditableValve.setImageResource(R.mipmap.mode_on);
        } else {
            imgEditableValve.setImageResource(R.mipmap.mode_off);
        }
    }

    private void initViews() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        mTextViewNotBeforeTime =  findViewById(R.id.not_before_time);
        mTextViewNotAfterTime = findViewById(R.id.not_after_time);

        mEditTextPumpCapacity = findViewById(R.id.edittext_pumpcapacity);

        mSpinnerLanguage = findViewById(R.id.spinner_language);
        mButtonSave = findViewById(R.id.btn_save);
        mImageViewLock =findViewById(R.id.img_lock);
        radioLock =findViewById(R.id.radioLock);
        btnSwitchLang=findViewById(R.id.btnSwitchLang);
        imgEditableValve=findViewById(R.id.imgEditableValve);
        mButtonSave.setTextColor(getResources().getColor(R.color.colorTextSecondary));
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        radioLock.setChecked(false);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.actionValveAssignment:
                if(item.isChecked()){
                    // If item already checked then unchecked it
                    item.setChecked(false);
                    Preferences.setEditableValveAssignment(this,false);
                }else {
                    // If item is unchecked then checked it
                    item.setChecked(true);
                    Preferences.setEditableValveAssignment(this,true);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setSpinnerValues() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.language_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerLanguage.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapterMode = ArrayAdapter.createFromResource(this,R.array.mode_array, android.R.layout.simple_spinner_item);
        adapterMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerMode.setAdapter(adapterMode);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnValveAssignment:
                startActivityForResult(new Intent(this,ValveAssignmentActivity.class),Constants.RequestCode.RESTART_APP);
                break;
            case R.id.btnNetworkConfig:
                startActivityForResult(new Intent(this,ConfigActivity.class),Constants.RequestCode.CONFIG_NETWORK);
                break;
            case R.id.btnLoadDeviceFile:
                startActivityForResult(new Intent(this,IpAddEGDEControllerActivity.class),Constants.RequestCode.RESTART_APP);
                break;
            case R.id.btnSwitchLang:
                if(Preferences.getLanguage(this).equals("en"))
                    Preferences.setLanguage(this,"zh");
                else
                    Preferences.setLanguage(this,"en");
                IrrigationWidgetProvider.isLanguageChange=true;
                IrrigationWidgetProvider.updateWidget(this);
                switchLanguage();
                break;
            case R.id.not_before_time:
                showTimePicker(mTextViewNotBeforeTime, false);
                break;
            case R.id.not_after_time:
                showTimePicker(mTextViewNotAfterTime, true);
                break;
            case R.id.btn_save:
                try {
                    permanentChange();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.img_lock:
                if (!mLock) {
                    mImageViewLock.setImageResource(R.mipmap.mode_on);
                    mLock = !mLock;
                } else {
                    mImageViewLock.setImageResource(R.mipmap.mode_off);
                    mLock = !mLock;
                }
                break;

            case R.id.radioLock:
                if (!mLock) {
                    radioLock.setChecked(true);
                    mLock = !mLock;
                } else {
                    radioLock.setChecked(false);
                    mLock = !mLock;
                }
                break;
            case R.id.imgEditableValve:
                Preferences.setEditableValveAssignment(this,!Preferences.getEditableValveAssignment(this));
                updateEditableSwitch();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==Constants.RequestCode.RESTART_APP && resultCode==Constants.ResultCode.RESTART_APP){
            setResult(Constants.ResultCode.RESTART_APP);
            finish();
        }else  if(requestCode==Constants.RequestCode.CONFIG_NETWORK && resultCode==Constants.ResultCode.CONFIG_NETWORK){
            // do somthig with configration status
        }
    }

    private void perfomClick() {
        findViewById(R.id.btnValveAssignment).setOnClickListener(this);
        findViewById(R.id.btnNetworkConfig).setOnClickListener(this);
        findViewById(R.id.btnLoadDeviceFile).setOnClickListener(this);
        radioLock.setOnClickListener(this);
        mTextViewNotBeforeTime.setOnClickListener(this);
        mTextViewNotAfterTime.setOnClickListener(this);
        mButtonSave.setOnClickListener(this);
        mImageViewLock.setOnClickListener(this);
        btnSwitchLang.setOnClickListener(this);
        imgEditableValve.setOnClickListener(this);
    }

    private void registerEditTextListener() {
        mEditTextPumpCapacity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    makeRequest("pumpcap", mEditTextPumpCapacity.getText().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    private void registerSpinerListener() {
        mSpinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    try {
                        makeRequest("cfg.lang", "English");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        makeRequest("cfg.lang", "Chinese");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        mSpinnerMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    try {
                        makeRequest("mode", "Expert");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        makeRequest("mode", "Detailed");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }


    private void showTimePicker(final TextView view, final boolean notAfter) {
        Calendar mcurrentTime = Calendar.getInstance();
        final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        final int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(ConfigurationScreenActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                view.setText(selectedHour + ":" + selectedMinute);

                if (!notAfter) {
                    mNotBefore = (selectedHour * 60 * 60) + (selectedMinute * 60);
                    try {
                        makeRequest("nbefore", String.valueOf(mNotBefore));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    mNotAfter = (selectedHour * 60 * 60) + (selectedMinute * 60);
                    try {
                        makeRequest("nafter", String.valueOf(mNotAfter));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, hour, minute, true);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();


    }


    @Override
    public void onSwipe(int id) {
        switch (id){
            case SwipeListener.SWIPE_UP:
                Utils.printLog(TAG, "top");
                if (mLock) {
                    Intent i = new Intent(ConfigurationScreenActivity.this, EngineeringActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                }
                break;

            case SwipeListener.SWIPE_DOWN:
                Utils.printLog(TAG, "down");
                onBackPressed();
                break;

            case SwipeListener.SWIPE_LEFT:
                Utils.printLog(TAG, "left");
                break;

            case SwipeListener.SWIPE_RIGHT:
                Utils.printLog(TAG, "right");
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        this.mDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        ConfigurationScreenActivity.this.overridePendingTransition(R.anim.slide_up2, R.anim.slide_down2);

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            setConfigataFromDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }


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
                //MySharedPreferences.getMySharedPreferences().saveDDGET(ConfigurationScreenActivity.this, s);
                if (s != null && s.length() > 0) {
                    configurationDDGET = new Gson().fromJson(s, ConfigurationDDGET.class);
                    setValuesFromDDGET(configurationDDGET);
                }
            }
        });
    }

    private void permanentChange() throws IOException {
        MakeHttpRequest.getMakeHttpRequest().sendRequest(Constants.Url.BASE_URL+"rcom/dsav/").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).onErrorResumeNext(new Func1<Throwable, Observable<? extends String>>() {
            @Override
            public Observable<? extends String> call(Throwable throwable) {
                return null;
            }
        })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.i("permanentChange res: ", s);

                    }
                });
    }

    private void setValuesFromDDGET(ConfigurationDDGET configurationDDGET) {

        mTextViewNotBeforeTime.setText(Utils.getTime(Integer.parseInt(configurationDDGET.getNbefore())));
        mTextViewNotAfterTime.setText(Utils.getTime(Integer.parseInt(configurationDDGET.getNafter())));
        mEditTextPumpCapacity.setText(configurationDDGET.getPumpcap());
        mEditTextNumberofDevice.setText(configurationDDGET.getCfgnos());

        if (configurationDDGET.getCfglang().equalsIgnoreCase("English"))
            mSpinnerLanguage.setSelection(0);
        else
            mSpinnerLanguage.setSelection(1);

        if (configurationDDGET.getMode().equalsIgnoreCase("Expert"))
            mSpinnerMode.setSelection(0);
        else
            mSpinnerMode.setSelection(1);
    }

    private void makeRequest(String property, String value) throws IOException {
        MakeHttpRequest.getMakeHttpRequest().sendRequest(Constants.Url.BASE_URL+"dbset/" + property + "/" + value).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.i("res: ", s);
            }
        });


    }

    private void setConfigataFromDatabase() {

        String responseInString = Preferences.getHotspotdocfile(this);
        Gson gson = new Gson();
        ConfiguratonModel modelResponse = gson.fromJson(responseInString, ConfiguratonModel.class);

        if (modelResponse != null) {
            mTextViewNotBeforeTime.setText(MySharedPreferences.getMySharedPreferences().convertSecondtoTime(Integer.parseInt(modelResponse.getNot_beforetime())));
            mTextViewNotAfterTime.setText(MySharedPreferences.getMySharedPreferences().convertSecondtoTime(Integer.parseInt(modelResponse.getNot_aftertime())));
            mEditTextPumpCapacity.setText(modelResponse.getPumpcapacity());
            mEditTextNumberofDevice.setText(modelResponse.getDevice_inchain());
            if (modelResponse.getLanguage().equalsIgnoreCase("english"))
                mSpinnerLanguage.setSelection(0);
            else
                mSpinnerLanguage.setSelection(1);
            if (modelResponse.getMode().equalsIgnoreCase("expert"))
                mSpinnerMode.setSelection(0);
            else
                mSpinnerMode.setSelection(1);
        }

        mSpinnerLanguage.requestFocus();

    }

    private void switchLanguage() {
        Locale locale = new Locale(Preferences.getLanguage(this));
        Locale.setDefault(locale);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLocale(new Locale(Preferences.getLanguage(this))); // API 17+ only.
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            conf.setLocale(locale);
            createConfigurationContext(conf);
        }
        res.updateConfiguration(conf, dm);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(),ConfigurationScreenActivity.class));
                ConfigurationScreenActivity.this.finish();
                Toast.makeText(getApplicationContext(),getString(R.string.msg_restart),Toast.LENGTH_LONG).show();
            }
        },150);
    }

}
