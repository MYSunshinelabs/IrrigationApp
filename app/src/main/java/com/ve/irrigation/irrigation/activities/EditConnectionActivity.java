package com.ve.irrigation.irrigation.activities;

import android.os.Handler;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.ve.irrigation.datavalues.AppDataBase;
import com.ve.irrigation.datavalues.ConnectionSourceData;
import com.ve.irrigation.irrigation.adapters.ConnectionAdapter;
import com.ve.irrigation.irrigation.EditConnectionContractor;
import com.ve.irrigation.irrigation.EditConnectionMainPresenter;
import com.ve.irrigation.irrigation.R;

import java.util.List;

import static com.ve.irrigation.irrigation.activities.EngineeringActivity.isValid;

public class EditConnectionActivity extends BaseActivity implements EditConnectionContractor.EditConnectionView, ConnectionAdapter.ConnectionListener {


    EditConnectionContractor.EditConnectionPresenter mEditConnectionPresenter;
    private EditText mEditTextName, mEditTextSSID, mEditTextURL, mEditTextPassword;
    private Spinner mSpinnerType;
    private Button mButtonSaveConnection;
    private boolean mBooleanConnectionType;
    LinearLayout mLayoutSSID, mLayoutURL;
    RecyclerView mRecyclerView;
    ConnectionAdapter mConnectionAdapter;
    LinearLayout mLayoutPassword;
    boolean mBoolEditorSave;
    ConnectionSourceData connectionSourceDatatoUpdate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_connection2);
        mEditConnectionPresenter = new EditConnectionMainPresenter(this);
        initViews();
        setSpinnerValues();
        registerSpinerListener();
        initListener();
        settData();


    }

    private void initViews() {
        mSpinnerType = findViewById(R.id.spinner_typeofconnection);
        mButtonSaveConnection = findViewById(R.id.btn_saveconnection);
        mEditTextName =  findViewById(R.id.edittext_name);
        mEditTextSSID = findViewById(R.id.edittext_ssid);
        mEditTextURL = findViewById(R.id.edittext_url);
        mLayoutURL = findViewById(R.id.layout_connectiont_url);
        mEditTextPassword = findViewById(R.id.edittext_password);
        mLayoutSSID = findViewById(R.id.layout_connectiont_ssid);
        mButtonSaveConnection = findViewById(R.id.btn_saveconnection);
        mRecyclerView = findViewById(R.id.recyclerview_connectionlist);
        mLayoutPassword = findViewById(R.id.layout_connectiont_password);
    }

    @Override
    public void returnUpdate() {
        settData();
    }

    private void setSpinnerValues() {
        ArrayAdapter<CharSequence> adaptertype = ArrayAdapter.createFromResource(this,R.array.debug_type, android.R.layout.simple_spinner_item);
        adaptertype.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerType.setAdapter(adaptertype);
    }


    private boolean checkConnectionData() {

        if (mEditTextName.getText().toString().length() == 0) {
            mEditTextName.setError("Name can not be black");
            return false;
        }

        if (!mBooleanConnectionType) {
            if (mEditTextSSID.getText().toString().length() == 0) {
                mEditTextSSID.setError("SSID can not be black");
                return false;
            }
            if (mEditTextPassword.getText().toString().length() == 0) {
                mEditTextPassword.setError("Password can not be black");
                return false;
            }
        }

        if (mBooleanConnectionType)
            if (!isValid(mEditTextURL.getText().toString().trim())) {
                mEditTextURL.setError("Please enter a valid url");
                return false;
            }

        return true;

    }

    private void registerSpinerListener() {


        mSpinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    mLayoutURL.setVisibility(View.GONE);
                    mLayoutSSID.setVisibility(View.VISIBLE);
                    mLayoutPassword.setVisibility(View.VISIBLE);
                    mBooleanConnectionType = false;


                } else {
                    mLayoutURL.setVisibility(View.VISIBLE);
                    mLayoutSSID.setVisibility(View.GONE);
                    mLayoutPassword.setVisibility(View.GONE);
                    mBooleanConnectionType = true;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void clearFields() {
        mEditTextName.setText("");
        mEditTextSSID.setText("");
        mEditTextURL.setText("");
        mEditTextPassword.setText("");
    }

    private void initListener() {


        mButtonSaveConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkConnectionData()) {

                    if (!mBoolEditorSave) {
                        mEditConnectionPresenter.saveConnectionData(EditConnectionActivity.this, mSpinnerType.getSelectedItem().toString(), mEditTextName.getText().toString(), mEditTextSSID.getText().toString(), mEditTextURL.getText().toString(), mEditTextPassword.getText().toString());
                        //reflectNewChange();
                    } else {


                        connectionSourceDatatoUpdate.setType(mSpinnerType.getSelectedItem().toString());
                        connectionSourceDatatoUpdate.setSSID(mEditTextSSID.getText().toString());
                        connectionSourceDatatoUpdate.setName(mEditTextName.getText().toString());
                        connectionSourceDatatoUpdate.setUrl(mEditTextURL.getText().toString());
                        connectionSourceDatatoUpdate.setPassword(mEditTextPassword.getText().toString());

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                AppDataBase.getAppDataBase(EditConnectionActivity.this).connectionSourceDAO().updateConnection(connectionSourceDatatoUpdate);
                            }
                        }).start();

                        reflectNewChange();
                        mBoolEditorSave = false;


                    }
                }
                clearFields();

            }
        });

    }


    @Override
    public void reflectNewChange() {
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                returnUpdate();
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    private void settData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<ConnectionSourceData> connectionSourceDatas = AppDataBase.getAppDataBase(EditConnectionActivity.this).connectionSourceDAO().getAllConnectionSource();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mConnectionAdapter = new ConnectionAdapter(EditConnectionActivity.this, connectionSourceDatas);
                        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(EditConnectionActivity.this);
                        mRecyclerView.setHasFixedSize(true);

                        mRecyclerView.setLayoutManager(mLinearLayoutManager);
                        mRecyclerView.setAdapter(mConnectionAdapter);
                    }
                });
            }
        }).start();

    }

    private void editforSameData(ConnectionSourceData connectionSourceData) {

        if (connectionSourceData.getType().equalsIgnoreCase("hotspot"))
            mSpinnerType.setSelection(0);
        else
            mSpinnerType.setSelection(1);

        mEditTextName.setText(connectionSourceData.getName());
        if (connectionSourceData.getSSID().length() > 0) {
            mEditTextSSID.setText(connectionSourceData.getSSID());
        }

        if (connectionSourceData.getUrl().length() > 0) {
            mEditTextURL.setText(connectionSourceData.getUrl());
        }

        if (connectionSourceData.getPassword() != null) {
            mEditTextPassword.setText(connectionSourceData.getPassword());
        }


    }


    @Override
    public void editConnection(final ConnectionSourceData connectionSourceData) {
        mBoolEditorSave = true;
        connectionSourceDatatoUpdate = (ConnectionSourceData) connectionSourceData;
        editforSameData(connectionSourceData);


    }

    @Override
    public void deleteConnection(final ConnectionSourceData connectionSourceData) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDataBase.getAppDataBase(EditConnectionActivity.this).connectionSourceDAO().deleteConnection(connectionSourceData);
            }
        }).start();
        mBoolEditorSave = false;
        clearFields();
        reflectNewChange();
    }
}
