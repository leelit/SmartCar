package com.example.bluetoothcar;

import java.io.IOException;
import java.io.OutputStream;

import com.example.bluetoothcar.R;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.widget.Button;

public class ControlActivity extends Activity {

	private OutputStream outputStream;
	private Sensor sensor;
	private SensorManager sensorManager;
	private boolean upOneTime = true;
	private boolean downOneTime = true;
	private boolean leftOneTime = true;
	private boolean rightOneTime = true;
	private boolean stopOneTime = true;
	// 屏幕常亮
	private PowerManager powerManager = null;
	private WakeLock wakeLock = null;

	private Button button1, button2, button3, button4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_control);

		powerManager = (PowerManager) this.getSystemService(POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK,
				"My Lock");

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		buttonInit();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		wakeLock.acquire();
		sensorManager.registerListener(eventListener, sensor,
				SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		wakeLock.release();
		sensorManager.unregisterListener(eventListener);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		try {
			outputStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onDestroy();
	}

	private SensorEventListener eventListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent arg0) {
			// TODO Auto-generated method stub
			if (arg0.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

				if (arg0.values[0] < -2.4 && upOneTime) {
					writeOutputStream("a");
					button1.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.up));
					button2.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.down));
					button3.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.left));
					button4.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.right1));
					upOneTime = false;
					downOneTime = true;
					leftOneTime = true;
					rightOneTime = true;
					stopOneTime = true;
				}
				if (arg0.values[0] > 2.4 && downOneTime) {
					writeOutputStream("b");
					button1.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.up));
					button2.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.down));
					button3.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.left1));
					button4.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.right));
					upOneTime = true;
					downOneTime = false;
					leftOneTime = true;
					rightOneTime = true;
					stopOneTime = true;

				}
				if (arg0.values[1] < -2.2 && leftOneTime) {
					writeOutputStream("c");
					button1.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.up1));
					button2.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.down));
					button3.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.left));
					button4.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.right));
					upOneTime = true;
					downOneTime = true;
					leftOneTime = false;
					rightOneTime = true;
					stopOneTime = true;

				}
				if (arg0.values[1] > 2.2 && rightOneTime) {
					writeOutputStream("d");
					button1.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.up));
					button2.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.down1));
					button3.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.left));
					button4.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.right));
					upOneTime = true;
					downOneTime = true;
					leftOneTime = true;
					rightOneTime = false;
					stopOneTime = true;

				}
				if ((-2.4 < arg0.values[0] && arg0.values[0] < 2.4)
						&& (-2.4 < arg0.values[1] && arg0.values[1] < 2.4)
						&& stopOneTime) {
					writeOutputStream("e");
					button1.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.up));
					button2.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.down));
					button3.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.left));
					button4.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.right));
					upOneTime = true;
					downOneTime = true;
					leftOneTime = true;
					rightOneTime = true;
					stopOneTime = false;
				}

			}
		}

		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			// TODO Auto-generated method stub

		}
	};

	private void writeOutputStream(String m1) {
		// 每次点击都要重新获得输出流，以防止蓝牙断开重新连接后输出流改变了
		outputStream = MainActivity.outputStream;
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
	}

	private void buttonInit() {
		button1 = (Button) findViewById(R.id.upButton);
		button2 = (Button) findViewById(R.id.downButton);
		button3 = (Button) findViewById(R.id.leftButton);
		button4 = (Button) findViewById(R.id.rightButton);
		button1.setText("左");
		button2.setText("右");
		button3.setText("下");
		button4.setText("上");
	}

}
