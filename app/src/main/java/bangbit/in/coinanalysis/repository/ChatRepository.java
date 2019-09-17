package bangbit.in.coinanalysis.repository;

import android.content.Context;

import java.util.List;

import bangbit.in.coinanalysis.Chat.ChatData;
import bangbit.in.coinanalysis.database.DatabaseHelper;

public class ChatRepository {

    private final DatabaseHelper databaseHelper;

    public ChatRepository(Context  context){
        databaseHelper = new DatabaseHelper(context);
    }

    public List<ChatData> getChatHistory(long id) {
        return databaseHelper.getChatHistory(id);
    }

    public void insertOneChatData(String date, String time, String chatData) {
        databaseHelper.insertOneChatData(date,time,chatData);
    }
}
