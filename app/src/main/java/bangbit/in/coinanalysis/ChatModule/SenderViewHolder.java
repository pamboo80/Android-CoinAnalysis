package bangbit.in.coinanalysis.ChatModule;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import bangbit.in.coinanalysis.Chat.ChatData;
import bangbit.in.coinanalysis.Chat.MessageInsertListner;
import bangbit.in.coinanalysis.R;

import static bangbit.in.coinanalysis.Constant.TEXT_SENDER;

public class SenderViewHolder extends RecyclerView.ViewHolder {
    private final TextView messageTextView;
    HolderView chatView;
    private final View itemView;

    public SenderViewHolder(final View itemView, HolderView chatView) {
        super(itemView);
        this.itemView = itemView;
        messageTextView = itemView.findViewById(R.id.text_message_body);
        this.chatView = chatView;
    }

    void bind(final ChatData chatData) {
        messageTextView.setText(chatData.getMessage());
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatView.showDetail(chatData, TEXT_SENDER);
            }
        });
    }
}
