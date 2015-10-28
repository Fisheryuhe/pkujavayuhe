package db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import bean.City;

/**
 * Created by yuchanghe on 2015/10/16.
 */
public class CityDB {
    public static final String CITY_DB_NAME="city.db";
    public static final String CITY_TABLE_NAME="city";
    private SQLiteDatabase db;
    public CityDB(Context context,String path){
        db=context.openOrCreateDatabase(path,Context.MODE_PRIVATE,null);
        //db=SQLiteDatabase.openOrCreateDatabase(path,null);

    }
    public List<City> getAllCity(){
        List<City> list=new ArrayList();
        Cursor c=db.rawQuery("Select * from "+CITY_TABLE_NAME,null);
        while (c.moveToNext()){
            String province=c.getString(c.getColumnIndex("province"));
            String city=c.getString(c.getColumnIndex("city"));
            String number=c.getString(c.getColumnIndex("number"));
            String allPY=c.getString(c.getColumnIndex("allpy"));
            String allFirstPY=c.getString(c.getColumnIndex("allfirstpy"));
            String firstPY=c.getString(c.getColumnIndex("firstpy"));
          City item=new City(province,city,number,firstPY,allFirstPY,allPY);
            list.add(item);
        }
        return list;
    }
    public List<City> findCity(String str){
        List<City> list=new ArrayList();
        String sql="Select * from "+CITY_TABLE_NAME+" where city like ?";
        String stra="%"+str+"%";
        Cursor c=db.rawQuery(sql,new String[]{stra});
        while (c.moveToNext()){
            String province=c.getString(c.getColumnIndex("province"));
            String city=c.getString(c.getColumnIndex("city"));
            String number=c.getString(c.getColumnIndex("number"));
            String allPY=c.getString(c.getColumnIndex("allpy"));
            String allFirstPY=c.getString(c.getColumnIndex("allfirstpy"));
            String firstPY=c.getString(c.getColumnIndex("firstpy"));
            City item=new City(province,city,number,firstPY,allFirstPY,allPY);
            list.add(item);
        }
        return list;


    }


}
