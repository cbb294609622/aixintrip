package net.aixin.app.adapter;

import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.IconTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.aixin.app.AppContext;
import net.aixin.app.R;
import net.aixin.app.api.ApiHttpClient;
import net.aixin.app.base.ListBaseAdapter;
import net.aixin.app.bean.MessageDetail;
import net.aixin.app.emoji.InputHelper;
import net.aixin.app.util.ChatImageDisplayer;
import net.aixin.app.util.StringUtils;
import net.aixin.app.util.TDevice;
import net.aixin.app.util.UIHelper;
import net.aixin.app.widget.AvatarView;
import net.aixin.app.widget.MyLinkMovementMethod;
import net.aixin.app.widget.MyURLSpan;
import net.aixin.app.widget.TweetTextView;

import org.kymjs.kjframe.KJBitmap;
import org.kymjs.kjframe.bitmap.BitmapConfig;
import org.kymjs.kjframe.http.HttpConfig;

import java.lang.reflect.Field;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 铂金小鸟 2015-09-16 修改(聊天界面改进)
 */
public class MessageDetailAdapter extends ListBaseAdapter<MessageDetail> {

    private OnRetrySendMessageListener mOnRetrySendMessageListener;
    private KJBitmap mKjBitmap;

    @Override
    protected boolean loadMoreHasBg() {
        return false;
    }

    public MessageDetailAdapter() {
        HttpConfig config = new HttpConfig();
        config.setCookieString(ApiHttpClient.getCookie(AppContext.getInstance()));
        mKjBitmap = new KJBitmap();
        try {
            //初始化display，设置图片的最大宽高和最小宽高
            ChatImageDisplayer displayer = new ChatImageDisplayer(config, new BitmapConfig());
            int maxWidth = TDevice.getDisplayMetrics().widthPixels / 2;
            int maxHeight = maxWidth;
            int minWidth = maxWidth / 2;
            int minHeight = minWidth;
            displayer.setImageSize(maxWidth, maxHeight, minWidth, minHeight);
            //kjBitmap 不能设置自定义的displayer，这里通过反射设置自定义的displayer
            Class<?> classType = mKjBitmap.getClass();
            Field field = classType.getDeclaredField("displayer");
            field.setAccessible(true);
            field.set(mKjBitmap, displayer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected View getRealView(int position, View convertView,
            final ViewGroup parent) {
        final MessageDetail item = mDatas.get(mDatas.size() - position - 1);

        int itemType = 0;
        if (item.getAuthorId() == AppContext.getInstance().getLoginUid()) {
            itemType = 1;
        }
        boolean needCreateView = false;
        ViewHolder vh = null;
        if (convertView == null) {
            needCreateView = true;
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        if (vh == null || vh.type != itemType) {
            needCreateView = true;
        }

        if (needCreateView) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    itemType == 0 ? R.layout.list_cell_chat_from : R.layout.list_cell_chat_to, null);
            vh = new ViewHolder(convertView);
            vh.type = itemType;
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.avatar.setAvatarUrl(item.getPortrait());
        vh.avatar.setUserInfo(item.getAuthorId(), item.getAuthor());

        //判断是不是图片
        if (item.getBtype() == 3) {
            showImage(vh,item);
        } else /*if(item.getBtype()==1)*/ {
            //文本消息
            showText(vh,item);
        }
        showStatus(vh,item);

        //检查是否需要显示时间
        if (item.isShowDate()) {
            vh.time.setText(StringUtils.friendly_time3(item.getPubDate()));
            vh.time.setVisibility(View.VISIBLE);
        } else {
            vh.time.setVisibility(View.GONE);
        }

        return convertView;
    }

    /**
     * 显示文字消息
     * @param vh
     * @param msg
     */
    private void showText(ViewHolder vh,MessageDetail msg){
        vh.image.setVisibility(View.GONE);
        vh.content.setVisibility(View.VISIBLE);
        Spanned span = Html.fromHtml(msg.getContent());
        span = InputHelper.displayEmoji(vh.content.getResources(), span);
        vh.content.setText(span);
        MyURLSpan.parseLinkText(vh.content, span);
    }

    /**
     * 显示图片
     * @param vh
     * @param msg
     */
    private void showImage(ViewHolder vh,MessageDetail msg){
        vh.content.setVisibility(View.GONE);
        vh.image.setVisibility(View.VISIBLE);
        //加载图片
        vh.image.setImageResource(R.drawable.load_img_loading);
        mKjBitmap.display(vh.image, msg.getContent(), R.drawable.load_img_error, 0, 0, null);
    }

    /**
     * 显示消息状态
     * @param vh
     * @param msg
     */
    private void showStatus(ViewHolder vh,MessageDetail msg) {
        //如果msg正在发送，则显示progressBar. 发送错误显示错误图标
        if (msg.getStatus() != null && msg.getStatus() != MessageDetail.MessageStatus.NORMAL) {
            vh.msgStatusPanel.setVisibility(View.VISIBLE);
            if (msg.getStatus() == MessageDetail.MessageStatus.SENDING) {
                //sending 正在发送
                vh.progressBar.setVisibility(View.VISIBLE);
                vh.error.setVisibility(View.GONE);
                vh.error.setTag(null);
            } else {
                //error 发送出错
                vh.progressBar.setVisibility(View.GONE);
                vh.error.setVisibility(View.VISIBLE);
                //设置tag为msg id，以便点击重试发送
                vh.error.setTag(msg.getId());

            }
        } else {
            //注意，此处隐藏要用INVISIBLE，不能使用GONE
            vh.msgStatusPanel.setVisibility(View.INVISIBLE);
            vh.error.setTag(null);
        }
    }

    public OnRetrySendMessageListener getOnRetrySendMessageListener() {
        return mOnRetrySendMessageListener;
    }

    public void setOnRetrySendMessageListener(OnRetrySendMessageListener onRetrySendMessageListener) {
        this.mOnRetrySendMessageListener = onRetrySendMessageListener;
    }

    @Override
    protected boolean hasFooterView() {
        return false;
    }

    class ViewHolder {
        int type;
        @InjectView(R.id.iv_avatar)
        AvatarView avatar;
        @InjectView(R.id.tv_time)
        TextView time;
        @InjectView(R.id.tv_content)
        TweetTextView content;
        @InjectView(R.id.iv_img)
        ImageView image;
        @InjectView(R.id.progress)
        ProgressBar progressBar;
        @InjectView(R.id.rl_msg_status_panel)
        RelativeLayout msgStatusPanel;
        @InjectView(R.id.itv_error)
        IconTextView error;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);

            content.setMovementMethod(MyLinkMovementMethod.a());
            content.setFocusable(false);
            content.setDispatchToParent(true);
            content.setLongClickable(false);
        }

        @OnClick(R.id.iv_img)
        void viewImage(View v) {
            if (v.getTag() != null) {
                String url = (String) v.getTag();
                UIHelper.showImagePreview(v.getContext(), new String[]{url});
            }
        }

        /**
         * 重试发送
         *
         * @param v
         */
        @OnClick(R.id.itv_error)
        void retry(View v) {
            if (v.getTag() != null && mOnRetrySendMessageListener != null) {
                mOnRetrySendMessageListener.onRetrySendMessage((int) v.getTag());
            }
        }
    }


    public interface OnRetrySendMessageListener{
        void onRetrySendMessage(int msgId);
    }
}
