package bangbit.in.coinanalysis.ChatModule;

import bangbit.in.coinanalysis.Chat.ChatData;

public interface HolderView {
    void showDetail(ChatData chatData, int type);
    void addMessageBySender(String sugession, int type);
}
