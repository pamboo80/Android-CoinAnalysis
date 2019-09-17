package bangbit.in.coinanalysis.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import bangbit.in.coinanalysis.R;

/**
 * Created by Nagarajan on 1/6/2018.
 */

public class PercentageCustomDialog {
    private DialogListner dialogListner;
    Dialog dialog;
    EditText editText;

    public void showDialog(final Context context, final String time, double value) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_percentage);
        dialogListner = (DialogListner) context;
//        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;


        editText = dialog.findViewById(R.id.editTextPercentage);
        editText.setText(String.valueOf((int) value));
        editText.setSelection(String.valueOf((int) value).length());

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            if (editText.getText().toString().trim().equals("")) {
                                editText.setError("Enter some value.");
                                editText.requestFocus();
                                return false;
                            }
                            if (Integer.parseInt(editText.getText().toString()) <= 9999 && Integer.parseInt(editText.getText().toString()) >= -9999) {
                                dialogListner.onDismissDialog(time, Integer.parseInt(editText.getText().toString()));
                                dialog.dismiss();
                            } else {
                                editText.setError("Value must be in between -9999 to 9999.");
                                editText.requestFocus();
                            }

                        default:
                            break;
                    }
                }
                return false;
            }
        });
        TextView textView = dialog.findViewById(R.id.textViewTitle);
        textView.setText("" + time + " Max %");

        Button dialogButton = dialog.findViewById(R.id.submit);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().trim().equals("")) {
                    editText.setError("Enter some value.");
                    return;
                }
                if (Integer.parseInt(editText.getText().toString()) <= 9999 && Integer.parseInt(editText.getText().toString()) >= -9999) {
                    dialogListner.onDismissDialog(time, Integer.parseInt(editText.getText().toString()));
                    dialog.dismiss();
                } else {
                    editText.setError("Value must be in between -9999 to 9999.");
                    editText.requestFocus();
                }
            }
        });

        dialog.setTitle("CoinAnalysis");
        dialog.show();
        editText.requestFocus();

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

}
