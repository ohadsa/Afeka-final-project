package com.example.final_project_afeka.ui.theme

import android.content.Context
import android.graphics.Typeface
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import com.example.final_project_afeka.R

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

open class TedoooFont(
    val weight: FontWeight,
    val textSize: TextUnit,
    val fontName: FontName = FontName.DMSans,
    val fontStyle: FontStyle = FontStyle.Normal
) {

    object Caption : TedoooFont(weight = FontWeight.Normal, textSize = 12.sp)

    object Body14 : TedoooFont(weight = FontWeight.Normal, textSize = 14.sp)

    object Body14Italic :
        TedoooFont(weight = FontWeight.Normal, textSize = 14.sp, fontStyle = FontStyle.Italic)

    object Body16 : TedoooFont(weight = FontWeight.Normal, textSize = 16.sp)

    object LabelSmall : TedoooFont(weight = FontWeight.Medium, textSize = 12.sp)

    object ButtonSmall : TedoooFont(weight = FontWeight.Medium, textSize = 14.sp)

    object ButtonMedium : TedoooFont(weight = FontWeight.Medium, textSize = 16.sp)

    object SubHeading14 :
        TedoooFont(weight = FontWeight.SemiBold, textSize = 14.sp, fontName = FontName.Poppins)

    object SubHeading :
        TedoooFont(weight = FontWeight.SemiBold, textSize = 12.sp, fontName = FontName.Poppins)

    object Heading5 :
        TedoooFont(weight = FontWeight.SemiBold, textSize = 16.sp, fontName = FontName.Poppins)

    object Heading4 :
        TedoooFont(weight = FontWeight.SemiBold, textSize = 20.sp, fontName = FontName.Poppins)

    object Heading3 :
        TedoooFont(weight = FontWeight.SemiBold, textSize = 24.sp, fontName = FontName.Poppins)

    object Heading2 :
        TedoooFont(weight = FontWeight.SemiBold, textSize = 28.sp, fontName = FontName.Poppins)

    object Heading1 :
        TedoooFont(weight = FontWeight.SemiBold, textSize = 32.sp, fontName = FontName.Poppins)

    object GiantHeading :
        TedoooFont(weight = FontWeight.SemiBold, textSize = 44.sp, fontName = FontName.Poppins)

    val lineHeight : TextUnit
        get() = when (this) {
            Caption, SubHeading -> 16.sp
            LabelSmall -> 18.sp
            Body14, Body14Italic,ButtonSmall,SubHeading14,  -> 20.sp
            Body16, ButtonMedium, Heading5-> 24.sp
            Heading4 -> 28.sp
            Heading3, Heading2, Heading1 -> 32.sp
            GiantHeading -> 66.sp
            else -> 16.sp
        }


    fun getTypeface(context: Context): android.graphics.Typeface {
        //ResourcesCompat.getFont(ctx, R.font.dmsans_family)
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