package net.aixin.app.base;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import net.aixin.app.AppConfig;
import net.aixin.app.AppContext;
import net.aixin.app.R;
import net.aixin.app.api.ApiHttpClient;
import net.aixin.app.api.remote.OSChinaApi;
import net.aixin.app.bean.DetailBean;
import net.aixin.app.bean.News;
import net.aixin.app.bean.Report;
import net.aixin.app.bean.Result;
import net.aixin.app.bean.ResultBean;
import net.aixin.app.cache.CacheManager;
import net.aixin.app.emoji.OnSendClickListener;
import net.aixin.app.fragment.NewsDetailFragment;
import net.aixin.app.ui.DetailActivity;
import net.aixin.app.ui.ReportDialog;
import net.aixin.app.ui.ShareDialog;
import net.aixin.app.ui.empty.EmptyLayout;
import net.aixin.app.util.ACache;
import net.aixin.app.util.DialogHelp;
import net.aixin.app.util.FontSizeUtils;
import net.aixin.app.util.HTMLUtil;
import net.aixin.app.util.HttpUrl;
import net.aixin.app.util.TDevice;
import net.aixin.app.util.UIHelper;
import net.aixin.app.util.XmlUtils;

import cz.msebera.android.httpclient.Header;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;

import butterknife.ButterKnife;

/**
 * 通用的详情fragment
 * Created by 火蚁 on 15/5/25.
 */
public abstract class CommonDetailFragment<T extends Serializable> extends BaseFragment implements OnSendClickListener {

    protected int mId;

    protected EmptyLayout mEmptyLayout;

    protected int mCommentCount = 0;

    protected WebView mWebView;
    private ACache mCache;
    protected T mDetail;

    private AsyncTask<String, Void, T> mCacheTask;

    private RequestQueue mQueue;
    private Context mContext;
    private ImageView detail_title_img;
    private TextView detail_title_state;
    private TextView detail_title_m;
    private TextView detail_title_in;
    private TextView detail_title_d;
    private TextView detail_title_prdno;
    private TextView detail_title_author;
    private RatingBar detail_title_ratingbar;
    //头部图片，默认不显示
    private RelativeLayout common_title;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_news_detail;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container,
                false);
        common_title = (RelativeLayout) view.findViewById(R.id.common_title);
        common_title.setVisibility(flag?View.VISIBLE:View.GONE);

        mCommentCount = getActivity().getIntent().getIntExtra("comment_count",
                0);
        mId = getActivity().getIntent().getIntExtra("id", 0);
        ButterKnife.inject(this, view);
        mContext = getActivity();
        mQueue = Volley.newRequestQueue(mContext);
        //缓存机制
        mCache = ACache.get(mContext);
        initView(view);
        initData();
        requestData(false);
        return view;
    }

    private boolean flag = false;

    /**
     * 设置头布局是否显示图片
     * @param flag
     */
    public void setTitleImageVisible(boolean flag){
        this.flag = flag;
    }

    @Override
    public void initView(View view) {
        mEmptyLayout = (EmptyLayout) view.findViewById(R.id.error_layout);
        setCommentCount(mCommentCount);
        mWebView = (WebView) view.findViewById(R.id.webview);
        detail_title_img = (ImageView) view.findViewById(R.id.detail_title_img);
        detail_title_state = (TextView) view.findViewById(R.id.detail_title_state);
        detail_title_m = (TextView) view.findViewById(R.id.detail_title_m);
        detail_title_in = (TextView) view.findViewById(R.id.detail_title_in);
        detail_title_d = (TextView) view.findViewById(R.id.detail_title_d);
        detail_title_prdno = (TextView) view.findViewById(R.id.detail_title_prdno);
        detail_title_author = (TextView) view.findViewById(R.id.detail_title_author);
        detail_title_ratingbar = (RatingBar) view.findViewById(R.id.detail_title_ratingbar);

        UIHelper.initWebView(mWebView);
    }

    private void showTitleImage(News news){
        String str = news.getBanner();
        Log.i("TAG",news.getBanner());

        ImageLoader.getInstance().loadImage(news.getBanner(), new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view,
                                        FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                detail_title_img.setImageBitmap(loadedImage);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });


//        ImageLoader.getInstance().loadImage(news.getBanner(),new SimpleImageLoadingListener() {
//            @Override
//            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                super.onLoadingComplete(imageUri, view, loadedImage);
//                detail_title_img.setImageBitmap(loadedImage);
//            }
//        });
        detail_title_state.setText(news.getType());
        detail_title_in.setText(news.getInPrice());
        detail_title_m.setText(news.getmKPrice());
        detail_title_d.setText(news.getDescPlace());
        detail_title_prdno.setText(news.getPrdNo());
        detail_title_author.setText(news.getFromPlace());
        float rating = news.getRating();
        detail_title_ratingbar.setRating(rating);
    }
    protected void setCommentCount(int commentCount) {
        ((DetailActivity) getActivity()).toolFragment
                .setCommentCount(commentCount);
    }

    private void requestData(boolean refresh) {
        String key = getCacheKey();
        if (TDevice.hasInternet()
                && (!CacheManager.isExistDataCache(getActivity(), key) || refresh)) {
            sendRequestDataForNet();
        } else {
            readCacheData(key);
        }
    }

    @Override
    public void onDestroyView() {
        recycleWebView();
        flag = false;
        super.onDestroyView();
    }

    private void recycleWebView() {
        if (mWebView != null) {
            mWebView.setVisibility(View.GONE);
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView = null;
        }
    }

    private void readCacheData(String cacheKey) {
        cancelReadCache();
        mCacheTask = new CacheTask(getActivity()).execute(cacheKey);
    }

    private void cancelReadCache() {
        if (mCacheTask != null) {
            mCacheTask.cancel(true);
            mCacheTask = null;
        }
    }

    protected AsyncHttpResponseHandler mDetailHeandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            try {
                T detail = parseData(new ByteArrayInputStream(arg2));
                if (detail != null) {
                    mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                    executeOnLoadDataSuccess(detail);
                    saveCache(detail);
                } else {
                    executeOnLoadDataError();
                }
            } catch (Exception e) {
                e.printStackTrace();
                executeOnLoadDataError();
            }
        }

        @Override
        public void onStart() {
            super.onStart();
            mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        }

        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                              Throwable arg3) {
            readCacheData(getCacheKey());
        }
    };

    private class CacheTask extends AsyncTask<String, Void, T> {
        private final WeakReference<Context> mContext;

        private CacheTask(Context context) {
            mContext = new WeakReference<Context>(context);
        }

        @Override
        protected T doInBackground(String... params) {
            if (mContext.get() != null) {
                Serializable seri = CacheManager.readObject(mContext.get(),
                        params[0]);
                if (seri == null) {
                    return null;
                } else {
                    return (T)seri;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(T detail) {
            super.onPostExecute(detail);
            mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            if (detail != null) {
                executeOnLoadDataSuccess(detail);
            } else {
                executeOnLoadDataError();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        }
    }

    protected void executeOnLoadDataSuccess(T detail) {
        this.mDetail = detail;

        if (this.mDetail == null || TextUtils.isEmpty(this.getWebViewBody(detail))) {
            executeOnLoadDataError();
            return;
        }
        /**
         * 加载数据
         */
        mWebView.loadDataWithBaseURL("", this.getWebViewBody(detail), "text/html", "UTF-8", "");
        // 显示存储的字体大小
        mWebView.loadUrl(FontSizeUtils.getSaveFontSize());
        boolean favoriteState = getFavoriteState() == 1;
        setFavoriteState(favoriteState);

        // 判断最新的评论数是否大于
        if (getCommentCount() > mCommentCount) {
            mCommentCount = getCommentCount();
        }

        //TODO:头布局上的图片问题
        if (flag && mDetail != null && mDetail instanceof News){
            showTitleImage((News)mDetail);
        }

        setCommentCount(mCommentCount);
    }

    protected void executeOnLoadDataError() {
        mEmptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mState = STATE_REFRESH;
                mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                requestData(true);
            }
        });
    }

    protected void saveCache(T detail) {
        new SaveCacheTask(getActivity(), detail, getCacheKey()).execute();
    }

    private class SaveCacheTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<Context> mContext;
        private final Serializable seri;
        private final String key;

        private SaveCacheTask(Context context, Serializable seri, String key) {
            mContext = new WeakReference<Context>(context);
            this.seri = seri;
            this.key = key;
        }

        @Override
        protected Void doInBackground(Void... params) {
            CacheManager.saveObject(mContext.get(), seri, key);
            return null;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.common_detail_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    int i = 0;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                sendRequestDataForNet();
                return false;
            case R.id.font_size:
                showChangeFontSize();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    AlertDialog fontSizeChange;

    private void showChangeFontSize() {

        final String[] items = getResources().getStringArray(
                R.array.font_size);
        fontSizeChange = DialogHelp.getSingleChoiceDialog(getActivity(), items, FontSizeUtils.getSaveFontSizeIndex(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 更改字体大小
                FontSizeUtils.saveFontSize(i);
                mWebView.loadUrl(FontSizeUtils.getFontSize(i));
                fontSizeChange.dismiss();
            }
        }).show();
    }

    // 收藏或者取消收藏
    public void handleFavoriteOrNot() {
        if (mDetail == null) {
            return;
        }
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_no_internet);
            return;
        }
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(getActivity());
            return;
        }
        int uid = AppContext.getInstance().getLoginUid();
        final boolean isFavorited = getFavoriteState() == 1 ? true : false;
        AsyncHttpResponseHandler mFavoriteHandler = new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                try {
                    Result res = XmlUtils.toBean(ResultBean.class,
                            new ByteArrayInputStream(arg2)).getResult();
                    if (res.OK()) {
                        AppContext.showToast(res.getErrorMessage());
                        boolean newFavorited = !isFavorited;
                        setFavoriteState(newFavorited);
                        // 更新收藏的状态
                        updateFavoriteChanged(!newFavorited ? 0 : 1);
                    } else {
                        onFailure(arg0, arg1, arg2, null);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(arg0, arg1, arg2, e);
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                  Throwable arg3) {
                AppContext.showToastShort(R.string.add_favorite_faile);
            }

            @Override
            public void onStart() {
                super.onStart();
                showWaitDialog("请稍候...");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                hideWaitDialog();
            }
        };

        if (isFavorited) {
            OSChinaApi.delFavorite(uid, mId,
                    getFavoriteTargetType(), mFavoriteHandler);
        } else {
            OSChinaApi.addFavorite(uid, mId,
                    getFavoriteTargetType(), mFavoriteHandler);
        }
    }

    private void setFavoriteState(boolean isFavorited) {
        ((DetailActivity) getActivity()).toolFragment.setFavorite(isFavorited);
    }

    // 举报帖子
    public void onReportMenuClick() {
        if (mId == 0 || mDetail == null) {
            AppContext.showToast("正在加载，请稍等...");
        }
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(getActivity());
            return;
        }
        int reportId = mId;

        final ReportDialog dialog = new ReportDialog(getActivity(),
                getRepotrUrl(), reportId, getReportType());
        dialog.setCancelable(true);
        dialog.setTitle(R.string.report);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setNegativeButton(R.string.cancle, null);
        final TextHttpResponseHandler mReportHandler = new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int arg0, Header[] arg1, String arg2) {
                if (TextUtils.isEmpty(arg2)) {
                    AppContext.showToastShort(R.string.tip_report_success);
                } else {
                    AppContext.showToastShort(new String(arg2));
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, String arg2,
                                  Throwable arg3) {
                AppContext.showToastShort(R.string.tip_report_faile);
            }

            @Override
            public void onFinish() {
                hideWaitDialog();
            }
        };
        dialog.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface d, int which) {
                        Report report = null;
                        if ((report = dialog.getReport()) != null) {
                            showWaitDialog(R.string.progress_submit);
                            OSChinaApi.report(report, mReportHandler);
                        }
                        d.dismiss();
                    }
                });
        dialog.show();
    }
    // 分享
    public void handleShare() {
        if (mDetail == null || TextUtils.isEmpty(getShareContent())
                || TextUtils.isEmpty(getShareUrl()) || TextUtils.isEmpty(getShareTitle())) {
            AppContext.showToast("内容加载失败...");
            return;
        }
        final ShareDialog dialog = new ShareDialog(getActivity());
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setTitle(R.string.share_to);
        dialog.setShareInfo(getShareTitle(), getShareContent(), getShareUrl());
        dialog.show();
    }
    // 显示评论列表
    public void onCilckShowComment() {
        showCommentView();
    }

    // 刷新数据
    protected void refresh() {
        sendRequestDataForNet();
    }

    protected AsyncHttpResponseHandler mCommentHandler = new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            try {
                ResultBean rsb = XmlUtils.toBean(ResultBean.class,
                        new ByteArrayInputStream(arg2));
                Result res = rsb.getResult();
                if (res.OK()) {
                    hideWaitDialog();
                    if(AppConfig.isShowUnLogin)
                        AppContext.showToastShort(res.getErrorMessage());
                    // 评论成功之后，评论数加1
                    setCommentCount(mCommentCount + 1);
                } else {
                    hideWaitDialog();
                    if(AppConfig.isShowUnLogin)
                        AppContext.showToastShort(res.getErrorMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                onFailure(arg0, arg1, arg2, e);
            }
            ((DetailActivity) getActivity()).emojiFragment.clean();
        }

        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                              Throwable arg3) {
            hideWaitDialog();
            AppContext.showToastShort(R.string.comment_publish_faile);
        }

        @Override
        public void onFinish() {
            ((DetailActivity) getActivity()).emojiFragment.hideAllKeyBoard();
        };
    };

    // 发表评论
    @Override
    public void onClickSendButton(Editable str) {
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_network_error);
            return;
        }
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(getActivity());
            return;
        }
        if (TextUtils.isEmpty(str)) {
            AppContext.showToastShort(R.string.tip_comment_content_empty);
            return;
        }
        showWaitDialog(R.string.progress_submit);

        OSChinaApi.publicComment(getCommentType(), mId, AppContext
                        .getInstance().getLoginUid(), str.toString(), 0,
                mCommentHandler);
    }

    @Override
    public void onClickFlagButton() {

    }

    /***
     * 获取去除html标签的body
     *
     * @param body
     * @return
     */
    protected String getFilterHtmlBody(String body) {
        if (body == null)
            return "";
        return HTMLUtil.delHTMLTag(body.trim());
    }

    // 获取缓存的key
    protected abstract String getCacheKey();
    // 从网络中读取数据
    protected abstract void sendRequestDataForNet();
    // 解析数据
    protected abstract T parseData(InputStream is);
    // 返回填充到webview中的内容
    protected abstract String getWebViewBody(T detail);
    // 显示评论列表
    protected abstract void showCommentView();
    // 获取评论的类型
    protected abstract int getCommentType();
    protected abstract String getShareTitle();
    protected abstract String getShareContent();
    protected abstract String getShareUrl();
    // 返回举报的url
    protected String getRepotrUrl() {return  "";}
    // 返回举报的类型
    protected byte getReportType() {return  Report.TYPE_QUESTION;}

    // 获取收藏类型（如新闻、博客、帖子）
    protected abstract int getFavoriteTargetType();
    protected abstract int getFavoriteState();
    protected abstract void updateFavoriteChanged(int newFavoritedState);
    protected abstract int getCommentCount();

}
