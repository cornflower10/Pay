package com.cornflower.pay.presenter;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import com.cornflower.pay.BuildConfig;
import com.cornflower.pay.R;
import com.cornflower.pay.wxapi.WXPayEntryActivity;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;

import java.util.ArrayList;

/**
 * Created by xiejingbao on 2016/12/28.
 */

public class QQSharePresenter {
    private Context context;
    private Tencent mTencent;
    private int shareType = QQShare.SHARE_TO_QQ_TYPE_DEFAULT;
    private int mExtarFlag = 0x00;
    private IUiListener iUiListener;
    private static final String TITLE = "丁丁乐掌柜专属";
    private static final String SUMMARY = "丁丁乐掌柜全国招募令";
    private static final String IMAGEURL = "http://www.51hszg.com/xbos/front/resource/images/zm.png";

    public QQSharePresenter(Context context, IUiListener iUiListener) {
        mTencent = Tencent.createInstance(BuildConfig.QQ_APP_ID, context);
        this.context = context;
        this.iUiListener = iUiListener;
    }

    public void shareQQ(String url, String type) {
        if (type.equals(context.getString(R.string.qq))) {
            final Bundle params = new Bundle();
            if (shareType != QQShare.SHARE_TO_QQ_TYPE_IMAGE) {
                params.putString(QQShare.SHARE_TO_QQ_TITLE, TITLE);
                params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);
                params.putString(QQShare.SHARE_TO_QQ_SUMMARY, SUMMARY);
            }
//        if (shareType == QQShare.SHARE_TO_QQ_TYPE_IMAGE) {
//            params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, "file:///android_asset/ic_launcher.png");
//        } else {
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, IMAGEURL);
//        }
//        params.putString(shareType == QQShare.SHARE_TO_QQ_TYPE_IMAGE ? QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL
//                : QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl.getText().toString());
            params.putString(QQShare.SHARE_TO_QQ_APP_NAME, context.getString(R.string.app_name));
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, shareType);
            params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, mExtarFlag);

            if ((mExtarFlag & QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN) != 0) {
//            showToast("在好友选择列表会自动打开分享到qzone的弹窗~~~");
            } else if ((mExtarFlag & QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE) != 0) {
//            showToast("在好友选择列表隐藏了qzone分享选项~~~");
            }
            mTencent.shareToQQ((WXPayEntryActivity) context, params, iUiListener);
        } else {
            final Bundle paramsZone = new Bundle();
            paramsZone.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
            paramsZone.putString(QzoneShare.SHARE_TO_QQ_TITLE, TITLE);
            paramsZone.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, SUMMARY);
            paramsZone.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, url);
            ArrayList image = new ArrayList<String>();
            image.add(IMAGEURL);
            paramsZone.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, image);
           new Handler().post(new Runnable() {
               @Override
               public void run() {
                   mTencent.shareToQzone((WXPayEntryActivity) context, paramsZone, iUiListener);
               }
           });

        }

    }

    public void destory() {
        if (mTencent != null) {
            mTencent.releaseResource();
        }

    }

}
