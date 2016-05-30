/*
 * Copyright (C) 2012 yueyueniao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codeanywhere.openread.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import org.codeanywhere.openread.R;
import org.codeanywhere.openread.activity.SubscribActivity;
import org.codeanywhere.openread.view.PullToRefreshView;
import org.codeanywhere.openread.view.PullToRefreshView.OnFooterRefreshListener;
import org.codeanywhere.openread.view.PullToRefreshView.OnHeaderRefreshListener;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;

public class PageFragment1 extends Fragment implements OnHeaderRefreshListener,
		OnFooterRefreshListener {
	PullToRefreshView mPullToRefreshView;
	GridView mGridView;
	private LayoutInflater inflater;
	private String texts[] = null;
	private int images[] = null;
	private SimpleAdapter saImageItems;
	ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;
		View view = inflater.inflate(R.layout.page1, null);
		Button btn = (Button) view.findViewById(R.id.top_add_btn);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				Intent intent = new Intent(view.getContext(),
						SubscribActivity.class);
				view.getContext().startActivity(intent);
			}
		});
		images = new int[] { R.drawable.test_1, R.drawable.test_1,
				R.drawable.test_1, R.drawable.test_1, R.drawable.test_1,
				R.drawable.test_1, R.drawable.test_1, R.drawable.test_1 };
		texts = new String[] { "宫式布局1", "宫式布局2", "宫式布局3", "宫式布局4", "宫式布局5",
				"宫式布局6", "宫式布局7", "宫式布局8" };
		mPullToRefreshView = (PullToRefreshView) view
				.findViewById(R.id.main_pull_refresh_view);
		mGridView = (GridView) view.findViewById(R.id.gridview);
		for (int i = 0; i < 8; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", images[i]);
			map.put("itemText", texts[i]);
			lstImageItem.add(map);
		}

		saImageItems = new SimpleAdapter(view.getContext(), lstImageItem,// 数据源
				R.layout.test,// 显示布局
				new String[] { "itemImage", "itemText" }, new int[] {
						R.id.itemImage, R.id.itemText });
		try {
			// mGridView.setAdapter(new DataAdapter(this));
			mGridView.setAdapter(saImageItems);
			mPullToRefreshView.setOnHeaderRefreshListener(this);
			mPullToRefreshView.setOnFooterRefreshListener(this);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return view;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				System.out.println("上拉加载");
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("itemImage", R.drawable.test_1);
				map.put("itemText", "haha");
				lstImageItem.add(map);
				saImageItems.notifyDataSetChanged();
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
				mPullToRefreshView.onHeaderRefreshComplete("最近更新:01-23 12:01");
				System.out.println("下拉更新");
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("itemImage", R.drawable.test_1);
				map.put("itemText", "haha");
				lstImageItem.add(map);
				saImageItems.notifyDataSetChanged();
				mPullToRefreshView.onHeaderRefreshComplete();
			}
		}, 1000);

	}

}
