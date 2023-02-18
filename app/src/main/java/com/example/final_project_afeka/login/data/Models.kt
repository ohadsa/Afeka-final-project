package com.example.final_project_afeka.login.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

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


data class Duration(
    val hours: Long,
    val minutes: Long,
    val seconds: Long,
) {
    val formatted =
        if (hours > 0) String.format("%02d:%02d:%02d", hours, minutes, seconds)
        else String.format("%02d:%02d", minutes, seconds)
}

data class MyTime(
    val startTime: Long,
    val duration: Duration,
)