package nekonic.listeners;

import nekonic.managers.DatabaseManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.entity.Player;

public class ChatListener implements Listener {
    private final DatabaseManager databaseManager;

    public ChatListener(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String playerId = player.getUniqueId().toString();

        // DatabaseManager에서 플레이어의 name_id를 가져옴
        String nameId = databaseManager.getNameId(playerId);
        if (nameId != null) {
            event.setFormat(nameId + ": " + event.getMessage()); // ID로 표시
        } else {
            event.setFormat(player.getName() + ": " + event.getMessage()); // ID가 없는 경우 기본 이름 사용
        }
    }
}
