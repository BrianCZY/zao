package com.hzu.zao;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hzu.zao.base.BaseAppCompatActivity;
import com.hzu.zao.config.Contants;
import com.hzu.zao.config.MyApplication;
import com.hzu.zao.model.MyUser;
import com.hzu.zao.utils.GetAddressUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;


public class EditUserActivity extends BaseAppCompatActivity {
    private ImageView edituserIv;
    private EditText nickNameEd, qqEd, ageEd, signatureEd;
    private TextView cancelBt;
    private TextView saveBt;
    private Spinner provinceSpiner, citySpiner, sexeSpiner;
    private ArrayAdapter<String> sexAdapter, provinceAdapter, cityAdapter;
    private GetAddressUtil addressUtil;
    private List<String> listProvinces, listCities;
    private MyUser currentUser;
    private Uri photoUri;
    private AlertDialog dialog;

    private final int PIC_FROM_CAMERA = 1;
    private final int PIC_FROM＿LOCALPHOTO = 0;

    private boolean isSetHead = false;

    // 男 ，女
    String[] sex = new String[]{"男", "女"};


    @Override
    protected int getLayoutId() {
        return R.layout.activity_edituser;
    }

    @Override
    protected void initData() {
        initializeToolbar();
        setTitle(R.string.edit_user);
        // 从缓存中读取用户 get current user in cache
        currentUser = (MyUser) MyApplication.getInstance().getCacheInstance().getAsObject(Contants.CURRENTUSER_KEY);
    }

    @Override
    protected void findView() {
        edituserIv = (ImageView) findViewById(R.id.edit_user_iv);
        nickNameEd = (EditText) findViewById(R.id.edit_nick_name);
        qqEd = (EditText) findViewById(R.id.edit_qq);
        ageEd = (EditText) findViewById(R.id.edit_age);
        signatureEd = (EditText) findViewById(R.id.edit_signature);
        sexeSpiner = (Spinner) findViewById(R.id.spiner_sex);
        provinceSpiner = (Spinner) findViewById(R.id.spiner_province);
        citySpiner = (Spinner) findViewById(R.id.spiner_city);
        cancelBt = (TextView) findViewById(R.id.edit_cancel);
        saveBt = (TextView) findViewById(R.id.edit_save);


    }

    @Override
    protected void bindData() {

        ImageLoader.getInstance().displayImage(currentUser.getIcon().getFileUrl(this), edituserIv);

        if (currentUser.getNickName() != null)
            nickNameEd.setText(currentUser.getNickName()); // 避免currentUser.getNickName()内容为空所以加
        if (currentUser.getQq() != null)
            qqEd.setText(currentUser.getQq());
//        Log.i("xy", "qqEd.setText");
        ageEd.setText(String.valueOf(currentUser.getAge()) );
//        Log.i("xy", "ageEd.setText");
        if (currentUser.getIntroduction() != null)
            signatureEd.setText(currentUser.getIntroduction());

        edituserIv.setOnClickListener(new EdOnclickListener());
        cancelBt.setOnClickListener(new EdOnclickListener());
        saveBt.setOnClickListener(new EdOnclickListener());

        initSpinter();
    }

    @Override
    protected void handerMessage(Message msg) {

    }

    /**
     * 初始化toolbar
     */
    private void initializeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_edit_user);
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
     * 测试获取地点 test to obtain location
     */
    public void testCity() {
        GetAddressUtil addressUtil = new GetAddressUtil(this);
        List<String> listProvinces = addressUtil.getProvinceList();
        for (int i = 0; i < 6; i++) {
//			Log.i("zao", "listProvinces.get(" + i + ")" + listProvinces.get(i)); // 得到省
            List<String> listCity = addressUtil.getCityList(listProvinces
                    .get(i)); // 得到是列表
//			for (int j = 0; j < listCity.size(); j++) {
//				Log.i("zao", "listCity.get(" + j + ")" + listCity.get(j));
//			}
        }

    }



    public class EdOnclickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.edit_user_iv:
//				Log.i("zao", "EditUserActivity   R.id.edit_user_iv");
                    // 修改用户头像。弹窗显示调用相机还是使用本地图片
                    // Modify user Avater。show popups：Call the camera or the use of
                    // local image
                    dialog = new AlertDialog.Builder(EditUserActivity.this).create();
                    dialog.show();
                    Window win = dialog.getWindow();
                    win.setContentView(R.layout.edit_user_icon_dialog);
                    TextView cameraTv = (TextView) win
                            .findViewById(R.id.dialog_camera_tv);
                    TextView localPictureTv = (TextView) win
                            .findViewById(R.id.dialog_local_tv);
                    cameraTv.setOnClickListener(new DiaOnclickListener());
                    localPictureTv.setOnClickListener(new DiaOnclickListener());

                    break;
                case R.id.edit_cancel:
//				Log.i("zao", "EditUserActivity    触发了取消button");
                    EditUserActivity.this.finish();
                    break;
                case R.id.edit_save:
//				Log.i("zao", "EditUserActivity    触发了保存button");

                    if (isSetHead) { // 如果设置了头像
//					Log.i("zao", "EditUserActivity    upLoadBmobFile()");
                        upLoadBmobFile();
                    } else {// 如果没有设置头像，则直接保存信息到bmob，不上传图片
//					Log.i("zao", "EditUserActivity    saveBtEvent()");
                        saveBtEvent();
                    }

                    break;

                default:
                    break;
            }

        }

    }

    public class DiaOnclickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.dialog_camera_tv:
                    doHandlerPhoto(PIC_FROM_CAMERA);
                    break;
                case R.id.dialog_local_tv:
                    doHandlerPhoto(PIC_FROM＿LOCALPHOTO);
                    break;

                default:
                    break;
            }

        }

    }

    /**
     * 上传图片到服务器
     */
    public void upLoadBmobFile() {

        File pictureFileDir;
        if (Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED)) {
            pictureFileDir = new File(
                    Environment.getExternalStorageDirectory(), "/zao");

        } else {
            pictureFileDir = new File(getFilesDir().toString(), "/zao");
        }
        final File picFile = new File(pictureFileDir, "userIcon.png");
        final BmobFile bmobFile = new BmobFile(picFile);
        Toast.makeText(getApplicationContext(),
                getResources().getString(R.string.please_wait_upload), Toast.LENGTH_LONG)
                .show();// 请等待上传

        bmobFile.uploadblock(this, new UploadFileListener() {

            @Override
            public void onSuccess() {
                // bmobFile.getUrl()---返回的上传文件的地址（不带域名）
                // bmobFile.getFileUrl(context)--返回的上传文件的完整地址（带域名）
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.success_upload), Toast.LENGTH_LONG)
                        .show();// 上传文件成功
//				Log.i("zao",
//						"EditUserActivity   bmobFile.getFileUrl()="
//								+ bmobFile.getUrl());
                // currentUser.setIcon(new BmobFile(new
                // File(bmobFile.getFileUrl())));

                currentUser.setIcon(bmobFile);// 设置头像

//                try { //将用户头像保存在缓存中
//                    FileInputStream is;
//                    is = new FileInputStream(picFile);
//                    Bitmap userBitmap = BitmapFactory.decodeStream(is);
//                    cacheUtility.SaveBitmap(Contants.CURRENTUSERICON_KEY,
//                            userBitmap);
//
//                } catch (FileNotFoundException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }

                saveBtEvent();
                // bmobFile.getFileUrl()
                // http://file.bmob.cn/M00/34/DD/oYYBAFU0bW2AQjPGAA-x20z2DVE50.jpeg;
            }

            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）

            }

            @Override
            public void onFailure(int code, String msg) {
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.failed_upload), Toast.LENGTH_LONG)
                        .show();// 上传文件失败
            }
        });

    }

    /**
     * 保存用户信息到User表，并更新本地用户
     */
    private void saveBtEvent() {
//		Log.i("zao", "EditUserActivity    savaBtEvent");

        // BmobFile bmobFile = new BmobFile(new
        // File("/mnt/sdcard/zao/userIcon.png"));

        String nickNameStr =  nickNameEd.getText().toString();
        String qqStr =  qqEd.getText().toString();

        String signatureStr =  signatureEd.getText().toString();
        String sexeSpinerStr = sexeSpiner.getSelectedItem().toString();
        String provinceStr = provinceSpiner.getSelectedItem().toString();
        String citySpinerStr = citySpiner.getSelectedItem().toString();
        String address = provinceStr + "-" + citySpinerStr;

        if (sexeSpinerStr.equals("男")) { // 如果是 “男”
            currentUser.setSex("M");// 设置性别
        } else if (sexeSpinerStr.equals("女")) {// 如果是 “女”
            currentUser.setSex("F");// 设置性别
        }

        if (!ageEd.getText().toString().equals("")) {
            int ageInt = Integer.parseInt(ageEd.getText().toString());
            currentUser.setAge(ageInt);// 设置年龄
        }

//		Log.i("zao", "EditUserActivity   Toast.LENGTH_LONG");
        currentUser.setNickName(nickNameStr);// 设置昵称
        currentUser.setQq(qqStr);// 设置QQ
        currentUser.setIntroduction(signatureStr);// 设置简介
        currentUser.setAddress(address);// 设置地址

        try {

            currentUser.update(getApplicationContext(),
                    currentUser.getObjectId(), new UpdateListener() {

                        @Override
                        public void onSuccess() {
//							Log.i("zao", "EditUserActivity    更新_User表成功");
                            MyApplication.getInstance().getCacheInstance().put(Contants.CURRENTUSER_KEY,
                                    currentUser); // 缓存本地用户

                            Toast.makeText(
                                    getApplicationContext(),
                                    getResources().getString(
                                            R.string.success_update), Toast.LENGTH_LONG).show();
                            new Thread() {
                                public void run() {
                                    try {
                                        sleep(1000);
                                        startActivity(new Intent(EditUserActivity.this,
                                                MainActivity.class));
                                        overridePendingTransition(
                                                R.anim.enter_from_right_animation,
                                                R.anim.exit_to_left_animation);

                                        EditUserActivity.this.finish();// 销毁当前Activity
                                    } catch (InterruptedException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }
                            }.start();

                        }

                        @Override
                        public void onFailure(int arg0, String arg1) {
//							Log.i("zao", "EditUserActivity    更新_User表失败");

                        }
                    });
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    private void initSpinter() {
//		Log.i("zao", "EditUserActivity    initSpinter");
//		Log.i("zao", "EditUserActivity    sex[0]=" + sex[0]);
        sexAdapter = new ArrayAdapter<String>(EditUserActivity.this,
                R.layout.my_spinner, sex);
        sexeSpiner.setAdapter(sexAdapter);

        if (currentUser.getSex() != null) { // 如果用户曾设置过sex

            for (int i = 0; i < sex.length; i++) {
                if (currentUser.getSex().equals("M")) {
                    sexeSpiner.setSelection(0, true); // 显示对应项
                } else {
                    sexeSpiner.setSelection(1, true); // 显示对应项
                }
            }

        } else {// 如果用户未曾设置地址
            sexeSpiner.setSelection(0, true);
        }
//		Log.i("zao", "EditUserActivity    initSpinter 1");
        // provinceAdapter = new ArrayAdapter<String>(getApplicationContext(),
        // R.layout.my_spinner, province);
        // test

        // 获得省列表
        addressUtil = new GetAddressUtil(this);
        listProvinces = addressUtil.getProvinceList();

        provinceAdapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.my_spinner, listProvinces);
        provinceSpiner.setAdapter(provinceAdapter); // 设置省
        // provinceSpiner.setSelection(0, true);

        cityAdapter = new ArrayAdapter<String>(EditUserActivity.this,
                R.layout.my_spinner, addressUtil.getCityList(listProvinces
                .get(0)));
        citySpiner.setAdapter(cityAdapter);// 设置市

//		Log.i("zao", "EditUserActivity    initSpinter 2");

        if (currentUser.getAddress() != null) { // 如果用户曾设置过地址
            String[] provinces = currentUser.getAddress().split("-");
            for (int i = 0; i < listProvinces.size(); i++) {
                if (provinces[0].equals(listProvinces.get(i))) {
                    provinceSpiner.setSelection(i, true);
                    // 根据用户的省份重新获取对应的市列表
                    listCities = addressUtil.getCityList(listProvinces.get(i));
                    cityAdapter = new ArrayAdapter<String>(EditUserActivity.this,
                            R.layout.my_spinner, listCities);
                    citySpiner.setAdapter(cityAdapter);// 重新设置市
                }
            }

            for (int i = 0; i < listCities.size(); i++) {
                if (provinces[1].equals(listCities.get(i))) {
                    citySpiner.setSelection(i, true);
                }
            }

        } else {// 如果用户未曾设置地址
            provinceSpiner.setSelection(0, true);
            citySpiner.setSelection(0, true);
        }
//		Log.i("zao", "EditUserActivity    initSpinter 3");

        provinceSpiner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // 获取该省对应的市区列表
                List<String> listCity = addressUtil.getCityList(listProvinces
                        .get(position));
                cityAdapter = new ArrayAdapter<String>(EditUserActivity.this,
                        android.R.layout.simple_spinner_item, listCity);
                citySpiner.setAdapter(cityAdapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

    }

    /**
     * 根据不同方式选择图片设置ImageView
     *
     * @param type 0-本地相册选择，非0为拍照
     */
    private void doHandlerPhoto(int type) {
        try {
            // 保存裁剪后的图片文件
            File pictureFileDir;
            if (Environment.getExternalStorageState()
                    .equals(android.os.Environment.MEDIA_MOUNTED)) {
                pictureFileDir = new File(
                        Environment.getExternalStorageDirectory(), "/zao");

            } else {
                pictureFileDir = new File(getFilesDir().toString(), "/zao");
            }
            File picFile = new File(pictureFileDir, "userIcon.png");
            if (!picFile.exists()) {
                picFile.createNewFile();
            }
            photoUri = Uri.fromFile(picFile);

            if (type == PIC_FROM＿LOCALPHOTO) {
                Intent intent = getCropImageIntent();
                startActivityForResult(intent, PIC_FROM＿LOCALPHOTO);
            } else {
                Intent cameraIntent = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(cameraIntent, PIC_FROM_CAMERA);
            }

        } catch (Exception e) {
//			Log.i("HandlerPicError", "处理图片出现错误");
        }
    }

    /**
     * 调用图片剪辑程序
     */
    public Intent getCropImageIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
        setIntentParams(intent);
        return intent;
    }

    /**
     * 启动裁剪
     */
    private void cropImageUriByTakePhoto() {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");
        setIntentParams(intent);
        startActivityForResult(intent, PIC_FROM＿LOCALPHOTO);
    }

    /**
     * 设置公用参数
     */
    private void setIntentParams(Intent intent) {
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 600);
        intent.putExtra("outputY", 600);
        intent.putExtra("noFaceDetection", true); // no face detection
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            isSetHead = true;
        }
        switch (requestCode) {
            case PIC_FROM_CAMERA: // 拍照
                try {
                    cropImageUriByTakePhoto();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case PIC_FROM＿LOCALPHOTO:
                try {
                    if (photoUri != null) {
                        Bitmap bitmap = decodeUriAsBitmap(photoUri);
                        edituserIv.setImageBitmap(bitmap);
                        dialog.cancel();// 退出dialog
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private Bitmap decodeUriAsBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver()
                    .openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    @Override
    protected void onPause() {
        overridePendingTransition(R.anim.enter_from_left_animation,
                R.anim.exit_to_right_animaion);
        super.onPause();
    }

}
