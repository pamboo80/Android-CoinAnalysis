package bangbit.in.coinanalysis.EditAddInvestmentActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import bangbit.in.coinanalysis.BaseActivity;
import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.pojo.Coin;
import bangbit.in.coinanalysis.pojo.Investment;

import static bangbit.in.coinanalysis.Constant.ADD_FRAGMENT;
import static bangbit.in.coinanalysis.Constant.COIN;
import static bangbit.in.coinanalysis.Constant.COIN_IMAGE_URL;
import static bangbit.in.coinanalysis.Constant.EDIT_FRAGMENT;
import static bangbit.in.coinanalysis.Constant.INVESTMENT;
import static bangbit.in.coinanalysis.Constant.LOAD_FRAGMENT;

public class EditAddInvestmentActivity extends BaseActivity {

    String TAG=EditAddInvestmentActivity.class.getSimpleName();
    private Coin coin;
    private String imageUrl = "";
    private Investment investment;
    private ImageView saveImageView;
    private ImageView coinImageView;
    private ImageView backImageView;
    private ImageView deleteImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_investment);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            saveImageView = findViewById(R.id.save_icon);
            coinImageView = findViewById(R.id.coin_icon);
            backImageView = findViewById(R.id.back_imageView);
            deleteImageView = findViewById(R.id.delete_icon);
            coin = bundle.getParcelable(COIN);
            imageUrl = bundle.getString(COIN_IMAGE_URL);
            Log.d(TAG, "onCreate: "+imageUrl);
            final TextView titleTextView=findViewById(R.id.coin_name);

            if (bundle.getInt(LOAD_FRAGMENT) == EDIT_FRAGMENT) {
                investment = bundle.getParcelable(INVESTMENT);
                deleteImageView.setVisibility(View.VISIBLE);

                final EditInvestmentFragment editInvestmentFragment=EditInvestmentFragment.newInstance(coin, imageUrl, investment);
                loadFragment(editInvestmentFragment);
                saveImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editInvestmentFragment.save()){
                            finish();
                        }
                    }
                });
                titleTextView.setText("Edit Investment");
                deleteImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final Dialog dialog = new Dialog(EditAddInvestmentActivity.this);
                        dialog.setContentView(R.layout.delete_dialog);
                        dialog.setTitle("CoinAnalysis");

                        Button yesDialogButton = dialog.findViewById(R.id.yes);
                        Button noDialogButton = dialog.findViewById(R.id.no);

                        yesDialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                editInvestmentFragment.delete(investment.getId());
                                finish();
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
                });
            } else if (bundle.getInt(LOAD_FRAGMENT) == ADD_FRAGMENT) {
                deleteImageView.setVisibility(View.GONE);
                final AddInvestmentFragment addInvestmentFragment=AddInvestmentFragment.newInstance(coin, imageUrl);
                loadFragment(addInvestmentFragment);
                titleTextView.setText("Add Investment");
                saveImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(addInvestmentFragment.save()){
                            finish();
                        }
                    }
                });
            }
            Picasso.with(this).load(imageUrl).placeholder(R.drawable.default_coin).into(coinImageView);
            backImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

    }

    void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, fragment);
        fragmentTransaction.setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }

}
