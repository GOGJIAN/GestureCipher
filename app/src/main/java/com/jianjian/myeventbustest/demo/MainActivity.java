package com.jianjian.myeventbustest.demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jianjian.myeventbustest.R;
import com.jianjian.gesturecipher.PointGroup;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.greenrobot.eventbus.meta.SubscriberInfo;
import org.greenrobot.eventbus.meta.SubscriberInfoIndex;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

//    TextView mTextView;
    MyReceiver mReceiver;
    PointGroup mPointGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPointGroup = findViewById(R.id.pointGroup);
        mPointGroup.setAnswer(new int[]{3,4,5});
        mPointGroup.setStateListener(new PointGroup.StateListener() {
            @Override
            public void onCorrect() {

            }

            @Override
            public void onIncorrect() {

            }
        });
//        mTextView = findViewById(R.id.text);
//        mTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
////                startActivity(intent);
//                Request request = new Request.Builder().method("GET",null)
//                        .url("http://www.baidu.com")
//                        .build();
//                OkHttpClient okHttpClient = new OkHttpClient();
//
//                okHttpClient.newCall(request).enqueue(new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//
//                    }
//
//                    @Override
//                    public void onResponse(Call call, @NonNull final Response response) throws IOException {
//                        final String str = response.body().string();
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                mTextView.setText(str);
//                            }
//                        });
//
//                    }
//                });
//            }
//        });
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("com.jianjian.change");
//
//        mReceiver = new MyReceiver();
//        registerReceiver(mReceiver, intentFilter);
//        EventBus.getDefault().register(this);
//        String key = this.getPackageName() + "funid:123,123";
//        key = key.substring("funid:".length(), key.length());
//        String[] keys = key.split(":");
//        Log.d("MAINACTIVITY", "onCreate: " + key);
    }

    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
//            mTextView.setText(intent.getStringExtra("123"));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(String str) {
//        mTextView.setText(str);
    }

    @Override
    protected void onDestroy() {
//        EventBus.getDefault().unregister(this);
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}
