package com.yuwei.dh8900card;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CustomCharActivity extends Activity {

    @BindView(R.id.back_iv)
    ImageView backIv;
    @BindView(R.id.save_iv)
    ImageView saveIv;
    @BindView(R.id.title_rl)
    RelativeLayout titleRl;
    @BindView(R.id.ll_3)
    LinearLayout ll3;
    @BindView(R.id.text1)
    TextView text1;
    @BindView(R.id.first_et)
    EditText firstEt;
    @BindView(R.id.rl1)
    RelativeLayout rl1;
    @BindView(R.id.text2)
    TextView text2;
    @BindView(R.id.second_et)
    EditText secondEt;
    @BindView(R.id.rl2)
    RelativeLayout rl2;
    @BindView(R.id.text3)
    TextView text3;
    @BindView(R.id.third_et)
    EditText thirdEt;
    @BindView(R.id.rl3)
    RelativeLayout rl3;
    @BindView(R.id.text4)
    TextView text4;
    @BindView(R.id.fourth_et)
    EditText fourthEt;
    @BindView(R.id.rl4)
    RelativeLayout rl4;

    public static final String FIRST = "first";

    public static final String SECOND = "second";

    public static final String THIRD = "third";

    public static final String FOURTH = "fourth";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_char);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if( App.spUtils.getString(FIRST)!=null )
            firstEt.setText( App.spUtils.getString(FIRST));

        if( App.spUtils.getString(SECOND)!=null )
            secondEt.setText( App.spUtils.getString(SECOND));

        if( App.spUtils.getString(THIRD)!=null )
            thirdEt.setText( App.spUtils.getString(THIRD));

        if( App.spUtils.getString(FOURTH)!=null )
            fourthEt.setText( App.spUtils.getString(FOURTH));
    }

    @OnClick({R.id.save_iv, R.id.back_iv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_iv:

                App.spUtils.put(FIRST, firstEt.getText().toString().trim());
                App.spUtils.put(SECOND, secondEt.getText().toString().trim());
                App.spUtils.put(THIRD, thirdEt.getText().toString().trim());
                App.spUtils.put(FOURTH, fourthEt.getText().toString().trim());

                Toast.makeText(this, "数据保存成功", Toast.LENGTH_SHORT).show();

                break;

            case R.id.back_iv:

                this.finish();

                break;

        }

    }

}
