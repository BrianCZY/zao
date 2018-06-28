package com.hzu.zao.utils;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.hzu.zao.R;
import com.hzu.zao.adapter.EmotionGvAdapter;
import com.hzu.zao.adapter.EmotionPagerAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmotionUtils implements Serializable {

	private ViewPager vp;
	private Activity mActivity;
	private EditText content_et;

	public EmotionUtils(Activity mActivity,ViewPager vp, EditText content_et) {
		this.vp = vp;
		this.mActivity = mActivity;
		this.content_et =content_et;
		initEmotion();
	}

	public static int getImgByName(String imgName) {
		Integer integer = emojiMap.get(imgName);
		return integer == null ? -1 : integer;
	}

	/**
	 * 初始化表情面板内容
	 */
	private void initEmotion() {
		// 获取屏幕宽度
		int gvWidth = DisplayUtils.getScreenWidthPixels(mActivity);
		// 表情边距
		int spacing = DisplayUtils.dip2px(mActivity, 8);
		// GridView中item的宽度
		int itemWidth = (gvWidth - spacing * 8) / 7;
		int gvHeight = itemWidth * 3 + spacing * 4;

		List<GridView> gvs = new ArrayList<GridView>();
		List<String> emotionNames = new ArrayList<String>();
		// 遍历所有的表情名字
		for (String emojiName : EmotionUtils.emojiMap.keySet()) {
			emotionNames.add(emojiName);
			// 每20个表情作为一组,同时添加到ViewPager对应的view集合中
			if (emotionNames.size() == 20) {
				GridView gv = createEmotionGridView(emotionNames, gvWidth, spacing, itemWidth, gvHeight);
				gvs.add(gv);
				// 添加完一组表情,重新创建一个表情名字集合
				emotionNames = new ArrayList<String>();
			}
		}

		// 检查最后是否有不足20个表情的剩余情况
		if (emotionNames.size() > 0) {
			GridView gv = createEmotionGridView(emotionNames, gvWidth, spacing, itemWidth, gvHeight);
			gvs.add(gv);
		}

//        ViewGroup.LayoutParams layoutParams = vp_emotion_dashboard.getLayoutParams();
		// 将多个GridView添加显示到ViewPager中
		EmotionPagerAdapter emotionPagerGvAdapter = new EmotionPagerAdapter(gvs);
		vp.setAdapter(emotionPagerGvAdapter);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(gvWidth, gvHeight);
		vp.setLayoutParams(params);
	}

	/**
	 * 创建显示表情的GridView
	 */
	private GridView createEmotionGridView(List<String> emotionNames, int gvWidth, int padding, int itemWidth, int gvHeight) {
		// 创建GridView
		GridView gv = new GridView(mActivity);
		gv.setBackgroundColor(Color.rgb(233, 233, 233));
		gv.setSelector(android.R.color.transparent);
		gv.setNumColumns(7);
		gv.setPadding(padding, padding, padding, padding);
		gv.setHorizontalSpacing(padding);
		gv.setVerticalSpacing(padding);
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(gvWidth, gvHeight);
		gv.setLayoutParams(params);
		// 给GridView设置表情图片
		EmotionGvAdapter adapter = new EmotionGvAdapter(mActivity, emotionNames, itemWidth);
		gv.setAdapter(adapter);

		gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Object itemAdapter = parent.getAdapter();

				if (itemAdapter instanceof EmotionGvAdapter) {
					// 点击的是表情
					EmotionGvAdapter emotionGvAdapter = (EmotionGvAdapter) itemAdapter;

					if (position == emotionGvAdapter.getCount() - 1) {
						// 如果点击了最后一个回退按钮,则调用删除键事件
						content_et.dispatchKeyEvent(new KeyEvent(
								KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
					} else {
						// 如果点击了表情,则添加到输入框中
						String emotionName = emotionGvAdapter.getItem(position);

						// 获取当前光标位置,在指定位置上添加表情图片文本
						int curPosition = content_et.getSelectionStart();
						StringBuilder sb = new StringBuilder(content_et.getText().toString());
						sb.insert(curPosition, emotionName);

						// 特殊文字处理,将表情等转换一下
						content_et.setText(StringUtils.getEmotionContent(
								mActivity,  sb.toString()));

						// 将光标设置到新增完表情的右侧
						content_et.setSelection(curPosition + emotionName.length());
					}

				}
			}
		});
		return gv;
	}



	/**
	 * key-表情文字;value-表情图片资源
	 */
	public static Map<String, Integer> emojiMap;

	static {
		emojiMap = new HashMap<String, Integer>();
		emojiMap.put("[呵呵]", R.drawable.d_hehe);
		emojiMap.put("[嘻嘻]", R.drawable.d_xixi);
		emojiMap.put("[哈哈]", R.drawable.d_haha);
		emojiMap.put("[爱你]", R.drawable.d_aini);
		emojiMap.put("[挖鼻屎]", R.drawable.d_wabishi);
		emojiMap.put("[吃惊]", R.drawable.d_chijing);
		emojiMap.put("[晕]", R.drawable.d_yun);
		emojiMap.put("[泪]", R.drawable.d_lei);
		emojiMap.put("[馋嘴]", R.drawable.d_chanzui);
		emojiMap.put("[抓狂]", R.drawable.d_zhuakuang);
		emojiMap.put("[哼]", R.drawable.d_heng);
		emojiMap.put("[可爱]", R.drawable.d_keai);
		emojiMap.put("[怒]", R.drawable.d_nu);
		emojiMap.put("[汗]", R.drawable.d_han);
		emojiMap.put("[害羞]", R.drawable.d_haixiu);
		emojiMap.put("[睡觉]", R.drawable.d_shuijiao);
		emojiMap.put("[钱]", R.drawable.d_qian);
		emojiMap.put("[偷笑]", R.drawable.d_touxiao);
		emojiMap.put("[笑cry]", R.drawable.d_xiaoku);
		emojiMap.put("[doge]", R.drawable.d_doge);
		emojiMap.put("[喵喵]", R.drawable.d_miao);
		emojiMap.put("[酷]", R.drawable.d_ku);
		emojiMap.put("[衰]", R.drawable.d_shuai);
		emojiMap.put("[闭嘴]", R.drawable.d_bizui);
		emojiMap.put("[鄙视]", R.drawable.d_bishi);
		emojiMap.put("[花心]", R.drawable.d_huaxin);
		emojiMap.put("[鼓掌]", R.drawable.d_guzhang);
		emojiMap.put("[悲伤]", R.drawable.d_beishang);
		emojiMap.put("[思考]", R.drawable.d_sikao);
		emojiMap.put("[生病]", R.drawable.d_shengbing);
		emojiMap.put("[亲亲]", R.drawable.d_qinqin);
		emojiMap.put("[怒骂]", R.drawable.d_numa);
		emojiMap.put("[太开心]", R.drawable.d_taikaixin);
		emojiMap.put("[懒得理你]", R.drawable.d_landelini);
		emojiMap.put("[右哼哼]", R.drawable.d_youhengheng);
		emojiMap.put("[左哼哼]", R.drawable.d_zuohengheng);
		emojiMap.put("[嘘]", R.drawable.d_xu);
		emojiMap.put("[委屈]", R.drawable.d_weiqu);
		emojiMap.put("[吐]", R.drawable.d_tu);
		emojiMap.put("[可怜]", R.drawable.d_kelian);
		emojiMap.put("[打哈气]", R.drawable.d_dahaqi);
		emojiMap.put("[挤眼]", R.drawable.d_jiyan);
		emojiMap.put("[失望]", R.drawable.d_shiwang);
		emojiMap.put("[顶]", R.drawable.d_ding);
		emojiMap.put("[疑问]", R.drawable.d_yiwen);
		emojiMap.put("[困]", R.drawable.d_kun);
		emojiMap.put("[感冒]", R.drawable.d_ganmao);
		emojiMap.put("[拜拜]", R.drawable.d_baibai);
		emojiMap.put("[黑线]", R.drawable.d_heixian);
		emojiMap.put("[阴险]", R.drawable.d_yinxian);
		emojiMap.put("[打脸]", R.drawable.d_dalian);
		emojiMap.put("[傻眼]", R.drawable.d_shayan);
		emojiMap.put("[猪头]", R.drawable.d_zhutou);
		emojiMap.put("[熊猫]", R.drawable.d_xiongmao);
		emojiMap.put("[兔子]", R.drawable.d_tuzi);
	}


}
