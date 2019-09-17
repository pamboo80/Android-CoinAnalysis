package bangbit.in.coinanalysis.Chat;

public interface MessageInsertListner {
    void messageInserted(boolean isInsetred);
    void addMessageBySender(String message);
    void showDetail(ChatData chatData, int type);
    void addToDatabase(ChatData chatData);
}
