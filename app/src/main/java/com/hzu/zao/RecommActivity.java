package com.hzu.zao;

import android.graphics.Color;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.hzu.zao.adapter.RecommAdapter;
import com.hzu.zao.base.BaseAppCompatActivity;
import com.hzu.zao.config.Contants;
import com.hzu.zao.config.MyApplication;
import com.hzu.zao.model.InviteCode;
import com.hzu.zao.model.MyUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class RecommActivity extends BaseAppCompatActivity {
    private ListView listView;
    private MyUser currentUser;
    public int inviteNum;
    public boolean dofindObject;
    public boolean dofindWhile;
    public List<InviteCode> list;



    @Override
    protected int getLayoutId() {
        return R.layout.activity_recomm;
    }

    @Override
    protected void initData() {
        currentUser = MyApplication.getInstance().getCUser();
        initializeToolbar();
        setTitle(R.string.recomm);
    }

    @Override
    protected void findView() {

        listView = (ListView) findViewById(R.id.card_listView);

		/* 添加头和尾 */
        listView.addHeaderView(new View(this));
        listView.addFooterView(new View(this));
    }

    @Override
    protected void bindData() {
        list = (List<InviteCode>) MyApplication.getInstance().getCacheInstance().getAsListObject(Contants.RECOMMENDED_KEY);

//		Log.i("xy", "Recomm  list =" + list);
        // Log.i("xy", "Recomm  list " +list);
        if (list != null) {
//			Log.i("xy", "Recomm  list.size()= " + list.size());
            listView.setAdapter(new RecommAdapter(getApplicationContext(), list));
        }

        // setAdapter();
//		Log.i("xy", "Recomm   setAdapter()");
        querydata();
    }

    @Override
    protected void handerMessage(Message msg) {

    }

    /**
     * 初始化toolbar
     */
    private void initializeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_recomm);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.icon_menu_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mActivity.finish();
            }
        });

    }


    /**
     * 查找数据
     */
    public void querydata() {
        //Log.i("xy", "Recomm   querydata()");
        try {
            BmobQuery<InviteCode> query = new BmobQuery<InviteCode>();
//			Log.i("xy",
//					"Recomm currentUser.getObjectId()="
//							+ currentUser.getObjectId());
            query.addWhereEqualTo("creator", currentUser);
            query.order("-createdAt");
            query.addWhereNotEqualTo("invitees", "");
            query.findObjects(this, new FindListener<InviteCode>() {

                @Override
                public void onSuccess(List<InviteCode> value) {

//					Log.i("xy", "Recomm 查找Invite表成功   ");
                    // 缓存value

                    MyApplication.getInstance().getCacheInstance().put(Contants.RECOMMENDED_KEY, value);
                    //Log.i("xy", "Recomm  queryInvitees()");
                    queryInvitee();

                }

                @Override
                public void onError(int arg0, String arg1) {
                    Log.i("zao", "Recomm 查找Invite表失败");
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @author xiaoyang 查找推荐用户信息
     */
    public void queryInvitee() {
        try {
            list = (List<InviteCode>) MyApplication.getInstance().getCacheInstance().getAsListObject(Contants.RECOMMENDED_KEY);
            if (list != null) {
                //Log.i("xy", "Recomm queryInvitees  list != null");
                // 每次获取到invitees都缓存一下

                BmobQuery<MyUser> query = new BmobQuery<MyUser>();
                Collection<String> coll = new ArrayList<String>();

                for (int i = 0; i < list.size(); i++) {

                    if (list.get(i).getInvitees() != null) {// 如果invitees 不为空
//						Log.i("xy", "Recomm   queryInvitee  object:" +list.get(i).getInvitees().getObjectId());
                        coll.add(list.get(i).getInvitees().getObjectId() + "");
                    } else {
                        list.remove(i);
                    }

                }
                //
                query.addWhereContainedIn("objectId", coll);
                // {"$or":[{"objectId":"6a424cd86a"},{"objectId":"03ac08bfbe"},{"objectId":"ec9494a54d"}]}
                query.findObjects(this, new FindListener<MyUser>() {

                    @Override
                    public void onSuccess(List<MyUser> value) {
                        try {
//							Log.i("xy", "Recomm queryInvitee  查找成功  ");

                            for (int j = 0; j < value.size(); j++) {
                                // 从list<User> 中得到objectId
                                String inviteObject = value.get(j).getObjectId();


//								Log.i("xy", "Recomm queryInvitee  list.size()= "
//										+ list.size());

                                // 将查找得到的invitees队列插入对应的InviteCode队列
                                List<InviteCode> list2 = new ArrayList<InviteCode>();
                                for (int n = 0; n < list.size(); n++) {
                                    // 从缓存中的list<InviteCode> 中得到objectId

                                    String inviteObject1 = list.get(n)
                                            .getInvitees().getObjectId();
//									Log.i("xy", "Recomm queryInvitee  inviteObject1= "
//											+ inviteObject1);
                                    if (inviteObject.equals(inviteObject1)) {// 如果过匹配，则将invitees
                                        // set
                                        // 进inviteCode项中
                                        list.get(n).setInvitees(value.get(j));

                                    }
                                }

                            }
                            // 保存处理过的数据
                            MyApplication.getInstance().getCacheInstance().put(Contants.RECOMMENDED_KEY, list);
                            // 重新设置Adapter
                            //Log.i("xy", "Recomm queryInvitee  SavelistObject");
                            listView.setAdapter(new RecommAdapter(
                                    getApplicationContext(), list));
                            //Log.i("xy", "Recomm queryInvitee  setAdapter");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(int arg0, String arg1) {
                        Log.i("zao", "Recomm queryInvitees  查找User表失败");
                    }

                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onPause() {
        overridePendingTransition(R.anim.enter_from_left_animation,
                R.anim.exit_to_right_animaion);
        super.onPause();
    }

    public static interface AsyncTaskCallback {
        // 显示结果
        void onAsyncResult(int e, String s);

        // 显示进度条
        void onAsyncProcess(Integer pi);
    }

    /**
     * 异步获取推荐用户
     *
     * @author xiaoyang
     *
     */


}
