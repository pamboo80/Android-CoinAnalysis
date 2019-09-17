package bangbit.in.coinanalysis.ChatModule;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import bangbit.in.coinanalysis.Chat.MessageInsertListner;
import bangbit.in.coinanalysis.R;

import static bangbit.in.coinanalysis.Constant.EXCHANGE_SUGESSION;

public class SugessionAdapter extends RecyclerView.Adapter<SugessionAdapter.MySugessionViewHolder> {

    private final ArrayList<String> sugessionArrayList;
    HolderView chatView;

    public static final int OTHER = 0;

    final int type;
    String coinName;

    public SugessionAdapter(ArrayList<String> sugessionArrayList, HolderView chatView) {
        this.sugessionArrayList = sugessionArrayList;
        this.chatView = chatView;
        type = OTHER;
    }

    public SugessionAdapter(ArrayList<String> sugessionArrayList, HolderView chatView, String coinName) {
        this.sugessionArrayList = sugessionArrayList;
        this.chatView = chatView;
        type = EXCHANGE_SUGESSION;
        this.coinName = coinName.toUpperCase();
    }

    @Override
    public MySugessionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sugession_row, parent, false);
        return new MySugessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MySugessionViewHolder holder, int position) {
        final String sugession = sugessionArrayList.get(position);

        if (type == OTHER) {
            holder.sugessionTextView.setText(sugession);
            holder.sugessionTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chatView.addMessageBySender(sugession, OTHER);
                }
            });
        } else if (type == EXCHANGE_SUGESSION) {
            holder.sugessionTextView.setText(coinName + "/" + sugession);
            holder.sugessionTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chatView.addMessageBySender(coinName + "/" + sugession, EXCHANGE_SUGESSION);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return sugessionArrayList.size();
    }

    public class MySugessionViewHolder extends RecyclerView.ViewHolder {
        TextView sugessionTextView;

        public MySugessionViewHolder(View itemView) {
            super(itemView);
            sugessionTextView = itemView.findViewById(R.id.sugessionTextView);
        }
    }
}
