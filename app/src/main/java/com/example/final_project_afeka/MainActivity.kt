package com.example.final_project_afeka

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import androidx.fragment.app.FragmentActivity
import com.example.final_project_afeka.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : FragmentActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    @Inject
    lateinit var auth: FirebaseAuth

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

    private fun startLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            println("cur userId = ${auth.currentUser?.uid?:"null"}")

            startLoginActivity()
        }
        else {
            println("cur userId = ${auth.currentUser?.uid}")
            println("cur user email = ${auth.currentUser?.email}")
        }

    }

}
