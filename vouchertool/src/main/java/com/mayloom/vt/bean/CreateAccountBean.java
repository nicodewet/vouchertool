package com.mayloom.vt.bean;


public class CreateAccountBean extends LoginBean {

	private String confirmPassword = "";

	public String getConfirmPassword() {
		return confirmPassword;
	}
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

}
