package com.mojo.com.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.mojo.com.R;

import java.util.List;

/**
 * Created by Dytstudio.
 */

public class ChatroomAdapter extends SelectableAdapter<ChatroomAdapter.ViewHolder> {

    private List<Chatroom> mArrayList;
    private Context mContext;
    private ChatroomAdapter.ViewHolder.ClickListener clickListener;



    public ChatroomAdapter(Context context, List<Chatroom> arrayList, ChatroomAdapter.ViewHolder.ClickListener clickListener) {
        this.mArrayList = arrayList;
        this.mContext = context;
        this.clickListener = clickListener;

    }

    // Create new views
    @Override
    public ChatroomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item_contact, null);

        ChatroomAdapter.ViewHolder viewHolder = new ChatroomAdapter.ViewHolder(itemLayoutView,clickListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ChatroomAdapter.ViewHolder viewHolder, int position) {

        viewHolder.tvName.setText(mArrayList.get(position).getName());
        viewHolder.userPhoto.setImageResource(mArrayList.get(position).getImage());
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener,View.OnLongClickListener  {

        public TextView tvName;
        public ImageView userPhoto;
        private ChatroomAdapter.ViewHolder.ClickListener listener;
        //private final View selectedOverlay;


        public ViewHolder(View itemLayoutView,ChatroomAdapter.ViewHolder.ClickListener listener) {
            super(itemLayoutView);

            this.listener = listener;

            tvName = (TextView) itemLayoutView.findViewById(R.id.tv_user_name);
            userPhoto = (ImageView) itemLayoutView.findViewById(R.id.iv_user_photo);

            itemLayoutView.setOnClickListener(this);

            itemLayoutView.setOnLongClickListener (this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onItemClicked(getAdapterPosition ());
            }
        }
        @Override
        public boolean onLongClick (View view) {
            if (listener != null) {
                return listener.onItemLongClicked(getAdapterPosition ());
            }
            return false;
        }

        public interface ClickListener {
            public void onItemClicked(int position);

            public boolean onItemLongClicked(int position);

            boolean onCreateOptionsMenu(Menu menu);
        }
    }
}
