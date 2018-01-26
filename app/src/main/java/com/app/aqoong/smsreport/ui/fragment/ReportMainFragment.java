package com.app.aqoong.smsreport.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.aqoong.smsreport.R;
import com.app.aqoong.smsreport.data.Globar;
import com.app.aqoong.smsreport.ui.PagerActivity;
import com.app.aqoong.smsreport.ui.ReportInsertActivity;


/**
 * Created by aqoong on 2017. 10. 11..
 */

public class ReportMainFragment extends Fragment {
    private Context mContext;

    public ReportMainFragment(){

    }
    @SuppressLint("ValidFragment")
    public ReportMainFragment(Context _context){
        mContext = _context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.intro, null);


        view.findViewById(R.id.btn_taxi).setOnClickListener(btnClickListener);
        view.findViewById(R.id.btn_bus).setOnClickListener(btnClickListener);
//        findViewById(R.id.btn_vilbus).setOnClickListener(btnClickListener);
        view.findViewById(R.id.btn_sub).setOnClickListener(btnClickListener);

        return view;
    }

    private View.OnClickListener btnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(mContext, ReportInsertActivity.class);

            switch(v.getId()){
                case R.id.btn_taxi:
                    intent.putExtra("TYPE", Globar.TYPE_TAXI);
                    break;
                case R.id.btn_bus:
                    intent.putExtra("TYPE",Globar.TYPE_BUS);
                    break;
//                case R.id.btn_vilbus:
//                    intent.putExtra("TYPE",Globar.TYPE_VILBUS);
//                    break;
                case R.id.btn_sub:
                    intent.putExtra("TYPE",Globar.TYPE_SUB);
                    break;
            }

            startActivity(intent);
        }
    };
}
