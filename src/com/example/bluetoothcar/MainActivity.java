package com.example.bluetoothcar;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	private static final String TAG = "ProcessInfo";

	// 蓝牙管理类
	private BluetoothAdapter btAdapter;
	private BluetoothDevice btDevice;
	private BluetoothSocket btSocket;
	static OutputStream outputStream;

	// 蓝牙扫描广播
	private BroadcastReceiver mReceiver;
	private BroadcastReceiver mReceiver1;

	// 扫描开始与结束的标志
	private ProgressBar progressBar;

	// 用来显示信息的TextView
	private TextView textView1;

	// 用来显示已配对和扫描到的设备的ListView
	private ListView listView1;

	// list1用于识别device，list2用于显示device的MAC地址和名称在ListView上，arrayAdapter1
	// 装载的list就是list2
	private List<BluetoothDevice> list1 = new ArrayList<BluetoothDevice>();
	private List<String> list2 = new ArrayList<String>();
	private ArrayAdapter<String> arrayAdapter1 = null;

	// 创建Rfcomm通道的UUDI码
	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");

	// 蓝牙连接与断开的广播和标志
	private BroadcastReceiver btConnectReceiver;
	private BroadcastReceiver btDisonnectReceiver;
	private IntentFilter connectIntentFilter;
	private IntentFilter disconnectIntentFilter;
	private boolean isConnect = false;

	private String deviceName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		listView1 = (ListView) findViewById(R.id.listview1);
		textView1 = (TextView) findViewById(R.id.textview1);
		progressBar = (ProgressBar) findViewById(R.id.progressbar);

		btAdapter = BluetoothAdapter.getDefaultAdapter();

		// 如果扫描到有蓝牙设备，将其MAC地址和设备名称添加到ListView上
		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				String action = intent.getAction();
				// 找到设备
				textView1.setText("扫描到的蓝牙设备：");
				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					// 通过这个意图获得device
					BluetoothDevice device = intent
							.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					list1.add(device);
					list2.add(device.getAddress() + "  " + device.getName());
					arrayAdapter1 = new ArrayAdapter<String>(MainActivity.this,
							android.R.layout.simple_list_item_1, list2);
					listView1.setAdapter(arrayAdapter1);

				}
			}

		};

		IntentFilter intentFilter = new IntentFilter(
				BluetoothDevice.ACTION_FOUND);
		registerReceiver(mReceiver, intentFilter);

		// 扫描结束时将ProgressBar不可见
		mReceiver1 = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				progressBar.setVisibility(ProgressBar.INVISIBLE);
				if (list1.size() == 0) {
					textView1.setText("没有扫描到有蓝牙设备");
				}
			}

		};

		IntentFilter intentFilter1 = new IntentFilter(
				BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(mReceiver1, intentFilter1);

		// 蓝牙连接时将isConncet标志位置为True
		btConnectReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				// TODO Auto-generated method stub
				Log.e(TAG, "-- connected");
				isConnect = true;
				textView1.setText("连接至" + deviceName + "，请进入控制台");
			}
		};

		connectIntentFilter = new IntentFilter(
				BluetoothDevice.ACTION_ACL_CONNECTED);
		registerReceiver(btConnectReceiver, connectIntentFilter);

		// 蓝牙连接断开时启动TryToConnect线程，确保断开后连接
		btDisonnectReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				// TODO Auto-generated method stub
				Log.e(TAG, "-- disconnected");
				isConnect = false;
				new TryToConnet().start();
			}
		};

		disconnectIntentFilter = new IntentFilter(
				BluetoothDevice.ACTION_ACL_DISCONNECTED);
		registerReceiver(btDisonnectReceiver, disconnectIntentFilter);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		// 当点击一个扫描到的Item时，发起连接，此处没有用线程，通过list1识别设备
		listView1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				// 要连接的设备的设备名
				deviceName = list1.get(arg2).getName();
				// 选择item时立马取消寻找设备
				btAdapter.cancelDiscovery();
				progressBar.setVisibility(ProgressBar.INVISIBLE);
				// 通过list1和arg2识别连接哪个device
				btDevice = btAdapter.getRemoteDevice(list1.get(arg2)
						.getAddress());

				// 点击产生一个Dialog提示是否连接，连接成功后textview显示连接的设备名称
				new AlertDialog.Builder(MainActivity.this)
						.setIcon(R.drawable.ic_launcher).setTitle("连接此设备？")
						.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								new TryToConnet().start();
							}
						}).show();
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// 关闭socket
		if (isConnect) {
			try {
				outputStream.close();
				btSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// 取消广播注册
		unregisterReceiver(btConnectReceiver);
		unregisterReceiver(btDisonnectReceiver);
		unregisterReceiver(mReceiver);
		unregisterReceiver(mReceiver1);
		Log.e(TAG, "-- onDestroy");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		menu.add(Menu.NONE, 0, Menu.NONE, "打开蓝牙");
		menu.add(Menu.NONE, 1, Menu.NONE, "关闭蓝牙");
		menu.add(Menu.NONE, 2, Menu.NONE, "扫描周围蓝牙设备");
		menu.add(Menu.NONE, 3, Menu.NONE, "重力感应控制台");
		menu.add(Menu.NONE, 4, Menu.NONE, "进入按键控制台");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case 0:
			if (!btAdapter.isEnabled()) {
				btAdapter.enable();
				Toast.makeText(MainActivity.this, "蓝牙开启中", Toast.LENGTH_SHORT)
						.show();
			}
			break;
		case 1:
			if (btAdapter.isEnabled()) {
				btAdapter.disable();
			}
			break;

		case 2:
			if (btAdapter.isEnabled()) {
				btAdapter.startDiscovery();
				Toast.makeText(MainActivity.this, "开始扫描", Toast.LENGTH_SHORT)
						.show();
				progressBar.setVisibility(ProgressBar.VISIBLE);
			} else {
				Toast.makeText(MainActivity.this, "请打开蓝牙", Toast.LENGTH_SHORT)
						.show();

			}
			break;
		case 3:
			// 如果连接成功，点击即可进入控制台
			if (isConnect) {
				Intent intent = new Intent(MainActivity.this,
						ControlActivity.class);
				startActivity(intent);
			} else {
				Toast.makeText(MainActivity.this, "请先连接蓝牙", Toast.LENGTH_LONG)
						.show();
			}
			break;

		case 4:
			if (isConnect) {
				Intent intent = new Intent(MainActivity.this,
						ButtonControlActivity.class);
				startActivity(intent);
			} else {
				Toast.makeText(MainActivity.this, "请先连接蓝牙", Toast.LENGTH_LONG)
						.show();
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	// 断开连接后尝试重新连接的线程，连接成功后退出线程
	private class TryToConnet extends Thread {
		public void run() {
			/* 此处必须重新创建一个socket，否则重新连接后无法传输数据，个人猜想是Rfcomm通道已经改变 */
			try {
				btSocket = btDevice.createRfcommSocketToServiceRecord(MY_UUID);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.e(TAG, "-- failed to create btSocket");
			}
			try {
				outputStream = btSocket.getOutputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while (true) {
				try {
					btSocket.connect();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (isConnect) {
					Log.e(TAG, "-- Connect again,ending the TryToConnet thread");
					break;
				}
			}
		}
	}
}
