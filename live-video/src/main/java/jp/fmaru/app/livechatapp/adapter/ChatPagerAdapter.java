package jp.fmaru.app.livechatapp.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import jp.fmaru.app.livechatapp.R;

public class ChatPagerAdapter extends PagerAdapter {

    private Context context;

    private List<View> views = new ArrayList<>();

    public List<View> getViews() {
        return views;
    }

    public ChatPagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = views.get(position);
        if(view.getParent() == null) container.addView(view);
        return view;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.title_chat_log_1);
            case 1:
                return context.getString(R.string.title_chat_log_2);
        }
        return super.getPageTitle(position);
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
