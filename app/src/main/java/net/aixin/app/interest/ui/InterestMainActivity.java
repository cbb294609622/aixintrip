package net.aixin.app.interest.ui;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.RequestQueue;

import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import net.aixin.app.R;
import net.aixin.app.api.ApiHttpClient;
import net.aixin.app.base.BaseActivity;
import net.aixin.app.bean.InterestBean;
import net.aixin.app.bean.InterestSubmit;
import net.aixin.app.ui.empty.EmptyLayout;
import net.aixin.app.util.ACache;
import net.aixin.app.util.GsonUtil;
import net.aixin.app.util.HttpUrl;
import net.aixin.app.util.SharedPreferencesUitl;
import net.aixin.app.widget.PredicateLayout;



import java.util.ArrayList;

import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;


/**
 * 兴趣界面
 * Created by BoBo on 2015/12/3.
 */
public class InterestMainActivity extends BaseActivity {

    private PredicateLayout ply;
    private int one = R.color.one_state;
    private int two = R.drawable.img_interest_bg;
    private List<String> listClick;
    private ACache mCache;
    /**
     * 增加进去的数据
     */
    private List<String> addList;
    /**
     * 默认的数据状态
     */
    private List<Integer> stateList;
    /**
     * 修改过后的状态
     */
    private List<Integer> findList;
    /**
     * 提交的数据
     */
    private List<Map<String,Object>> submitList;


    private TextView tvt;
    private InterestBean interestBean;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_interest_fragment;
    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    private Context mContext;
    private RequestQueue mQueue;
    protected EmptyLayout mEmptyLayout;

    @Override
    public void initView() {
        setActionBarTitle(R.string.interest_title);
        mContext = getApplicationContext();
        mQueue = Volley.newRequestQueue(mContext);
        listClick = new ArrayList<String>();
        //缓存机制
        mCache = ACache.get(mContext);

        mEmptyLayout = (EmptyLayout)findViewById(R.id.error_layout);
        ply = (PredicateLayout) findViewById(R.id.predicate_add_view);
        tvt = (TextView) findViewById(R.id.tv);

        //判断是否有缓存的方法
        caCheData();

    }

    private void caCheData() {
        String response = mCache.getAsString(HttpUrl.INTEREST_URL);
        if (TextUtils.isEmpty(response)) {
            initGet();
        } else {
            initGet();
//            mResponse(response);
        }
    }

    @Override
    public void initData() {

    }

    private void initGet() {
        ApiHttpClient.get(HttpUrl.INTEREST_URL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String s = new String(responseBody);
                /**
                 * 1.缓存名称
                 * 2.缓存数据
                 * 3.缓存时间
                 */
                mCache.put(HttpUrl.INTEREST_URL, s.toString(), 1*ACache.TIME_DAY);
                mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                mResponse(s.toString());
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
            }
        });
    }

    private void mResponse(String response) {
        interestBean = GsonUtil.json2Bean(response, InterestBean.class);
        addList = new ArrayList<String>();
        stateList = new ArrayList<Integer>();
        findList = new ArrayList<Integer>();
        for (int i = 0; i < interestBean.rs.size(); i++) {
            addList.add(interestBean.rs.get(i).n);
            stateList.add(interestBean.rs.get(i).s);
            findList.add(interestBean.rs.get(i).s);
        }
        showView();
    }
    private void showView() {
        for (int i = 0; i < addList.size(); i++) {
            final int s = i;
            final TextView tv = new TextView(mContext);
            tv.setWidth(210);
            tv.setHeight(120);
            tv.setText(addList.get(i));
            tv.setTextColor(Color.BLACK);
            if (1 == stateList.get(s)) {
                tv.setBackgroundResource(two);
                SharedPreferencesUitl.saveBooleanData(mContext, addList.get(s), true);
                listClick.add(addList.get(s));
            } else {
                tv.setBackgroundColor(this.getResources().getColor(one));
                SharedPreferencesUitl.saveBooleanData(mContext, addList.get(s), false);
                listClick.remove(addList.get(s));

            }
            tv.setGravity(Gravity.CENTER);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Boolean booleanData = SharedPreferencesUitl.getBooleanData(mContext, addList.get(s), false);
                    if (booleanData) {
                        tv.setBackgroundColor(mContext.getResources().getColor(one));
                        SharedPreferencesUitl.saveBooleanData(mContext, addList.get(s), false);
                        listClick.remove(addList.get(s));
                        if (1 == findList.get(s)) {
                            findList.set(s, 0);
                        } else {
                            findList.set(s, 1);
                        }
                    } else {
                        tv.setBackgroundResource(two);
                        SharedPreferencesUitl.saveBooleanData(mContext, addList.get(s), true);
                        listClick.add(addList.get(s));
                        if (1 == findList.get(s)) {
                            findList.set(s, 0);
                        } else {
                            findList.set(s, 1);
                        }
                    }

                    tvt.setText(listClick + "-   -" + findList);
                }
            });
            ply.addView(tv);
        }
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.interest_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.submit:
                subMitPost();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 提交选择的兴趣标签
     */
    private void subMitPost() {
//        HttpUtils http = new HttpUtils();
        RequestParams params = new RequestParams();
        JSONArray array = new JSONArray();
        for (int i = 0; i < interestBean.rs.size(); i++){
            JSONObject object = new JSONObject();
                object.put("id",interestBean.rs.get(i).id);
                object.put("n",interestBean.rs.get(i).n);
                object.put("s",findList.get(i));
                array.add(object);
        }
        params.add("data",array.toJSONString());
        ApiHttpClient.post(HttpUrl.INTEREST_POST_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String s = new String(responseBody);
                showResponseInfo(s.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("TAG",error.toString());
            }
        });
    }

    private void showResponseInfo(String result) {
        InterestSubmit submit =  GsonUtil.json2Bean(result, InterestSubmit.class);
        if (1 == submit.rs.rs){
            tvt.setText(listClick + "-   -" + findList);
            Toast.makeText(mContext,"完成标签设置",Toast.LENGTH_SHORT).show();
            onBackPressed();
        }else{
            tvt.setText(listClick + "-   -" + findList);
            Toast.makeText(mContext,"设置标签失败，请重试",Toast.LENGTH_SHORT).show();
        }
    }
}
