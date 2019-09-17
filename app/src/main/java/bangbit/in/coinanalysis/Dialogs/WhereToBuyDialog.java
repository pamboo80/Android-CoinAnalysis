package bangbit.in.coinanalysis.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.pojo.CurrencyExchange;

/**
 * Created by Nagarajan on 3/26/2018.
 */

public class WhereToBuyDialog {


    private final Context context;

    public WhereToBuyDialog(Context context){
        this.context=context;
    }
    Dialog dialog;

    public void showDialog(final CurrencyExchange currencyExchange) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.where_to_buy_dialog);
//        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;


        dialog.setTitle("CoinAnalysis");
        TextView messageTextView=dialog.findViewById(R.id.contentText);
        String message=currencyExchange.getName();
        messageTextView.setText("Do you want to open the "+message+" official website?");

        Button yesDialogButton = dialog.findViewById(R.id.yes);
        Button noDialogButton = dialog.findViewById(R.id.no);

        yesDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri webpage = Uri.parse(currencyExchange.getWebsite());
                Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
                Intent chooser = Intent.createChooser(webIntent, "Select Your Browser");

// Verify the intent will resolve to at least one activity
                if (webIntent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(chooser);
                }
                dialog.dismiss();
            }
        });

        noDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }
}
