package jp.fmaru.app.livechatapp.adapter;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jp.fmaru.app.livechatapp.R;
import jp.fmaru.app.livechatapp.model.ChatMessage;

/**
 * Created by HoangVuNam on 3/23/17.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private static final String NAME_PATTERN = "%s: ";

    private List<ChatMessage> items = new ArrayList<>();

    public List<ChatMessage> getItems() {
        return items;
    }

    public ChatAdapter() {
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member_message_layout, parent, false);
        return new ChatViewHolder(parentView);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        ChatMessage message = items.get(position);
        holder.bindData(message);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
//        public TextView mTvName;
        public TextView mTvMessage;

        public ChatViewHolder(View itemView) {
            super(itemView);
//            mTvName = (TextView) itemView.findViewById(R.id.tv_name);
            mTvMessage = (TextView) itemView.findViewById(R.id.tv_message);
        }

        public void bindData(ChatMessage chatMessage) {
//            mTvName.setText(String.format(Locale.getDefault(),  NAME_PATTERN, chatMessage.getName()));
//            if (chatMessage.isMyMessage()) {
//                mTvName.setTextColor(itemView.getContext().getResources().getColor(R.color.member_name_color));
//            } else {
//                mTvName.setTextColor(itemView.getContext().getResources().getColor(R.color.performer_name_color));
//            }

            int color = itemView.getContext().getResources().getColor(chatMessage.isMyMessage() ? R.color.member_name_color : R.color.performer_name_color);
            String name = String.format(Locale.getDefault(),  NAME_PATTERN, chatMessage.getName());
            Spannable spannable = new SpannableString(name + chatMessage.getMessageContent());
            spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new ForegroundColorSpan(color), 0, name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            mTvMessage.setText(spannable);
        }
    }
}
