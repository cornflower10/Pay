package com.cornflower.pay.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.alipay.sdk.app.PayTask;
import com.cornflower.pay.PayResultActivity;
import com.cornflower.pay.R;
import com.cornflower.pay.entity.PayResult;
import com.cornflower.pay.network.RetrofitManager;
import com.cornflower.pay.util.PublicUtils;
import com.cornflower.pay.view.PayView;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import me.curzbin.library.BottomDialog;
import me.curzbin.library.Item;
import me.curzbin.library.OnItemClickListener;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by xiejingbao on 2016/11/10.
 */

public class PayPresenter extends BasePresenter<PayView> implements IUiListener{
    private QQSharePresenter qqSharePresenter;
    public PayPresenter(Context mContext, PayView IView) {
        super(mContext, IView);
        qqSharePresenter = new QQSharePresenter(mContext,this);
    }

    public void aliPay(){
        IView.showProgress("请稍后...");
        RetrofitManager.builder().toPay()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .lift(new Observable.Operator<String, String>() {

                    @Override
                    public Subscriber<? super String> call(final Subscriber<? super String> subscriber) {
                        return new Subscriber<String>() {
                            @Override
                            public void onCompleted() {
                                subscriber.onCompleted();
                            }

                            @Override
                            public void onError(Throwable e) {
                                IView.hideProgress();
                                Log.e("lift--orderInfo", e.getMessage());
                                IView.onError(e.getMessage());
                            }

                            @Override
                            public void onNext(String s) {
                                Log.i("lift--orderInfo", s);
                                subscriber.onNext(s);
                            }
                        };
                    }
                })
                .observeOn(Schedulers.io())
                .flatMap(new Func1<String, Observable<PayResult>>() {
            @Override
            public Observable<PayResult> call(String s) {
                Log.i("Observable<PayResult>>", "====start====");
                PayTask alipay = new PayTask((Activity) mContext);
                Map<String, String> result = alipay.payV2(s, true);
                return Observable.just(new PayResult(result));
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .lift(new Observable.Operator<String, PayResult>() {
            @Override
            public Subscriber<? super PayResult> call(final Subscriber<? super String> subscriber) {
                return new Subscriber<PayResult>() {
                    @Override
                    public void onCompleted() {
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        IView.hideProgress();
                        Log.e("lift PayResult ==", e.getMessage());
                        IView.onError(e.getMessage());
                    }

                    @Override
                    public void onNext(PayResult payResult) {
                        Log.i("lift PayResult ==", payResult.toString());
                        if(payResult.getResultStatus().equals("9000"))
                        subscriber.onNext(payResult.getResult());
                        else {
                            IView.onError(PublicUtils.codeToString(payResult.getResultStatus()));
                        }
                    }
                };
            }
        }).observeOn(Schedulers.io())
                .flatMap(new Func1<String, Observable<String>>() {
            @Override
            public Observable<String> call(String s) {
                Log.i("flatMap 同步数据 ==", s);
                return  RetrofitManager.builder().sendPay(s);
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        IView.hideProgress();
                    }

                    @Override
                    public void onError(Throwable e) {
                        IView.hideProgress();
                        Log.e("subscribe 同步数据失败 ==", e.getMessage());
                        IView.onError(e.getMessage());
                    }

                    @Override
                    public void onNext(String s) {
                        Log.i("subscribe 同步数据成功 ==",s);
                        if(s.startsWith("http")||s.startsWith("https")){
//                            IView.onSuccess(s);
                            Intent intent = new Intent(mContext, PayResultActivity.class);
                            intent.putExtra("url",s);
                            mContext.startActivity(intent);
                            ((Activity)mContext).finish();
                        }else
                        {
                            IView.onError(s);
                        }

                    }
                });


    }
    public void weiChatPay(final IWXAPI api){
        IView.showProgress("请稍后...");
        if(!api.isWXAppInstalled()){
            IView.hideProgress();
            IView.onError("未安装微信！");
            return;
        }
        if(!api.isWXAppSupportAPI()){
            IView.hideProgress();
            IView.onError("微信版本低，请升级高版本");
            return;
        }
        RetrofitManager.builder().weiPay()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .lift(new Observable.Operator<String, String>() {

                    @Override
                    public Subscriber<? super String> call(final Subscriber<? super String> subscriber) {
                        return new Subscriber<String>() {
                            @Override
                            public void onCompleted() {
                                subscriber.onCompleted();
                            }

                            @Override
                            public void onError(Throwable e) {
                                IView.hideProgress();
                                Log.e("lift--orderInfo", e.getMessage());
                                IView.onError(e.getMessage());
                            }

                            @Override
                            public void onNext(String s) {
                                Log.i("lift--orderInfo", s);
                                subscriber.onNext(s);
                            }
                        };
                    }
                })
                .observeOn(Schedulers.io())
                .flatMap(new Func1<String, Observable<PayReq>>() {
                    @Override
                    public Observable<PayReq> call(String s) {

                        JSONObject json = null;  PayReq req = new PayReq();
                        try {
                            json = new JSONObject(s);

                            if(null != json && !json.has("retcode") ) {
                                req.appId = json.getString("appid");
                                req.partnerId = json.getString("partnerid");
                                req.prepayId = json.getString("prepayid");
                                req.nonceStr = json.getString("noncestr");
                                req.timeStamp = json.getString("timestamp");
                                req.packageValue = json.getString("package");
                                req.sign = json.getString("sign");
                                req.extData = "app data"; // optional trade_type
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        boolean b = api.registerApp(req.appId);
                        Log.e("检查是否注册微信", b+"");

                     // 在支付之前，如果应用没有注册到微信，
                        return Observable.just(req);
                    }
                }).observeOn(AndroidSchedulers.mainThread())

                .subscribe(new Subscriber<PayReq>() {
                    @Override
                    public void onCompleted() {
                        IView.hideProgress();
                    }

                    @Override
                    public void onError(Throwable e) {
                        IView.hideProgress();
                        Log.e("error", e.getMessage());
                        IView.onError(e.getMessage());
                    }

                    @Override
                    public void onNext(PayReq req) {
                        Log.e("req ","success");
                        api.sendReq(req);

                    }
                });


    }
    public void weiChatPayResult(){
        IView.showProgress("请稍后...");
        RetrofitManager.builder().weiPayResult()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        IView.hideProgress();
                    }

                    @Override
                    public void onError(Throwable e) {
                        IView.hideProgress();
                        Log.e("error", e.getMessage());
                        IView.onError(e.getMessage());
                    }

                    @Override
                    public void onNext(String s) {
                        Log.i("http ",s);
                        if(s.startsWith("http")||s.startsWith("https")){
                            Intent intent = new Intent(mContext, PayResultActivity.class);
                            intent.putExtra("url",s);
                            mContext.startActivity(intent);
//                            ((Activity)mContext).finish();
                        }else
                        {
                            IView.onError(s);
                        }
                    }
                });


    }
    public void showShareDialog(final String url, final IWXAPI api){
        new BottomDialog(mContext)
                .title("分享")
                .layout(BottomDialog.GRID)
                .orientation(BottomDialog.VERTICAL)
                .inflateMenu(R.menu.menu_grid, new OnItemClickListener() {
                    @Override
                    public void click(Item item) {
                        Log.i("http","item");
                        WXWebpageObject webpage = new WXWebpageObject();
                        webpage.webpageUrl = url;
                        WXMediaMessage msg = new WXMediaMessage(webpage);
                        msg.title = "WebPage Title ";
                        msg.description = "WebPage Description ";
                        Bitmap thumb = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.myicon);
                        msg.thumbData = PublicUtils.bmpToByteArray(thumb, true);

                        SendMessageToWX.Req req = new SendMessageToWX.Req();
                        req.transaction = buildTransaction("webpage");
                        req.message = msg;
                        if(item.getTitle().equals(mContext.getString(R.string.wechat))){
                            req.scene =  SendMessageToWX.Req.WXSceneSession;
                            api.sendReq(req);
                            Log.i("http ","微信好友");
                        }else if(item.getTitle().equals(mContext.getString(R.string.moments))){
                            Log.i("http ","微信盆友圈");
                            req.scene = SendMessageToWX.Req.WXSceneTimeline;
                            api.sendReq(req);
                        }else{
                            Log.i("http","qq");
                            qqSharePresenter.shareQQ(url,item.getTitle());
                        }

                    }
                })
                .show();
    }
    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    @Override
    public void onComplete(Object o) {

    }

    @Override
    public void onError(UiError uiError) {

    }

    @Override
    public void onCancel() {

    }
    public void destory(){
           qqSharePresenter.destory();
    }
}
