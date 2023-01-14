package com.example.memory_ohad.ui.generic

import android.content.Context
import android.graphics.Typeface
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import com.example.memory_ohad.R

private val dmSansFamily = FontFamily(
    Font(R.font.dmsans_regular),
    Font(R.font.dmsans_bold, FontWeight.Bold),
    Font(R.font.dmsans_medium, FontWeight.Medium),
    Font(R.font.dmsans_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.dmsans_bold_italic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.dmsans_medium_italic, FontWeight.Medium, FontStyle.Italic),
)
private val poppinsFamily = FontFamily(
    Font(R.font.poppins_medium),
    Font(R.font.poppins_semibold, FontWeight.SemiBold)
)


enum class FontName {
    Poppins, DMSans;

    fun toFamily(): FontFamily {
        return when (this) {
            DMSans -> dmSansFamily
            Poppins -> poppinsFamily
        }
    }

}

open class MyFont(
    val weight: FontWeight,
    val textSize: TextUnit,
    val fontName: FontName = FontName.DMSans,
    val fontStyle: FontStyle = FontStyle.Normal
) {

    object Caption : MyFont(weight = FontWeight.Normal, textSize = 12.sp)

    object Body14 : MyFont(weight = FontWeight.Normal, textSize = 14.sp)

    object Body14Italic :
        MyFont(weight = FontWeight.Normal, textSize = 14.sp, fontStyle = FontStyle.Italic)

    object Body16 : MyFont(weight = FontWeight.Normal, textSize = 16.sp)

    object LabelSmall : MyFont(weight = FontWeight.Medium, textSize = 12.sp)

    object ButtonSmall : MyFont(weight = FontWeight.Medium, textSize = 14.sp)

    object ButtonMedium : MyFont(weight = FontWeight.Medium, textSize = 16.sp)

    object SubHeading14 :
        MyFont(weight = FontWeight.SemiBold, textSize = 14.sp, fontName = FontName.Poppins)

    object SubHeading :
        MyFont(weight = FontWeight.SemiBold, textSize = 12.sp, fontName = FontName.Poppins)

    object Heading5 :
        MyFont(weight = FontWeight.SemiBold, textSize = 16.sp, fontName = FontName.Poppins)

    object Heading4 :
        MyFont(weight = FontWeight.SemiBold, textSize = 20.sp, fontName = FontName.Poppins)

    object Heading3 :
        MyFont(weight = FontWeight.SemiBold, textSize = 24.sp, fontName = FontName.Poppins)

    object Heading2 :
        MyFont(weight = FontWeight.SemiBold, textSize = 28.sp, fontName = FontName.Poppins)

    object Heading1 :
        MyFont(weight = FontWeight.SemiBold, textSize = 32.sp, fontName = FontName.Poppins)

    object GiantHeading :
        MyFont(weight = FontWeight.SemiBold, textSize = 44.sp, fontName = FontName.Poppins)

    val lineHeight : TextUnit
        get() = when (this) {
            Caption, SubHeading -> 16.sp
            LabelSmall -> 18.sp
            Body14, Body14Italic, ButtonSmall, SubHeading14,  -> 20.sp
            Body16, ButtonMedium, Heading5 -> 24.sp
            Heading4 -> 28.sp
            Heading3, Heading2, Heading1 -> 32.sp
            GiantHeading -> 66.sp
            else -> 16.sp
        }


    fun getTypeface(context: Context): Typeface {
        val tf = ResourcesCompat.getFont(
            context,
            when (fontName) {
                FontName.DMSans -> {
                    when (weight) {
                        FontWeight.Medium, FontWeight.SemiBold -> {
                            if (fontStyle == FontStyle.Italic)
                                R.font.dmsans_medium_italic
                            else
                                R.font.dmsans_medium
                        }
                        FontWeight.Bold -> {
                            if (fontStyle == FontStyle.Italic)
                                R.font.dmsans_bold_italic
                            else
                                R.font.dmsans_bold
                        }
                        else -> {
                            if (fontStyle == FontStyle.Italic)
                                R.font.dmsans_italic
                            else
                                R.font.dmsans_regular
                        }
                    }
                }
                FontName.Poppins -> {
                    if (weight == FontWeight.Medium)
                        R.font.poppins_medium
                    else
                        R.font.poppins_semibold
                }
            }
        )

        return tf ?: Typeface.DEFAULT
    }
}