package net.aixin.app.viewpagerfragment;

import net.aixin.app.R;
import net.aixin.app.adapter.ViewPageFragmentAdapter;
import net.aixin.app.base.BaseFragment;
import net.aixin.app.base.BaseViewPagerFragment;
import net.aixin.app.bean.SoftwareList;
import net.aixin.app.fragment.SoftwareCatalogListFragment;
import net.aixin.app.fragment.SoftwareListFragment;
import android.os.Bundle;
import android.view.View;

/**
 * 产品分类的基类ViewPager页面
 */
public class OpensourceSoftwareFragment extends BaseViewPagerFragment {

	public static OpensourceSoftwareFragment newInstance() {
		return new OpensourceSoftwareFragment();
	}

	@Override
	protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
		String[] title = getResources().getStringArray(
				R.array.opensourcesoftware);
		adapter.addTab(title[0], "software_catalog",
				SoftwareCatalogListFragment.class, null);
		adapter.addTab(title[1], "software_recommend",
				SoftwareListFragment.class,
				getBundle(SoftwareList.CATALOG_RECOMMEND));
		adapter.addTab(title[2], "software_latest", SoftwareListFragment.class,
				getBundle(SoftwareList.CATALOG_TIME));
		adapter.addTab(title[3], "software_hot", SoftwareListFragment.class,
				getBundle(SoftwareList.CATALOG_VIEW));
		adapter.addTab(title[4], "software_china", SoftwareListFragment.class,
				getBundle(SoftwareList.CATALOG_LIST_CN));
	}

	private Bundle getBundle(String catalog) {
		Bundle bundle = new Bundle();
		bundle.putString(SoftwareListFragment.BUNDLE_SOFTWARE, catalog);
		return bundle;
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public void initView(View view) {

	}

	@Override
	public void initData() {

	}

	@Override
	public boolean onBackPressed() {
		BaseFragment fragment = (BaseFragment) mTabsAdapter.getItem(mViewPager
				.getCurrentItem());
		if (fragment instanceof SoftwareCatalogListFragment) {
			return fragment.onBackPressed();
		}
		return super.onBackPressed();
	}
}