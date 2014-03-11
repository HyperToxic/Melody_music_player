package com.guohow.melody;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.guohow.melody.player.MusicPlayer;
import com.guohow.melody.ui.SlideBarView;
import com.guohow.melody.ui.TitleIndicator;
import com.guohow.melody.ui.SlideBarView.OnTouchLetterChangeListenner;
import com.guohow.melody.utils.Mp3Info;
import com.guohow.melody.utils.MusicList;

public class AllSongsList extends Fragment {

	public View mRootView;
	TextView mName = null;

	TextView bottomInfo, float_letter;
	ListView listView = null;
	SlideBarView mSlideBar = null;

	String mTitle = "请选择歌曲";
	String mArt = "melody";

	public static int _delIndex;
	// 更新底兰info

	Handler handler = new Handler();

	// List<File> mList;
	List<HashMap<String, String>> data;

	protected Context mContext;

	public AllSongsList() {
		super();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity.getApplicationContext();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.activity_songslist, container,
				false);

		initMusicListAdapter();
		listItemAda();
		// 监听长按事件
		onItemLongPressedControler();
		// mySlideBarInit();

		return mRootView;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		// handler更新底兰
		handler.post(new Runnable() {
			public void run() {

				if (MusicPlayer.data != null && bottomInfo != null) {
					mTitle = data.get(MusicPlayer.current).get("title");
					mArt = data.get(MusicPlayer.current).get("artist");
					bottomInfo.setText("正在播放:" + mTitle + "\t" + "艺术家:" + mArt);
					// 传递歌曲参数给player
					Player.playingSongInfo = mTitle;
					Player.playingArtInfo = "艺术家:" + mArt;

				}

				// 1秒之后再次发送
				handler.postDelayed(this, 1000);
			}
		});

		super.onResume();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onViewStateRestored(android.os.Bundle)
	 */
	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		// handler更新底兰
		handler.post(new Runnable() {
			public void run() {
				if (MusicPlayer.data != null && bottomInfo != null) {
					mTitle = data.get(MusicPlayer.current).get("title");
					mArt = data.get(MusicPlayer.current).get("artist");
					bottomInfo.setText("正在播放:" + mTitle + "\t" + "艺术家:" + mArt);
					// 传递歌曲参数给player
					Player.playingSongInfo = mTitle;
					Player.playingArtInfo = mArt;
				}
				// 1秒之后再次发送
				handler.postDelayed(this, 1000);
			}
		});
		super.onViewStateRestored(savedInstanceState);
	}

	public void initMusicListAdapter() {

		listView = (ListView) mRootView.findViewById(R.id.mListView);
		// mSlideBar = (SlideBarView) mRootView.findViewById(R.id.slideBar);

		bottomInfo = (TextView) mRootView.findViewById(R.id.info);
		// 獲取CONTEXT
		Context con = mRootView.getContext();
		// 获取CURSOR
		Cursor cursor = con.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
				MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		List<Mp3Info> mp3InfoList = MusicList.getMp3Infos(cursor);
		MusicList.listUpdate(mp3InfoList);
		// 更新data
		data = MusicList.mp3list;
		// 更新WholeSize
		// update info of the botttomBar by guohao
		// mTitle = "请选择";

		bottomInfo.setText("本地曲目：" + data.size() + "首");
		// 自定义ADP
		// 2行显示，曲目和作者
		SimpleAdapter adapter = new SimpleAdapter(con, data,
				R.layout.songsitem, new String[] { "title", "artist",
						"duration" }, new int[] { R.id.mTitle, R.id.mArt,
						R.id.mDuration });
		listView.setAdapter(adapter);

	}

	private void listItemAda() {

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub

				// 每次只会运行一个实例？？？
				MusicPlayer.hasEverPlayed = true;
				bottomInfo.setVisibility(View.VISIBLE);
				if (MusicPlayer.FLAG == 0) {
					new MusicPlayer(position, data).play();
					MusicPlayer.FLAG = 1;
					// 设置listView的当前位置
					// listView.setSelection(position);
				} else if (MusicPlayer.FLAG == 1) {

					if (position == MusicPlayer.current) {
						MusicPlayer.pause();
						MusicPlayer.FLAG = 0;
					} else {
						new MusicPlayer(position, data).play();
						// 设置FLAG为1
						MusicPlayer.FLAG = 1;
					}

				}
			}
		});

		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE: //
					if (bottomInfo != null) {
						bottomInfo.setVisibility(View.VISIBLE);
					}

					System.out.println("停止...");
					break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
					if (bottomInfo != null) {
						bottomInfo.setVisibility(View.GONE);
					}

					System.out.println("正在滑动...");
					break;
				case OnScrollListener.SCROLL_STATE_FLING:

					System.out.println("开始滚动...");

					break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});

	}

	private void onItemLongPressedControler() {
		listView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

			@Override
			public void onCreateContextMenu(ContextMenu menu, View arg1,
					ContextMenuInfo menuInfo) {
				// TODO Auto-generated method stub
				_delIndex = ((AdapterContextMenuInfo) menuInfo).position;// 获取menu点击项的position

				// menu.setHeaderIcon(R.drawable.ic_launcher);
				menu.setHeaderTitle(data.get(_delIndex).get("title"));
				menu.add(0, 0, 0, "删除这首");
				menu.add(0, 1, 0, "标记为我喜欢");
				menu.add(0, 2, 0, "查看该歌手其他作品");
				menu.add(0, 3, 0, "设为铃声");
				menu.add(0, 4, 0, "分享");

			}
		});

	}

	// 长按菜单响应函数
	public boolean onContextItemSelected(MenuItem item) {

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		// MID = (int) info.id;// 这里的info.id对应的就是数据库中_id的值

		switch (item.getItemId()) {
		case 0:

			File file = new File(data.get(_delIndex).get("url"));
			if (file.exists()) {
				file.delete();
				Toast.makeText(mRootView.getContext(),
						"已经删除" + data.get(_delIndex).get("url"),
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(mRootView.getContext(),
						"删除失败" + "错误代码：" + _delIndex, Toast.LENGTH_SHORT)
						.show();
			}
			break;

		case 1:

			break;

		case 2:

			break;

		default:
			break;
		}

		return super.onContextItemSelected(item);

	}

	// 可能是fargment问题，改控件点击白屏
	public void mySlideBarInit() {
		// mSlideBar = (SlideBarView) mRootView.findViewById(R.id.slideBar);
		float_letter = (TextView) mRootView.findViewById(R.id.float_letter);

		mSlideBar
				.setOnTouchLetterChangeListenner(new OnTouchLetterChangeListenner() {

					@Override
					public void onTouchLetterChange(MotionEvent event, String s) {
						// TODO Auto-generated method stub
						float_letter.setText(s);
						switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
						case MotionEvent.ACTION_MOVE:
							float_letter.setVisibility(View.VISIBLE);
							break;

						case MotionEvent.ACTION_UP:
						default:
							float_letter.postDelayed(new Runnable() {

								@Override
								public void run() {
									float_letter.setVisibility(View.GONE);
								}
							}, 100);
							break;
						}
						int position = data.indexOf(s);// 这个array就是传给自定义Adapter的
						listView.setSelection(position);// 调用ListView的setSelection()方法就可实现了
					}
				});
	}

}
