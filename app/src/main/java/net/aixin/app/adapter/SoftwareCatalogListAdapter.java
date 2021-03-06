package net.aixin.app.adapter;

import net.aixin.app.R;
import net.aixin.app.base.ListBaseAdapter;
import net.aixin.app.bean.SoftwareCatalogList.SoftwareType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 产品分类的一级，二级
 */
public class SoftwareCatalogListAdapter extends ListBaseAdapter<SoftwareType> {
	
	static class ViewHold{
		@InjectView(R.id.tv_software_catalog_name)TextView name;
		public ViewHold(View view) {
			ButterKnife.inject(this,view);
		}
	}

	@Override
	protected View getRealView(int position, View convertView, ViewGroup parent) {
		
		ViewHold vh = null;
		
		if (convertView == null || convertView.getTag() == null) {
			convertView = getLayoutInflater(parent.getContext()).inflate(R.layout.list_cell_softwarecatalog, null);
			vh = new ViewHold(convertView);
			convertView.setTag(vh);
		} else {
			vh = (ViewHold)convertView.getTag();
		}
		
		SoftwareType softwareType = (SoftwareType) mDatas.get(position);
		vh.name.setText(softwareType.getName());
		return convertView;
		
	}
}
