package com.hzu.zao.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzu.zao.R;
import com.hzu.zao.adapter.QuesPagerAdapter;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.ArrayList;
import java.util.List;

public class QuesFragment extends Fragment {

	private View QuesView;
	private Context context;
	private ViewPager quesPager;
	private TextView myQuestion, myAnswer, myExperience;

	ImageView imageView;// 页面指示器
	int offset = 0;// 动画图片偏移量
	int tabWidth = 0;// 每个Tab的宽度
	int screenW = 0;// 屏幕宽度
	int bmpW;// 动画图片宽度
	int currIndex = 0;// 当前页卡编号
	OvershootInterpolator overshootInterpolator;
	int DURATION = 500;

	public QuesFragment() {
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		this.context = context;
	}

	@Override
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		QuesView = inflater.inflate(R.layout.activity_question, container,false);
//		ViewGroup vg = (ViewGroup) QuesView.getParent();
//
//		if (vg != null) {
//			vg.removeAllViewsInLayout();
//		}

		return QuesView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		intiQuesView();
		quesPager.setAdapter(new QuesPagerAdapter(context, inflateThreeView()));
		quesPager.setOnPageChangeListener(new nOnPageChangeListener());
		initCursorAnimation();// 初始化ViewPager指示标




	}

	private void intiQuesView() {

		imageView = (ImageView) QuesView.findViewById(R.id.cursor);
		quesPager = (ViewPager) QuesView.findViewById(R.id.quespager);
		myQuestion = (TextView) QuesView.findViewById(R.id.myQuestion);
		myAnswer = (TextView) QuesView.findViewById(R.id.myAnswer);
		myExperience = (TextView) QuesView.findViewById(R.id.myExperience);
		myQuestion.setOnClickListener(new MyOnClickListener(0));
		myAnswer.setOnClickListener(new MyOnClickListener(1));
		myExperience.setOnClickListener(new MyOnClickListener(2));
	}

	// 初始化ViewPager指示标
	private void initCursorAnimation() {

		bmpW = BitmapFactory.decodeResource(
				((Activity) context).getResources(), R.drawable.cursor)
				.getWidth();// 获取图片宽度
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		screenW = dm.widthPixels;// 获取分辨率宽度
		tabWidth = screenW / 3;
		Matrix matrix = new Matrix();
		offset = (tabWidth - bmpW) / 3;
		matrix.postTranslate(offset, 0);
		imageView.setImageMatrix(matrix);// 设置动画初始位置
		overshootInterpolator = new OvershootInterpolator();
	}

	// 填充ViewPager三个界面，封装到List
	private List<View> inflateThreeView() {
		List<View> nviews = new ArrayList<View>();
		nviews.add(LayoutInflater.from(context).inflate(
				R.layout.activity_myqusetion, null));
		nviews.add(LayoutInflater.from(context).inflate(
				R.layout.activity_myanswer, null));
		nviews.add(LayoutInflater.from(context).inflate(
				R.layout.activity_myexperience, null));
		return nviews;
	}


	public class MyOnClickListener implements OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		public void onClick(View v) {
			quesPager.setCurrentItem(index);
		}

	}

	/**
	 * 页卡切换监听
	 */
	public class nOnPageChangeListener implements OnPageChangeListener {

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		public void onPageSelected(int arg0) {
			changeTab(arg0);// 页面变化时指示器的动画效果
		}
	}

	private void changeTab(int index) {
		int position = tabWidth * (index + 1) - tabWidth / 3 - bmpW / 2
				- offset;
		ViewPropertyAnimator.animate(imageView).translationX(position)
				.setInterpolator(overshootInterpolator).setDuration(DURATION);
		currIndex = index;
	}
}
