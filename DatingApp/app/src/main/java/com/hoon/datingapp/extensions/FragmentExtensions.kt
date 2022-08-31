package com.hoon.datingapp.extensions

import android.widget.Toast
import androidx.fragment.app.Fragment

internal fun Fragment.toast(msg: String) {
    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
}