package net.aixin.app.fragment;

import java.io.InputStream;
import java.io.Serializable;

import net.aixin.app.AppContext;
import net.aixin.app.adapter.UserFavoriteAdapter;
import net.aixin.app.api.remote.OSChinaApi;
import net.aixin.app.base.BaseListFragment;
import net.aixin.app.bean.Favorite;
import net.aixin.app.bean.FavoriteList;
import net.aixin.app.util.UIHelper;
import net.aixin.app.util.XmlUtils;
import android.view.View;
import android.widget.AdapterView;

public class UserFavoriteFragment extends BaseListFragment<Favorite> {

    protected static final String TAG = UserFavoriteFragment.class
	    .getSimpleName();
    private static final String CACHE_KEY_PREFIX = "userfavorite_";

    @Override
    protected UserFavoriteAdapter getListAdapter() {
	return new UserFavoriteAdapter();
    }

    @Override
    protected String getCacheKeyPrefix() {
	return CACHE_KEY_PREFIX + mCatalog;
    }

    @Override
    protected FavoriteList parseList(InputStream is) throws Exception {

	FavoriteList list = XmlUtils.toBean(FavoriteList.class, is);
	return list;
    }

    @Override
    protected FavoriteList readList(Serializable seri) {
	return ((FavoriteList) seri);
    }

    @Override
    protected void sendRequestData() {
	OSChinaApi.getFavoriteList(AppContext.getInstance().getLoginUid(),
		mCatalog, mCurrentPage, mHandler);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
	    long id) {

	Favorite favorite = (Favorite) mAdapter.getItem(position);
	if (favorite != null) {
	    switch (favorite.getType()) {

	    case Favorite.CATALOG_BLOGS:
		UIHelper.showUrlRedirect(getActivity(), favorite.getUrl());
		break;
	    case Favorite.CATALOG_CODE:
		UIHelper.showUrlRedirect(getActivity(), favorite.getUrl());
		break;
	    case Favorite.CATALOG_NEWS:
		UIHelper.showUrlRedirect(getActivity(), favorite.getUrl());
		break;
	    case Favorite.CATALOG_SOFTWARE:
		UIHelper.showUrlRedirect(getActivity(), favorite.getUrl());
		break;
	    case Favorite.CATALOG_TOPIC:
		UIHelper.showUrlRedirect(getActivity(), favorite.getUrl());
		break;

	    }
	}

    }
}
