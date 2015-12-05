package com.example.bluetoothcar;

import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.view.View.OnTouchListener;
import android.widget.Button;

public class ButtonControlActivity extends Activity implements OnTouchListener {

	private Button upButton, downButton, leftButton, rightButton;
	private OutputStream outputStream;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_control);
		upButton = (Button) findViewById(R.id.upButton);
		downButton = (Button) findViewById(R.id.downButton);
		leftButton = (Button) findViewById(R.id.leftButton);
		rightButton = (Button) findViewById(R.id.rightButton);

		upButton.setOnTouchListener(this);
		downButton.setOnTouchListener(this);
		leftButton.setOnTouchListener(this);
		rightButton.setOnTouchListener(this);
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.upButton:
			writeOutputStream(arg1, "a", "e");
			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
				upButton.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.up1));
			}
			if (arg1.getAction() == MotionEvent.ACTION_UP) {
				upButton.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.up));
			}
			break;

		case R.id.downButton:
			writeOutputStream(arg1, "b", "e");
			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
				downButton.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.down1));
			}
			if (arg1.getAction() == MotionEvent.ACTION_UP) {
				downButton.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.down));
			}
			break;

		case R.id.leftButton:
			writeOutputStream(arg1, "c", "e");
			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
				leftButton.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.left1));
			}
			if (arg1.getAction() == MotionEvent.ACTION_UP) {
				leftButton.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.left));
			}
			break;

		case R.id.rightButton:
			writeOutputStream(arg1, "d", "e");
			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
				rightButton.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.right1));
			}
			if (arg1.getAction() == MotionEvent.ACTION_UP) {
				rightButton.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.right));
			}
			break;
		}

		return false;
	}

	private void writeOutputStream(MotionEvent arg1, String m1, String m2) {
		// 每次点击都要重新获得输出流，以防止蓝牙断开重新连接后输出流改变了
		outputStream = MainActivity.outputStream;
		if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
			String message;
			byte[] buffer;
			message = m1;
			buffer = message.getBytes();
			try {
				outputStream.write(buffer);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (arg1.getAction() == MotionEvent.ACTION_UP) {
			String message;
			byte[] buffer;
			message = m2;
			buffer = message.getBytes();
			try {
				outputStream.write(buffer);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
