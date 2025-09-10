package com.dmss.burbankapp.data.api

import android.content.Context
import com.dmss.burbankapp.R
import java.io.IOException

class NoConnectivityException(private val context: Context) :
    IOException() {
    override val message: String
        get() = context.getString(R.string.error_no_internet_connection)

}
