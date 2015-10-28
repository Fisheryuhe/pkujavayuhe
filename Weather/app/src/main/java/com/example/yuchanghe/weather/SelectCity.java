package com.example.yuchanghe.weather;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import bean.City;
import db.CityDB;
import myapp.MyApplication;

/**
 * Created by yuchanghe on 2015/10/16.
 */
public class SelectCity extends Activity implements View.OnClickListener {
 private  ImageView mBackBtn;
    private ListView cityListView;
    private List<Map<String,Object>> citylist;
    private EditText search_edit;
    SimpleAdapter sa;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);
        Application myApplication=MyApplication.getInstance();
        Log.d("listapp","selectcity-------myapplication");
        mBackBtn=(ImageView)findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);
        cityListView=(ListView)findViewById(R.id.citilistView);

        citylist=new ArrayList<Map<String, Object>>();
        getCityListName();

        String []from={"c_name"};
        int [] to={R.id.list_city_name};
        sa =new SimpleAdapter(this,citylist,R.layout.my_list_view,from,to);

        cityListView.setAdapter(sa);
        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> item = (Map<String, Object>) sa.getItem(position);
                String city_id = (String) item.get("c_number");
                Toast.makeText(getApplicationContext(), "citynumber:" + city_id,
                        Toast.LENGTH_SHORT).show();
                Intent i = new Intent();
                Bundle b = new Bundle();
                b.putSerializable("item", (Serializable) item);
                i.putExtras(b);
                //  i.putExtra("number",city_id);
                setResult(RESULT_OK, i);
                finish();
            }
        });

        search_edit=(EditText)findViewById(R.id.search_edit);
        search_edit.addTextChangedListener(mTextWatcher);

    }
    TextWatcher mTextWatcher=new TextWatcher() {
        private CharSequence temp;
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            temp=s;
            Log.d("myapp","beforeTextChanged:"+temp) ;

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.d("myapp","onTextChanged:"+s) ;
        }


        @Override
        public void afterTextChanged(Editable s) {
            searchCity(s);
            sa.notifyDataSetChanged();


        }
    };
    private void searchCity(Editable s){
        citylist.clear();
        MyApplication mApp=MyApplication.getInstance();
        CityDB citydb=mApp.getmCityDB();
        List<City> templist;
        templist=citydb.findCity(s.toString());
        for(City mcity: templist) {
            Map<String,Object> item=new HashMap<String,Object>();
            item.put("c_name",mcity.getCity());
            item.put("c_province",mcity.getProvince());
            item.put("c_number",mcity.getNumber());
            item.put("c_firstPY",mcity.getFirstPY());
            item.put("c_allFirstPY",mcity.getAllFirstPY());
            item.put("c_allPY",mcity.getAllPY());
            citylist.add(item);
        }

    }
    private  void getCityListName(){

    //Application myapp=MyApplication.getInstance();
         for(City mcity: MyApplication.mCityList) {
           Map<String,Object> item=new HashMap<String,Object>();
            item.put("c_name",mcity.getCity());
            item.put("c_province",mcity.getProvince());
            item.put("c_number",mcity.getNumber());
            item.put("c_firstPY",mcity.getFirstPY());
            item.put("c_allFirstPY",mcity.getAllFirstPY());
            item.put("c_allPY",mcity.getAllPY());
            citylist.add(item);
        }

    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.title_back:
//                Intent i=new Intent(getApplicationContext(),MainActivity.class);
//                startActivity(i);
                finish();
                break;
            default:
                break;
        }

    }
}
