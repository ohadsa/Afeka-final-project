package com.example.memory_ohad

import android.os.Bundle
import android.view.MotionEvent
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.AndroidEntryPoint
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil


@AndroidEntryPoint
class MainActivity : FragmentActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    private var shouldHide = false

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
            MotionEvent.ACTION_UP -> {
                if (shouldHide) {
                    currentFocus?.let { focus ->
                        UIUtil.hideKeyboard(this)
                        focus.clearFocus()
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> shouldHide = false
            MotionEvent.ACTION_DOWN -> shouldHide = true
        }
        return super.dispatchTouchEvent(ev)
    }

}





@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}
