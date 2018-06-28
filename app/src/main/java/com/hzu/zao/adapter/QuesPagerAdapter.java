package com.hzu.zao.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.hzu.zao.mainPager.MyAnswer;
import com.hzu.zao.mainPager.MyExperience;
import com.hzu.zao.mainPager.MyQuestion;
import com.hzu.zao.model.MyUser;
import com.hzu.zao.utils.RealTimeData;

import java.util.List;

public class QuesPagerAdapter extends PagerAdapter {

    private Context context;
    private List<View> views;
    private MyQuestion myQuestion;
    private MyAnswer myAnswer;
    private MyExperience myEx;
    private MyUser userInfo = null;
    private RealTimeData real;
    
    public QuesPagerAdapter(Context context, List<View> views) {
        this.context = context;
        this.views = views;

    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = views.get(position);
        container.addView(view);
        switch (position) {
            case 0:
                getMyQuesView(view);
                break;
            case 1:
                getMyAnsView(view);
                break;
            case 2:
                getMyExpView(view);
                break;
        }
        return view;
    }

  //
    private void getMyExpView(View v) {
    	myEx=new MyExperience(context, v);
    	myEx.initData();
    	myEx.initWiget();
    }
//
    
    private void getMyAnsView(View v) {
    	myAnswer=new MyAnswer(context, v);
    	myAnswer.initData();
    	myAnswer.initView();

    }
//
    private void getMyQuesView(View v) {
    	 myQuestion=new MyQuestion(context, v);
    	 myQuestion.initData();
    	 myQuestion.initView();
    	 
    	
    }
    


}
