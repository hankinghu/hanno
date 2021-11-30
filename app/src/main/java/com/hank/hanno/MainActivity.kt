package com.hank.hanno

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.hank.hannotation.HannoLog
import kotlin.concurrent.thread

@HannoLog
class MainActivity : AppCompatActivity() {
    private var a=1;
    private val b=false;
    private val c="ccc"
    @HannoLog(level = Log.DEBUG, enableTime = true)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        a=3
        thread(name = "thread_1") {
            test()
        }
    }

    @HannoLog(tagName = "test", watchField = true)
    override fun onResume() {
        super.onResume()
    }

    @HannoLog(level = Log.INFO, enableTime = false)
    private fun test(a: Int = 3, b: String = "good"): Int {
        return a + 1
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}