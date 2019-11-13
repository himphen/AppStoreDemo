package com.hibernatev2.appstoredemo.rest.model

import android.content.Context
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.ref.WeakReference


/**
 * Created by himphen on 18/5/16.
 */
open class BaseCallback<BaseResponse>(mContext: Context) : Callback<BaseResponse> {

    public var mContextReference: WeakReference<Context>

    init {
        mContextReference = WeakReference(mContext)
    }

    override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {}

    override fun onFailure(call: Call<BaseResponse>, t: Throwable) {}
}