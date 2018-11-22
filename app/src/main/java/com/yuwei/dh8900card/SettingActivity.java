package com.yuwei.dh8900card;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.yuwei.dh8900card.entity.Record;
import com.yuwei.dh8900card.utils.DateUtils;
import com.yuwei.dh8900card.utils.SqliteHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends Activity implements AdapterView.OnItemSelectedListener {

    @BindView(R.id.back_iv)
    ImageView backIv;
    @BindView(R.id.save_iv)
    ImageView saveIv;
    @BindView(R.id.dev_num_text_tv)
    TextView devNumTextTv;
    @BindView(R.id.dev_num_tv)
    EditText devNumTv;
    @BindView(R.id.local_name_tv)
    EditText localNameTv;
    @BindView(R.id.time_format_sp)
    Spinner timeFormatSp;
    @BindView(R.id.time_format_rl)
    RelativeLayout timeFormatRl;
    @BindView(R.id.upload_rate_sp)
    Spinner uploadRateSp;
    @BindView(R.id.out_of_data_btn)
    Button outOfDataBtn;

    SqliteHelper sqliteHelper;

    public static final String[] uploadRate = {"即时", "3秒", "5秒", "10秒", "60秒"};

    public static final String[] timeFormat = {"yyyy-MM-dd", "MM-dd-yyyy", "dd-MM-yyyy"};

    public static final String[] timeType = {"chiptime", "qrtime", "manualtime"};  //chiptime,qrtime,manualtime

    @BindView(R.id.set_chat_btn)
    Button setChatBtn;
    @BindView(R.id.title_rl) RelativeLayout titleRl;
    @BindView(R.id.dev_num_rl) RelativeLayout devNumRl;
    @BindView(R.id.ll_3) LinearLayout ll3;
    @BindView(R.id.textView2) TextView textView2;
    @BindView(R.id.local_name_rl) RelativeLayout localNameRl;
    @BindView(R.id.textView3) TextView textView3;
    @BindView(R.id.time_type_tv) TextView timeTypeTv;
    @BindView(R.id.time_type_sp) Spinner timeTypeSp;
    @BindView(R.id.time_type_rl) RelativeLayout timeTypeRl;
    @BindView(R.id.text5) TextView text5;
    @BindView(R.id.upload_rate_rl) RelativeLayout uploadRateRl;
    @BindView(R.id.out_of_all_data_btn) Button outOfAllDataBtn;

    private ArrayAdapter uploadRateAdapter = null;

    private ArrayAdapter timeFormatAdapter = null;

    private ArrayAdapter timeTypeAdapter = null;

    public static final String DEV_NUM = "dev_num";

    public static final String LOCAL_NAME = "local_name";

    public static final String TIME_FORMAT = "time_format";

    public static final String TIME_TYPE = "time_type";

    public static final String UPLOAD_RATE = "upload_rate";

    private final static int REQUEST_WRITE_EXTERNAL_STORAGE = 5;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        mContext = this;
        checkPermission();


        sqliteHelper = SqliteHelper.getInstantiation();

    }

    ProgressDialog pd2;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                pd2 = ProgressDialog.show(mContext, "提示", "正在导出数据", false, false);
            } else {
                if (pd2 != null)
                    pd2.dismiss();
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    private void initView() {
        timeFormatAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, timeFormat);
        timeFormatAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        timeFormatSp.setAdapter(timeFormatAdapter);
        timeFormatSp.setOnItemSelectedListener(this);
        int select = App.spUtils.getInt(TIME_FORMAT);
        if (select >= 0)
            timeFormatSp.setSelection(select);

        timeFormatSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //showPrice(position);
                TextView tv = (TextView) view;
                tv.setTextColor(getResources().getColor(R.color.white));    //设置颜色

                tv.setTextSize(18.0f);    //设置大小

                //    tv.setGravity(android.view.Gravity.CENTER_HORIZONTAL);   //设置居中
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        timeTypeAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, timeType);
        timeTypeAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        timeTypeSp.setAdapter(timeTypeAdapter);
        timeTypeSp.setOnItemSelectedListener(this);
        select = App.spUtils.getInt(TIME_TYPE);
        if (select >= 0)
            timeTypeSp.setSelection(select);

        timeTypeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //showPrice(position);
                TextView tv = (TextView) view;
                tv.setTextColor(getResources().getColor(R.color.white));    //设置颜色

                tv.setTextSize(18.0f);    //设置大小

                //    tv.setGravity(android.view.Gravity.CENTER_HORIZONTAL);   //设置居中
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //  timeFormatSp.setSelection(App.spUtils.getInt(TIME_FORMAT));

        uploadRateAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, uploadRate);
        uploadRateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        uploadRateSp.setAdapter(uploadRateAdapter);
        uploadRateSp.setOnItemSelectedListener(this);
        select = App.spUtils.getInt(UPLOAD_RATE);
        if (select >= 0)
            uploadRateSp.setSelection(select);
        //     uploadRateSp.setSelection(App.spUtils.getInt(UPLOAD_RATE));

        uploadRateSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //showPrice(position);
                TextView tv = (TextView) view;
                tv.setTextColor(getResources().getColor(R.color.white));    //设置颜色

                tv.setTextSize(18.0f);    //设置大小

                //    tv.setGravity(android.view.Gravity.CENTER_HORIZONTAL);   //设置居中
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        devNumTv.setText(App.spUtils.getString(DEV_NUM));
        localNameTv.setText(App.spUtils.getString(LOCAL_NAME));

    }

    @OnClick({R.id.back_iv, R.id.save_iv, R.id.out_of_data_btn, R.id.out_of_all_data_btn, R.id.set_chat_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                SettingActivity.this.finish();
                break;

            case R.id.save_iv:
                String devNo = devNumTv.getText().toString().trim();
                String localName = localNameTv.getText().toString().trim();
                App.spUtils.put(DEV_NUM, devNo);
                App.spUtils.put(LOCAL_NAME, localName);
                int position =  timeFormatSp.getSelectedItemPosition();
                App.spUtils.put(TIME_FORMAT, timeFormatSp.getSelectedItemPosition());
                App.spUtils.put(TIME_TYPE, timeTypeSp.getSelectedItemPosition());
                position = timeTypeSp.getSelectedItemPosition();
                App.spUtils.put(UPLOAD_RATE, uploadRateSp.getSelectedItemPosition());
                Toast.makeText(SettingActivity.this, "数据已保存", Toast.LENGTH_SHORT).show();
                break;

            case R.id.out_of_data_btn:
                pd2 = ProgressDialog.show(mContext, "提示", "正在导出数据", false, false);
                new Thread() {
                    @Override
                    public void run() {
                        saveFile();
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                pd2.dismiss();
                            }
                        }, 500);
                    }
                }.start();
                break;

            case R.id.set_chat_btn:
                startActivity(new Intent(mContext, CustomCharActivity.class));
                break;

            case R.id.out_of_all_data_btn:
                pd2 = ProgressDialog.show(mContext, "提示", "正在导出数据", false, false);
                new Thread() {
                    @Override
                    public void run() {
                        saveFileAllData();
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                pd2.dismiss();
                            }
                        }, 500);
                    }
                }.start();

                break;

        }
    }

    // 导出数据：导出全部数据，并非全部数据都写在一个文件中，而是按 设备号+CP点名称+日期.txt 。每日生成的一个文件，当日的数据全部放在这个文件里。比如历史数据有30天。那么就是30个文件。另：所有文件内的数据，按时间升序排列.导出数据：导出全部数据，并非全部数据都写在一个文件中，而是按 设备号+CP点名称+日期.txt 。每日生成的一个文件，当日的数据全部放在这个文件里。比如历史数据有30天。那么就是30个文件。另：所有文件内的数据，按时间升序排列
    private void saveFileAllData() {
        List<String> allTimeList = sqliteHelper.getyyyyMMdd();
        for (String yyyyMMdd : allTimeList) {
            List<String> localNameList = sqliteHelper.getLocalNameByToday(yyyyMMdd);
            for (String localName : localNameList) {

                String filePath = "";
                String filePathDir = "";
                boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
                if (hasSDCard) { // SD卡根目录的hello.text
                    filePathDir = Environment.getExternalStorageDirectory().toString() + File.separator + "data_log/" + "全部数据/" + yyyyMMdd + "/" + localName;
                    filePath = filePathDir + "/" + App.spUtils.getString(DEV_NUM) + "_" +
                            localName + ".txt";

                }
                try {
                    List<Record> recordList = sqliteHelper.getRecordByToday(yyyyMMdd, localName);
                    File file = new File(filePath);
                    if (!file.exists()) {
                        File dir = new File(file.getParent());
                        dir.mkdirs();
                        boolean isSuccess = file.createNewFile();
                        Log.i("yuweiTest", "mkdirs isSuccess = " + isSuccess);
                        FileOutputStream outStream = new FileOutputStream(file);
                        if (recordList != null) {
                            for (Record record : recordList) {
                                outStream.write(record.toBak().getBytes());
                                outStream.write("\r\n".getBytes());   //换行
                            }
                        }
                        outStream.close();
                    } else {
                        file.delete();
                        File dir = new File(file.getParent());
                        dir.mkdirs();
                        boolean isSuccess = file.createNewFile();
                        Log.i("yuweiTest", "mkdirs isSuccess = " + isSuccess);
                        FileOutputStream outStream = new FileOutputStream(file);
                        if (recordList != null) {
                            for (Record record : recordList) {
                                //    if (record.getToTxt() == 1) continue;
                                outStream.write(record.toBak().getBytes());
                                outStream.write("\r\n".getBytes());   //换行
                                sqliteHelper.uploadToTxtState(record.getId());
                            }
                        }
                        outStream.close();
                    }

                    Uri uri = Uri.fromFile(file);
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
                    mContext.sendBroadcast(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.i("yqLuo", "view = " + view.getId() + "\n position = " + position);
        switch (parent.getId()) {
            case R.id.time_format_sp:
                App.spUtils.put(TIME_FORMAT, position);
                Log.i("yqLuo", "点击了时间格式sp position = " + position);
                break;

            case R.id.time_type_sp:
                App.spUtils.put(TIME_TYPE, position);
                Log.i("yqLuo", "点击了时间类型sp position = " + position);
                break;

            case R.id.upload_rate_sp:
                App.spUtils.put(UPLOAD_RATE, position);
                Log.i("yqLuo", "点击了上传数据频率sp position = " + position);
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public void saveFile() {
        String yyyyMMdd = DateUtils.getYYMMDDToday();
        List<String> localNameList = sqliteHelper.getLocalNameByToday(yyyyMMdd);
        for (String localName : localNameList) {

            String filePath = "";
            String filePathDir = "";
            boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
            if (hasSDCard) { // SD卡根目录的hello.text
                filePathDir = Environment.getExternalStorageDirectory().toString() + File.separator + "data_log/" + "当天数据/" + "/" + localName;
                filePath = filePathDir + "/" + App.spUtils.getString(DEV_NUM) + "_" +
                        localName + ".txt";

            }
            try {
                List<Record> recordList = sqliteHelper.getRecordByToday(yyyyMMdd, localName);
                File file = new File(filePath);
                if (!file.exists()) {
                    File dir = new File(file.getParent());
                    dir.mkdirs();
                    boolean isSuccess = file.createNewFile();
                    Log.i("yuweiTest", "mkdirs isSuccess = " + isSuccess);
                    FileOutputStream outStream = new FileOutputStream(file);
                    if (recordList != null) {
                        for (Record record : recordList) {
                            outStream.write(record.toString().getBytes());
                            outStream.write("\r\n".getBytes());   //换行
                        }
                    }
                    outStream.close();
                } else {
                    file.delete();
                    File dir = new File(file.getParent());
                    dir.mkdirs();
                    boolean isSuccess = file.createNewFile();
                    Log.i("yuweiTest", "mkdirs isSuccess = " + isSuccess);
                    FileOutputStream outStream = new FileOutputStream(file);
                    if (recordList != null) {
                        for (Record record : recordList) {
                            //    if (record.getToTxt() == 1) continue;
                            outStream.write(record.toString().getBytes());
                            outStream.write("\r\n".getBytes());   //换行
                            sqliteHelper.uploadToTxtState(record.getId());
                        }
                    }
                    outStream.close();
                }

                Uri uri = Uri.fromFile(file);
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
                mContext.sendBroadcast(intent);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    private void checkPermission() {
        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
            }
            //申请权限
            //    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
            this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);

        } else {
            //   Toast.makeText(this, "授权成功！", Toast.LENGTH_SHORT).show();
            Log.i("yuweiTest", "checkPermission: 已经授权！");
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    SettingActivity.this.finish();
                }
            }
            break;

        }
    }

}


