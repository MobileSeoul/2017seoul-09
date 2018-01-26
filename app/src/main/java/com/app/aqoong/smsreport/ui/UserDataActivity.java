package com.app.aqoong.smsreport.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.app.aqoong.smsreport.R;
import com.app.aqoong.smsreport.data.Globar;
import com.app.aqoong.smsreport.data.SMS;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by aqoong on 2017. 9. 25..
 */

public class UserDataActivity extends AppCompatActivity {
    private Context context = null;

    private final String TAG = "UserDataActivity";
    private HashMap<String, Object> mGetDatas = null;

    //reporter
    private EditText reporterName = null;
    private EditText reporterPhone = null;

    //receive
    private LinearLayout receivePanel = null;       //hide & show
    private RadioButton radioSMS = null;
    private RadioButton radioEmail = null;
    private EditText emailID = null;                //email front user id
    private Spinner spinEmailAdd = null;                //email back address selector
    private EditText emailAdd_users = null;         //email back address edit

    private Button btnReport = null;

    private boolean isCustom = false;

    Dialog tempDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        tempDialog = new Dialog(this);

        mGetDatas = (HashMap<String, Object>) getIntent().getSerializableExtra("DATA");
//        if(mGetDatas.equals(null)){Log.e(TAG, "data is null");}
        String[] d = getResources().getStringArray(R.array.spin_email);

        String subTitle = null;
        switch((int)mGetDatas.get(Globar.KEY_TYPE))
        {
            case Globar.TYPE_TAXI:
                subTitle = "택시";
                break;
            case Globar.TYPE_BUS:
                subTitle = "버스";
                break;
            case Globar.TYPE_SUB:
                subTitle = "지하철";
                break;
        }

        setContentView(R.layout.insert_userdata_layout);

        View vParent = findViewById(R.id.report_main);
        vParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                });
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setSubtitle(subTitle);


        reporterName = (EditText)findViewById(R.id.report_name);
        reporterPhone = (EditText)findViewById(R.id.report_phone);
        findViewById(R.id.report_btn_bringnum).setOnClickListener(reportButtonEvent);

        receivePanel = (LinearLayout)findViewById(R.id.report_email_panel);
        radioSMS = (RadioButton)findViewById(R.id.checkbox_sms);
        radioEmail = (RadioButton)findViewById(R.id.checkbox_email);
        emailID = (EditText)findViewById(R.id.report_email_front);
        emailAdd_users = (EditText)findViewById(R.id.report_email_back);
        spinEmailAdd = (Spinner) findViewById(R.id.report_email_back_spin);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, d);
        spinEmailAdd.setAdapter(adapter);

        btnReport = (Button)findViewById(R.id.btn_report);
        btnReport.setOnClickListener(reportButtonEvent);

        setView();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }


    private void setView(){
        radioEmail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked)   receivePanel.setVisibility(View.VISIBLE);
                else            receivePanel.setVisibility(View.GONE);

            }
        });

        spinEmailAdd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getSelectedItem().equals("직접입력")) {
                    emailAdd_users.setVisibility(View.VISIBLE);
                    isCustom = true;
                }
                else {
                    emailAdd_users.setVisibility(View.GONE);
                    isCustom = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private View.OnClickListener reportButtonEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //내 전화번호 가져오기
            if(v.getId() == R.id.report_btn_bringnum){
                TedPermission.with(context)
                        .setPermissionListener(bringNumberListener)
                        .setRationaleMessage("전화번호를 가져오기위해 휴대전화 내용을 읽는 권한이 필요합니다")
//                                .setGotoSettingButton(true)
                        .setPermissions(Globar.permissions[2])
                        .check();
            }
            //신고하기
            else if(v.getId() == R.id.btn_report) {

                mGetDatas.put(Globar.KEY_REPORTER_NAME, reporterName.getText());

                mGetDatas.put(Globar.KEY_REPORTER_PHONE, reporterPhone.getText());

                String email = null;

                if (radioEmail.isChecked()) {
                    if (isCustom)
                        email = emailID.getText() + "@" + emailAdd_users.getText();
                    else
                        email = emailID.getText() + "@" + spinEmailAdd.getSelectedItem().toString();

                    mGetDatas.put(Globar.KEY_REPORTER_EMAIL, email);
                }

                TedPermission.with(context)
                        .setPermissionListener(serviceListener)
                        .setRationaleMessage("신고 서비스는 문자 권한이 필요합니다\n권한 허용 뒤 이용해 주시기 바랍니다")
//                                .setGotoSettingButton(true)
                        .setPermissions(Globar.permissions[0])
                        .check();
            }
        }
    };

    private PermissionListener serviceListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            if(reporterName.getText().length() <= 0) {
                Snackbar.make(reporterName, "이름을 입력하세요", Snackbar.LENGTH_LONG).show();
                return;
            }

            if(!radioSMS.isChecked() && !radioEmail.isChecked()){
                Snackbar.make(reporterName, "수신 방법을 선택해 주세요", Snackbar.LENGTH_LONG).show();
                return;
            }
            else if(radioSMS.isChecked()){
                if(reporterPhone.getText().length() <= 0) {
                    Snackbar.make(reporterName, "휴대폰번호를 입력하세요", Snackbar.LENGTH_LONG).show();
                    return;
                }
            }else if(radioEmail.isChecked()){
                if(emailID.getText().length() <= 0) {
                    Snackbar.make(reporterName, "이메일을 입력하세요", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if(spinEmailAdd.getSelectedItem().equals("직접입력")){
                    if(emailAdd_users.getText().length() <= 0){
                        Snackbar.make(reporterName, "이메일을 입력하세요", Snackbar.LENGTH_LONG).show();
                        return;
                    }
                }

            }

            SMS sms = new SMS(context, mGetDatas, radioSMS.isChecked(),radioEmail.isChecked());
            sms.sendSMS("02120");
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {

        }
    };

    private PermissionListener bringNumberListener = new PermissionListener() {

        @Override
        public void onPermissionGranted() {
            TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String mPhoneNumber = tMgr.getLine1Number();
            reporterPhone.setText(mPhoneNumber);
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {

        }
    };

}
