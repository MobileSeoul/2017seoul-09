package com.app.aqoong.smsreport.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.app.aqoong.smsreport.R;
import com.app.aqoong.smsreport.data.FindListAdapter;
import com.app.aqoong.smsreport.data.Globar;
import com.app.aqoong.smsreport.util.OpenAPIPaser;

import java.util.Locale;

/**
 * Created by aqoong on 2017. 9. 22..
 */

@SuppressLint("ValidFragment")
public class FindThingsFragment extends Fragment {
    private boolean isDebug = false;

    private Activity mActivity = null;

    private Spinner spinTransType = null;
    private EditText editSearch = null;

    private View panelSearch = null;
    private View panelTableGrid = null;
    private WebView mWebView = null;

    private RecyclerView mRecyclerView = null;
    private LinearLayoutManager mLayoutManager = null;
    private FindListAdapter mAdapter = null;

    private ProgressDialog progressDialog = null;
    private InputMethodManager imm = null;

    public FindThingsFragment() {}


    @SuppressLint("ValidFragment")
    public FindThingsFragment(Activity activity) {
        mActivity = activity;

        mLayoutManager = new LinearLayoutManager(mActivity);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mAdapter = new FindListAdapter(mActivity, Globar.findProductList);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        //키보드 내리기
        View view = inflater.inflate(R.layout.find_main, null);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        panelSearch = view.findViewById(R.id.find_panel_search);
        panelTableGrid = view.findViewById(R.id.find_panel_grid);
        mWebView = (WebView)view.findViewById(R.id.web);
        mWebView.setVisibility(View.GONE);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
//        mWebView.setInitialScale(1);

        mWebView.setWebViewClient(new WebViewClient());


        spinTransType = (Spinner) view.findViewById(R.id.find_spin);
        spinTransType.setSelection(0);

        editSearch = (EditText)view.findViewById(R.id.find_edit_search);
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                mAdapter.filter(editSearch.getText().toString().toLowerCase(Locale.getDefault()));
            }
        });

        mRecyclerView = (RecyclerView) view.findViewById(R.id.find_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        spinTransType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                editSearch.setText("");

                ConnectivityManager cm =
                        (ConnectivityManager)mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();

                if(!isConnected) {
                    Toast.makeText(mActivity, "인터넷이 연결되어 있지않아 분실물 서비스에 제한이 있습니다",Toast.LENGTH_LONG).show();
                    return;
                }

                if(spinTransType.getSelectedItemPosition() == 8){
                    //경찰청 유실물 센터 웹뷰
                    panelSearch.setVisibility(View.GONE);
                    panelTableGrid.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.GONE);
                    mWebView.setVisibility(View.VISIBLE);
                    mWebView.loadUrl("https://lost112.go.kr/find/findList.do");
                    return;
                }else{
                    panelSearch.setVisibility(View.VISIBLE);
                    panelTableGrid.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mWebView.setVisibility(View.GONE);
                }

                //search
                //connect openAPI
                String urlFront = null, urlBack = null, keyValue = null, keyValue_Debug = null;
                try {
                    ApplicationInfo appInfo = mActivity.getPackageManager().getApplicationInfo(mActivity.getPackageName(),
                            PackageManager.GET_META_DATA);
                    Bundle bundle = appInfo.metaData;
                    urlFront = bundle.getString("URL_front");
                    urlBack = bundle.getString("URL_back");
                    keyValue = bundle.getString("DATA.key");
                    keyValue_Debug = bundle.getString("DATA.key.Debug");
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                progressDialog = new ProgressDialog(mActivity);
                progressDialog.setMessage("Loading...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

                OpenAPIPaser apiPaser = new OpenAPIPaser(mActivity, mAdapter, progressDialog);

                if(!isDebug)
                    apiPaser.execute(urlFront+keyValue+urlBack + "1/100/" + getType(spinTransType.getSelectedItemPosition()) +"/");
                else
                    apiPaser.execute(urlFront+keyValue_Debug+urlBack + "1/5/" + getType(spinTransType.getSelectedItemPosition()) +"/");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        view.findViewById(R.id.find_btn_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.filter(editSearch.getText().toString().toLowerCase(Locale.getDefault()));
            }
        });

        return view;
    }


    private String getType(int spinItem){
        String result = null;

        switch (spinItem){
            case 0:
                //버스
                result = Globar.FIND_CODE_BUS;
                break;
            case 1:
                //1~4호선
                result = Globar.FIND_CODE_SUB14;
                break;
            case 2:
                //법인택시
                result = Globar.FIND_CODE_COMPANYTAXI;
                break;
            case 3:
                //개인택시
                result = Globar.FIND_CODE_TAXI;
                break;
            case 4:
                //코레일
                result = Globar.FIND_CODE_KORAIL;
                break;
            case 5:
                //마을버스
                result = Globar.FIND_CODE_VILBUS;
                break;
            case 6:
                //5~8호선
                result = Globar.FIND_CODE_SUB58;
                break;
            case 7:
                //9호선
                result = Globar.FIND_CODE_SUB9;
                break;
            case 8:
                //경찰청 사이트
                break;

        }

        return result;
    }

    public boolean isStatusWeb(){
        if(spinTransType.getSelectedItemPosition() == 8)
            return true;
        else return false;
    }

    public WebView getWebView(){
        return mWebView;
    }


}
