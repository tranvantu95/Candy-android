package com.candy.android.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.candy.android.R;
import com.candy.android.configs.Define;
import com.candy.android.model.IMessage;
import com.candy.android.model.MessageDetail;
import com.candy.android.utils.Emojione;
import com.candy.android.utils.HimecasUtils;
import com.candy.android.utils.RkLogger;

import java.util.List;

/**
 * @author Favo
 * Created on 17/10/2016.
 */

public class MailAdapter extends RecyclerView.Adapter<MailAdapter.MailViewHolder> {
    private static final String TAG = "IDK-MailAdapter";
    private final Context mContext;

    private OnMemberMailClickedListener mListener;

    private List<IMessage> itemList;

//    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public MailAdapter(Context context, List<IMessage> itemList) {
        this.mContext = context;
        this.itemList = itemList;
//        viewBinderHelper.setOpenOnlyOne(true);
    }

    @Override
    public MailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_performer_message, parent, false);
        return new MailViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MailViewHolder holder, int position) {

//        viewBinderHelper.bind(holder.swipeRevealLayout, String.valueOf(position));
//        if(holder.swipeRevealLayout.isOpened()) holder.swipeRevealLayout.close(true);

        final MessageDetail mailDetail = (MessageDetail) itemList.get(position);

        RkLogger.d("mail image",mailDetail.getPerformerImageUrl());
        //set performer's image
        Glide.with(mContext)
                .load(mailDetail.getPerformerImageUrl())
                .override(Define.SMALL_AVATAR_SIZE, Define.SMALL_AVATAR_SIZE)
                .into(holder.performerImage);

        holder.performerName.setText(mailDetail.getPerformerName());
        try {
            String body = Emojione.shortnameToUnicode(mailDetail.getBody(true));
            HimecasUtils.buildGiffableSpanTextView(holder.msgBody, body);
//            holder.msgBody.setText(URLDecoder.decode(mailDetail.getBody(false),"UTF-8"));
        } catch (IllegalArgumentException ignored) {

        }
        // if it's a read mail or it's a member mail, set normal type face
        holder.msgBody.setTypeface(mailDetail.isRead() || mailDetail.isMemberMail() ?
                Typeface.DEFAULT : Typeface.DEFAULT_BOLD);
        // if it's a read mail or it's a member mail, hide (new) icon
        holder.nNewMsg.setVisibility(mailDetail.isRead() || mailDetail.isMemberMail()
                ? View.GONE : View.VISIBLE);
        holder.sendDate.setText(HimecasUtils.formatSentEmailDateAndTime(mailDetail.getSendDate()));

        if(mailDetail.isLive()){
            holder.liveStatusLayout.setVisibility(View.VISIBLE);
            GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(holder.liveStatusImage);
            Glide.with(mContext).load(R.raw.ic_live).into(imageViewTarget);
        }else{
            holder.liveStatusLayout.setVisibility(View.GONE);
        }

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onMailClicked(mailDetail.getPerformerCode(),
                            mailDetail.getPerformerName(),
                            mailDetail.getPerformerAge(),
                            mailDetail.getPerformerImageUrl());
                } else {
                    RkLogger.w(TAG, "No listener for mail item clicked");
                }
            }
        });

//        holder.btnDeleteMessage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mListener != null) {
//                    mListener.onDeleteMessageClick(mailDetail);
//                } else {
//                    RkLogger.w(TAG, "No listener for mail item clicked");
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public interface OnMemberMailClickedListener {
        void onMailClicked(int performerCode, String performerOriginName, int age, String imageUrl);
        void onDeleteMessageClick(MessageDetail messageDetail);
    }

    public void setListener(OnMemberMailClickedListener listener) {
        mListener = listener;
    }

    static class MailViewHolder extends RecyclerView.ViewHolder {

        //        SwipeRevealLayout swipeRevealLayout;
//        View btnDeleteMessage;
        View mainLayout;
        ImageView liveStatusImage;

        ImageView performerImage;
        TextView performerName;
        TextView nNewMsg;
        TextView sendDate;
        TextView msgBody;
        View liveStatusLayout;

        MailViewHolder(View itemView) {
            super(itemView);
//            swipeRevealLayout = (SwipeRevealLayout) itemView.findViewById(R.id.swipe_reveal_layout);
//            btnDeleteMessage = itemView.findViewById(R.id.btn_delete_message);
            mainLayout = itemView.findViewById(R.id.main_layout);
            liveStatusImage = (ImageView) itemView.findViewById(R.id.live_status_image);
            performerImage = (ImageView) itemView.findViewById(R.id.performerImage);
            performerName = (TextView) itemView.findViewById(R.id.performerName);
            nNewMsg = (TextView) itemView.findViewById(R.id.n_unread_message);
            sendDate = (TextView) itemView.findViewById(R.id.sendDate);
            msgBody = (TextView) itemView.findViewById(R.id.message_body);
            liveStatusLayout = itemView.findViewById(R.id.live_status_layout);
        }
    }
}
