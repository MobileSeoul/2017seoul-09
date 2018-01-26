package com.app.aqoong.smsreport.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.app.aqoong.smsreport.R;
import com.app.aqoong.smsreport.data.FindListAdapter;
import com.app.aqoong.smsreport.data.Globar;
import com.app.aqoong.smsreport.data.ProductData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;


/**
 * Created by aqoong on 2017. 10. 11..
 */

public class OpenAPIPaser extends AsyncTask<String, String, String>{

    private Context mContext = null;
    private ProgressDialog progressDialog = null;
    private FindListAdapter mAdapter = null;

    public OpenAPIPaser(Context _context, FindListAdapter adapter, ProgressDialog pd){
        mContext = _context;
        mAdapter = adapter;
        progressDialog = pd;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try{
            URL url = new URL(params[0]);
            connection = (HttpURLConnection)url.openConnection();

            connection.connect();

            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buffer = new StringBuffer();
            String line = "";
            while((line = reader.readLine()) != null){
                buffer.append(line);
//                Log.d("URLPaser",line);
            }


            return buffer.toString();


        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(connection != null)
                connection.disconnect();
            try{
                if(reader != null)
                    reader.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }



        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        while(Globar.findProductList.size() > 0){
            Globar.findProductList.remove(Globar.findProductList.size()-1);
        }

        String msg = "";

        if(s.contains(progressDialog.getContext().getString(R.string.apimsg_non))){
            msg = "데이터가 없습니다";
        }else if(s.contains(progressDialog.getContext().getString(R.string.apimsg_errserver))){
            msg = "[서비스에러]\n데이터제공자에게 문의해야합니다";

        }else if(s.contains(progressDialog.getContext().getString(R.string.apimsg_endservice))){
            msg = "[서비스종료]\n데이터제공자에게 문의해야합니다";
        }

        if(msg.length() > 0)
        {
            AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
            dialog.setMessage(msg);
            dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.show();

            mAdapter.notifyDataSetChanged();
            if(progressDialog.isShowing())
                progressDialog.dismiss();

            return;
        }

        String result = s.split("\"row\":")[1];
        JSONArray array = getJSONArray(result);
        try {
            for(int i = 0 ; i < array.length() ; i++){
                ProductData data = new ProductData();
                JSONObject obj = array.getJSONObject(i);

                data.id = obj.getString(Globar.MapKeys[Globar.FIND_KEY_ID]);
                data.name = obj.getString(Globar.MapKeys[Globar.FIND_KEY_NAME]);
                data.category = obj.getString(Globar.MapKeys[Globar.FIND_KEY_CATEGORY]);
                data.date = obj.getString(Globar.MapKeys[Globar.FIND_KEY_DATE]);
                data.takePlace = obj.getString(Globar.MapKeys[Globar.FIND_KEY_TAKEPLACE]);
                data.takeTel = obj.getString(Globar.MapKeys[Globar.FIND_KEY_TEL]);
                data.position = obj.getString(Globar.MapKeys[Globar.FIND_KEY_POSITION]);
                data.place = obj.getString(Globar.MapKeys[Globar.FIND_KEY_PLACE]);
                data.notic = obj.getString(Globar.MapKeys[Globar.FIND_KEY_NOTIC]);
                data.status = obj.getString(Globar.MapKeys[Globar.FIND_KEY_STATUS]);
                data.imageUrl = obj.getString(Globar.MapKeys[Globar.FIND_KEY_IMAGE_URL]);
                data.code = obj.getString(Globar.MapKeys[Globar.FIND_KEY_CODE]);

                DecimalFormat df = new DecimalFormat("#.#");
                double d = Double.parseDouble(data.id);
                data.id = df.format(d);

//                //비어있는건 건너뛰자
//                if(data.status.length() > 0 && !data.status.contains("보관"))
//                    continue;

                if(data.name.length() <= 0)
                    continue;
                else if(data.name.contains(":"))            //지하철은 이상한 문자가 포함된게 있으니 제외
                    data.name = data.name.split(":")[1];

                if(data.takePlace.contains(":"))            //지하철은 이상한 문자가 포함된게 있으니 제외
                    data.takePlace = data.takePlace.split(":")[1];

                //지하철은 이상한문자가 있더라
                if(data.takeTel.contains(":"))
                    data.takeTel = data.takeTel.split(":")[1];
                data.takeTel = data.takeTel.replaceAll(" ","");

                //안내문 <br> -> \n변경
                if(data.notic.contains("<br>"))
                    data.notic = data.notic.replaceAll("<br>", "\n");

                String notic = mContext.getString(R.string.empty_notic);

                //전화번호 지역번호 넣기
                if(data.takeTel.length() > 0) {
                    if (getCharNumber(data.takeTel, '-') < 2)
                        data.takeTel = "02-" + data.takeTel;
                }else{
                    data.takeTel = notic;
                }

                if(data.category.length() <= 0) data.category = notic;
                if(data.date.length() <= 0) data.date = notic;
                if(data.place.length() <= 0) data.place = notic;
                if(data.notic.length() <= 0) data.notic = notic;
                if(data.takePlace.length() <= 0) data.takePlace = notic;

                Globar.findProductList.add(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }finally {
            mAdapter.setItemList(Globar.findProductList);
            mAdapter.notifyDataSetChanged();

            if(progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }

    private int getCharNumber(String str, char c)
    {
        int count = 0;
        for(int i=0;i<str.length();i++)
        {
            if(str.charAt(i) == c)
                count++;
        }
        return count;
    }


    private JSONArray getJSONArray(String s){
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }
}
