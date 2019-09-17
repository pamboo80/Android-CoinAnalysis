package bangbit.in.coinanalysis.Chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import bangbit.in.coinanalysis.R;

import static bangbit.in.coinanalysis.Constant.TEXT_SENDER;

public class SenderViewHolder extends RecyclerView.ViewHolder {
    private final TextView messageTextView;
    private final MessageInsertListner messageInsertListner;
    private final View itemView;

    public SenderViewHolder(final View itemView, MessageInsertListner messageInsertListner) {
        super(itemView);
        this.itemView = itemView;
        messageTextView= itemView.findViewById(R.id.text_message_body);
        this.messageInsertListner=messageInsertListner;

    }
    void bind(final ChatData chatData){
        messageTextView.setText(chatData.getMessage());
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageInsertListner.showDetail(chatData,TEXT_SENDER);
            }
        });
    }
}
