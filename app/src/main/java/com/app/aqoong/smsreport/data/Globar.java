package com.app.aqoong.smsreport.data;

import android.Manifest;

import java.util.ArrayList;

/**
 * Created by aqoong on 2017. 9. 22..
 */

public class Globar {
    public final static String[] permissions = {
            Manifest.permission.SEND_SMS,
//            Manifest.permission.READ_SMS,
//            Manifest.permission.RECEIVE_SMS,

            Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.ACCESS_COARSE_LOCATION
            Manifest.permission.READ_PHONE_STATE
    };

    public final static String pageTitle_0 = "신고";
    public final static String pageTitle_1 = "분실물";

    public final static int TYPE_TAXI = 0;
    public final static int TYPE_BUS = 1;
//    public final static int TYPE_VILBUS = 2;
    public final static int TYPE_SUB = 2;

    public final static String SPLIT_FILTER = "///";


    public final static String KEY_TYPE = "TYPE";

//     common
    public final static String KEY_LOCATION = "LOCATION";
    public final static String KEY_TIME = "TIME";
    public final static String KEY_CONTENT = "CONTENT";
//     taxi or bus
    public final static String KEY_CARINFO = "CARINFO";
    public final static String KEY_COMPANY = "COMPANY";
//    subway
    public final static  String KEY_TRAINNUM = "TRAINNUM";
//  bus or subway
    public final static String KEY_LINENUMBER = "LINENUMBER";

    //UserData
    public final static String KEY_REPORTER_NAME = "NAME";
    public final static String KEY_REPORTER_PHONE = "PHONE";
    public final static String KEY_REPORTER_EMAIL = "EMAIL";


    /**
     *  Find things
     */
    public final static String FIND_CODE_BUS            = "b1";
    public final static String FIND_CODE_VILBUS         = "b2";
    public final static String FIND_CODE_COMPANYTAXI    = "t1";
    public final static String FIND_CODE_TAXI           = "t2";
    public final static String FIND_CODE_SUB14          = "s1";
    public final static String FIND_CODE_SUB58          = "s2";
    public final static String FIND_CODE_KORAIL         = "s3";
    public final static String FIND_CODE_SUB9           = "s4";

    public final static String[] MapKeys = {
        "ID", "GET_NAME", "GET_DATE", "TAKE_PLACE", "CONTACT",
            "CATE", "GET_POSITION", "GET_PLACE", "GET_THING", "STATUS",
            "CODE", "IMAGE_URL"
    };
    public final static int FIND_KEY_ID = 0;
    public final static int FIND_KEY_NAME = 1;
    public final static int FIND_KEY_DATE = 2;
    public final static int FIND_KEY_TAKEPLACE = 3;
    public final static int FIND_KEY_TEL = 4;
    public final static int FIND_KEY_CATEGORY = 5;
    public final static int FIND_KEY_POSITION = 6;
    public final static int FIND_KEY_PLACE = 7;
    public final static int FIND_KEY_NOTIC = 8;
    public final static int FIND_KEY_STATUS = 9;
    public final static int FIND_KEY_CODE = 10;
    public final static int FIND_KEY_IMAGE_URL = 11;

    public static ArrayList<ProductData> findProductList = new ArrayList<ProductData>();



}
