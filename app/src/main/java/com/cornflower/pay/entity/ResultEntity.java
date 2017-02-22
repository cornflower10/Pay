package com.cornflower.pay.entity;

import java.io.Serializable;

public class ResultEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	private String member_id;
	private String nickname;
	private String headimgurl;
	public String getMember_id() {
		return member_id;
	}
	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getHeadimgurl() {
		return headimgurl;
	}
	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}
	

}
