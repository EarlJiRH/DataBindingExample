package com.example.databinding

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.databinding.databinding.ActivityMainBinding
import com.example.databinding.http.HiOKHttp
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val getUrl = "/user/query?userId=1600932269"

    private val postUrl = "/tag/toggleTagFollow"
//    private val mBinding: ActivityMainBinding by binding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this,
            R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        HiOKHttp.getAsync(getUrl)
        HiOKHttp.postAsync(postUrl)
        text.setOnClickListener {
            // HiOKHttp.get(getUrl)
//            HiOKHttp.post(postUrl)
            HiOKHttp.postString()
        }

    }
}