package com.example.voluntask.util

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.voluntask.R

class CustomToast(private val textView: TextView, private val context: android.content.Context) {

    fun showCustomToast(msg: String, type: Types) {
        val fadeInAnimation =
            AnimationUtils.loadAnimation(context, androidx.appcompat.R.anim.abc_fade_in)
        val fadeOutAnimation =
            AnimationUtils.loadAnimation(context, androidx.appcompat.R.anim.abc_fade_out)
        val drawable: Drawable
        val color: String

        when (type) {
            Types.SUCESS -> {
                drawable = ContextCompat.getDrawable(context, R.drawable.baseline_check_circle_24)!!
                color = "#268C26"
            };
            Types.WARNING -> {
                drawable = ContextCompat.getDrawable(context, R.drawable.baseline_info_24)!!
                color = "#FFB446"
            };
            Types.ERROR -> {
                drawable = ContextCompat.getDrawable(context, R.drawable.baseline_error_24)!!
                color = "#A60707"
            };
        }

        val colorInt = Color.parseColor(color)
        drawable.setTint(colorInt)

        textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, drawable, null);
        textView.text = msg
        textView.visibility = View.VISIBLE
        textView.startAnimation(fadeInAnimation)

        // Defina um tempo em milissegundos para que a Toast fique visível
        val delayMillis = 2000L // 2 segundos

        // Use um Handler para ocultar a Toast após o atraso
        Handler(Looper.getMainLooper()).postDelayed({
            textView.startAnimation(fadeOutAnimation)
            textView.visibility = View.GONE
        }, delayMillis)
    }
}