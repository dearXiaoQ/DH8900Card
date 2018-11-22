package com.yuwei.dh8900card;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yuwei.dh8900card.entity.Record;
import com.yuwei.dh8900card.utils.DateUtils;
import com.yuwei.dh8900card.utils.SqliteHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MoreRecordActivity extends Activity {

    @BindView(R.id.back_iv)
    ImageView backIv;
    @BindView(R.id.delete_icon)
    ImageView deleteIcon;
    @BindView(R.id.title_rl)
    RelativeLayout titleRl;
    @BindView(R.id.record_lv)
    ListView recordLv;

    SqliteHelper sqliteHelper;

    private Context mContext;

    List<Record> recordList;

    MyAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_record);
        ButterKnife.bind(this);
        sqliteHelper = SqliteHelper.getInstantiation();
        mContext = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        recordList = sqliteHelper.getAllRecord();
        mAdapter = new MyAdapter();
        recordLv.setAdapter(mAdapter);
    }

    @OnClick({R.id.back_iv, R.id.delete_icon})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                MoreRecordActivity.this.finish();
                break;

            case R.id.delete_icon:
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("是否删除全部打卡记录")
                        //设置对话框的按钮
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sqliteHelper.deleteAllData();
                                MoreRecordActivity.this.finish();
                            }
                        }).create();
                dialog.show();

                break;
        }
    }




    private class MyAdapter extends BaseAdapter {

        Record record = null;
        int recordListSize;

        @Override
        public int getCount() {
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
            if(convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.main_record_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            record = recordList.get(position);
            holder.cardNoTv.setText(record.getCardNo());
            holder.noTv.setText(String.valueOf(recordListSize - position));
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
       /* @BindView(R.id.ll_1)
        LinearLayout ll_1;*/
        @BindView(R.id.ll_2)
        LinearLayout ll_2;

        public ViewHolder(View view) {
            ButterKnife.bind( this, view);
        }

    }

}
