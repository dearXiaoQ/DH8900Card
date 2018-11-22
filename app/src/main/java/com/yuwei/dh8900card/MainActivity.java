package com.yuwei.dh8900card;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.device.ScanDevice;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.base.ReaderBase;
import com.github.shenyuanqing.zxingsimplify.zxing.Activity.CaptureActivity;
import com.helper.InventoryBuffer;
import com.helper.ReaderHelper;
import com.helper.ReaderSetting;
import com.magicrf.uhfreaderlib.reader.UhfReader;
import com.rfid.ReaderConnector;
import com.yuwei.dh8900card.entity.Record;
import com.yuwei.dh8900card.uhf.ScreenStateReceiver;
import com.yuwei.dh8900card.uhf.UhfReaderDevice;
import com.yuwei.dh8900card.uhf.Util;
import com.yuwei.dh8900card.utils.DateUtils;
import com.yuwei.dh8900card.utils.NetworkUtils;
import com.yuwei.dh8900card.utils.SqliteHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pda.serialport.SerialPort;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * create by yuwei xiaoQ
 * 2018/03/31
 * 读卡主界面
 */
public class MainActivity extends Activity {


    @BindView(R.id.dev_num)
    TextView devNum;
    @BindView(R.id.yy_mm_dd_tv)
    TextView yyMmDdTv;
    @BindView(R.id.hh_mm_ss)
    TextView hhMmSs;
    @BindView(R.id.time_synchronization_btn)
    Button timeSynchronizationbtn;
    @BindView(R.id.play_card_count_tv)
    TextView playCardCountTv;
    @BindView(R.id.play_card_tv)
    TextView playCardTv;
    @BindView(R.id.local_name_tv)
    TextView localNameTv;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.add_iv)
    ImageView addIv;
    @BindView(R.id.more_iv)
    ImageView moreIv;
    @BindView(R.id.remote_card_num_tv)
    TextView remoteCardNumTv;
    @BindView(R.id.remote_tv)
    TextView remoteTv;
    @BindView(R.id.up_arrow_iv)
    ImageView upArrowIv;
    @BindView(R.id.play_card_record_lv)
    ListView playCardRecordLv;
    @BindView(R.id.play_btn)
    Button playBtn;
    @BindView(R.id.stop_btn)
    Button stopBtn;
    @BindView(R.id.setting_iv)
    ImageView settingIv;
    @BindView(R.id.gps_iv)
    ImageView gpsIv;
    @BindView(R.id.status_tv)
    TextView statusTv;
    @BindView(R.id.bottom_ll)
    LinearLayout mBottomLL;

    SqliteHelper sqliteHelper;

    Context mContext;

    MyAdapter mAdapter;

    List<Record> recordList;
    @BindView(R.id.ll_1) LinearLayout ll1;
    @BindView(R.id.title_right_rl) RelativeLayout titleRightRl;
    @BindView(R.id.title_rl) RelativeLayout titleRl;
    @BindView(R.id.ll_2) LinearLayout ll2;
    @BindView(R.id.time_right_rl) RelativeLayout timeRightRl;
    @BindView(R.id.linearLayout) LinearLayout linearLayout;
    @BindView(R.id.time_rl) RelativeLayout timeRl;
    @BindView(R.id.ll_3) LinearLayout ll3;
    @BindView(R.id.setting_white_iv) ImageView settingWhiteIv;
    @BindView(R.id.split_tv) TextView splitTv;
    @BindView(R.id.reads_rl) RelativeLayout readsRl;
    @BindView(R.id.pass_rl) RelativeLayout passRl;
    @BindView(R.id.card_ll) LinearLayout cardLl;
    @BindView(R.id.ll_4) LinearLayout ll4;
    @BindView(R.id.split_rl) RelativeLayout splitRl;
    @BindView(R.id.linearLayout2) LinearLayout linearLayout2;
    @BindView(R.id.remote_iv) ImageView remoteIv;
    @BindView(R.id.remote_rl) RelativeLayout remoteRl;
    @BindView(R.id.card_data_ll) LinearLayout cardDataLl;
    @BindView(R.id.ll_5) LinearLayout ll5;
    @BindView(R.id.ll_6) LinearLayout ll6;

    private UhfReader reader; //超高频读写器

    private String serialPortPath = "/dev/ttyS2";

    private UhfReaderDevice readerDevice; // 读写器设备，抓哟操作读写器电源

    private ScreenStateReceiver screenReceiver;

    //超高频读卡模块
    private static ReaderConnector mReaderConnector; //构建连接器
    private static final int RFID_BAUD = 115200; //超高频读卡模块

    private boolean runFlag = true;

    private boolean startFlag = false;
    //定位都要通过LocationManager这个类实现
    private LocationManager locationManager;
    private String provider;

    private static final int LOCATION_CODE = 2;

    private static final int FILE_CODE = 2;

    private static final int SHOW_LOCATION = 500;

    private static final int NETWORK_STATS_CONN = 501;

    private static final int NETWORK_STATS_DISS_CONN = 502;

    static String gpsStr = "";

    final int REFRESH_DATA = 100;

    final int SHOW_TIME = 800;

    int playCardNum = 0, playCardAllNum = 0, uploadSuccessNum = 0;

    boolean isShowTime = true;

    boolean isReader = true;

    private Vibrator mVibrator;  //声明一个振动器对象

    private static final int REQUEST_SCAN = 0;

    ScanDevice sm;
    private final static String SCAN_ACTION = "scan.rcv.message";
    private String barcodeStr;

    private NfcAdapter mNfcAdapter;

    private PendingIntent mPendingIntent;
    private NdefMessage mNdefPushMessage;


    private PowerManager powerManager = null;
    private PowerManager.WakeLock wakeLock = null;

    OkHttpClient okHttpClient;
    String url;

    /** uhf读卡模块相关 */
    private static final String PORT = "Com13";
    private static final int BAUD = 115200;
    private ReaderHelper mReaderHelper;
    private ReaderBase mReader;
    private static ReaderSetting m_curReaderSetting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mContext = this;
        sqliteHelper = SqliteHelper.getInstantiation();

        //运行时权限
        getCameraRuntimeRight();
        getVaitabeRuntimeRight();
        getNetWorkRuntimeRight();
        //申请屏幕常量
        powerManager = (PowerManager) this.getSystemService(Service.POWER_SERVICE);
        wakeLock = this.powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Lock");

        url = "http://139.199.194.247/pbapi/PBTrailTimeAdd.php";
        okHttpClient = new OkHttpClient();
        //数据上传线程
        new UploadThread().start();

        initSlide();

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("yuweiTest", "requestCode = " + requestCode + "    resultCode = " + resultCode);
        if (requestCode == REQUEST_SCAN && resultCode == RESULT_OK) {
            // Toast.makeText(MainActivity.this, data.getStringExtra("barCode"), Toast.LENGTH_LONG).show();
            String qrCode = data.getStringExtra("barCode");
            boolean isExists = sqliteHelper.existsOnDataBases(qrCode);

            Record record = new Record();
            record.setCardNo(qrCode);
            record.setTimeStamp("" + System.currentTimeMillis());
            record.setGps(gpsStr);
            record.setType(2);
            String name = App.spUtils.getString(SettingActivity.LOCAL_NAME);
            record.setLocalName(name);
            record.setYyyyMMdd(DateUtils.getYYMMDDToday());
            record.setTimeType("qrtime");
            sqliteHelper.addRecord(record);


            mHandler.obtainMessage();

            recordList = sqliteHelper.getShowRecord();
            Message msg = mHandler.obtainMessage();
            msg.what = REFRESH_DATA;
            msg.obj = isExists;
            mHandler.sendMessage(msg);

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                }
            }, 500);
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_DATA:
                    playCardTv.setText(playCardNum + "");
                    localNameTv.setText(playCardAllNum + "");
                    //震动 300 毫秒
                    mVibrator.vibrate(300);

                    if (recordList == null) return;
                    mAdapter.notifyDataSetChanged();
                    break;

                case SHOW_TIME:
                    String timeStr = String.valueOf(System.currentTimeMillis());
                    yyMmDdTv.setText(DateUtils.getShowYYmmDD(timeStr));
                    hhMmSs.setText(DateUtils.getHHMMSS(timeStr));
                    break;

                case SHOW_LOCATION:
                    if (!"".equals(gpsStr))
                        gpsIv.setImageResource(R.drawable.location_icon);
                    else
                        gpsIv.setImageResource(R.drawable.location_error);
                    break;

                case NETWORK_STATS_CONN:
                    remoteIv.setImageResource(R.drawable.right_icon);
                    remoteCardNumTv.setText(App.REMOTE_COUNT + "");
                    break;

                case NETWORK_STATS_DISS_CONN:
                    remoteIv.setImageResource(R.drawable.error_icon);
                    break;
            }
        }
    };


    public void onNewIntent(Intent intent) {
        if (!initNFC()) {
            Toast.makeText(this, "NFC功能初始化失败！", Toast.LENGTH_SHORT).show();
            return;
        }

        handleData(intent);

        return;
    }

    //初始化NFC
    private boolean initNFC() {
        if (mNfcAdapter == null) {
            Toast.makeText(this, "设备不支持NFC功能！", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mNfcAdapter != null && !mNfcAdapter.isEnabled()) {
            Toast.makeText(this, "请在系统设置中先启用NFC功能！", Toast.LENGTH_SHORT).show();
            return false;
        }
        // 定义程序可以兼容的nfc协议，例子为nfca和nfcv
        // 在Intent filters里声明你想要处理的Intent，一个tag被检测到时先检查前台发布系统，
        // 如果前台Activity符合Intent filter的要求，那么前台的Activity的将处理此Intent。
        // 如果不符合，前台发布系统将Intent转到Intent发布系统。如果指定了null的Intent filters，
        // 当任意tag被检测到时，你将收到TAG_DISCOVERED intent。因此请注意你应该只处理你想要的Intent。
        return true;
    }


    private NdefRecord newTextRecord(String text, Locale locale, boolean encodeInUtf8) {
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));

        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
        byte[] textBytes = text.getBytes(utfEncoding);

        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        char status = (char) (utfBit + langBytes.length);

        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
    }

    private void handleData(Intent intent) {
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction()) ||
                NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()) ||
                NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()) ||
                NfcAdapter.ACTION_ADAPTER_STATE_CHANGED.equals(intent.getAction())) {
            Log.i("yuweiTest", "isOk = " + isOk);
            if (isOk) {

                isOk = false;

                Log.i("yuweiTest", "isOk = " + isOk);

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                byte[] tagId = tag.getId();
                String cardNo = toHexString(tagId);
                Log.i("yuweiTest", "接收的值 = " + cardNo);

                boolean isExists = sqliteHelper.existsOnDataBases(cardNo);

                Record record = new Record();
                record.setCardNo(cardNo);
                record.setTimeStamp("" + System.currentTimeMillis());
                record.setGps(gpsStr);
                record.setType(2);
                String name = App.spUtils.getString(SettingActivity.LOCAL_NAME);
                record.setLocalName(name);
                record.setYyyyMMdd(DateUtils.getYYMMDDToday());
                record.setTimeType("chiptime");  //如果是nfc打卡，就是chiptime 如果是，调用摄像头扫描二维码或者条码就是qrtime  如果是点+号手工录入，就是manual time
                sqliteHelper.addRecord(record);


                recordList = sqliteHelper.getShowRecord();
                Message msg = mHandler.obtainMessage();
                msg.what = REFRESH_DATA;
                msg.obj = isExists;
                mHandler.sendMessage(msg);
                //onHandler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isOk = true;
                    }
                }, 500);
            }
        }
    }

    /**
     * 字节数组转成16进制表示格式的字符串
     *
     * @param byteArray 要转换的字节数组
     * @return 16进制表示格式的字符串
     **/
    public static String toHexString(byte[] byteArray) {
        if (byteArray == null || byteArray.length < 1) {
//            System.out.println("this byteArray must not be null or empty");
            return null;
        }


        final StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < byteArray.length; i++) {
            if ((byteArray[i] & 0xff) < 0x10)//0~F前面不零
                hexString.append("0");
            hexString.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return hexString.toString().toLowerCase();
    }

    boolean isOk;

    @Override
    protected void onResume() {
        super.onResume();
        //   acquireWakeLock(this);
        runFlag = true;
        getLocationRuntimeRight();

        //初始化读卡设备
        initRFID();

 /*       //防止一次刷卡两次调用          uhf 版本屏蔽nfc
        isOk = true;
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        mNdefPushMessage = new NdefMessage(new NdefRecord[]{newTextRecord(
                "Message from NFC Reader :-)", Locale.ENGLISH, true)});
        // 获取nfc适配器
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
            mNfcAdapter.enableForegroundNdefPush(this, mNdefPushMessage);
        }
        //NFC
        onNewIntent(getIntent());
*/
        //

        initView();

        playCardNum = sqliteHelper.getPlayCardNum();
        playCardTv.setText(playCardNum + "");
        playCardAllNum = sqliteHelper.getAllCount();
        localNameTv.setText(playCardAllNum + "");

/**
 * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
 */

        mVibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
        isShowTime = true;
/** 显示当前系统时间 */

        new Thread() {
            @Override
            public void run() {
                while (isShowTime) {
                    try {
                        Thread.sleep(1000);
                        mHandler.sendEmptyMessage(SHOW_TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();


        //是否需计算锁的数量
        wakeLock.setReferenceCounted(false);

        /** 初始化UHF读卡模块 */
        initUhfDev();
        /** 判断是否能够获取到UHF读卡器的版本号  如成功获取到版本号就提示连接成功，否则就提示重新连接读写器并重启app */
        m_curReaderSetting = mReaderHelper.getCurReaderSetting();
        m_curInventoryBuffer = mReaderHelper.getCurInventoryBuffer();
        mReader.getFirmwareVersion(m_curReaderSetting.btReadId);
        String uhf_version = ReaderHelper.hardwareStr;
        Log.i("yqLuo", "uhf_version = " + uhf_version);
        if (uhf_version.length() > 0 ) {
            Toast.makeText(mContext, "UHF初始化成功，版本号：" + uhf_version, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(mContext, "UHF读卡模块初始化失败，请检查设置后重启app。" + uhf_version, Toast.LENGTH_LONG).show();
        }
        //开始读卡
        uhfStartReader();
        //请求屏幕常亮，onResume()方法中执行
        wakeLock.acquire();

    }


    //初始化超高频读卡设备
    private void initRFID() {
        mReaderConnector = new ReaderConnector();
        // mReaderConnector = mReaderConnector.connectCom("", RFID_BAUD);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        isShowTime = false;
        if (screenReceiver != null)
            unregisterReceiver(screenReceiver);

        runFlag = false;
        if (reader != null)
            reader.close();

        if (readerDevice != null)
            readerDevice.powerOff();


        if (mVibrator != null)
            mVibrator.cancel();

        /** 关闭uhf读卡 */
        mReaderHelper.setInventoryFlag(false);
        m_curInventoryBuffer.bLoopInventoryReal = false;


        super.onDestroy();
    }


    @OnClick({R.id.setting_white_iv, R.id.bottom_ll, R.id.gps_iv, R.id.time_synchronization_btn, R.id.add_iv, R.id.more_iv, R.id.up_arrow_iv, R.id.play_btn, R.id.stop_btn, R.id.setting_iv})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.gps_iv:

                new AlertDialog.Builder(mContext).setTitle("提示").setMessage(getGps()).
                        setPositiveButton("确定", null).create().show();
                Log.i("", "");

                break;

            case R.id.add_iv:
                startActivity(new Intent(mContext, AddRecordActivity.class));
                break;

            case R.id.more_iv:
                startActivity(new Intent(mContext, MoreRecordActivity.class));
                break;

            case R.id.up_arrow_iv:

                break;

            case R.id.play_btn:
                startReader();
                break;

            case R.id.stop_btn:

                stopReader();
                break;

            case R.id.setting_iv:
                startActivity(new Intent(this, SettingActivity.class));
                break;

            case R.id.bottom_ll:
                // startActivityForResult(new Intent(MainActivity.this, CaptureActivity.class), REQUEST_SCAN);
                break;

            case R.id.setting_white_iv:
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                break;
        }
    }


    /**
     * 获得运行时权限
     */
    private void getCameraRuntimeRight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        1);
            } else
                Log.i("xiaoQTest", "已授权摄像头权限");
        }
    }


    public void getLocationRuntimeRight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);
            } else
                Log.i("xiaoQTest", "已授权定位权限");
            initGps();
        }
    }


    public void getVaitabeRuntimeRight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.VIBRATE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.VIBRATE},
                        1);
            } else
                Log.i("xiaoQTest", "已授权震动权限");
        }
    }

    public void getNetWorkRuntimeRight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_NETWORK_STATE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                        5);
            } else
                Log.i("xiaoQTest", "网络权限");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //取消屏幕常亮，onPause()方法中执行
        wakeLock.release();
    }

    private void stopReader() {
        try {
            FileWriter localFileWriterOff = new FileWriter(new File(
                    "/proc/gpiocontrol/set_uhf"));
            localFileWriterOff.write("0");
            localFileWriterOff.close();
            startFlag = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 开始读卡
     */

    private void startReader() {
        byte[] versionBytes = reader.getFirmware();
        if (versionBytes != null) {
            //reader.setWorkArea(3);//设置成欧标
            //  Util.play(1, 0);
            Util.play();
            String version = new String(versionBytes);
            //textVersion.setText(new String(versionBytes));
        }

        startFlag = true;
        //打开电源
        try {
            FileWriter localFileWriterOn = new FileWriter(new File(
                    "/proc/gpiocontrol/set_uhf"));
            localFileWriterOn.write("1");
            localFileWriterOn.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initView() {
        recordList = sqliteHelper.getShowRecord();
        mAdapter = new MyAdapter();
        playCardRecordLv.setAdapter(mAdapter);
        playCardRecordLv.setEnabled(false);

        playCardCountTv.setText(App.spUtils.getString(SettingActivity.LOCAL_NAME));
        devNum.setText(App.spUtils.getString(SettingActivity.DEV_NUM));
    }

    public String getGps() {
        if ("".equals(gpsStr))
            return "无法获取到定位数据，请检查gps设置！";
        return gpsStr;
    }



    private class MyAdapter extends BaseAdapter {

        Record record = null;
        int recordListSize;

        @Override
        public int getCount() {
            if (recordList == null) return 0;
            recordListSize = recordList.size();
            return recordListSize;
        }

        @Override
        public Object getItem(int position) {
            return recordList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.main_record_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            record = recordList.get(position);
            holder.cardNoTv.setText(record.getCardNo());
            holder.noTv.setText(String.valueOf(recordListSize - position));
            // holder.noTv.setText(String.valueOf( position));
            holder.yearTv.setText(DateUtils.getYYMMDD(record.getTimeStamp()));
            holder.timeTv.setText(DateUtils.getHHMMSS(record.getTimeStamp()));

            return convertView;
        }
    }


    class ViewHolder {
        @BindView(R.id.no_tv)
        TextView noTv;
        @BindView(R.id.cardNo_tv)
        TextView cardNoTv;
        @BindView(R.id.year_tv)
        TextView yearTv;
        @BindView(R.id.time_tv)
        TextView timeTv;
        /*    @BindView(R.id.ll_1)
            LinearLayout ll_1;*/
        @BindView(R.id.ll_2)
        LinearLayout ll_2;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }


    LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
            // TODO Auto-generated method stub
            //      Toast.makeText(mContext, "onStatusChanged", Toast.LENGTH_SHORT).show();
            Log.i("gps", "onStatusChanged");
        }

        @Override
        public void onProviderEnabled(String arg0) {
            // TODO Auto-generated method stub
            Log.i("gps", "onProviderEnabled");
            //    Toast.makeText(mContext, "onProviderEnabled", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onProviderDisabled(String arg0) {
            // TODO Auto-generated method stub
            gpsStr = "";
            mHandler.sendEmptyMessage(SHOW_LOCATION);
            Log.i("gps", "onProviderDisabled");
            //     Toast.makeText(mContext, "onProviderDisabled", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub
            // 更新当前经纬度
            // 获取当前位置，这里只用到了经纬度
            gpsStr = "纬度为：" + location.getLatitude() + ",经度为："
                    + location.getLongitude();
            Log.i("gps", gpsStr);
            //    Toast.makeText(mContext, gpsStr, Toast.LENGTH_SHORT).show();
            if (location != null) {
                try {
                    handlerLocation(location);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mHandler.sendEmptyMessage(SHOW_LOCATION);
            //    Toast.makeText(mContext, gpsStr, Toast.LENGTH_SHORT).show();
        }
    };


    /**
     * 初始化GPS
     */

    private void initGps() {

        //检查是否开启权限！
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "权限不够", Toast.LENGTH_LONG).show();
            return;
        }

        //获取定位服务
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //获取当前可用的位置控制器
        List<String> list = locationManager.getProviders(true);

        if (list.contains(LocationManager.GPS_PROVIDER)) {
            //是否为GPS位置控制器
            provider = LocationManager.GPS_PROVIDER;
        } else if (list.contains(LocationManager.NETWORK_PROVIDER)) {
            //是否为网络位置控制器
            provider = LocationManager.NETWORK_PROVIDER;

        } else {

            Intent locationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(locationIntent);
            Toast.makeText(this, "请检查网络或GPS是否打开",
                    Toast.LENGTH_LONG).show();
            return;
        }
        provider = LocationManager.NETWORK_PROVIDER;
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            try {
                handlerLocation(location);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//绑定定位事件，监听位置是否改变
//第一个参数为控制器类型第二个参数为监听位置变化的时间间隔（单位：毫秒）
//第三个参数为位置变化的间隔（单位：米）第四个参数为位置监听器
        locationManager.requestLocationUpdates(provider, 2000, 2,
                locationListener);
    }

    private void handlerLocation(Location location) {
        try {
            //获取当前位置，这里只用到了经纬度
            String latitudeStr = String.valueOf(location.getLatitude());
            int zero = latitudeStr.indexOf(".");
            latitudeStr = latitudeStr.substring(0, zero) + latitudeStr.substring(zero + 1, (latitudeStr.length() - 1));
            if (latitudeStr.length() > 6) {
                if (zero == 2)
                    latitudeStr = latitudeStr.substring(0, 2) + "°" + latitudeStr.substring(2, 4) + "-" + latitudeStr.substring(4, 6) + " N";
                if (zero == 3)
                    latitudeStr = latitudeStr.substring(0, 3) + "°" + latitudeStr.substring(3, 5) + "-" + latitudeStr.substring(5, 7) + " N";
            }
            String longitudeStr = String.valueOf(location.getLongitude());
            zero = longitudeStr.indexOf(".");
            longitudeStr = longitudeStr.substring(0, zero) + longitudeStr.substring(zero + 1, (longitudeStr.length() - 1));
            if (longitudeStr.length() > 6) {
                if (zero == 2)
                    longitudeStr = longitudeStr.substring(0, 2) + "°" + longitudeStr.substring(2, 4) + "-" + longitudeStr.substring(4, 6) + " E";
                if (zero == 3)
                    longitudeStr = longitudeStr.substring(0, 3) + "°" + longitudeStr.substring(3, 5) + "-" + longitudeStr.substring(5, 7) + " E";
            }
            gpsStr = "纬度为：" + latitudeStr + ",经度为："
                    + longitudeStr;
            Log.i("gps", gpsStr);
            // Toast.makeText(mContext, gpsStr, Toast.LENGTH_SHORT).show();
            mHandler.sendEmptyMessage(SHOW_LOCATION);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i("yuweiTest", "keyCode = " + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                isShowTime = false;
                isReader = false;
                Util.destorySound();
                MainActivity.this.finish();
                return false;

            case 301:  //中间扫描
                Log.i("yuweiTest", "按下中间扫描键");

            case 303:  //侧扫描
                Log.i("yuweiTest", "按下侧描键");

            case 302:  //侧扫描
                Log.i("yuweiTest", "按下侧扫描键");

                if (startFlag)
                    return false;
                else {
                    Toast.makeText(mContext, "请按下F1启动打卡", Toast.LENGTH_SHORT).show();
                    return true;
                }

            case 82: //F1左边健
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                return false;
        }

        return super.onKeyDown(keyCode, event);
    }

    float  mPosX, mPosY, mCurPosX, mCurPosY;
    /** 向右滑动监听 */
    @SuppressLint("ClickableViewAccessibility")
    public void initSlide() {
        mBottomLL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // TODO Auto-generated method stub
                switch (motionEvent.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        mPosX = motionEvent.getX();
                        mPosY = motionEvent.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mCurPosX = motionEvent.getX();
                        mCurPosY = motionEvent.getY();

                        break;
                    case MotionEvent.ACTION_UP:
                        if (mCurPosY - mPosY > 0
                                && (Math.abs(mCurPosY - mPosY) > 25)) {
                            //向下滑動

                        } else if (mCurPosY - mPosY < 0
                                && (Math.abs(mCurPosY - mPosY) > 25)) {
                            //向上滑动
                            //   collapse();
                        }

                        Log.i("yqLuo", "mCurPosX - mPosX = "  +  (mCurPosX - mPosX));

                        if (mCurPosX - mPosX >0 && (Math.abs(mCurPosX - mPosX) > 200)) {  //向右滑动
                            startActivityForResult(new Intent(MainActivity.this, CaptureActivity.class), REQUEST_SCAN);
                        }

                        break;
                }
                return true;
            }
        });
    }


    /** uhf操作 */
    private void initUhfDev() {
        try {
            SerialPort mhSerialPort = new SerialPort(13, BAUD, 0);
            Log.e("PortConnect", "try");
            try {
                mReaderHelper = ReaderHelper.getDefaultHelper();
                mReaderHelper.setReader(mhSerialPort.getInputStream(), mhSerialPort.getOutputStream());
                mReader = mReaderHelper.getReader();
            } catch (Exception e) {
                // TODO: handle exception
                Log.e("PortConnect", e.toString());
                return;
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private static InventoryBuffer m_curInventoryBuffer;
    private long mRefreshTime;

    /** uhf读卡开始 */
    public void uhfStartReader() {
        m_curInventoryBuffer.clearInventoryPar();

        String strInventoryCount = "65535";
        if (strInventoryCount == null || strInventoryCount.length() <= 0)
        {
            Toast.makeText(
                    getApplicationContext(),
                    getResources().getString(R.string.repeat_empty),
                    Toast.LENGTH_SHORT).show();
            return ;
        }
        m_curInventoryBuffer.Inventory_Count = (int) Integer.parseInt(strInventoryCount);
        if(m_curInventoryBuffer.Inventory_Count<=0)
        {
            return;
        }

        m_curInventoryBuffer.clearInventoryRealResult();
        mReaderHelper.setInventoryFlag(true);
        m_curInventoryBuffer.bLoopInventoryReal = true;
        mReaderHelper.clearInventoryTotal();

        ReaderHelper.RunInventroy_Once();
        mRefreshTime = new Date().getTime();
        mHandler.removeCallbacks(mRefreshRunnable);
        mHandler.postDelayed(mRefreshRunnable,50);
    }

    List<InventoryBuffer.InventoryTagMap>  tagList;

    /** uhf数据处理回调 */
    private Runnable mRefreshRunnable = new Runnable()
    {
        public void run ()
        {
            // refreshList();
            tagList =  m_curInventoryBuffer.lsTagList;

            for (InventoryBuffer.InventoryTagMap inventoryTagMap : tagList) {
                boolean isExists = sqliteHelper.existsOnDataBases(inventoryTagMap.strEPC);

                Record record = new Record();
                record.setCardNo(inventoryTagMap.strEPC);
                record.setTimeStamp(inventoryTagMap.strFreq);   //这里使用读卡器时间
                record.setGps(gpsStr);
                record.setType(1);
                String name = App.spUtils.getString(SettingActivity.LOCAL_NAME);
                record.setLocalName(name);
                record.setYyyyMMdd(DateUtils.getYYMMDDToday());
                record.setTimeType("qrtime");
                sqliteHelper.addRecord(record);

                mHandler.obtainMessage();

                recordList = sqliteHelper.getShowRecord();
                Message msg = mHandler.obtainMessage();
                msg.what = REFRESH_DATA;
                msg.obj = isExists;
                mHandler.sendMessage(msg);
            }

            Log.i("yqLuo", "tag = " + tagList.toString());
            mHandler.postDelayed(this, 50);
        }
    };

    /**
     * 上传数据线程
     */
    class UploadThread extends Thread {
        @Override
        public void run() {
            while (runFlag) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {}
                Log.i("yqLuo", "进入数据上传");
                if (NetworkUtils.isNetworkConnected(mContext)) {
                    List<Record> records = sqliteHelper.getUploadRecord();
                    Log.i("yqLuo", "需数据上传数据 = " + records.size() + "条" );

                    for (Record record : records) {
                        RequestBody body = new FormBody.Builder()
                                .add("device_id", App.spUtils.getString(SettingActivity.DEV_NUM))
                                .add("sign_id", record.getCardNo())
                                .add("time", record.getYyyyMMdd())
                                .add("time_type", record.getTimeType())
                                .add("gps", record.getGps())
                                .add("temperature", "")
                                .add("humidity", "")
                                .add("remark", "")
                                .build();

                        Request request = new Request.Builder()
                                .url(url)
                                .post(body)
                                .build();

                        Call call = okHttpClient.newCall(request);
                        try {
                            Response response = call.execute();
                            String responseStr = response.body().string();
                            System.out.println("yqLuo " + responseStr);

                            JSONObject jsonObject = new JSONObject(responseStr);
                            if (jsonObject.getString("errorCode").equals("0")) {
                                sqliteHelper.upload(record);
                                App.REMOTE_COUNT++;
                            }


                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    mHandler.sendEmptyMessage(NETWORK_STATS_CONN);
                } else {
                    Log.i("yqLuo", "网络异常");
                    mHandler.sendEmptyMessage(NETWORK_STATS_DISS_CONN);
                }
            }

        }
    }

}






















