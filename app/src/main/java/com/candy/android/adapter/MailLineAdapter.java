package com.candy.android.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.candy.android.R;
import com.candy.android.model.AttachImage;
import com.candy.android.model.FooterMessage;
import com.candy.android.model.IMessage;
import com.candy.android.model.IntroFreeTrialMessage;
import com.candy.android.model.MessageDetail;
import com.candy.android.utils.Emojione;
import com.candy.android.utils.HimecasUtils;
import com.candy.android.utils.RkLogger;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

/**
 * @author Favo
 * Created on 21/10/2016.
 */
public class MailLineAdapter extends RecyclerView.Adapter<MailLineAdapter.MailLineViewHolder> {
    private static final String TAG = "IDK-MailAdapter";

    private OnMessageBubbleClickedListener mMessageBubbleClickedListener;
    private OnPerformerImageClickedListener mPerformerImageClickedListener;
    private OnErrorPlaceHolderClickedListener mErrorPlaceHolderClickedListener;

    private List<IMessage> itemList;
    private Context mContext;

    public MailLineAdapter(Context context, List<IMessage> itemList) {
        this.mContext = context;
        this.itemList = itemList;
    }

    @Override
    public MailLineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        MailLineViewHolder holder = null;

        switch (viewType) {
            case R.layout.mail_line_member_bubble:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.mail_line_member_bubble,
                        parent, false);
                holder = new MemberMessageViewHolder(itemView);
                break;
            case R.layout.mail_line_performer_bubble:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.mail_line_performer_bubble,
                        parent, false);
                holder = new PerformerMessageViewHolder(itemView);
                break;
            case R.layout.mail_line_free_message_trial_intro:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.mail_line_free_message_trial_intro,
                        parent, false);
                holder = new MailLineViewHolder(itemView);
                break;
            case R.layout.mail_line_footer_without_content:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.mail_line_footer_without_content,
                        parent, false);
                holder = new MailLineViewHolder(itemView);
                break;
            default:
                break;
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(MailLineViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case R.layout.mail_line_member_bubble:
                bindMemberViewHolder((MemberMessageViewHolder) holder, position);
                break;
            case R.layout.mail_line_performer_bubble:
                bindPerformerViewHolder((PerformerMessageViewHolder) holder, position);
                break;
            case R.layout.mail_line_free_message_trial_intro:
                bakeFreeMessageTrialIntroView(holder, position);
                break;
            case R.layout.mail_line_footer_without_content:
                // no-op
                break;
            default:
                break;
        }
    }

    private void bakeFreeMessageTrialIntroView(MailLineViewHolder holder, int position) {
        IMessage message = itemList.get(position);
        try {
            holder.msgBody.setText(URLDecoder.decode(message.getBody(true), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void bindPerformerViewHolder(final PerformerMessageViewHolder holder, final int position) {
        final MessageDetail performerMessage = (MessageDetail) itemList.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
        if (!TextUtils.isEmpty(performerMessage.getPerformerImageUrl())) {
            //set performer's image
            Glide.with(mContext).load(performerMessage.getPerformerImageUrl())
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(holder.performerImage);
            // event
            if (itemList.get(position).getPerformerCode() == 0) {
                holder.performerImage.setClickable(false);
            } else {
                holder.performerImage.setClickable(true);
                holder.performerImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mPerformerImageClickedListener != null) {
                            mPerformerImageClickedListener.onPerformerImageClicked();
                        }
                    }
                });
            }
        }

        View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mMessageBubbleClickedListener != null) {
                    mMessageBubbleClickedListener.onLongClicked(holder.getAdapterPosition());
                    return true;    //consumed
                } else {
                    RkLogger.w(TAG, "long clicked detected but no listener");
                    return false;   //not consumed
                }
            }
        };

        //display attached image
        String smallImageUrl = null;
        if (performerMessage.getAttachImageUrl() != null) {
            smallImageUrl = performerMessage.getAttachImageUrl().getSmall();
        }

        if (TextUtils.isEmpty(smallImageUrl)) {
            Glide.clear(holder.attachedImage);
            holder.attachedImage.setVisibility(View.GONE);
        } else {
            holder.attachedImage.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(smallImageUrl)
                    .dontAnimate()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_network_error)
                    .into(holder.attachedImage);

            // recalculate padding
            if (TextUtils.isEmpty(performerMessage.getBody(true))) {
                holder.attachedImage.setPadding(0, 0, 0, 0);
            } else {
                holder.attachedImage.setPadding(0, 0, 0, mContext.getResources()
                        .getDimensionPixelSize(R.dimen.large_padding));
            }

            //events
            holder.attachedImage.setOnLongClickListener(longClickListener);
            holder.attachedImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mMessageBubbleClickedListener != null) {
                        mMessageBubbleClickedListener.onImageClicked(holder.getAdapterPosition());
                    }
                }
            });
        }

        // set body message
        if (TextUtils.isEmpty(performerMessage.getBody(true))) {
            holder.msgBody.setVisibility(View.GONE);
        } else {
            holder.msgBody.setVisibility(View.VISIBLE);
            String body = Emojione.shortnameToUnicode(performerMessage.getBody(true));
            HimecasUtils.buildGiffableSpanTextView(holder.msgBody, body);
            holder.msgBody.setTypeface(performerMessage.isRead() ? Typeface.DEFAULT : Typeface.DEFAULT_BOLD);
            //events
            holder.msgBody.setOnLongClickListener(longClickListener);
            holder.msgBody.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            });
        }

        if (TextUtils.isEmpty(performerMessage.getSendDate())) {
            holder.groupSentDateTime.setVisibility(View.GONE);
            holder.sendDate.setVisibility(View.GONE);
        } else {
            holder.groupSentDateTime.setVisibility(View.VISIBLE);
            holder.sendDate.setVisibility(View.VISIBLE);
            // bind send date, group date
            holder.sendDate.setText(HimecasUtils.formatSentEmailOnlyTime(performerMessage.getSendDate()));
            holder.groupSentDateTime.setText(HimecasUtils.formatSentEmailDateOnlyDate(performerMessage.getSendDate()));
            holder.groupSentDateTime.setVisibility(shouldShowSentDateBubble(position) ?
                    View.VISIBLE : View.GONE);
        }
    }

    private void bindMemberViewHolder(final MemberMessageViewHolder holder, final int position) {
        final MessageDetail memberMessage = (MessageDetail) itemList.get(position);

        View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mMessageBubbleClickedListener != null) {
                    mMessageBubbleClickedListener.onLongClicked(holder.getAdapterPosition());
                    return true;    //consumed
                } else {
                    RkLogger.w(TAG, "long clicked detected but no listener");
                    return false;   //not consumed
                }
            }
        };
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        //display attached image
        AttachImage attachImage = memberMessage.getAttachImageUrl();
        final String smallImageUrl = attachImage != null ?
                attachImage.getSmall() : null;

//        RkLog.d(TAG, "position=" + position + ", message=" + memberMessage.getBody()
//            + ", smallImageUrl=" + smallImageUrl);

        if (TextUtils.isEmpty(smallImageUrl)) {
            Glide.clear(holder.attachedImage);
            holder.attachedImage.setVisibility(View.GONE);
        } else {
            holder.attachedImage.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(memberMessage.getBody(true))) {
                holder.attachedImage.setPadding(0, 0, 0, 0);
            } else {
                holder.attachedImage.setPadding(0, 0, 0, mContext.getResources()
                        .getDimensionPixelSize(R.dimen.large_padding));
            }

            // fuck you, Glide
//            if (attachImage.isFromCached()) {
//                holder.attachedImage.setImageBitmap(BitmapFactory.decodeFile(smallImageUrl));
//            } else {
            Glide.with(mContext)
                    .load(smallImageUrl)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .error(R.drawable.ic_network_error)
                    .into(holder.attachedImage);
//            }

            // events
            holder.attachedImage.setOnLongClickListener(longClickListener);
            holder.attachedImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mMessageBubbleClickedListener != null) {
                        mMessageBubbleClickedListener.onImageClicked(holder.getAdapterPosition());
                    }
                }
            });
        }

        if (TextUtils.isEmpty(memberMessage.getBody(true))) {
            holder.msgBody.setVisibility(View.GONE);
        } else {
            holder.msgBody.setVisibility(View.VISIBLE);
            try {
                String body = Emojione.shortnameToUnicode(memberMessage.getBody(true));
                HimecasUtils.buildGiffableSpanTextView(holder.msgBody, body);
//                holder.msgBody.setText(URLDecoder.decode(memberMessage.getBody(true),"UTF-8"));
            } catch (IllegalArgumentException ignored) {

            }
            holder.msgBody.setTypeface(memberMessage.isRead() ? Typeface.DEFAULT : Typeface.DEFAULT_BOLD);
            //events
            holder.msgBody.setOnLongClickListener(longClickListener);
            holder.msgBody.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            });
        }
        holder.sendDate.setText(HimecasUtils.formatSentEmailOnlyTime(memberMessage.getSendDate()));
        holder.groupSentDateTime.setText(HimecasUtils.formatSentEmailDateOnlyDate(memberMessage.getSendDate()));
        holder.groupSentDateTime.setVisibility(shouldShowSentDateBubble(position) ?
                View.VISIBLE : View.GONE);

        boolean memberMailRead = memberMessage.isMemberMail() && memberMessage.isRead();
        holder.alreadyRead.setVisibility(memberMailRead ? View.VISIBLE : View.GONE);

        holder.errorPlaceHolder.setVisibility(memberMessage.isSentError() ? View.VISIBLE : View.GONE);
        if (memberMessage.isSentError()) {
            holder.errorPlaceHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mErrorPlaceHolderClickedListener != null) {
                        mErrorPlaceHolderClickedListener.onErrorPlaceHolderClicked(holder.getAdapterPosition());
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        final IMessage iMessage = itemList.get(position);

        if (iMessage instanceof IntroFreeTrialMessage) {
            return R.layout.mail_line_free_message_trial_intro;
        }

        if (iMessage instanceof FooterMessage) {
            return R.layout.mail_line_footer_without_content;
        }

        //else
        MessageDetail messageDetail = (MessageDetail) iMessage;
        return messageDetail.isMemberMail() ? R.layout.mail_line_member_bubble : R.layout.mail_line_performer_bubble;
    }

    /**
     * general view holder for mail line's item
     */
    static class MailLineViewHolder extends RecyclerView.ViewHolder {

        TextView msgBody;

        MailLineViewHolder(View itemView) {
            super(itemView);
            msgBody = (TextView) itemView.findViewById(R.id.message_body);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            });
        }
    }

    static abstract class MessageViewHolder extends MailLineViewHolder {

        TextView sendDate;
        TextView groupSentDateTime;
        ImageView attachedImage;

        MessageViewHolder(View itemView) {
            super(itemView);
            sendDate = (TextView) itemView.findViewById(R.id.sendDate);
            groupSentDateTime = (TextView) itemView.findViewById(R.id.groupSentDateTime);
            attachedImage = (ImageView) itemView.findViewById(R.id.attached_image);
        }
    }

    /**
     * * general view holder for both member and performer
     */
    private static class MemberMessageViewHolder extends MessageViewHolder {
        TextView alreadyRead;
        ImageView errorPlaceHolder;

        MemberMessageViewHolder(View itemView) {
            super(itemView);
            alreadyRead = (TextView) itemView.findViewById(R.id.alreadyRead);
            errorPlaceHolder = (ImageView) itemView.findViewById(R.id.error_place_holder);
        }
    }

    /**
     * view holder for performer
     */
    private static class PerformerMessageViewHolder extends MessageViewHolder {

        ImageView performerImage;

        PerformerMessageViewHolder(View itemView) {
            super(itemView);
            performerImage = (ImageView) itemView.findViewById(R.id.performerImage);
        }
    }

    public void setMessageBubbleClickedListener(OnMessageBubbleClickedListener messageBubbleClickedListener) {
        mMessageBubbleClickedListener = messageBubbleClickedListener;
    }

    public void setPerformerImageClickedListener(OnPerformerImageClickedListener performerImageClickedListener) {
        mPerformerImageClickedListener = performerImageClickedListener;
    }

    public void setErrorPlaceHolderClickedListener(OnErrorPlaceHolderClickedListener errorPlaceHolderClickedListener) {
        mErrorPlaceHolderClickedListener = errorPlaceHolderClickedListener;
    }

    private boolean shouldShowSentDateBubble(int position) {
        try {
            //if it's the first item, we should show sent date bubble
            if (position == 0) {
                return true;
            }

            IMessage current = itemList.get(position);
            IMessage previous = itemList.get(position - 1);

            String currentMessageSentDate = HimecasUtils.formatSentEmailDateOnlyDate(current.getSendDate());
            String previousMessageSentDate = HimecasUtils.formatSentEmailDateOnlyDate(previous.getSendDate());

//            RkLog.d(TAG, "position=" + position + ", currentMessageSentDate=" + currentMessageSentDate + ", previousMessageSentDate=" +
//                previousMessageSentDate);

            if (currentMessageSentDate.equals(previousMessageSentDate)) {
//                RkLog.d(TAG, "both are same");
                //no need to display groupSentDateTime
                return false;
            }

        } catch (IndexOutOfBoundsException ioobe) {
            RkLogger.w(TAG, "IndexOutOfBoundsException: ", ioobe);
        } catch (NullPointerException npe) {
            RkLogger.w(TAG, "NullPointerException: ", npe);
        }

        return true;
    }

    public interface OnPerformerImageClickedListener {
        void onPerformerImageClicked();
    }

    public interface OnMessageBubbleClickedListener extends OnImageClickedListener {
        /**
         * @param position zero-based index of clicked object
         */
        void onLongClicked(int position);
    }

    interface OnImageClickedListener {
        /**
         * @param position zero-based index of clicked object
         */
        void onImageClicked(int position);
    }

    public interface OnErrorPlaceHolderClickedListener {
        void onErrorPlaceHolderClicked(int position);
    }
}
