package bangbit.in.coinanalysis.ChatModule;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import bangbit.in.coinanalysis.BaseActivity;
import bangbit.in.coinanalysis.Chat.ChatData;
import bangbit.in.coinanalysis.Chat.ChatDetail.ChatDetail;
import bangbit.in.coinanalysis.Chat.ChatDetail.ChatDetailAnalysisFragment;
import bangbit.in.coinanalysis.Chat.ChatDetail.ChatDetailChartFragment;
import bangbit.in.coinanalysis.Chat.ChatDetail.ChatDetailHistoricalFragment;
import bangbit.in.coinanalysis.Chat.ChatDetail.ChatDetailWhereToBuyFragment;
import bangbit.in.coinanalysis.Chat.ChatDetail.ChatHelpFragment;
import bangbit.in.coinanalysis.ChatModule.ChatFragment.ChatFragment;
import bangbit.in.coinanalysis.ChildNetworkAvailable;
import bangbit.in.coinanalysis.R;

import static bangbit.in.coinanalysis.Constant.ANALYSIS;
import static bangbit.in.coinanalysis.Constant.CHART;
import static bangbit.in.coinanalysis.Constant.COIN_YEARLY_PRICE_TREND;
import static bangbit.in.coinanalysis.Constant.HISTORY;
import static bangbit.in.coinanalysis.Constant.WHERE_TO_BUY;

public class ChatActivity extends BaseActivity implements ChatFragment.OnChatFragmentInteractionListener, ChatDetailChartFragment.ChatFragmentListner{

    private final String TAG = ChatActivity.class.getSimpleName();
    private ImageView backImageView;

    private ImageView helpImageView;
    public TextView titleTextView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTimeToDisplay(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toolbar = findViewById(R.id.main_toolbar);
        titleTextView = findViewById(R.id.title);
        backImageView = findViewById(R.id.back_icon);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        helpImageView = findViewById(R.id.help_icon);
        helpImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new ChatHelpFragment(),"help");
                titleTextView.setText("ChatBot Help");
            }
        });
        ChatFragment chatFragment=ChatFragment.newInstance();
        loadFragment(chatFragment);
    }

    @Override
    public void showDetail(ChatData chatData, int type) {
        switch (type) {
            case CHART:
                loadFragment(ChatDetailChartFragment.newInstance(chatData),"chatChartDetail");
                break;
            case HISTORY:
                loadFragment(ChatDetailHistoricalFragment.newInstance(chatData),"chatHistory");
                break;
            case WHERE_TO_BUY:
                loadFragment(ChatDetailWhereToBuyFragment.newInstance(chatData),"chatWhereToBuy");
                break;
            case COIN_YEARLY_PRICE_TREND:
                loadFragment(ChatDetail.newInstance(chatData, type),"chatDetail");
                break;
            case ANALYSIS:
                loadFragment(ChatDetailAnalysisFragment.newInstance(chatData),"chatAnalysis");
                break;
            default:
                loadFragment(ChatDetail.newInstance(chatData, type),"chatDetail");
        }
        titleTextView.setText("Message");
    }

    public void loadFragment(Fragment fragment,String backStackName) {
        hideKeyboard();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.addToBackStack(backStackName);
        fragmentTransaction.commit();
        showMainContent(false);
    }

    private void hideKeyboard() {
        InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getCurrentFocus()!=null){
            keyboard.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.commit();
    }

    void showMainContent(boolean isDisplay) {
        if (isDisplay) {
            helpImageView.setVisibility(View.VISIBLE);
            titleTextView.setText("ChatBot");
        } else {
            helpImageView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        showMainContent(true);
    }

    public void addChildReceiverListner(ChildNetworkAvailable childNetworkAvailable) {
        super.addChildReceiverListner(childNetworkAvailable);
    }

    public void removeChildReceiverListner(ChildNetworkAvailable childNetworkAvailable) {
        super.removeChildReceiverListner(childNetworkAvailable);
    }

    @Override
    public void onOrientationChange(boolean isLandscape) {
        if (isLandscape){
            toolbar.setVisibility(View.GONE);
        }else {
            toolbar.setVisibility(View.VISIBLE);
        }
    }
}
