package com.lxm.javapoet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.lxm.HelloWorld;
import com.lxm.hello_annotation.BindViewCustom;
import com.lxm.hello_annotation.HelloAnnotation;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@HelloAnnotation
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tvMain)
    TextView tvmain;
    @BindViewCustom(R.id.tvTwo)
    TextView tvTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HelloWorld.main(null);
        ButterKnife.bind(this);
        tvmain.setText("哈哈");

    }

    @OnClick(R.id.tvClick)
    public void click(){
        Toast.makeText(this,"haha",Toast.LENGTH_SHORT).show();
    }
}
