package bangbit.in.coinanalysis.ChatModule.ChatFragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


import ai.api.AIDataService;
import ai.api.android.AIConfiguration;


import ai.api.android.AIService;
import bangbit.in.coinanalysis.BaseActivity;
import bangbit.in.coinanalysis.Chat.ChatData;
import bangbit.in.coinanalysis.Chat.EndlessRecyclerOnScrollListener;
import bangbit.in.coinanalysis.ChatModule.ChatActivity;
import bangbit.in.coinanalysis.ChatModule.ChatAdapter;
import bangbit.in.coinanalysis.ChildNetworkAvailable;
import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.Util;
import bangbit.in.coinanalysis.pojo.Exchange;
import bangbit.in.coinanalysis.pojo.LambdaResponse;
import bangbit.in.coinanalysis.repository.ChatRepository;

import static bangbit.in.coinanalysis.Constant.EXCHANGE_SUGESSION;


public class ChatFragment extends Fragment implements ChatFragmentContract.View,ChildNetworkAvailable {

    private static final String LIST_STATE_KEY = "recycler_list_state";
    Parcelable listState;
    private OnChatFragmentInteractionListener mListener;
    private final String TAG = ChatActivity.class.getSimpleName();

    ChatRepository chatRepository;
    private AIConfiguration config;
    private AIService aiService;

    private AIDataService aiDataService;
    private ChatAdapter chatAdapter;
    private RecyclerView recyclerView;
    private ChatFragmentPresenter chatFragmentPresenter;

    private EditText chatEditText;
    private LinearLayout chatboxLinearLayout;
    private LinearLayoutManager linearLayoutManager;
    private boolean isNetworkAvailable=false;
    private Toast toast;
    private BaseActivity baseActivity;
    private Snackbar snackbar;

    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        setupSnackbar(view);
        chatboxLinearLayout = view.findViewById(R.id.layout_chatbox);
        recyclerView = view.findViewById(R.id.reyclerview_message_list);

        chatEditText = view.findViewById(R.id.edittext_chatbox);
        ImageButton sendButton = view.findViewById(R.id.button_chatbox_send);
        final TextView timeTextView=(TextView)view.findViewById(R.id.time_date_textView);

        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                ChatData lastChatData = chatAdapter.getLastItem();
                chatFragmentPresenter.loadMoreData(lastChatData, chatAdapter.getTime());
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int currentFirstVisible = linearLayoutManager.findFirstVisibleItemPosition();
                ChatData chatData=chatAdapter.getChatData(currentFirstVisible);
                if(chatData !=null && chatData.getDate()!=null && getContext()!=null && getContext().getApplicationContext()!=null){
                    timeTextView.setText(Util.parseDate(chatData.getDate()));

                    if (timeTextView.getVisibility()==View.GONE) {

                        try
                        {
                            timeTextView.setVisibility(View.VISIBLE);
                            final Animation slide_up = AnimationUtils.loadAnimation(getContext().getApplicationContext(),
                                    R.anim.slid_up);
                            slide_up.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    timeTextView.setVisibility(View.GONE);
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {
                                }
                            });
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    timeTextView.startAnimation(slide_up);
                                }
                            }, 2000);
                            Log.d(TAG, "onScrolled: ");
                        }
                        catch(NullPointerException ex)
                        {
                            Log.d(TAG, "onScrolled: exception ");
                        }
                    }
                }
            }
        });

        chatEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linearLayoutManager.findFirstVisibleItemPosition() == 0) {
                    recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            chatFragmentPresenter.scrollToPosition(0);
                        }
                    }, 500);
                } else {
                    Log.d(TAG, "onClick: " + linearLayoutManager.findFirstVisibleItemPosition());
                }
            }
        });
        chatEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNetworkAvailable){
                    displayCheckInternetToast();
                    return;
                }
                if (chatAdapter == null) {
                    return;
                }
                String response = chatEditText.getText().toString().trim();
                if (!response.isEmpty()) {
                    chatAdapter.addSenderMessage(response);
                    chatFragmentPresenter.getQuestionAnswer(response);
                    chatEditText.setText("");
                }
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnChatFragmentInteractionListener) {
            mListener = (OnChatFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        if (getActivity() instanceof BaseActivity) {
            baseActivity = (BaseActivity) getActivity();
            baseActivity.addChildReceiverListner(this);
        }
        config = new AIConfiguration("660c740c285f4b259538539dedda07c7",
//        config = new AIConfiguration("59a6ee5600ae4d9782680f4a9f4b5f80",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(getContext(), config);
        aiDataService = new AIDataService(config);
        chatRepository = new ChatRepository(getContext());
        chatFragmentPresenter = new ChatFragmentPresenter(this, context, chatRepository, aiService, aiDataService);
    }

    @Override
    public void onPause() {
        super.onPause();
        listState=linearLayoutManager.onSaveInstanceState();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listState != null) {
            recyclerView.setAdapter(chatAdapter);
            chatAdapter.notifyDataSetChanged();
            linearLayoutManager.onRestoreInstanceState(listState);
        }else {
            chatAdapter = new ChatAdapter(this, chatFragmentPresenter.getChatData());
            recyclerView.setAdapter(chatAdapter);
            chatFragmentPresenter.scrollToPosition(0);
            chatFragmentPresenter.loadInitialChatHistory();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).removeChildReceiverListner(this);
        }
        mListener = null;
    }

    @Override
    public void addExchangeMessage(String message) {
        chatAdapter.addExchangeMessage(message);
    }

    @Override
    public void addReceiverMessage(String message) {
        chatAdapter.addReceiverMessage(message);
    }

    @Override
    public void addReceiverMessage(LambdaResponse lambdaResponse, String json) {
        chatAdapter.addReceiverMessage(lambdaResponse, json);
    }

    @Override
    public void addAnalysisData(LambdaResponse lambdaResponse, String json) {
        chatAdapter.addAnalysisData(lambdaResponse, json);
    }

    @Override
    public void showChatBotHelp() {
        chatAdapter.showChatBotHelp();
    }

    @Override
    public void addReceiverMessageWithSugession(String message, ArrayList<String> sugessionArrayList) {
        chatAdapter.addReceiverMessageWithSugession(message, sugessionArrayList);
    }

    @Override
    public void scrollToPosition(int position) {
        recyclerView.smoothScrollToPosition(position);
    }

    @Override
    public void removeServerNotResponding()
    {
        if (snackbar !=null){
            snackbar.dismiss();
        }
    }

    @Override
    public void addServerNotResponding() {
        if (snackbar!=null){
            snackbar.show();
            chatAdapter.removeTypingView();
        }
    }

    @Override
    public void messageInserted() {
        chatFragmentPresenter.scrollToPosition(0);
    }

    @Override
    public void loadPreviousData(List<ChatData> loadFromdb) {
        chatAdapter.addNewDataToTop(loadFromdb);
    }

    @Override
    public void addExchangeData(ArrayList<Exchange> exchanges, String coinName, String currency) {
        chatAdapter.addExchangeData(exchanges, coinName,currency);
    }

    private void displayCheckInternetToast(){
        if (baseActivity!=null){
            baseActivity.showSnackbar();
        }
    }

    @Override
    public void addMessageBySender(String message, int type) {
        if (!isNetworkAvailable){
            displayCheckInternetToast();
            return;
        }
        if (type == EXCHANGE_SUGESSION) {
            String[] names = message.split("/");
            chatAdapter.addSenderMessage(message);
            chatFragmentPresenter.getExchangeData(names[0], names[1]);
        } else {
            chatAdapter.addSenderMessage(message);
            chatFragmentPresenter.getQuestionAnswer(message);
        }
    }
    
    @Override
    public void addToDatabase(ChatData chatData) {
        String date = chatData.getDate();
        String time = chatData.getTime();
        String chat = Util.getChatData(chatData);
        chatFragmentPresenter.insertOneChatData(date, time, chat);
        scrollToPosition(0);
    }

    @Override
    public void showDetail(ChatData chatData, int type) {
        mListener.showDetail(chatData, type);
    }

    @Override
    public void onChildNetworkAvailable() {
        isNetworkAvailable=true;
    }

    @Override
    public void onChildNetworkUnavailable() {
        isNetworkAvailable=false;
    }

    private void setupSnackbar(View view) {

        snackbar = Snackbar.make(view.findViewById(R.id.coordinatorLayout),
                    "Our chat server is not responding on time. Please try again.", Snackbar.LENGTH_INDEFINITE);

        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.setActionTextColor(Color.WHITE);

        View snackbarView = snackbar.getView();
        snackbarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (snackbar !=null){
                    snackbar.dismiss();
                }
            }
        });
        snackbarView.setBackgroundColor(getResources().getColor(R.color.red));
        TextView textView = snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
    }

    public interface OnChatFragmentInteractionListener {
        void showDetail(ChatData chatData, int type);
    }
}
