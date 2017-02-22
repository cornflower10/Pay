package com.cornflower.pay;

import android.app.Application;


/**
 * Created by xiejingbao on 2016/11/10.
 */

public class PayApplication extends Application {
    private static PayApplication instance;
    public static PayApplication getInstance() {
        return instance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
//        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",APP_ID,APP_PRIVATE_KEY,"json",CHARSET,ALIPAY_PUBLIC_KEY);
    }
}
