package com.coyni.pos.app.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.coyni.pos.app.R
import com.coyni.pos.app.utils.MyApplication

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val myApplication: MyApplication = getApplicationContext() as MyApplication

    }
}