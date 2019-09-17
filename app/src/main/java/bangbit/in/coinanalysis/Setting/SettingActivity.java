package bangbit.in.coinanalysis.Setting;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import bangbit.in.coinanalysis.Dialogs.FiatDialog.CurrencySelector;
import bangbit.in.coinanalysis.MyApplication;
import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.Util;
import bangbit.in.coinanalysis.pojo.Currency;

public class SettingActivity extends AppCompatActivity implements CurrencySelector {


    private SettingFragment settingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ImageView backImageView=findViewById(R.id.back_imageView);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        settingFragment = new SettingFragment();
        loadFragment(settingFragment);
    }

    void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void selectcurrency(Currency currency) {
        Util.setCurrency(currency, this);
        settingFragment.setCurrency(currency);
        ((MyApplication) getApplicationContext()).setCurrency(currency);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}
