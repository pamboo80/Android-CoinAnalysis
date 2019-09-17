package bangbit.in.coinanalysis.Chat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import bangbit.in.coinanalysis.R;

public class SugessionAdapter extends RecyclerView.Adapter<SugessionAdapter.MySugessionViewHolder> {

    private final ArrayList<String> sugessionArrayList;
    private final MessageInsertListner messageInsertListner;

    public SugessionAdapter(ArrayList<String> sugessionArrayList, MessageInsertListner messageInsertListner) {
        this.sugessionArrayList = sugessionArrayList;
        this.messageInsertListner=messageInsertListner;
    }

    @Override
    public MySugessionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sugession_row, parent, false);
        return new MySugessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MySugessionViewHolder holder, int position) {
        final String sugession = sugessionArrayList.get(position);
        holder.sugessionTextView.setText(sugession);
        holder.sugessionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messageInsertListner.addMessageBySender(sugession);
            }
        });
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
