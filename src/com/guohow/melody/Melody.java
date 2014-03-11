package com.guohow.melody;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.guohow.melody.player.MusicPlayer;
import com.guohow.melody.settings.Settings;
import com.guohow.melody.ui.IndicatorFragmentActivity;

public class Melody extends IndicatorFragmentActivity {

	BroadcastReceiver br1;
	BroadcastReceiver br2;

	boolean fullScreen;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.guohow.melody.ui.IndicatorFragmentActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(br1);
		unregisterReceiver(br2);

		super.onDestroy();
	}

	public static final int FRAGMENT_ONE = 0;
	public static final int FRAGMENT_TWO = 1;
	public static final int FRAGMENT_THREE = 2;

	// 列表页
	public static int FLAG = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getIfFullScreen()) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}

		// 意图过滤器
		IntentFilter filter = new IntentFilter();
		IntentFilter filter2 = new IntentFilter();
		br1 = new PhoneListener();
		// 耳机监听
		br2 = new HeadsetPlugReceiver();

		filter2.addAction("android.intent.action.HEADSET_PLUG");
		// 播出电话暂停音乐播放
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");

		registerReceiver(br1, filter);
		registerReceiver(br2, filter2);

		// 创建一个电话服务
		TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

		// 监听电话状态，接电话时停止播放
		manager.listen(new MyPhoneStateListener(),
				PhoneStateListener.LISTEN_CALL_STATE);

	}

	@Override
	protected int supplyTabs(List<TabInfo> tabs) {
		tabs.add(new TabInfo(FRAGMENT_ONE, "红心", FavourList.class));
		tabs.add(new TabInfo(FRAGMENT_TWO, "正在播放", Player.class));
		tabs.add(new TabInfo(FRAGMENT_THREE, "全部", AllSongsList.class));

		return FRAGMENT_TWO;
		// return FRAGMENT_THREE;

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// do something...
			// 退出时暂停，等待恢复，继续播放
			MusicPlayer.stop();
			MusicPlayer.FLAG = 0;
			// MusicPlayer.player = null;
			finish();

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuItem about = menu.add("更多");
		about.setIntent(new Intent(this, Settings.class));
		// MenuItem quit = menu.add("退出");
		// about.s

		return true;
	}

	public boolean getIfFullScreen() {

		SharedPreferences spf = PreferenceManager
				.getDefaultSharedPreferences(this);
		fullScreen = spf.getBoolean("full_screen_perf", true);
		return fullScreen;
	}

	private final class PhoneListener extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			MusicPlayer.pause();
			MusicPlayer.FLAG = 0;
		}
	}

	private final class MyPhoneStateListener extends PhoneStateListener {
		public void onCallStateChanged(int state, String incomingNumber) {
			MusicPlayer.pause();
			MusicPlayer.FLAG = 0;
		}
	}

	private class HeadsetPlugReceiver extends BroadcastReceiver {

		private static final String TAG = "HeadsetPlugReceiver";

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.hasExtra("state")) {
				if (intent.getIntExtra("state", 0) == 0) {
					Toast.makeText(context, "耳机未连接", Toast.LENGTH_LONG).show();
					MusicPlayer.pause();
					MusicPlayer.FLAG = 0;
				} else if (intent.getIntExtra("state", 0) == 1) {
					Toast.makeText(context, "耳机已连接", Toast.LENGTH_LONG).show();
				}
			}

		}

	}

}
