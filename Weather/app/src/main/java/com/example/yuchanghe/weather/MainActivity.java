package com.example.yuchanghe.weather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Network;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Map;
import java.util.logging.LogRecord;

import bean.TodayWeather;
import util.NetUtil;


public class MainActivity extends ActionBarActivity implements View.OnClickListener{
    private ImageView mUpdateBtn;
    private ProgressBar mUpdatePro;
    private TextView cityTv,timeTv,humidityTv,weekTv,pmDataTv,pmQulityTv,temperatureTv,climateTv,windTv;
    private ImageView weatherImg,pmImg;
    private ImageView mCitySelect;
    private static final int UPDATE_TODAY_WEATHER=1;
    private Handler mHandler=new Handler() {
        public void handleMessage(android.os.Message msg){
            switch (msg.what){
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather)msg.obj);
                    mUpdatePro.setVisibility(View.GONE);
                    mUpdateBtn.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
    };

    void initView(){
        cityTv=(TextView)findViewById(R.id.city);
        timeTv=(TextView)findViewById(R.id.time);
        humidityTv=(TextView)findViewById(R.id.humidity);
        weekTv=(TextView)findViewById(R.id.week_today);
        pmDataTv=(TextView)findViewById(R.id.pm_data);
        pmQulityTv=(TextView)findViewById(R.id.pm2_5_quality);
        pmImg=(ImageView)findViewById(R.id.pm2_5_img);
        temperatureTv=(TextView)findViewById(R.id.temperature);
        climateTv=(TextView)findViewById(R.id.climate);
        windTv=(TextView)findViewById(R.id.wind);
        weatherImg=(ImageView)findViewById(R.id.weather_img);
        cityTv.setText("N/A");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        weekTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQulityTv.setText("N/A");
        temperatureTv.setText("N/A");
        climateTv.setText("N/A");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);
        mUpdateBtn=(ImageView)findViewById(R.id.title_update_btn);
        mUpdatePro=(ProgressBar)findViewById(R.id.title_update_progress);
        mUpdateBtn.setOnClickListener(this);
        initView();
          mCitySelect=(ImageView)findViewById(R.id.title_city_manager);
          mCitySelect.setOnClickListener(this);
        Log.d("MainActivite", "MainActivite->onCreate");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view){
        if(view.getId()==R.id.title_update_btn) {
           // Toast.makeText(this,"vvvvvvvvv",Toast.LENGTH_LONG).show();
           mUpdatePro.setVisibility(View.VISIBLE);
            mUpdateBtn.setVisibility(View.INVISIBLE);
            SharedPreferences sharedPreferences=getSharedPreferences("config",MODE_PRIVATE);
            String cityCode=sharedPreferences.getString("main_city_code", "101010100");
            Log.d("myweather", cityCode);
            if(NetUtil.getNetworkState(this)!=NetUtil.NETWORN_NONE){
                 Log.d("myweather", "网络OK");
                queryWeatherCode(cityCode);
            }else{
                mUpdatePro.setVisibility(View.GONE);
                mUpdateBtn.setVisibility(View.VISIBLE);
                Log.d("myweather","网络未连接");
                Toast.makeText(MainActivity.this,"网络未连接",Toast.LENGTH_LONG).show();
            }


        }
        if(view.getId()==R.id.title_city_manager){
            Intent i=new Intent(getApplicationContext(),SelectCity.class);
            startActivityForResult(i,1);
        }

    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode==1&&resultCode==RESULT_OK){
           // String str=data.getStringExtra("number");
            Map<String,Object> item=(Map<String,Object>)data.getSerializableExtra("item");
            String name= (String) item.get("c_name");
            String citycode=(String)item.get("c_number");
            queryWeatherCode(citycode);
            String str=(name+"  "+citycode);
            Toast.makeText(getApplicationContext(),str,Toast.LENGTH_LONG).show();
        }

    }
    private void queryWeatherCode(String cityCode){
        final String address="http://wthrcdn.etouch.cn/WeatherApi?citykey="+cityCode;
        Log.d("myweather",address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn=null;
                try {
                   URL url=new URL(address);
                    conn=(HttpURLConnection)url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(8000);
                    conn.setReadTimeout(8000);
                    InputStream input=conn.getInputStream();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(input));
                    StringBuilder response=new StringBuilder();
                    String str;
                    while((str=reader.readLine())!=null){
                        response.append(str);
                    }
                    String responseStr=response.toString();
                    Log.d("myweather", responseStr);
                    TodayWeather todayWeather=new TodayWeather();
                   todayWeather= parasXML(responseStr);
                    if(todayWeather!=null)
                    {
                        Log.d("myapp2",todayWeather.toString());
                        Message msg=new Message();
                        msg.what=UPDATE_TODAY_WEATHER;
                        msg.obj=todayWeather;
                       mHandler.sendMessage(msg);
                    }



                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }).start();
    }
    private TodayWeather parasXML(String xmldata){
        TodayWeather todayWeather=null;
        try{
            int fengxiangCount=0;
            int fengliCount=0;
            int dateCount=0;
            int highCount=0;
            int lowCount=0;
            int typeCount=0;
            XmlPullParserFactory fac=XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser=fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int evevType=xmlPullParser.getEventType();
            Log.d("myapp2","parasXML");
            while(evevType!=XmlPullParser.END_DOCUMENT){
                switch(evevType){
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if(xmlPullParser.getName().equals("resp")){
                            todayWeather=new TodayWeather();
                        }
                        if(todayWeather!=null) {
                            if (xmlPullParser.getName().equals("city")) {
                                evevType = xmlPullParser.next();
                                todayWeather.setCity(xmlPullParser.getText());
                              //  Log.d("myapp2", "city:  " + xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("updatetime")) {
                                evevType = xmlPullParser.next();
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                               // Log.d("myapp2", "update:  " + xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("shidu")) {
                                evevType = xmlPullParser.next();
                                todayWeather.setShidu(xmlPullParser.getText());
                               // Log.d("myapp2", "shidu:  " + xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("wendu")) {
                                evevType = xmlPullParser.next();
                                todayWeather.setWendu(xmlPullParser.getText());
                               // Log.d("myapp2", "wendu:  " + xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("pm25")) {
                                evevType = xmlPullParser.next();
                                todayWeather.setPm25(xmlPullParser.getText());
                                //Log.d("myapp2", "pm2.5:  " + xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("quality")) {
                                evevType = xmlPullParser.next();
                                todayWeather.setQuality(xmlPullParser.getText());
                               // Log.d("myapp2", "quality:  " + xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                                evevType = xmlPullParser.next();
                                todayWeather.setFengxiang(xmlPullParser.getText());
                               // Log.d("myapp2", "fengxiang:  " + xmlPullParser.getText());
                                fengxiangCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                                evevType = xmlPullParser.next();
                                todayWeather.setFengli(xmlPullParser.getText());
                               // Log.d("myapp2", "fengli:  " + xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                                evevType = xmlPullParser.next();
                                todayWeather.setDate(xmlPullParser.getText());
                                Log.d("myapp2", "date:  " + xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                                evevType = xmlPullParser.next();
                                todayWeather.setHigh(xmlPullParser.getText());
                               // Log.d("myapp2", "date:  " + xmlPullParser.getText());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                                evevType = xmlPullParser.next();
                                todayWeather.setLow(xmlPullParser.getText());
                               // Log.d("myapp2", "low:  " + xmlPullParser.getText());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                                evevType = xmlPullParser.next();
                                todayWeather.setType(xmlPullParser.getText());
                               // Log.d("myapp2", "type:  " + xmlPullParser.getText());
                                typeCount++;
                            }
                        }
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                }
                evevType=xmlPullParser.next();

            }

        }catch (Exception e){
            e.printStackTrace();
         }
        return todayWeather;
    }
    void updateTodayWeather(TodayWeather todayWeather){
        Log.d("myapp3",todayWeather.toString());
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime() + "更新");
        humidityTv.setText("湿度" + todayWeather.getShidu());
        weekTv.setText(todayWeather.getDate());
        pmDataTv.setText(todayWeather.getPm25());
        pmQulityTv.setText(todayWeather.getQuality());
        temperatureTv.setText(todayWeather.getHigh()+"~"+todayWeather.getLow());
        climateTv.setText(todayWeather.getType());
        windTv.setText(todayWeather.getFengli());
        Toast.makeText(MainActivity.this,"更新成功",Toast.LENGTH_LONG).show();
    }
}
