package com.cornflower.pay;

import com.cornflower.pay.presenter.PayPresenter;
import com.cornflower.pay.util.PublicUtils;
import com.cornflower.pay.view.PayView;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.ShowMessageFromWX;
import com.tencent.mm.sdk.modelmsg.WXAppExtendObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import me.curzbin.library.BottomDialog;
import me.curzbin.library.Item;
import me.curzbin.library.OnItemClickListener;

public class H5PayDemoActivity extends Activity implements PayView, IWXAPIEventHandler {

	private WebView mWebView;
	private PayPresenter payPresenter;
	private ProgressDialog progressDialog;
	IWXAPI api = null ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		payPresenter = new PayPresenter(this,this);
		api = WXAPIFactory.createWXAPI(this, "wx4c7b40f2ce4a29c5");
		api.handleIntent(getIntent(), this);
//		String url = "http://www.51hszg.com/xbos/front/login.jsp";
		String url ="http://58.211.161.180:8080/alipay/item.jsp"	;
		if (TextUtils.isEmpty(url)) {
			// 测试H5支付，必须设置要打开的url网站
			new AlertDialog.Builder(H5PayDemoActivity.this).setTitle("警告")
					.setMessage("必须配置需要打开的url 站点，请在PayDemoActivity类的h5Pay中配置")
					.setPositiveButton("确定", new OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							finish();
						}
					}).show();

		}
		super.requestWindowFeature(Window.FEATURE_NO_TITLE);
		LinearLayout layout = new LinearLayout(getApplicationContext());
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		layout.setOrientation(LinearLayout.VERTICAL);
		setContentView(layout, params);

		mWebView = new WebView(getApplicationContext());
		params.weight = 1;
		mWebView.setVisibility(View.VISIBLE);
		layout.addView(mWebView, params);

		WebSettings settings = mWebView.getSettings();
		settings.setRenderPriority(RenderPriority.HIGH);
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

	@Override
	public void onReq(BaseReq baseReq) {
		switch (baseReq.getType()) {
			case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
//				goToGetMsg();
				Toast.makeText(this,
						ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX, Toast.LENGTH_SHORT)
						.show();
				break;
			case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
				goToShowMsg((ShowMessageFromWX.Req) baseReq);
				break;
			default:
				break;
		}
	}
	private void goToShowMsg(ShowMessageFromWX.Req showReq) {
		WXMediaMessage wxMsg = showReq.message;
		WXAppExtendObject obj = (WXAppExtendObject) wxMsg.mediaObject;

		StringBuffer msg = new StringBuffer(); // 组织一个待显示的消息内容
		msg.append("description: ");
		msg.append(wxMsg.description);
		msg.append("\n");
		msg.append("extInfo: ");
		msg.append(obj.extInfo);
		msg.append("\n");
		msg.append("filePath: ");
		msg.append(obj.filePath);
		Toast.makeText(this,
				msg.toString(), Toast.LENGTH_SHORT)
				.show();

	}
	@Override
	public void onResp(BaseResp resp) {
		Log.e("onResp", "onPayFinish, errCode = " + resp.errCode+"\n"+resp.openId+"\n"+resp.openId);

		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			builder.setTitle("提示");
//			builder.setMessage(String.valueOf(resp.errCode));
//			builder.show();
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
		}


		int result = 0;

		switch (resp.errCode) {
			case BaseResp.ErrCode.ERR_OK:
				result = R.string.errcode_success;
				break;
			case BaseResp.ErrCode.ERR_USER_CANCEL:
				result = R.string.errcode_cancel;
				break;
			case BaseResp.ErrCode.ERR_AUTH_DENIED:
				result = R.string.errcode_deny;
				break;
			default:
				result = R.string.errcode_unknown;
				break;
		}

		Toast.makeText(this, result, Toast.LENGTH_LONG).show();
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
				new BottomDialog(H5PayDemoActivity.this)
						.title("分享")
						.layout(BottomDialog.GRID)
						.orientation(BottomDialog.VERTICAL)
						.inflateMenu(R.menu.menu_grid, new OnItemClickListener() {
							@Override
							public void click(Item item) {
								WXWebpageObject webpage = new WXWebpageObject();
								webpage.webpageUrl = "http://58.211.161.180:8080/alipay/downloadPage";
								WXMediaMessage msg = new WXMediaMessage(webpage);
								msg.title = "WebPage Title WebPage Title WebPage Title WebPage Title WebPage Title WebPage Title WebPage Title WebPage Title WebPage Title Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long";
								msg.description = "WebPage Description WebPage Description WebPage Description WebPage Description WebPage Description WebPage Description WebPage Description WebPage Description WebPage Description Very Long Very Long Very Long Very Long Very Long Very Long Very Long";
								Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.myicon);
								msg.thumbData = PublicUtils.bmpToByteArray(thumb, true);

								SendMessageToWX.Req req = new SendMessageToWX.Req();
								req.transaction = buildTransaction("webpage");
								req.message = msg;
								if(item.getTitle().equals(getString(R.string.wechat))){
									req.scene =  SendMessageToWX.Req.WXSceneSession;
								}else if(item.getTitle().equals(getString(R.string.moments))){
									req.scene = SendMessageToWX.Req.WXSceneTimeline;
								}
								api.sendReq(req);
//								Toast.makeText(H5PayDemoActivity.this, getString(R.string.share_title) + item.getTitle(), Toast.LENGTH_SHORT).show();
							}
						})
						.show();
				return  true;
			}
//			   if(url.contains("pay/pay_weixinPrePay.action")){
//				payPresenter.weiChatPay(api);
//				return  true;
//			}else if(url.contains("alipay/alipay_orderInfo.action")){
//				payPresenter.aliPay();
//				return  true;
//			}
			else{
				view.loadUrl(url);
			}
			return true;

		}
	}
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
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
	}
}
