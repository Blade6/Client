package com.example.jianhong.note.entity;

public interface HttpCallbackListener {

	void onFinish(String response);
	
	void onError(Exception e);
	
}
