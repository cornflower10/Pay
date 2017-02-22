package com.cornflower.pay.entity;

import java.io.Serializable;

/**
 * Created by bao on 2016/10/24.
 */
public class Data implements Serializable {
    private static final long serialVersionUID = 1L;
    private String qrcode_url;

    public String getQrcode_url() {
        return qrcode_url;
    }

    public void setQrcode_url(String qrcode_url) {
        this.qrcode_url = qrcode_url;
    }
}
