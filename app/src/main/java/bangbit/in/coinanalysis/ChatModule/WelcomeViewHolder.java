package bangbit.in.coinanalysis.ChatModule;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

import bangbit.in.coinanalysis.Chat.MessageInsertListner;
import bangbit.in.coinanalysis.R;

public class WelcomeViewHolder extends RecyclerView.ViewHolder {

    private final Context context;
    private final LinearLayout linearLayout;
    private final  HolderView chatView;

    public WelcomeViewHolder(View itemView,  HolderView chatView) {
        super(itemView);
        linearLayout=itemView.findViewById(R.id.welcomeLinearLayout);
        context=itemView.getContext();
        this.chatView =chatView;
        addSugession(null);
    }
    void bind(ArrayList<String> sugessionArrayList){

    }


    void addSugession(ArrayList<String> sugessionArrayList) {
        View sugessionView = LayoutInflater.from(context).inflate(R.layout.response_sugession_text, null, false);
        RecyclerView recyclerView = sugessionView.findViewById(R.id.sugessionRecycleView);
        ArrayList<String> arrayList = new ArrayList();
        arrayList.add("Bitcoin price");
        arrayList.add("Bitcoin supply");
        arrayList.add("Bitcoin marketcap");
        arrayList.add("7 days Bitcoin chart");
        arrayList.add("1 month Bitcoin historical data");
        arrayList.add("Bitcoin yearly price trend");
        arrayList.add("Buy Bitcoin");
        arrayList.add("Top performing coins");

        SugessionAdapter sugessionAdapter = new SugessionAdapter(arrayList, chatView);
        recyclerView.setAdapter(sugessionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        linearLayout.addView(sugessionView);
    }
}
