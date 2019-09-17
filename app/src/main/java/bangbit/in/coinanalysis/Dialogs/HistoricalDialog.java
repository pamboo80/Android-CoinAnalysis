package bangbit.in.coinanalysis.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.Util;
import bangbit.in.coinanalysis.pojo.Datum;

import static bangbit.in.coinanalysis.MyApplication.currencySymbol;

/**
 * Created by Nagarajan on 4/27/2018.
 */

public class HistoricalDialog {


    TextView dateTextView,openTextView,closeTextView,highTextView,lowTextView;
    Dialog dialog;
    int currentpotition;
    public void showDialog(final Context context, final ArrayList<Datum> historicalDataList, final int position) {
        currentpotition=position;
        dialog = new Dialog(context);
        View view= LayoutInflater.from(context).inflate(R.layout.historical_dialog,null,false);
        dialog.setContentView(view);
//        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dateTextView=view.findViewById(R.id.date_textView);
        openTextView=view.findViewById(R.id.open_textView);
        closeTextView=view.findViewById(R.id.close_textView);
        highTextView=view.findViewById(R.id.high_textView);
        lowTextView=view.findViewById(R.id.low_textView);
        TextView cancelButtonTextView=view.findViewById(R.id.cancelButton);
        cancelButtonTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        final ImageView leftImageView=view.findViewById(R.id.left_image);
        final ImageView rightImageView=view.findViewById(R.id.right_image);
        rightImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentpotition==historicalDataList.size()-1){
                    rightImageView.setImageResource(R.drawable.arrow_right_gray);
                    return;
                }
                leftImageView.setImageResource(R.drawable.arrow_left_pink);
                currentpotition++;
                if (currentpotition<historicalDataList.size()){
                    setData(historicalDataList.get(currentpotition));
                }
                if (currentpotition==historicalDataList.size()-1){
                    rightImageView.setImageResource(R.drawable.arrow_right_gray);
                }
            }
        });
        leftImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentpotition==0){
                    leftImageView.setImageResource(R.drawable.arrow_left_gray);
                    return;
                }
                rightImageView.setImageResource(R.drawable.arrow_right_pink);
                currentpotition--;
                if (currentpotition>=0){
                    setData(historicalDataList.get(currentpotition));
                }
                if (currentpotition==0){
                    leftImageView.setImageResource(R.drawable.arrow_left_gray);
                }
            }
        });

        if (position==historicalDataList.size()){
            rightImageView.setImageResource(R.drawable.arrow_right_gray);
        }else if (position==0){
            leftImageView.setImageResource(R.drawable.arrow_left_gray);
        }
        setData(historicalDataList.get(position));

        dialog.show();
    }

    void setData(Datum history){
        String lowValue = Util.getMillionValue(history.getLow());
        String highValue = Util.getMillionValue(history.getHigh());
        String openValue = Util.getMillionValue(history.getOpen());
        String closeValue = Util.getMillionValue(history.getClose());

        dateTextView.setText(": "+Util.getDayMonthYearFromUnixTime(history.getTime()-20*1000));
        lowTextView.setText(": "+currencySymbol + lowValue);
        highTextView.setText(": "+currencySymbol + highValue);
        openTextView.setText(": "+currencySymbol + openValue);
        closeTextView.setText(": "+currencySymbol + closeValue);
    }

}
