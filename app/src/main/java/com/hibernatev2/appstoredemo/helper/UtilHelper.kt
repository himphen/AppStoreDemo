package com.hibernatev2.appstoredemo.helper

import android.util.Log

object UtilHelper {
    private const val TAG = "tag"
    private const val DEBUG_TAG = "debug_tag"

    fun log(message: String) {
        Log.d(TAG, message)
    }

    fun debug(message: String) {
        Log.d(DEBUG_TAG, message)
    }

    fun getCommonSeparatedString(stringList: List<String>): String {
        val sb = StringBuilder()
        for (string in stringList) {
            sb.append(string).append(",")
        }
        if (sb.isNotEmpty()) {
            sb.deleteCharAt(sb.lastIndexOf(","))
        }
        return sb.toString()
    }
}
