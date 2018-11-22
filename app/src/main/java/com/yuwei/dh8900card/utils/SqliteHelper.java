package com.yuwei.dh8900card.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.util.TimeUtils;

import com.yuwei.dh8900card.App;
import com.yuwei.dh8900card.SettingActivity;
import com.yuwei.dh8900card.entity.Record;

import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 * Created by xiaoQ on 2018/4/6.
 */

public class SqliteHelper {

    public final static String DATABASE = Constant.APP_NAME + ".db";

    static Context context = App.AppContext;

    private static SqliteHelper SQLHelper;

    static SQLiteDatabase db = null;

    private static SQLiteDatabase openOrCreateDatabase() {
        try {
            return context.openOrCreateDatabase(DATABASE, Context.MODE_PRIVATE, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static synchronized SqliteHelper  getInstantiation() {
        if (SQLHelper == null) {
            synchronized (SqliteHelper.class) {
                SQLHelper = new SqliteHelper();
            }
        }
        if (db == null)
            db = openOrCreateDatabase();
        return  SQLHelper;
    }

    public void createTable() {
        String recordSql = "create table if not exists Record (" +
                "Id integer primary key autoincrement," + //自增主键
                "type integer," +   //类型
                "cardNo varchar(50)," +
                "gps varchar(40)," +
                "timeStamp varchar(20)," +
                "timeType varchar(20)," +
                "upload integer," +
                "localName varchar(20)," +
                "yyyyMMdd varchar(10)," +
                "deviceNum varchar(10)," +
                "show integer," +
                "toTxt integer)";

        try {
            if(db == null) db = openOrCreateDatabase();
            db.execSQL(recordSql);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /** 获取当天不同的打卡点 */
    public List<String> getLocalNameByToday(String yyyyMMdd) {
        List<String> lists = null;
        Cursor mCursor = null;
        try {
            lists = new ArrayList<>();
            if(db == null) db = openOrCreateDatabase();
            mCursor = db.rawQuery("select  distinct localName from record where yyyyMMdd = ? ", new String[]{yyyyMMdd});
            while (mCursor.moveToNext()) {
                lists.add(mCursor.getString(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseUtils.closeIO(mCursor);
        }
        return lists;
    }


    /** 获取不同的时间 */
    public List<String> getyyyyMMdd() {
        List<String> lists = null;
        Cursor mCursor = null;
        try {
            lists = new ArrayList<>();
            if(db == null) db = openOrCreateDatabase();
            mCursor = db.rawQuery("select  distinct yyyyMMdd from record ", null);
            while (mCursor.moveToNext()) {
                lists.add(mCursor.getString(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseUtils.closeIO(mCursor);
        }
        return lists;
    }

    /** 获取当天打卡记录 */
    public List<Record> getRecordOfDay (String time) {
        List<Record> records = null;
        Cursor cursor = null;

        try {
            if(db == null) db = openOrCreateDatabase();
            cursor = db.rawQuery("select * from record where timeStamp > ? order by timeStamp desc", new String[]{time});
            Record record = null;
            records = new ArrayList<>();
            while (cursor.moveToNext()) {
                record = new Record();
                record.setId(cursor.getInt(cursor.getColumnIndex("Id")));
                record.setCardNo(cursor.getString(cursor.getColumnIndex("cardNo")));
                record.setGps(cursor.getString(cursor.getColumnIndex("gps")));
                record.setTimeStamp(cursor.getString(cursor.getColumnIndex("timeStamp")));
                record.setType(cursor.getInt(cursor.getColumnIndex("type")));
                record.setUpload(cursor.getInt(cursor.getColumnIndex("upload")));
                record.setToTxt(cursor.getInt(cursor.getColumnIndex("toTxt")));
                records.add(record);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseUtils.closeIO(cursor);
        }

        return records;
    }

    /** 获取指定天数和地点的数据 */
    public List<Record> getRecordByToday(String yyyyMMdd, String localName) {

        List<Record> records = null;
        Cursor cursor = null;

        try {
            if(db == null) db = openOrCreateDatabase();
            cursor = db.rawQuery("select * from record where yyyyMMdd = ? and localName = ?  order by timeStamp desc ", new String[]{yyyyMMdd, localName});
            Record record = null;
            records = new ArrayList<>();
            while (cursor.moveToNext()) {
                record = new Record();
                record.setId(cursor.getInt(cursor.getColumnIndex("Id")));
                record.setCardNo(cursor.getString(cursor.getColumnIndex("cardNo")));
                record.setGps(cursor.getString(cursor.getColumnIndex("gps")));
                record.setTimeStamp(cursor.getString(cursor.getColumnIndex("timeStamp")));
                record.setType(cursor.getInt(cursor.getColumnIndex("type")));
                record.setUpload(cursor.getInt(cursor.getColumnIndex("upload")));
                record.setTimeType(cursor.getString(cursor.getColumnIndex("timeType")));
                record.setToTxt(cursor.getInt(cursor.getColumnIndex("toTxt")));
                records.add(record);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseUtils.closeIO(cursor);
        }

        return records;
    }
    /** 获取所有可显示打卡记录 */
    public List<Record> getAllRecord() {

        List<Record> records = null;
        Cursor cursor = null;

        try {
            if(db == null) db = openOrCreateDatabase();
            cursor = db.rawQuery("select * from record where show = 0 order by timeStamp desc", null);
            Record record = null;
            records = new ArrayList<>();
            while (cursor.moveToNext()) {
                record = new Record();
                record.setId(cursor.getInt(cursor.getColumnIndex("Id")));
                record.setCardNo(cursor.getString(cursor.getColumnIndex("cardNo")));
                record.setGps(cursor.getString(cursor.getColumnIndex("gps")));
                record.setTimeStamp(cursor.getString(cursor.getColumnIndex("timeStamp")));
                record.setType(cursor.getInt(cursor.getColumnIndex("type")));
                record.setUpload(cursor.getInt(cursor.getColumnIndex("upload")));
                record.setToTxt(cursor.getInt(cursor.getColumnIndex("toTxt")));
                records.add(record);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseUtils.closeIO(cursor);
        }

        return records;
    }

    /**
     * 设备号:
     标签ID:
     打卡时间：格式为YYYY/MM/DD HH:MM:MM:SS.XXX
     打卡时
     打卡时间类型：单选，分为chiptime,qrtime,manualtime)，
     GPS坐标,
     温度,
     湿度,
     备注: 单选，分为DQ(取消成绩),WD(退赛/收容)

     注意：设备号，标签ID，打卡时间，打卡时间类型是必传，GPS坐标，温度，湿度，备注，有信息则传，无信息则留空。
     */

    /** 获取需要上传的记录 */
    public List<Record> getUploadRecord () {
        List<Record> records = null;
        Cursor cursor = null;

        try {
            if (db == null) db = openOrCreateDatabase();
            cursor = db.rawQuery("select * from record where upload = 0", null);
            Record record = null;
            records = new ArrayList<>();
            while (cursor.moveToNext()) {
                record = new Record();
                record.setId(cursor.getInt(cursor.getColumnIndex("Id")));
                record.setCardNo(cursor.getString(cursor.getColumnIndex("cardNo")));
                record.setGps(cursor.getString(cursor.getColumnIndex("gps")));
                record.setTimeStamp(cursor.getString(cursor.getColumnIndex("timeStamp")));
                record.setTimeType(cursor.getString(cursor.getColumnIndex("timeType")));
                record.setType(cursor.getInt(cursor.getColumnIndex("type")));
                record.setUpload(cursor.getInt(cursor.getColumnIndex("upload")));
                record.setDeviceNum(cursor.getString(cursor.getColumnIndex("deviceNum")));
                record.setYyyyMMdd(DateUtils.times(record.getTimeStamp()));
                records.add(record);
            }
        }  catch (Exception e) {
            e.printStackTrace();
        }

        return records;
    }

    /** 已经写入到文件的数据添加标志 */
    public void uploadToTxtState(int Id) {
        ContentValues cv = new ContentValues();
        cv.put("toTxt", 1);
        try {
            if(db == null) db = openOrCreateDatabase();
            db.update("record", cv, "Id = " + Id, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

    }


    /** 获取最新的十条记录 */
    public List<Record> getShowRecord() {
        List<Record> records = null;
        Cursor cursor = null;

        // order by  timeStamp asc
        try {
            if(db == null) db = openOrCreateDatabase();
            cursor = db.rawQuery("select cardNo, timeStamp  from record where show = 0 order by Id desc  ", null);
            // int count = cursor.getCount();

            Record record = null;
            records = new ArrayList<>();
            int i = 0;
            while (cursor.moveToNext() && i < 10) { //89157214
                record = new Record();
                record.setCardNo(cursor.getString(cursor.getColumnIndex("cardNo")));
                //    record.setGps(cursor.getString(cursor.getColumnIndex("gps")));
                record.setTimeStamp(cursor.getString(cursor.getColumnIndex("timeStamp")));
                //    record.setType(cursor.getInt(cursor.getColumnIndex("type")));
                //    record.setUpload(cursor.getInt(cursor.getColumnIndex("upload")));
                records.add(record);
                i ++;
            }
            Collections.reverse(records);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseUtils.closeIO(cursor);
        }

        return records;
    }


    /** 插入打卡记录 */
    public boolean addRecord(Record record) {
        ContentValues cv = new ContentValues();
        cv.put("type", record.getType());
        cv.put("cardNo", record.getCardNo());
        cv.put("gps",record.getGps());
        cv.put("timeStamp", record.getTimeStamp());
        cv.put("upload", record.getUpload());
        cv.put("localName", record.getLocalName());
        cv.put("yyyyMMdd", record.getYyyyMMdd());
        cv.put("deviceNum", record.getTimeType());
        cv.put("timeType", record.getTimeType());
        cv.put("show", record.getShow());
        try {
            if(db == null) db = openOrCreateDatabase();
            db.insert("record", null, cv);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return false;
    }

    /** 查看数据库中是否存在 */
    public boolean existsOnDataBases(String epcStr) {
        Cursor mCursor = null;
        try {
            if(db == null) db = openOrCreateDatabase();
            mCursor = db.rawQuery("select cardNo  from record where cardNo = ?", new String[]{epcStr});
            if (mCursor.getCount() > 0) {
                return true;
            } else return false;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseUtils.closeIO(mCursor);
        }
        return false;
    }

    /** 返回总共有多少个人打了卡 */
    public int getPlayCardNum() {
        //select count(a.*) from (select distinct 列名 from 表名) a
        Cursor mCursor = null;
        int allCount = 0;
        try {
            if(db == null) db = openOrCreateDatabase();
            mCursor = db.rawQuery("select count(a.cardNo) from (select distinct cardNo from record where show = 0) a ", null);
            while (mCursor.moveToNext()) {
                allCount = mCursor.getInt(0);
            }
            return allCount;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseUtils.closeIO(mCursor);
        }

        return  allCount;
    }

    /** 返回打卡总次数 */
    public int getAllCount () {
        Cursor mCursor = null;
        int allCount = 0;
        try {
            if(db == null) db = openOrCreateDatabase();
            mCursor = db.rawQuery("select (cardNo) from record where show = 0 ", null);
            allCount = mCursor.getCount();
            return allCount;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseUtils.closeIO( mCursor);
        }

        return  allCount;
    }

    /** 使全部数据不可见 */
    public void deleteAllData() {
        try {
            if(db == null) db = openOrCreateDatabase();
            db.execSQL("update record set show = 1", new Object[]{});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

    }

    /** 上传服务器成功，upload置为1 */
    public void upload(Record record) {
        try {
            if(db == null) db = openOrCreateDatabase();
            db.execSQL("update record set upload = 1 where Id = " + record.getId(), new Object[]{});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


























