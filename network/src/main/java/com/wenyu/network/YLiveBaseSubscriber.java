package com.wenyu.network;

import android.content.Context;

import java.lang.ref.SoftReference;

import rx.Subscriber;

/**
 * Created by jiacheng.li on 17/1/22.
 * Copyright © 2016年 扇贝网(shanbay.com).
 * All rights reserved.
 */
public abstract class YLiveBaseSubscriber<T> extends Subscriber<T> {
	private SoftReference<Context> mContextSoftReference;

	public YLiveBaseSubscriber(Context context) {
		mContextSoftReference = new SoftReference<Context>(context);
	}

	@Override
	public void onError(Throwable e) {
		if (e instanceof YLiveException && ((YLiveException) e).isNoAuthentication()) {
			onAuthFailed();
		}
	}

	public abstract void onAuthFailed();
}