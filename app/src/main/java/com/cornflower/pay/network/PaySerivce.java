package com.cornflower.pay.network;
import com.cornflower.pay.BuildConfig;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by bao on 2016/8/15.
 */
public interface PaySerivce {
 //   http://58.211.161.180:8080/location/sendLocation
    @POST(BuildConfig.ALIPAY)
    Observable<String> toPay();

   @FormUrlEncoded
    @POST(BuildConfig.APPRESULT)
    Observable<String> sendPay(@Field("result") String result);
//    @FormUrlEncoded
//    @POST(BuildConfig.WEI_PAY)
//    Observable<String> weiPay(@Field("username") String username);
   @POST(BuildConfig.WEI_PAY)
    Observable<String> weiPay();

    @POST(BuildConfig.WEIPAY_RESULT)
    Observable<String> weiPayResult();


//
//    @POST("refundInfo/{trade_no}/{refund_amount}/{refund_reason}")
//    Observable<String> refound(@Path("trade_no") String trade_no,
//                               @Path("refund_amount") String refund_amount,
//                               @Path("refund_reason") String refund_reason);
}
