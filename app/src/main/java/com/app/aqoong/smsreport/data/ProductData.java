package com.app.aqoong.smsreport.data;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by aqoong on 2017. 10. 11..
 */

public class ProductData implements Serializable {
    public String code = "";             //CODE : 코드
    public String id = "";               //ID : id
    public String name = "";         //GET_NAME : 습득물품명
    public String category = "";        //CATE : 습득물분류
    public String date = "";         //GET_DATE : 습득일자
    public String takePlace = "";        //TAKE_PLACE : 수령가능장소
    public String takeTel = "";          //CONTACT : 수령가능장소 전화번호
    public String position = "";      //GET_POSITION : 습득위치_회사명
    public String place = "";         //GET_PLACE : 습득위치_지하철
    public String notic = "";            //GET_THING : 상세내용
    public String status = "";           //STATUS : 분실물상태
    public String imageUrl = "";         //IMAGE_URL : 이미지url

    public ProductData(){}

    public ProductData(HashMap<String, Object> map){
        for(int i = 0 ; i < Globar.MapKeys.length ; i++){

            if(!map.containsKey(Globar.MapKeys[i]))
                continue;

            String key = Globar.MapKeys[i];

            switch(i){
                case Globar.FIND_KEY_ID:
                    id = (String)map.get(key);
                    break;
                case Globar.FIND_KEY_CODE:
                    code = (String)map.get(key);
                    break;
                case Globar.FIND_KEY_CATEGORY:
                    category = (String)map.get(key);
                    break;
                case Globar.FIND_KEY_TEL:
                    takeTel = (String)map.get(key);
                    break;
                case Globar.FIND_KEY_DATE:
                    date = (String)map.get(key);
                    break;
                case Globar.FIND_KEY_IMAGE_URL:
                    imageUrl = (String)map.get(key);
                    break;
                case Globar.FIND_KEY_NAME:
                    name = (String)map.get(key);
                    break;
                case Globar.FIND_KEY_PLACE:
                    place = (String)map.get(key);
                    break;
                case Globar.FIND_KEY_POSITION:
                    position = (String)map.get(key);
                    break;
                case Globar.FIND_KEY_STATUS:
                    status = (String)map.get(key);
                    break;
                case Globar.FIND_KEY_TAKEPLACE:
                    takePlace = (String)map.get(key);
                    break;
                case Globar.FIND_KEY_NOTIC:
                    notic = (String)map.get(key);
                    break;
            }
        }
    }
}