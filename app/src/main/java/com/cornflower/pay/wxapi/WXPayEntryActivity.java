package com.cornflower.pay.wxapi;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cornflower.pay.presenter.PayPresenter;
import com.cornflower.pay.view.PayView;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler,PayView {
	private WebView mWebView;
	private PayPresenter payPresenter;
	private ProgressDialog progressDialog;

	// IWXAPI 是第三方app和微信通信的openapi接口
	private IWXAPI api;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		payPresenter = new PayPresenter(this,this);
		api = WXAPIFactory.createWXAPI(this, "wx4c7b40f2ce4a29c5",false);
		api.handleIntent(getIntent(), this);
//		String url = "http://www.51hszg.com/xbos/front/login.jsp";
		String url ="http://58.211.161.180:8080/alipay/item.jsp"	;
		super.requestWindowFeature(Window.FEATURE_NO_TITLE);
		LinearLayout layout = new LinearLayout(getApplicationContext());
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		layout.setOrientation(LinearLayout.VERTICAL);
		setContentView(layout, params);

		mWebView = new WebView(getApplicationContext());
		params.weight = 1;
		mWebView.setVisibility(View.VISIBLE);
		layout.addView(mWebView, params);

		WebSettings settings = mWebView.getSettings();
		settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
		settings.setSupportMultipleWindows(true);
		settings.setJavaScriptEnabled(true);
		settings.setSavePassword(false);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setMinimumFontSize(settings.getMinimumFontSize() + 8);
		settings.setAllowFileAccess(false);
		settings.setTextSize(WebSettings.TextSize.NORMAL);
		mWebView.setVerticalScrollbarOverlay(true);
		mWebView.setWebViewClient(new MyWebViewClient());
		mWebView.loadUrl(url);
		Log.i("start", "start......");
	}
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.e("onNewIntent", "onNewIntent");
		setIntent(intent);
		api.handleIntent(intent, this);
	}
	@Override
	public void onBackPressed() {
		if (mWebView.canGoBack()) {
			mWebView.goBack();
		} else {
			finish();
		}
	}

	@Override
	public void finish() {
		super.finish();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}


	@Override
	public void onSuccess(String success) {
		Toast.makeText(this,
				success, Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public void onError(String error) {
		Toast.makeText(this,
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
	// 微信发送请求到第三方应用时，会回调到该方法
	@Override
	public void onReq(BaseReq req) {
	}


	// 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
	@Override
	public void onResp(BaseResp resp) {
		if(resp.errCode==0){
			payPresenter.weiChatPayResult();
			return;
		}else if(resp.errCode==-1){
			Toast.makeText(this,
					"支付错误！", Toast.LENGTH_SHORT)
					.show();
		}
		else if(resp.errCode==-2){
			Toast.makeText(this,
					"支付取消！", Toast.LENGTH_SHORT)
					.show();
		}
		Toast.makeText(this, "返回", Toast.LENGTH_LONG).show();
	}

	private class MyWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(final WebView view, String url) {
			Log.d("url",url);
			if (!(url.startsWith("http") || url.startsWith("https"))) {
				return true;
			}
			if(url.contains("http://58.211.161.180:8080/alipay/orderInfo")) {
				payPresenter.aliPay();
				return  true;
			}else if(url.contains("http://58.211.161.180:8080/alipay/weixinPrePay")){
				payPresenter.weiChatPay(api);
				return  true;
			}else if(url.contains("http://58.211.161.180:8080/alipay/downloadPage")){
				payPresenter.showShareDialog(url,api);
				return  true;
			}
			else{
				view.loadUrl(url);
			}
			return true;

		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mWebView != null) {
			mWebView.removeAllViews();
			try {
				mWebView.destroy();
			} catch (Throwable t) {
			}
			mWebView = null;
		}
		payPresenter.destory();
	}
}