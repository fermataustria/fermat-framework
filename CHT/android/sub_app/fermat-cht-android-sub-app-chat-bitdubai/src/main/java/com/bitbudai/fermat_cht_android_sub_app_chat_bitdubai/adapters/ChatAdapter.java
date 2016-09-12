package com.bitbudai.fermat_cht_android_sub_app_chat_bitdubai.adapters;

import android.content.ClipData;
import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bitbudai.fermat_cht_android_sub_app_chat_bitdubai.filters.ChatFilter;
import com.bitbudai.fermat_cht_android_sub_app_chat_bitdubai.holders.ChatHolder;
import com.bitbudai.fermat_cht_android_sub_app_chat_bitdubai.models.ChatMessage;
import com.bitbudai.fermat_cht_android_sub_app_chat_bitdubai.util.Utils;
import com.bitdubai.fermat_android_api.ui.adapters.FermatAdapter;
import com.bitdubai.fermat_cht_android_sub_app_chat_bitdubai.R;

import java.util.ArrayList;

/**
 * ChatAdapter
 *
 * @author Jose Cardozo josejcb (josejcb89@gmail.com) on 05/01/15.
 * @version 1.0
 */

public class ChatAdapter extends FermatAdapter<ChatMessage, ChatHolder>
        implements Filterable {

    ArrayList<ChatMessage> chatMessages = new ArrayList<>();

    ArrayList<ChatMessage> filteredData;
    private String filterString;

    public ChatAdapter(Context context, ArrayList<ChatMessage> chatMessages) {//ChatFactory
        super(context, chatMessages);
        this.chatMessages = chatMessages;
    }

    @Override
    protected ChatHolder createHolder(View itemView, int type) {
        return new ChatHolder(itemView);
    }

    @Override
    protected int getCardViewResource() {
        return R.layout.chat_list_item;
    }


    @Override
    protected void bindHolder(ChatHolder holder, ChatMessage data, int position) {
        try {
            if (data != null) {
                boolean myMsg = data.getIsme();
                setAlignment(holder, myMsg, data);
                final String copiedMessage = holder.txtMessage.getText().toString();
                holder.content.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("simple text", copiedMessage);
                            clipboard.setPrimaryClip(clip);
                        } else {
                            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                            clipboard.setText(copiedMessage);
                        }
                        if (copiedMessage.length() <= 10) {
                            Toast.makeText(context, context.getText(R.string.copy_message_toast) + " " + copiedMessage, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, context.getText(R.string.copy_message_toast) + " " + copiedMessage.substring(0, 11) + "...", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public View getView() {
        LayoutInflater vi = LayoutInflater.from(context);
        View convertView = vi.inflate(R.layout.chat_list_item, null);
        return convertView;
    }

    public void refreshEvents(ArrayList<ChatMessage> chatHistory) {
        for (int i = 0; i < chatHistory.size(); i++) {
            ChatMessage message = chatHistory.get(i);
            add(message);
            changeDataSet(chatHistory);
            notifyDataSetChanged();
        }
    }

    public void add(ChatMessage message) {
        chatMessages.add(message);
    }

    private void setAlignment(ChatHolder holder, boolean isMe, ChatMessage data) {
        try {
            holder.tickstatusimage.setImageResource(0);
            holder.txtMessage.setText(Utils.avoidingScientificNot(data.getMessage()));
            holder.txtInfo.setText(data.getDate());
            if (isMe) {
                holder.contentWithBG.setBackgroundResource(R.drawable.cht_burble_green);

                LinearLayout.LayoutParams layoutParams =
                        (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
                layoutParams.gravity = Gravity.RIGHT;
                holder.contentWithBG.setLayoutParams(layoutParams);

                RelativeLayout.LayoutParams lp =
                        (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                holder.content.setLayoutParams(lp);
                layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
                layoutParams.gravity = Gravity.RIGHT;
                holder.txtMessage.setLayoutParams(layoutParams);

                layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
                layoutParams.gravity = Gravity.RIGHT;
                holder.txtInfo.setLayoutParams(layoutParams);
                if (data.getStatus() != null) {
                    switch (data.getStatus()) {
                        case SENT:
                            holder.tickstatusimage.setVisibility(View.VISIBLE);
                            holder.tickstatusimage.setImageResource(R.drawable.cht_ticksent);
                            break;
                        case DELIVERED:
                            holder.tickstatusimage.setVisibility(View.VISIBLE);
                            holder.tickstatusimage.setImageResource(R.drawable.cht_tickdelivered);
                            break;
                        case RECEIVE:
                            holder.tickstatusimage.setVisibility(View.VISIBLE);
                            holder.tickstatusimage.setImageResource(R.drawable.cht_tickdelivered);
                            break;
                        case READ:
                            holder.tickstatusimage.setVisibility(View.VISIBLE);
                            holder.tickstatusimage.setImageResource(R.drawable.cht_tickread);
                            break;
                        case CANNOT_SEND:
                            holder.tickstatusimage.setVisibility(View.VISIBLE);
                            holder.tickstatusimage.setImageResource(R.drawable.cht_equis_icon);
                            break;
                        default:
                            holder.tickstatusimage.setImageResource(0);
                            holder.tickstatusimage.setVisibility(View.GONE);
                            break;
                    }
                } else {
                    holder.tickstatusimage.setImageResource(0);
                    holder.tickstatusimage.setVisibility(View.GONE);
                }
            } else {
                holder.contentWithBG.setBackgroundResource(R.drawable.cht_burble_white);

                LinearLayout.LayoutParams layoutParams =
                        (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
                layoutParams.gravity = Gravity.LEFT;
                holder.contentWithBG.setLayoutParams(layoutParams);

                RelativeLayout.LayoutParams lp =
                        (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                holder.content.setLayoutParams(lp);
                layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
                layoutParams.gravity = Gravity.LEFT;
                holder.txtMessage.setLayoutParams(layoutParams);

                layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
                layoutParams.gravity = Gravity.LEFT;
                holder.txtInfo.setLayoutParams(layoutParams);
                //holder.txtInfo.setPadding(20,0,20,7);
                holder.tickstatusimage.setVisibility(View.GONE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if (filterString != null)
            return filteredData == null ? 0 : filteredData.size();
        else
            return chatMessages == null ? 0 : chatMessages.size();
    }

    @Override
    public ChatMessage getItem(int position) {
        if (filterString != null)
            return filteredData != null ? (!filteredData.isEmpty()
                    && position < filteredData.size()) ? filteredData.get(position) : null : null;
        else
            return chatMessages != null ? (!chatMessages.isEmpty()
                    && position < chatMessages.size()) ? chatMessages.get(position) : null : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void changeDataSet(ArrayList<ChatMessage> data) {
        this.filteredData = data;
    }

    public Filter getFilter() {
        return new ChatFilter(chatMessages, this);
    }

    public void setFilterString(String filterString) {
        this.filterString = filterString;
    }

    public String getFilterString() {
        return filterString;
    }

}