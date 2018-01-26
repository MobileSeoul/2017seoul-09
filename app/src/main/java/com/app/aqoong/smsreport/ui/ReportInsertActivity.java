package com.app.aqoong.smsreport.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.app.aqoong.smsreport.R;
import com.app.aqoong.smsreport.data.Globar;
import com.app.aqoong.smsreport.util.MyLocation;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by aqoong on 2017. 10. 11..
 */

public class ReportInsertActivity extends AppCompatActivity {
    private String TAG = "ReportInsertActivity";

    private LocationManager locationManager = null;
    private Context mContext = null;
    private int transType = 0;

    //차량정보
    private LinearLayout carInfoPanel = null;
    private ImageButton btnHelp = null;         //차량번호 안내 버튼
    private EditText carInfoNumber1 = null;     //차량번호 앞2자리
    private Spinner carInfo        = null;     //아바사자
    private EditText carInfoNumber2 = null;     //차량번호 뒷4자리

    //회사정보
    private TextView titleCompany = null;       //운수회사/지하철노선
    private EditText comName = null;            //운수회사정보                1
    private EditText lineNumber = null;          //버스or지하철 노선          2

    //발생장소
    private TextView myLocationGPS = null;      //현재 내 위치
    private EditText myLocationDetail = null;   //상세위치

    //시간
    private TextView timeClock = null;               //발생시간

    //신고내용
    private EditText contentReport = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        //for search my location
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        transType = getIntent().getIntExtra("TYPE", 0);
        String subTitle = null;

        switch(transType){
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

        setContentView(R.layout.report_main);

        //keyboard hide
        View vParent = findViewById(R.id.report_main);
        vParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                });
            }
        });

        //actionbar setting
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setSubtitle(subTitle);

        //buttons
        findViewById(R.id.btn_nextstep).setOnClickListener(btnListener);
        findViewById(R.id.btn_gps).setOnClickListener(btnListener);

        //carinfo
        carInfoPanel   = (LinearLayout) findViewById(R.id.carinfo_panel);
        btnHelp        = (ImageButton)findViewById(R.id.btn_help);
        carInfoNumber1 = (EditText) findViewById(R.id.carinfo_1);
        carInfo        = (Spinner)findViewById(R.id.spin_carinfo);
        carInfoNumber2 = (EditText) findViewById(R.id.carinfo_2);

        titleCompany   = (TextView)findViewById(R.id.text_notic_company);
        comName = (EditText)findViewById(R.id.company);
        lineNumber = (EditText)findViewById(R.id.bus_linenumber);


        // get current time
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        timeClock = (TextView)findViewById(R.id.timer);
        timeClock.setText(changeTimeToString(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
        timeClock.setOnClickListener(btnListener);

        //location
        myLocationGPS = (TextView) findViewById(R.id.location_info);
        myLocationDetail = (EditText)findViewById(R.id.location_detail);

        //content
        contentReport = (EditText)findViewById(R.id.content_text);

        //setting init
        setView(transType);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.btn_nextstep:
                    // next step change activity
                    if(checkEmptyView(transType))
                        Snackbar.make(carInfoPanel, "필수 입력정보가 비어있습니다", Snackbar.LENGTH_LONG).show();
                    else
                    {
                        HashMap<String, Object> datas = new HashMap<>();
                        datas.put(Globar.KEY_TYPE, transType);

                        datas.put(Globar.KEY_LOCATION, myLocationGPS.getText()+Globar.SPLIT_FILTER+myLocationDetail.getText());
                        datas.put(Globar.KEY_CONTENT, contentReport.getText());
                        datas.put(Globar.KEY_TIME, timeClock.getText());
                        switch(transType){
                            case Globar.TYPE_BUS:
                                datas.put(Globar.KEY_COMPANY, comName.getText());
                                datas.put(Globar.KEY_LINENUMBER, lineNumber.getText());
                                datas.put(Globar.KEY_CARINFO, carInfoNumber1.getText()+carInfo.getSelectedItem().toString()+carInfoNumber2.getText());
                                break;
                            case Globar.TYPE_SUB:
                                datas.put(Globar.KEY_LINENUMBER, comName.getText());
                                datas.put(Globar.KEY_TRAINNUM, lineNumber.getText());
                                break;
                            case Globar.TYPE_TAXI:
                                datas.put(Globar.KEY_COMPANY, comName.getText());
                                datas.put(Globar.KEY_CARINFO, carInfoNumber1.getText()+carInfo.getSelectedItem().toString()+carInfoNumber2.getText());
                                break;
                        }

                        //화면이동
                        Intent intent = new Intent(mContext, UserDataActivity.class);
                        intent.putExtra("DATA", datas);
                        startActivity(intent);
                    }

                    break;
                case R.id.btn_gps:
                    TedPermission.with(mContext)
                            .setPermissionListener(gpsPermissionListener)
                            .setRationaleMessage("GPS 권한이 필요합니다\n권한 허용 뒤 이용해 주시기 바랍니다")
//                            .setGotoSettingButton(true)
                            .setPermissions(Globar.permissions[1])
                            .check();
                    break;
                case R.id.timer:
                    TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            timeClock.setText(changeTimeToString(hourOfDay, minute));
                        }
                    }, Calendar.HOUR_OF_DAY, Calendar.MINUTE, false);
                    timePickerDialog.show();
                    break;
                case R.id.btn_help:
                    Dialog dialog = new Dialog(mContext);
                    dialog.setContentView(R.layout.dialog_help);
                    dialog.show();
                    break;
            }
        }
    };

    /**
     *  Init View
     * @param type
     */
    private void setView(int type){

        btnHelp.setOnClickListener(btnListener);

        switch(type){
            case Globar.TYPE_TAXI:
                carInfoPanel.setVisibility(View.VISIBLE);
                lineNumber.setVisibility(View.GONE);
                titleCompany.setText("운수회사(선택)");
                btnHelp.setVisibility(View.GONE);
                break;
            case Globar.TYPE_BUS:
                lineNumber.setVisibility(View.VISIBLE);
                carInfoPanel.setVisibility(View.VISIBLE);
                titleCompany.setText("운수회사/버스노선(선택)");
                btnHelp.setVisibility(View.GONE);
                break;
            case Globar.TYPE_SUB:
                carInfoPanel.setVisibility(View.GONE);
                lineNumber.setVisibility(View.VISIBLE);
                comName.setVisibility(View.GONE);
                titleCompany.setText("지하철노선");
                lineNumber.setHint("차량번호");
                btnHelp.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * TimePicker's hour, minute To String(pm/am hh:mm)
     * @param _hourOfDay
     * @param _minute
     * @return
     */
    private String changeTimeToString(int _hourOfDay, int _minute){
        String result = "";

        String meridiem = "오전 ";

        int hour = _hourOfDay;
        int min = _minute;

        if(_hourOfDay >= 12){
            meridiem = "오후 ";
            hour = _hourOfDay-12;
            if(hour == 0)
                hour = 12;
        }
        String strHour = String.format("%02d", hour);
        String strMin = String.format("%02d", min);


        result = meridiem + strHour + ":" + strMin;

        return result;
    }

    /**
     * check empty view before next page
     * @param type
     * @return
     */
    private boolean checkEmptyView(int type){
        boolean result = false;

        //신고내용
        if(contentReport.getText().length() <= 0){
            result = true;
        }

        //위치
        if(myLocationDetail.getText().length() <= 0){
            if(myLocationGPS.getText().length() <= 0)
                result = true;
        }

        switch(type){
            case Globar.TYPE_BUS:
                if(comName.getText().length() <= 0 || lineNumber.getText().length() <= 0) {
                    if (carInfoNumber1.getText().length() != 2 || carInfoNumber2.getText().length() != 4) {
                        result = true;
                    }
                    else
                        result = false;
                }
                break;
            case Globar.TYPE_SUB:
//                Log.d(TAG, ""+lineNumber.getText());
                if(lineNumber.getText().length() <= 0) {
                    result = true;
                }
                break;
            case Globar.TYPE_TAXI:
                if(carInfoNumber1.getText().length() != 2 || carInfoNumber2.getText().length() != 4) {
                    result = true;
                }
                break;
        }


        return result;
    }

    /**
     *      GPS button Permission Work
     */
    PermissionListener gpsPermissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            //GPS switch on/off
            if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setMessage("GPS 기능이 활성화되어야 사용 가능합니다").
                        setPositiveButton("설정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                startActivity(intent);
                            }
                        }).setNegativeButton("취소",null).show();
            }
            else {
//                LocationManager myGPS = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//                boolean isGPSEnabled = myGPS.isProviderEnabled(LocationManager.GPS_PROVIDER);
//
//                if(!isGPSEnabled){
//                    Toast.makeText(mContext, "GPS기능이 비활성화 되어있습니다",Toast.LENGTH_LONG).show();
//                    return;
//                }
                //get location
                Location location = MyLocation.getInstance(mContext).getLocation();
                Geocoder geocoder = new Geocoder(mContext);
                Address add = null;
                try {
                    add = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0);
                    myLocationGPS.setText(add.getAddressLine(0));
//                    Log.d(TAG, add.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            //request

        }
    };
}
