package com.jianjian.myeventbustest.demo;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.jianjian.myeventbustest.R;
import com.jianjian.gesturecipher.PointGroup;

public class MainActivity extends AppCompatActivity {

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
                Toast.makeText(MainActivity.this, "Correct", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onIncorrect() {
                Toast.makeText(MainActivity.this, "InCorrect", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMaxRetryTimes() {
                Toast.makeText(MainActivity.this, "MaxRetryTimes", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
