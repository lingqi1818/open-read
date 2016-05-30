package org.codeanywhere.openread.activity;

import java.util.ArrayList;
import java.util.List;

import org.codeanywhere.openread.R;
import org.codeanywhere.openread.view.PullToRefreshView;
import org.codeanywhere.openread.view.PullToRefreshView.OnFooterRefreshListener;
import org.codeanywhere.openread.view.PullToRefreshView.OnHeaderRefreshListener;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

public class TestGridView extends Activity implements OnHeaderRefreshListener,
		OnFooterRefreshListener {
	PullToRefreshView mPullToRefreshView;
	GridView mGridView;
	private LayoutInflater inflater;
	private List<Integer> listDrawable = new ArrayList<Integer>();
	private MyAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.test_gridview);

			inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			listDrawable.add(R.drawable.pic1);
			listDrawable.add(R.drawable.pic1);

			mPullToRefreshView = (PullToRefreshView) findViewById(R.id.main_pull_refresh_view);
			mGridView = (GridView) findViewById(R.id.gridview);
			adapter = new MyAdapter();
			// mGridView.setAdapter(new DataAdapter(this));
			mGridView.setAdapter(adapter);
			mPullToRefreshView.setOnHeaderRefreshListener(this);
			mPullToRefreshView.setOnFooterRefreshListener(this);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				System.out.println("上拉加载");
				listDrawable.add(R.drawable.pic1);
				adapter.notifyDataSetChanged();
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
				System.out.println("下拉更新");
				listDrawable.add(R.drawable.pic1);
				adapter.notifyDataSetChanged();
				mPullToRefreshView.onHeaderRefreshComplete();
			}
		}, 1000);

	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listDrawable.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return listDrawable.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = inflater.inflate(R.layout.grid_item, null);

			return view;
		}

	}
}
