package net.aixin.app.fragment;

import java.io.InputStream;
import java.io.Serializable;

import net.aixin.app.adapter.PostAdapter;
import net.aixin.app.api.remote.OSChinaApi;
import net.aixin.app.base.BaseListFragment;
import net.aixin.app.bean.Post;
import net.aixin.app.bean.PostList;
import net.aixin.app.util.UIHelper;
import net.aixin.app.util.XmlUtils;
import android.view.View;
import android.widget.AdapterView;

/**
 * 问答
 * 
 * @author william_sim
 */
public class PostsFragment extends BaseListFragment<Post> {

    protected static final String TAG = PostsFragment.class.getSimpleName();
    private static final String CACHE_KEY_PREFIX = "postslist_";

    @Override
    protected PostAdapter getListAdapter() {
        return new PostAdapter();
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + mCatalog;
    }

    @Override
    protected PostList parseList(InputStream is) {
        PostList list;
        try{
            list = XmlUtils.toBean(PostList.class, is);
        }catch (Exception e){
            list = new PostList();
        }
        return list;
    }

    @Override
    protected PostList readList(Serializable seri) {
        return ((PostList) seri);
    }

    @Override
    protected void sendRequestData() {
        OSChinaApi.getPostList(mCatalog, mCurrentPage, mHandler);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        Post post = mAdapter.getItem(position);
        if (post != null) {
            UIHelper.showPostDetail(view.getContext(), post.getId(),
                    post.getAnswerCount());
            // 放入已读列表
            saveToReadedList(view, PostList.PREF_READED_POST_LIST, post.getId()
                    + "");
        }
    }
}
