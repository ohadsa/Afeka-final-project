package com.example.final_project_afeka.login.ui

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.example.final_project_afeka.login.data.MyUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val realTimeDB: FirebaseDatabase,

    ) : ViewModel() {
    val myUser = MutableStateFlow(MyUser().copy(premium = -1))
    private val email get() = myUser.value.email
    private val password get() = myUser.value.password

    val mode = MutableStateFlow(LoginMode.LOGIN)

    fun connect(context: Activity, runWhenSuccess: () -> Unit, runWhenFailure: () -> Unit) =
        when (mode.value) {
            LoginMode.LOGIN ->
                auth.signInWithEmailAndPassword(myUser.value.email, myUser.value.password)
                    .addOnCompleteListener(context) { task ->
                        if (task.isSuccessful) runWhenSuccess()
                        else runWhenFailure()
                    }

            LoginMode.SIGNUP ->
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(context) { task ->
                        if (task.isSuccessful) {
                            auth.currentUser?.let {
                                realTimeDB.getReference("users").child(it.uid).setValue(myUser.value)
                            }
                            runWhenSuccess()
                        } else runWhenFailure()
                    }
        }


    fun updateUser(newUser: MyUser) {
        myUser.value = newUser
    }

    fun changeModeTapped() {
        mode.value = if (mode.value == LoginMode.LOGIN) LoginMode.SIGNUP else LoginMode.LOGIN
    }
}

enum class LoginMode {
    LOGIN, SIGNUP
}