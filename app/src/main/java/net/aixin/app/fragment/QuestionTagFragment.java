package net.aixin.app.fragment;

import java.io.InputStream;
import java.io.Serializable;

import net.aixin.app.R;
import net.aixin.app.adapter.PostAdapter;
import net.aixin.app.api.remote.OSChinaApi;
import net.aixin.app.base.BaseActivity;
import net.aixin.app.base.BaseListFragment;
import net.aixin.app.bean.Post;
import net.aixin.app.bean.PostList;
import net.aixin.app.util.UIHelper;
import net.aixin.app.util.XmlUtils;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

/**
 * 标签相关帖子
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年11月6日 下午3:39:07
 * 
 */
public class QuestionTagFragment extends BaseListFragment<Post> {

    public static final String BUNDLE_KEY_TAG = "BUNDLE_KEY_TAG";
    protected static final String TAG = QuestionTagFragment.class
            .getSimpleName();
    private static final String CACHE_KEY_PREFIX = "post_tag_";
    private String mTag;

    @Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mTag = args.getString(BUNDLE_KEY_TAG);
            ((BaseActivity) getActivity()).setActionBarTitle(getString(
                    R.string.actionbar_title_question_tag, mTag));
        }
    }

    @Override
    protected PostAdapter getListAdapter() {
        return new PostAdapter();
    }

    @Override
    protected String getCacheKeyPrefix() {
        return new StringBuffer(CACHE_KEY_PREFIX).append(mTag).toString();
    }

    @Override
    protected PostList parseList(InputStream is) throws Exception {
        PostList list = XmlUtils.toBean(PostList.class, is);
        return list;
    }

    @Override
    protected PostList readList(Serializable seri) {
        return ((PostList) seri);
    }

    @Override
    protected void sendRequestData() {
        OSChinaApi.getPostListByTag(mTag, mCurrentPage, mHandler);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        Post post = mAdapter.getItem(position);
        if (post != null)
            UIHelper.showPostDetail(view.getContext(), post.getId(),
                    post.getAnswerCount());
    }
}
