package com.cornflower.pay.util;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

/**
 * Created by xiejingbao on 2016/11/10.
 */

public class PublicUtils {

    /**
     *
     * @param code
     * @return
     */
    public static String codeToString(String code){
        String msg = "";
        switch (code){
            case "9000":
                msg = "订单支付成功";
            break;
            case "8000":
                msg = "正在处理中，支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态!";
                break;
            case "4000":
                msg = "订单支付失败,系统繁忙，请稍后再试!";
                break;
            case "5000":
                msg = "重复请求!";
                break;
            case "6001":
                msg = "用户中途取消!";
                break;
            case "6002":
                msg = "网络连接出错!";
                break;
            case "6004":
                msg = "支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态!";
                break;
            default:
                msg = "其它支付错误";
                break;
        }
        return msg;
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}
