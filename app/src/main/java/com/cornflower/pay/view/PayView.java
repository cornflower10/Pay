package com.cornflower.pay.view;

import com.tencent.mm.sdk.modelbase.BaseResp;

/**
 * Created by xiejingbao on 2016/11/10.
 */

public interface PayView extends BaseView {
    void onSuccess(String success);
    void  onError(String error);
}
