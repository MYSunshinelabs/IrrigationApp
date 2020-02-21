package com.ve.irrigation.irrigation.activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ve.irrigation.customview.CustomTextViewLightBold;
import com.ve.irrigation.irrigation.R;
import com.ve.irrigation.utils.Utils;

/**
 * Created by dalvendrakumar on 29/11/18.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // to show application with any screen lock even pattern or pin set on device
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        Utils.setStatusBarColor(this, R.color.colorStatusBarNormal);

    }


    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try{
            ImageView imgLeft=findViewById(R.id.imgTollbarLeft);
            ImageView imgLeft2=findViewById(R.id.imgTollbarLeft2);
            imgLeft2.setVisibility(View.GONE);
            ImageView imgRight=findViewById(R.id.imgTollbarRight);
            CustomTextViewLightBold txtTitle=findViewById(R.id.title);

            if(this instanceof MainActivity4v2){
                txtTitle.setText(getString(R.string.dashboard));
                imgLeft.setImageResource(R.mipmap.up_down_arrow);
                imgLeft2.setVisibility(View.VISIBLE);
                imgLeft2.setImageResource(R.mipmap.left_right_arrow);
            }else if(this instanceof GroupsActivity  ){
                txtTitle.setText(getString(R.string.groups));
                imgLeft.setImageResource(R.mipmap.left_arrow);
            }else if(this instanceof ControlModeActivity){
                imgLeft.setImageResource(R.mipmap.left_right_arrow);
            }else if(this instanceof ConfigurationScreenActivity){
                txtTitle.setText(getString(R.string.user_configuration));
                imgLeft.setImageResource(R.mipmap.up_down_arrow);
            }else if(this instanceof SelectDeviceActivity){
                txtTitle.setText(getString(R.string.network_connection));
                imgLeft.setImageResource(R.mipmap.up_arrow);
                imgLeft.setRotation(180);
            }else if(this instanceof SwipeRightActivity){
                txtTitle.setText(getString(R.string.nutrigator_control));
                imgLeft.setImageResource(R.mipmap.left_arrow);
//                imgLeft.setRotation(180);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


}
