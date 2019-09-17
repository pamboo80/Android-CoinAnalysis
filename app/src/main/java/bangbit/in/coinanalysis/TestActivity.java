package bangbit.in.coinanalysis;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import bangbit.in.coinanalysis.Chat.ChatData;
import bangbit.in.coinanalysis.Chat.ChatDetail.ChatDetail;
import bangbit.in.coinanalysis.Chat.ChatDetail.ChatDetailChartFragment;
import bangbit.in.coinanalysis.Chat.ChatDetail.ChatDetailHistoricalFragment;
import bangbit.in.coinanalysis.Chat.ChatDetail.ChatDetailWhereToBuyFragment;
import bangbit.in.coinanalysis.ChatModule.ChatFragment.ChatFragment;

import static bangbit.in.coinanalysis.Constant.CHART;
import static bangbit.in.coinanalysis.Constant.HISTORY;
import static bangbit.in.coinanalysis.Constant.WHERE_TO_BUY;

public class TestActivity extends AppCompatActivity implements ChatFragment.OnChatFragmentInteractionListener, ChatDetailChartFragment.ChatFragmentListner{

    private String TAG = TestActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ChatFragment chatFragment=ChatFragment.newInstance();
        loadFragment(chatFragment);


    }

    private void loadFragment(Fragment fragment,String backStackName) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, fragment);
            fragmentTransaction.addToBackStack(backStackName);
            fragmentTransaction.commit();

    }

    private void loadFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.commit();

    }

    @Override
    public void showDetail(ChatData chatData, int type) {

        switch (type) {
            case CHART:
                loadFragment(ChatDetailChartFragment.newInstance(chatData),"chart");
                break;
            case HISTORY:
                loadFragment(ChatDetailHistoricalFragment.newInstance(chatData),"history");
                break;
            case WHERE_TO_BUY:
                loadFragment(ChatDetailWhereToBuyFragment.newInstance(chatData),"where to buy");
                break;
            default:
                loadFragment(ChatDetail.newInstance(chatData, type),"detail");
        }

    }


    @Override
    public void onOrientationChange(boolean isLandscape) {

    }
}
