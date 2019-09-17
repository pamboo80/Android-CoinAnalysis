package bangbit.in.coinanalysis.ChatModule.ChatFragment;

import java.util.ArrayList;
import java.util.List;

import bangbit.in.coinanalysis.Chat.ChatData;
import bangbit.in.coinanalysis.ChatModule.HolderView;
import bangbit.in.coinanalysis.pojo.Exchange;
import bangbit.in.coinanalysis.pojo.LambdaAnalysis;
import bangbit.in.coinanalysis.pojo.LambdaResponse;

public interface ChatFragmentContract {

    interface View extends HolderView{

        void messageInserted();

        void addToDatabase(ChatData chatData);

        void loadPreviousData(List<ChatData> loadFromdb);
        void scrollToPosition(int position);
        void addServerNotResponding();
        void removeServerNotResponding();
        void addReceiverMessage(String message);
        void addExchangeMessage(String message);
        void addExchangeData(ArrayList<Exchange> exchanges, String coinName,String currency);
        void addAnalysisData(LambdaResponse lambdaResponse, String json);

        void addReceiverMessageWithSugession(String s, ArrayList<String> sugessionArrayList);

        void addReceiverMessage(LambdaResponse lambdaResponse, String json);

        void showChatBotHelp();
    }

    interface UserActionsListener {

        void scrollToPosition(int position);
        void getQuestionAnswer(String response);

        void loadInitialChatHistory();
        void loadMoreData(ChatData lastChatData, String time);
        void getExchangeData(String coinName,String currency);

        void insertOneChatData(String date, String date1, String chat);
    }
}
