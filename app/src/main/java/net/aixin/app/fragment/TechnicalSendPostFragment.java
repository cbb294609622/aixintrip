package net.aixin.app.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.umeng.socialize.utils.BitmapUtils;

import net.aixin.app.AppContext;
import net.aixin.app.R;
import net.aixin.app.api.remote.OSChinaApi;
import net.aixin.app.base.BaseFragment;
import net.aixin.app.bean.TachnicalSendPostBean;
import net.aixin.app.bean.TechnicalBean;
import net.aixin.app.service.ServerTaskUtils;
import net.aixin.app.util.DialogHelp;
import net.aixin.app.util.FileUtil;
import net.aixin.app.util.ImageUtils;
import net.aixin.app.util.JsonUtils;
import net.aixin.app.util.StringUtils;
import net.aixin.app.util.TDevice;
import net.aixin.app.util.UIHelper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.msebera.android.httpclient.Header;

/**
 * Created by gzp on 2015/12/8.
 * 发帖
 */
public class TechnicalSendPostFragment extends BaseFragment {

    //下拉选择控件
    @InjectView(R.id.submit_spinner)
    Spinner submit_spinner;

    @InjectView(R.id.et_content)
    EditText et_content;

    @InjectView(R.id.et_title)
    EditText et_title;

    @InjectView(R.id.images_gridview)
    GridView images_gridview;

    public static final int ACTION_TYPE_ALBUM = 0;
    public static final int ACTION_TYPE_PHOTO = 1;
    private String theLarge, theThumbnail;
    //发帖中的上传的图片
    private File up_imgFile;
    //发帖的类型
    private String up_type;

    //发帖最多图片数
    private int bpNum = 4;

    //页面图片集合
    private Bitmap[] showbps = new Bitmap[bpNum];
    //图片的最后一个角标
    private int bpindex = 0;
    //适配器
    BaseAdapter adapter;
    //添加图片按钮是否显示
    private boolean isShowAdd = true;

    private AsyncHttpResponseHandler mHandler = new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            TachnicalSendPostBean jsb = JsonUtils.toBean(TachnicalSendPostBean.class, responseBody);
            if (jsb == null) {
                AppContext.showToast("获取类型失败");
            } else {
                initSpinner(jsb);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            AppContext.showToast("获取发帖类型失败");
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OSChinaApi.get_technical_send("", "", "", "", mHandler);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.technical_send_post_frag, null);
        ButterKnife.inject(this, view);

//        technical_image_click.setOnClickListener(this);
//        iv_clear_img.setOnClickListener(this);

        showbps[bpindex] = ImageUtils.drawableToBitmap(getResources().getDrawable(R.drawable.add_image));

        initData();
        initGridView();
        return view;
    }

    public void initData() {
    }

    public void initGridView(){

        if (images_gridview == null){
            Log.i(getClass().getName(),"界面出错了");
            return;
        }
        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return bpindex+1;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ImageView iv = new ImageView(getActivity());
                iv.setImageBitmap(showbps[position]);
                android.widget.AbsListView.LayoutParams params = new android.widget.AbsListView.LayoutParams(200, 200);
                iv.setLayoutParams(params);

                return iv;
            }
        };
        images_gridview.setAdapter(adapter);

        images_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (position == bpindex && bpindex < bpNum && isShowAdd) {
                    handleSelectPicture();
                } else {
                    DialogHelp.getConfirmDialog(getActivity(), "删除当前照片", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //分两种情况，第一种当前还有添加照片按钮，第二种没有
                            if (isShowAdd) {
                                //可以添加，只把后面的向前移动
                                canAddMove(position);
                            } else {
                                //不可添加，全部前移，最后变成添加
                                noCanAddMove(position);
                            }
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
                }
            }
        });
    }

    public void canAddMove(int position){
        for(int i = position;i < bpindex ; i++){
            showbps[i] = showbps[i+1];
        }
        for (int i = bpindex ; i< bpNum;i++){
            showbps[i] = null;
        }
        bpindex --;
        adapter.notifyDataSetChanged();
    }

    public void noCanAddMove(int position){
        for(int i = position;i < bpNum-1 ; i++){
            showbps[i] = showbps[i+1];
        }
        showbps[bpNum - 1] = ImageUtils.drawableToBitmap(getResources().getDrawable(R.drawable.add_image));
        isShowAdd = true;
        adapter.notifyDataSetChanged();
    }

    /**
     * 初始化发帖类型选择
     */
    public void initSpinner(final TachnicalSendPostBean jsb) {
        ArrayList<String> data_list = new ArrayList<String>();
        data_list.clear();
//        data_list.add("选择发帖类型");
        for (TachnicalSendPostBean.TachnicalSendPostData data : jsb.rs) {
            data_list.add(data.n);
        }
        ArrayAdapter<String> arr_adapter = null;
        if (data_list != null) {
            arr_adapter = new ArrayAdapter<String>(getActivity(), R.layout.submit_post_simple_spinner_item, data_list);
            arr_adapter.setDropDownViewResource(R.layout.submit_post_simple_spinner_item);
            submit_spinner.setAdapter(arr_adapter);
            submit_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    submit_spinner.setSelection(position, true);
                    up_type = jsb.rs.get(position).n;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    private void handleSelectPicture() {
        DialogHelp.getSelectDialog(getActivity(), getResources().getStringArray(R.array.choose_picture), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                goToSelectPicture(i);
            }
        }).show();
    }

    private void goToSelectPicture(int position) {
        switch (position) {
            case ACTION_TYPE_ALBUM:
                Intent intent;
                if (Build.VERSION.SDK_INT < 19) {
                    intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "选择图片"),
                            ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD);
                } else {
                    intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "选择图片"),
                            ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD);
                }
                break;
            case ACTION_TYPE_PHOTO:
                // 判断是否挂载了SD卡
                String savePath = "";
                String storageState = Environment.getExternalStorageState();
                if (storageState.equals(Environment.MEDIA_MOUNTED)) {
                    savePath = Environment.getExternalStorageDirectory()
                            .getAbsolutePath() + "/aixin/Camera/";
                    File savedir = new File(savePath);
                    if (!savedir.exists()) {
                        savedir.mkdirs();
                    }
                }
                // 没有挂载SD卡，无法保存文件
                if (StringUtils.isEmpty(savePath)) {
                    AppContext.showToastShort("无法保存照片，请检查SD卡是否挂载");
                    return;
                }
                String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
                        .format(new Date());
                String fileName = "osc_" + timeStamp + ".jpg";// 照片命名
                File out = new File(savePath, fileName);
                Uri uri = Uri.fromFile(out);

                theLarge = savePath + fileName;// 该照片的绝对路径

                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent,
                        ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode,
                                 final Intent imageReturnIntent) {
        new Thread() {
            private String selectedImagePath;

            @Override
            public void run() {
                Bitmap bitmap = null;

                if (requestCode == ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD) {
                    //相册
                    if (imageReturnIntent == null)
                        return;
                    Uri selectedImageUri = imageReturnIntent.getData();
                    if (selectedImageUri != null) {
                        selectedImagePath = ImageUtils.getImagePath(
                                selectedImageUri, getActivity());
                    }

                    if (selectedImagePath != null) {
                        theLarge = selectedImagePath;
                    } else {
                        bitmap = ImageUtils.loadPicasaImageFromGalley(
                                selectedImageUri, getActivity());
                    }

                    if (AppContext
                            .isMethodsCompat(Build.VERSION_CODES.ECLAIR_MR1)) {
                        String imaName = FileUtil.getFileName(theLarge);
                        if (imaName != null)
                            bitmap = ImageUtils.loadImgThumbnail(getActivity(),
                                    imaName,
                                    MediaStore.Images.Thumbnails.MICRO_KIND);
                    }
                    if (bitmap == null && !StringUtils.isEmpty(theLarge))
                        bitmap = ImageUtils
                                .loadImgThumbnail(theLarge, 100, 100);
                } else if (requestCode == ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA) {
                    // 拍摄图片
                    if (bitmap == null && !StringUtils.isEmpty(theLarge)) {
                        bitmap = ImageUtils
                                .loadImgThumbnail(theLarge, 100, 100);
                    }
                }

                if(bitmap != null){// 存放照片的文件夹
                    String savePath = Environment.getExternalStorageDirectory()
                            .getAbsolutePath() + "/aixin/Camera/";
                    File savedir = new File(savePath);
                    if (!savedir.exists()) {
                        savedir.mkdirs();
                    }

                    String largeFileName = FileUtil.getFileName(theLarge);
                    String largeFilePath = savePath + largeFileName;
                    // 判断是否已存在缩略图
                    if (largeFileName.startsWith("thumb_")
                            && new File(largeFilePath).exists()) {
                        theThumbnail = largeFilePath;
                        up_imgFile = new File(theThumbnail);
                    } else {
                        // 生成上传的800宽度图片
                        String thumbFileName = "thumb_" + largeFileName;
                        theThumbnail = savePath + thumbFileName;
                        if (new File(theThumbnail).exists()) {
                            up_imgFile = new File(theThumbnail);
                        } else {
                            try {
                                // 压缩上传的图片
                                ImageUtils.createImageThumbnail(getActivity(),
                                        theLarge, theThumbnail, 800, 80);
                                up_imgFile = new File(theThumbnail);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    // 保存动弹临时图片
                    // ((AppContext) getApplication()).setProperty(
                    // tempTweetImageKey, theThumbnail);
                }

                Message msg = new Message();
                msg.what = 1;
                msg.obj = bitmap;
                handler.sendMessage(msg);
            }
        }.start();
    }

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1 && msg.obj != null) {
                // 显示图片
//                add_image.setImageBitmap((Bitmap) msg.obj);
//                rl_img.setVisibility(View.VISIBLE);

                Bitmap bp = (Bitmap) msg.obj;
                Bitmap linshi = showbps[bpindex];
                showbps[bpindex] = bp;
                if(bpindex+1 < bpNum) {
                    bpindex++;
                    showbps[bpindex] = linshi;
                }else{
                    //添加照片按钮消失
                    isShowAdd = false;
                }
                adapter.notifyDataSetChanged();
            }
        }
    };

    /**
     * 提交发帖内容
     */
    public void sendPost(){
        //进行页面校验
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_network_error);
            return;
        }
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(getActivity());
            return;
        }
        String mTitle = et_title.getText().toString();
        if (TextUtils.isEmpty(mTitle)){
            et_title.requestFocus();
            AppContext.showToast("标题不能为空");
            return;
        }
        String mContent = et_content.getText().toString();
        if (TextUtils.isEmpty(mContent)){
            et_content.requestFocus();
            AppContext.showToast("内容不能为空");
            return;
        }
        if (TextUtils.isEmpty(up_type)){
            AppContext.showToast("先选择发帖类型");
            return;
        }
        //开始提交
        TechnicalBean bean = new TechnicalBean();
        bean.title = mTitle;
        bean.connt = mContent;
        bean.type = up_type;
        if (up_imgFile != null && up_imgFile.exists()) {
            bean.imgFile = up_imgFile.getAbsolutePath();
        }
        ServerTaskUtils.putTechnical(getActivity(),bean);
        getActivity().finish();
    }
}
