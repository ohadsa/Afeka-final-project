package com.example.final_project_afeka.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.FragmentActivity
import com.example.final_project_afeka.R
import dagger.hilt.android.AndroidEntryPoint
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil

const val LOGIN_EXTRA = "com.ohadsa.login"

@AndroidEntryPoint
class LoginActivity : FragmentActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_)
    }

    class Contract : LoginContract() {
        override fun createIntent(context: Context, input: Unit): Intent {
            return Intent(
                context,
                LoginActivity::class.java
            )
        }
        override fun parseResult(resultCode: Int, intent: Intent?): String? =
            intent?.getStringExtra(LOGIN_EXTRA)


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

abstract class LoginContract : ActivityResultContract<Unit, String?>()
