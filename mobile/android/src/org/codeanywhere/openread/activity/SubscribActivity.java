package org.codeanywhere.openread.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.codeanywhere.openread.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SubscribActivity extends Activity {
	ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subscrib);
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("itemImage", R.drawable.java);
		map.put("itemText", "java核心技术");
		lstImageItem.add(map);
		SimpleAdapter saImageItems = new SimpleAdapter(this, lstImageItem,// 数据源
				R.layout.sub_item,// 显示布局
				new String[] { "itemImage", "itemText" }, new int[] {
						R.id.itemImage, R.id.itemText });
		ListView lv = (ListView) findViewById(R.id.hotList);
		lv.setAdapter(saImageItems);
	}

}
