package com.cornflower.pay;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.cornflower.pay.entity.AuthResult;
import com.cornflower.pay.entity.PayResult;
import com.cornflower.pay.h5.H5GetTakePhotoActivity;
import com.cornflower.pay.presenter.PayPresenter;
import com.cornflower.pay.view.PayView;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.service.XGPushService;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.Map;

/**
 *  重要说明:
 *  
 *  这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
 *  真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
 *  防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险； 
 */
public class PayDemoActivity extends FragmentActivity implements PayView {
	private  ProgressDialog progressDialog;
	IWXAPI api = null ;

	
	private static final int SDK_PAY_FLAG = 1;
	private static final int SDK_AUTH_FLAG = 2;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@SuppressWarnings("unused")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				@SuppressWarnings("unchecked")
				PayResult payResult = new PayResult((Map<String, String>) msg.obj);
				/**
				 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
				 */
				String resultInfo = payResult.getResult();// 同步返回需要验证的信息
				String resultStatus = payResult.getResultStatus();
				// 判断resultStatus 为9000则代表支付成功
				if (TextUtils.equals(resultStatus, "9000")) {
					// 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
					Toast.makeText(PayDemoActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
				} else {
					// 该笔订单真实的支付结果，需要依赖服务端的异步通知。
					Toast.makeText(PayDemoActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
				}
				break;
			}
			case SDK_AUTH_FLAG: {
				@SuppressWarnings("unchecked")
				AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
				String resultStatus = authResult.getResultStatus();

				// 判断resultStatus 为“9000”且result_code
				// 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
				if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
					// 获取alipay_open_id，调支付时作为参数extern_token 的value
					// 传入，则支付账户为该授权账户
					Toast.makeText(PayDemoActivity.this,
							"授权成功\n" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT)
							.show();
				} else {
					// 其他状态值则为授权失败
					Toast.makeText(PayDemoActivity.this,
							"授权失败" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT).show();

				}
				break;
			}
			default:
				break;
			}
		};
	};
    PayPresenter payPresenter;
	AppCompatEditText ed_trade_no;
	AppCompatEditText amount;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_main);
		payPresenter = new PayPresenter(this,this);
		ed_trade_no = (AppCompatEditText) findViewById(R.id.ed_trade_no);
		amount = (AppCompatEditText) findViewById(R.id.amount);
		api = WXAPIFactory.createWXAPI(this, BuildConfig.APPID);
//		api.handleIntent(getIntent(), this);
		XGPushManager.registerPush(this,"18550073882");

// 2.36（不包括）之前的版本需要调用以下2行代码
		Intent service = new Intent(this, XGPushService.class);

		startService(service);
	}
	
	/**
	 * 支付宝支付业务
	 * 
	 * @param v
	 */
	public void payV2(View v) {
		payPresenter.aliPay();

	}

	/**
	 * get the sdk version. 获取SDK版本号
	 * 
	 */
//	public void getSDKVersion() {
//		PayTask payTask = new PayTask(this);
//		String version = payTask.getVersion();
//		Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
//	}

	/**
	 * 原生的H5（手机网页版支付切natvie支付） 【对应页面网页支付按钮】
	 * 
	 * @param v
	 */
	public void h5Pay(View v) {
		Intent intent = new Intent(this, H5PayDemoActivity.class);
		Bundle extras = new Bundle();
		/**
		 * url是测试的网站，在app内部打开页面是基于webview打开的，demo中的webview是H5PayDemoActivity，
		 * demo中拦截url进行支付的逻辑是在H5PayDemoActivity中shouldOverrideUrlLoading方法实现，
		 * 商户可以根据自己的需求来实现
		 */
//		String url = "http://m.taobao.com";
//		String url = "http://www.51hszg.com/xbos/message/message_showApplyInfo.action?bid=1&mid=1";
		// url可以是一号店或者淘宝等第三方的购物wap站点，在该网站的支付过程中，支付宝sdk完成拦截支付
		String url ="http://58.211.161.180:8080/alipay/item.jsp"	;
		extras.putString("url", url);
		intent.putExtras(extras);
		startActivity(intent);
	}
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
//		api.handleIntent(intent, this);
	}
	public void weipay(View view){
		payPresenter.weiChatPay(api);

	}
	public void takePhoto(View view){
		Intent intent = new Intent(this, H5GetTakePhotoActivity.class);
		startActivity(intent);

	}

	public void XGPush(View view){
		Intent intent = new Intent(this, com.cornflower.pay.XGPush.MainActivity.class);
		startActivity(intent);

	}



	@Override
	public void onSuccess(String success) {
		Toast.makeText(PayDemoActivity.this,
				success, Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public void onError(String error) {
		Toast.makeText(PayDemoActivity.this,
				error, Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public void showProgress(String s) {
		progressDialog = ProgressDialog.show(this, "", s);
	}

	@Override
	public void hideProgress() {
		if(progressDialog!=null&&progressDialog.isShowing())
			progressDialog.dismiss();
	}
}
