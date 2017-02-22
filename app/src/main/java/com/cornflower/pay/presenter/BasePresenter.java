package com.cornflower.pay.presenter;

import android.content.Context;

public class BasePresenter<T> {
	protected Context mContext;
	protected T IView;
	public BasePresenter(Context mContext, T IView){
		this.mContext = mContext; 
		this.IView = IView;
	}

}
