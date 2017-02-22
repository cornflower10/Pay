package com.cornflower.pay.view;


public interface ICommom<T> {
	void onSuccess(T t);
	void onError(String error);

}
