package com.example.final_project_afeka.login.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class MyUser(
    val username: String = "",
    val email: String = "",
    val avatar: String = "",
    val password: String = "",
    val favoriteCredit :Int = 3 ,
    val wishCredit :Int = 3 ,
    val premium : Long = Date().time + 10000

    ) : Parcelable {

    fun allFilled(confirm: String): Boolean {
        return username.isNotEmpty() && email.isNotEmpty() && avatar.isNotEmpty() && password.length > 4 && password == confirm
    }

    fun validPassword(): Boolean =( password.length >= 4 )
}

