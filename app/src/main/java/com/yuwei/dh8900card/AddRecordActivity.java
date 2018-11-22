package com.yuwei.dh8900card;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.yuwei.dh8900card.entity.Record;
import com.yuwei.dh8900card.utils.DateUtils;
import com.yuwei.dh8900card.utils.SqliteHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddRecordActivity extends Activity {

    @BindView(R.id.cardNo_tv)
    EditText cardNoTv;

    SqliteHelper sqliteHelper;

    public static  String[] homePage ;

    public static  String[] allPage = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "..."};

    boolean isHomePage = true;
    @BindView(R.id.keyboard_gv)
    GridView keyboardGv;

    private Context mContext;

    private keyboardAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);
        ButterKnife.bind(this);
        sqliteHelper = SqliteHelper.getInstantiation();



    }


    @Override
    protected void onResume() {
        super.onResume();
        homePage = new String[]{App.spUtils.getString(CustomCharActivity.FIRST) == null ? "" : App.spUtils.getString(CustomCharActivity.FIRST),
                App.spUtils.getString(CustomCharActivity.SECOND) == null ? "" : App.spUtils.getString(CustomCharActivity.SECOND),
                App.spUtils.getString(CustomCharActivity.THIRD) == null ? "" : App.spUtils.getString(CustomCharActivity.THIRD),
                "1", "2", "3", "4", "5", "6", "7", "8", "9", "", "0"};

        initView();
    }

    private void initView() {
        mContext = this;
        mAdapter = new keyboardAdapter();
        keyboardGv.setAdapter(mAdapter);
        cardNoTv.setInputType(InputType.TYPE_NULL);

    }

    @OnClick({R.id.add_record_btn, R.id.back_iv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_record_btn:
                String cardNo = cardNoTv.getText().toString().trim();
                if ("".equals(cardNo)) {
                    Toast.makeText(AddRecordActivity.this, "请输入卡号", Toast.LENGTH_SHORT).show();
                    return;
                }
                Record record = new Record();
                record.setCardNo(cardNo);
                record.setTimeStamp("" + System.currentTimeMillis());
                record.setGps(MainActivity.gpsStr);
                record.setType(2);
                String name = App.spUtils.getString(SettingActivity.LOCAL_NAME);
                record.setLocalName(name);
                record.setYyyyMMdd(DateUtils.getYYMMDDToday());
                record.setTimeType("manual time");
                sqliteHelper.addRecord(record);

                // AddRecordActivity.this.finish();
                break;

            case R.id.back_iv:
                AddRecordActivity.this.finish();
                break;
        }
    }



    /**
     * 自定义键盘适配器
     */
    private class keyboardAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return homePage.length + 1;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {
            view = LayoutInflater.from(mContext).
                    inflate(R.layout.gridview_item, null);
            Button btn =  view.findViewById(R.id.btn);

            if(position == homePage.length) {
               // btn.setBackgroundResource(R.drawable.enter_icon);
              //  btn.setText("");
                final Button btn2 = view.findViewById(R.id.btn2);
                btn2.setVisibility(View.VISIBLE);
                btn.setVisibility(View.GONE);
                btn2.setBackgroundResource(R.drawable.enter_icon);

                final Animation animation = new AlphaAnimation(0.1f, 1.0f);
                animation.setDuration(500);

                btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        btn2.setAnimation(animation);
                        if( position == (homePage.length) ) {
                            addRecord();
                            return;
                        }
                    }
                });
            } else if (position == (homePage.length - 2)) {
                final Button btn2 = view.findViewById(R.id.btn2);
                btn2.setVisibility(View.VISIBLE);
                btn.setVisibility(View.GONE);
               btn2.setBackgroundResource(R.drawable.backspace_icon);
                //动画效果参数直接定义
                final Animation animation = new AlphaAnimation(0.1f, 1.0f);
                animation.setDuration(500);

                btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String text = cardNoTv.getText().toString().trim();
                        if (text.length() > 0) {
                            cardNoTv.setText(text.substring(0, text.length() - 1));
                        }
                    }
                });
            } else
                btn.setText(homePage[position]);



            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if( position == (homePage.length) ) {
                        addRecord();
                        return;
                    }

                    String string = cardNoTv.getText().toString();

                    cardNoTv.setText(string + (homePage[position]));

                }
            });

            return view;
        }
    }


    /** 添加打卡记录 */
    private void addRecord() {
        String cardNo = cardNoTv.getText().toString().trim();
        if ("".equals(cardNo)) {
            Toast.makeText(AddRecordActivity.this, "请输入卡号", Toast.LENGTH_SHORT).show();
            return;
        }
        Record record = new Record();
        record.setCardNo(cardNo);
        record.setTimeStamp("" + System.currentTimeMillis());
        record.setGps(MainActivity.gpsStr);
        record.setType(2);
        String name = App.spUtils.getString(SettingActivity.LOCAL_NAME);
        record.setLocalName(name);
        record.setYyyyMMdd(DateUtils.getYYMMDDToday());
        record.setTimeType("manual time");
        sqliteHelper.addRecord(record);
        Toast.makeText(mContext, "卡号：" + cardNo + "添加成功！", Toast.LENGTH_SHORT).show();
        cardNoTv.setText("");
    }

}
