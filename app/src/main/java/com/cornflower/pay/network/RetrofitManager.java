package com.cornflower.pay.network;

import com.cornflower.pay.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.Observable;

/**
 * Created by laucherish on 16/3/15.
 */
public class RetrofitManager {

    //短缓存有效期为1分钟
    public static final int CACHE_STALE_SHORT = 60;
    //长缓存有效期为7天
    public static final int CACHE_STALE_LONG = 60 * 60 * 24 * 7;

    public static final String CACHE_CONTROL_AGE = "Cache-Control: public, max-age=";

    //查询缓存的Cache-Control设置，为if-only-cache时只查询缓存而不会请求服务器，max-stale可以配合设置缓存失效时间
    public static final String CACHE_CONTROL_CACHE = "only-if-cached, max-stale=" + CACHE_STALE_LONG;
    //查询网络的Cache-Control设置，头部Cache-Control设为max-age=0时则不会使用缓存而请求服务器
    public static final String CACHE_CONTROL_NETWORK = "max-age=0";
    private static OkHttpClient mOkHttpClient;
    private  PaySerivce payService ;

    public static RetrofitManager builder(){
        return new RetrofitManager();
    }

    private RetrofitManager() {

        initOkHttpClient();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.URL)
                .client(mOkHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
//                .addConverterFactory(new RxJavaCallAdapterFactory()))
                .build();
        payService = retrofit.create(PaySerivce.class);
    }

    private void initOkHttpClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        if (mOkHttpClient == null) {
            synchronized (RetrofitManager.class) {
                if (mOkHttpClient == null) {

//                    // 指定缓存路径,缓存大小100Mb
//                    Cache cache = new Cache(new File(App.getContext().getCacheDir(), "HttpCache"),
//                            1024 * 1024 * 100);
                    mOkHttpClient = new OkHttpClient.Builder()
                            .addInterceptor(interceptor)
                            .retryOnConnectionFailure(true)
                            .connectTimeout(15, TimeUnit.SECONDS)
                            .build();
                }
            }
        }
    }

    /**
     * 支付宝请求
     * @return
     */
    public Observable<String> toPay(){
        return payService.toPay();
    }


    /**
     * 同步支付宝支付结果
     * @return
     */
    public Observable<String> sendPay(String result){
        return payService.sendPay(result);
    }


//
//    /**
//     * 退款
//     * @return
//     */
//    public Observable<String> refound(String orderNo,String money,String reason){
//        return payService.refound(orderNo,money,reason);
//    }


    /**
     * 微信支付
     * @return
     */
    public Observable<String> weiPay(){
        return payService.weiPay();
    }
    /**
     * 微信支付结果
     * @return
     */
    public Observable<String> weiPayResult(){
        return payService.weiPayResult();
    }
}
