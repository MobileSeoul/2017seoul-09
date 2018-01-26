package com.app.aqoong.smsreport.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.DragEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.app.aqoong.smsreport.R;
import com.app.aqoong.smsreport.data.Globar;
import com.app.aqoong.smsreport.data.ProductData;

/**
 * Created by aqoong on 2017. 10. 11..
 */

public class FindDetailActivity extends AppCompatActivity{
    private ProductData data = null;

    private boolean isSub = false;                               //지하철 체크

    private TextView vID = null;                    //물품 id
    private TextView vName = null;                  //물품명
    private TextView vGetDate = null;               //습득일자
    private TextView vCategory = null;              //분류
    private TextView vTakePlace = null;             //보관장소
    private TextView vTel = null;                   //보관장소 번호
    private TextView vGetPlace = null;              //습득위치_회사명 or 지하철
    private TextView vNotic = null;                 //안내문
    private TextView vStatus = null;                //상태

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        data = (ProductData) getIntent().getSerializableExtra("DATA");

        setContentView(R.layout.find_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle("분실물");


        //set views
        vID = (TextView)findViewById(R.id.find_detail_id);
        vName = (TextView)findViewById(R.id.find_detail_name);
        vGetDate = (TextView)findViewById(R.id.find_detail_getdate);
        vCategory = (TextView)findViewById(R.id.find_detail_category);
        vTakePlace = (TextView)findViewById(R.id.find_detail_takeplace);
        vTel = (TextView)findViewById(R.id.find_detail_take_tel);
        vGetPlace = (TextView)findViewById(R.id.find_detail_getplace);
        vNotic = (TextView)findViewById(R.id.find_detail_thing);
        vStatus = (TextView)findViewById(R.id.find_detail_status);


        initViewData();
    }

    private void initViewData(){

        isSub = (data.code.equals(Globar.FIND_CODE_SUB9) ||
                data.code.equals(Globar.FIND_CODE_SUB14) ||
                data.code.equals(Globar.FIND_CODE_SUB58));


        vID.setText(""+data.id);
        vName.setText(data.name);
        vGetDate.setText(data.date);
        vCategory.setText(data.category);
        vTakePlace.setText(data.takePlace);
        vTel.setText(data.takeTel);
        vStatus.setText(data.status);
        vNotic.setText(data.notic);

        vGetPlace.setText(data.position);
        if(isSub)
            vGetPlace.setText(data.place);

        setEmptyView(vGetDate);
        setEmptyView(vTel);
        setEmptyView(vNotic);
        setEmptyView(vCategory);
        setEmptyView(vTakePlace);
        setEmptyView(vGetPlace);
    }

    private void setEmptyView(TextView v){
        if(v.getText().toString().contains("보관센터로부터"))
            v.setTextColor(Color.GRAY);

        v.invalidate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            onBackPressed();

        return super.onOptionsItemSelected(item);
    }

//
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//
//        try{
//            return super.dispatchTouchEvent(ev);
//        }catch (NullPointerException e){
//            e.printStackTrace();
//        }
//        return false;
//    }
}
