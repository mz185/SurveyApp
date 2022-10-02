package com.example.surveyapp.util

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.surveyapp.R
import com.example.surveyapp.data.models.Message
import com.google.android.material.snackbar.Snackbar

/**
 * Created by Marinos Zinonos on 29/09/2022.
 */

fun <V : View> V.showSnackBar(message: Message) {
    Snackbar.make(this, message.message, Snackbar.LENGTH_LONG).apply {
        message.action?.let {
            setAction("Retry") { it() }
                .setActionTextColor(ContextCompat.getColor(context, R.color.white))
        }
    }.setBackgroundTint(ContextCompat.getColor(context,
        if (message.isSuccess)
            R.color.green
        else R.color.red
    )).show()
}

fun <TV : TextView> TV.updateAvailability(enabled: Boolean) = apply {
    isEnabled = enabled
    setTextColor(ContextCompat.getColor(context,
        if (enabled) R.color.purple_700
        else R.color.gray)
    )
}

fun <F : Fragment> F.hideKeyboard() {
    activity?.let { activity ->
        view?.let {
            WindowCompat.getInsetsController(activity.window, it)
                .hide(WindowInsetsCompat.Type.ime())
        }
    }
}
