package com.zhang.myclock;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by zyw on 2016/12/14 11:56.
 */

public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }
    public void init(){
    }
}
