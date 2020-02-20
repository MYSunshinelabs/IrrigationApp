package com.ve.irrigation.irrigation.activities
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TextView
import com.ve.irrigation.Receiver.EspResponseReceiver
import com.ve.irrigation.irrigation.R
import com.ve.irrigation.services.ListenESPBarodService
import com.ve.irrigation.utils.HeartBeatTask
import com.ve.irrigation.utils.Preferences
import com.ve.irrigation.utils.Utils
import org.json.JSONObject
import java.util.*

open class SplashActivity : AppCompatActivity(), EspResponseReceiver.EspResponseObserver {

    override fun onEspResponse(response: String?) {
        Log.d("SplashActivity ","onEspResponse "+ response)
        try {
            val jsonHB = JSONObject(response)
            if (jsonHB.getInt("msg") == 3) {
//                val heartBeat = Gson().fromJson(response, HeartBeat::class.java)
                Preferences.setHeartBeat(this,response)
            }else if (jsonHB.getInt("msg") == 2) {
                var pId=jsonHB.optString("pid")
                Preferences.setProductId(pId,this)
                var group =jsonHB.optString("grp")
                Preferences.setVXG(this, jsonHB.getJSONArray("vxg").toString())
                Preferences.setGroups(group,this)
                Preferences.setNoGroups(this,Integer.parseInt(group))
//                Preferences.setValveAssignment(this,group.substring(1))
                Preferences.setNoValves(this,Integer.parseInt(pId[1]+""))
                setSplashInfo(pId, Preferences.getGroups(this))
            }
        } catch (e: Exception) {
            Utils.printLog("SplashActivity ",e.toString())
        }
    }

    internal lateinit var mHandler: Handler
    internal lateinit var mRunnable: Runnable
    internal lateinit var txtVersion: TextView
    internal lateinit var txtAppInfo: TextView
//    internal lateinit var intent: Intent;
    internal lateinit var mSharedPreferences: SharedPreferences
    private lateinit var espResponseReceiver:EspResponseReceiver
    private var intentEspService: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        init()

        setLanguage()

//        requestConfigData()

        //  Register Broadcast Reciver to getting the response comming from the Esp Board by a specify socket no.
        espResponseReceiver = EspResponseReceiver.registerReceiver(this, this)

        setSplashInfo(Preferences.getProductId(this),Preferences.getGroups(this))

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            startActivityForResult(Intent(this,PermissionsActivity::class.java),100)
        else
            startTimeToMoveToMainScreen()
    }

    private fun init() {
        txtVersion = findViewById<View>(R.id.txtVersion) as TextView
        txtAppInfo = findViewById<View>(R.id.txtAppInfo) as TextView

        if(intent.extras!=null&&intent.extras.getBoolean("isRestart"))
            Utils.startHeartBeatTask(applicationContext)
    }

    private fun setSplashInfo(pId: String, groups: String) {
        var infoMsg= ""
        if(pId.length>=4){
            infoMsg=infoMsg+"Irrigation with "+pId[1]+" Valves "
            if(pId[3].toString().equals("1"))
                infoMsg=infoMsg+"and Nutrition"
        }

        if(groups.length>0) {
            infoMsg = infoMsg + " by " + groups[0] + " Groups."
        }

        txtAppInfo.text = infoMsg;

        val pInfo = getPackageManager().getPackageInfo(packageName, 0)
        txtVersion.text ="App Version : "+ pInfo.versionName
    }

    override fun onPause() {
        super.onPause()
        //  Unregister the broadcast receiver as
        // activity is no longer visible to display information
        EspResponseReceiver.unregisterReceiver(espResponseReceiver,this)
    }

    private fun startTimeToMoveToSplash2Screen() {
        mHandler = Handler()
        mRunnable = Runnable {
            val intent = Intent(this, SplashScreen2::class.java)
            startActivity(intent)
            finish()
        }
        mHandler.postDelayed(mRunnable, 3000)
    }

    private fun startTimeToMoveToMainScreen() {
        Utils.startLocationService(this)
        mHandler = Handler()
        mRunnable = Runnable {
            val intent = Intent(this, MainActivity4v2::class.java);
            startActivity(intent);
            finish()
        }
        mHandler.postDelayed(mRunnable, 4000)
    }

    override fun onResume() {
        super.onResume()

        if (!Utils.isDeviceConnected(this))
            Utils.createNotification(this, "Connectivity", getString(R.string.error_msg_no_network))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !HeartBeatTask.isListen) {
            Utils.startHeartBeatTask(applicationContext)
        } else if (!ListenESPBarodService.isListen ){
            Utils.startHeartBeatTask(applicationContext)
        }

    }

    private fun requestConfigData() {
        mSharedPreferences = getSharedPreferences("myconfigdata", Context.MODE_PRIVATE);
        if (mSharedPreferences.getBoolean("firstdownload", false)) {
            startTimeToMoveToMainScreen()
        } else {
            startTimeToMoveToSplash2Screen()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==100 && resultCode== 200)
            startTimeToMoveToMainScreen()
        else
            finish();
    }
    private fun setLanguage() {
        val locale = Locale(Preferences.getLanguage(this))
        Locale.setDefault(locale)
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLocale(Locale(Preferences.getLanguage(this))) // API 17+ only.
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            conf.setLocale(locale)
            createConfigurationContext(conf)
        }
        res.updateConfiguration(conf, dm)
    }
}
