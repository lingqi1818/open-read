package org.codeanywhere.openread.activity;

import org.codeanywhere.openread.R;
import org.codeanywhere.openread.view.PullToRefreshView;
import org.codeanywhere.openread.view.PullToRefreshView.OnFooterRefreshListener;
import org.codeanywhere.openread.view.PullToRefreshView.OnHeaderRefreshListener;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 实现OnHeaderRefreshListener,OnFooterRefreshListener接口
 * 
 * @author Administrator
 * 
 */
public class TestListView extends ListActivity implements
		OnHeaderRefreshListener, OnFooterRefreshListener {
	PullToRefreshView mPullToRefreshView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_listview);
		mPullToRefreshView = (PullToRefreshView) findViewById(R.id.main_pull_refresh_view);

		setListAdapter(new DataAdapter(this));
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Toast.makeText(this, "positon = " + position, 1000).show();
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				mPullToRefreshView.onFooterRefreshComplete();
			}
		}, 1000);
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				// 设置更新时间
				// mPullToRefreshView.onHeaderRefreshComplete("最近更新:01-23 12:01");
				mPullToRefreshView.onHeaderRefreshComplete();
			}
		}, 1000);

	}
}
