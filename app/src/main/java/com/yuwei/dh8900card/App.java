package com.yuwei.dh8900card;

import android.app.Application;
import android.content.Context;

import com.helper.ReaderHelper;
import com.yuwei.dh8900card.utils.SPUtils;
import com.yuwei.dh8900card.utils.SqliteHelper;
import com.yuwei.dh8900card.utils.Utils;

/**
 * Created by xiaoQ on 2018/4/6.
 */

public class App extends Application {

    public static SPUtils spUtils;

    public static Context AppContext;

    SqliteHelper sqliteHelper;

    public static int REMOTE_COUNT = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        AppContext = this;
        sqliteHelper = SqliteHelper.getInstantiation();
        sqliteHelper.createTable();
        Utils.init(this);
        setSPUp();
        try {
            ReaderHelper.setContext(AppContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSPUp() {
        if (spUtils == null) {
            /**
             * SharedPreferences存储在sd卡中的文件名字
             */
            spUtils = new SPUtils(getPackageName());
        }
    }

}
