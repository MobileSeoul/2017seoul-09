package com.app.aqoong.smsreport.data;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.HashMap;

/**
 * Created by aqoong on 2017. 9. 21..
 */

public class SMS{
    private Context context = null;
    private HashMap<String, Object> datas = null;

    private int type = 0;

    /**
     *  Reporter
     */
    private String name = null;
    private String phone = null;
    private String email = null;

    private boolean receivePhone = false;
    private boolean receiveEmail = false;

    /**
     *  Report
     */
    private String carInfo = null;
    private String company = null;  //택시:운수회사
    private String number = null;   //버스:노선번호 , 지하철:열차번호
    private String line = null;     //지하철노선

    private String[] address = null;      // 0 : 발생지역 / 1 : 상세위치
//    private String addDetail = null;    //상세위치

    private String time = null;         //발생시간

    private String content = null;      //내용

    public SMS(Context _context, HashMap<String, Object> data, boolean isPhone, boolean isEmail){
        context = _context;
        datas = data;

        type = (int)data.get(Globar.KEY_TYPE);

        receivePhone = isPhone;
        receiveEmail = isEmail;

        /**
         *  reporter
         */
        name = mapParser(Globar.KEY_REPORTER_NAME);
        phone = mapParser(Globar.KEY_REPORTER_PHONE);
        email = mapParser(Globar.KEY_REPORTER_EMAIL);

        /**
         *  content
         */
        switch(type){
            case Globar.TYPE_BUS:
                carInfo = mapParser(Globar.KEY_CARINFO);
                company = mapParser(Globar.KEY_COMPANY);
                number = mapParser(Globar.KEY_LINENUMBER);
                break;
            case Globar.TYPE_SUB:
                line = mapParser(Globar.KEY_LINENUMBER);
                number = mapParser(Globar.KEY_TRAINNUM);
                break;
            case Globar.TYPE_TAXI:
                carInfo = mapParser(Globar.KEY_CARINFO);
                company = mapParser(Globar.KEY_COMPANY);
                break;
        }

        time = mapParser(Globar.KEY_TIME);
        content = mapParser(Globar.KEY_CONTENT);

        address = mapParser(Globar.KEY_LOCATION).split(Globar.SPLIT_FILTER);

    }

    //change sms text
    public String changeSMS(){
        String how = "";
        StringBuffer result = null;

        switch (type){
            case Globar.TYPE_BUS:
                result = new StringBuffer("[버스");
                break;
            case Globar.TYPE_SUB:
                result = new StringBuffer("[지하철");
                break;
            case Globar.TYPE_TAXI:
                result = new StringBuffer("[택시");
                break;
        }
        result.append(" 불편신고]\n");

        //car info
        result.append("- 차량정보\n");
        if(type != Globar.TYPE_SUB) {
            if(!company.equals(null)) {
                result.append("운수회사 : ");
                result.append(company);
            }

            if(type == Globar.TYPE_BUS) {
                result.append("\n버스노선 : ");
                result.append(number);
            }
            result.append("\n차량번호 : ");   result.append(carInfo);
        }else{
            result.append(line);
            result.append(", 열차번호 : "); result.append(number);
        }

        //content
        result.append("\n\n");
        result.append("- 사건정보\n");
        result.append("날짜 및 시간 : "); result.append(time);
        result.append("\n장소 : "); result.append(address[0]);
        if(address.length == 2) {
            result.append(" / ");   result.append(address[1]);
        }
        result.append("\n내용 : "); result.append(content);

        //reporter info
        result.append("\n\n");
        result.append("- 신고자\n");
        result.append("이름 : "); result.append(name);
        if(receivePhone) {
            result.append("\n번호 : ");
            result.append(phone);

            how = "[문자]";
        }
        if(receiveEmail) {
            result.append("\n이메일 : ");
            result.append(email);

            how += "[이메일]";
        }

        /////
        result.append("\n\n");
        result.append("결과통보방법 : ");
        result.append(how);

        return new String(result);
    }

    //send message
    public void sendSMS(String tel){

        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + tel));
        intent.putExtra("sms_body", changeSMS());
        context.startActivity(intent);
    }



    private String mapParser(String key){

        Object obj = null;
        if(datas.containsKey(key))
            obj = datas.get(key);

        if(obj == null)
            return "";
        else
            return obj.toString();
    }
}
