package nekonic.commands;

import nekonic.managers.DatabaseManager;
import nekonic.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NameCommand implements CommandExecutor {

    private final DatabaseManager databaseManager;

    public NameCommand(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // 명령어 입력 확인
        if (args.length < 1) {
            sender.sendMessage(MessageUtils.getMessage("command.name.usage"));
            return false;
        }

        // player만 사용가능 명령어
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtils.getMessage("command.only_player"));
            return true;
        }

        String nameId = args[0];

        // ID 중복 체크
        if (databaseManager.isNameIdDuplicate(nameId)) {
            sender.sendMessage(MessageUtils.getMessage("command.name.id.error"));
            return true;
        }

        Player player = (Player) sender;

        String uuid = player.getUniqueId().toString();

        // UUID 중복 체크
        if (databaseManager.isUUIDDuplicate(uuid)) {
            sender.sendMessage(MessageUtils.getMessage("command.name.uuid.error"));
            return true;
        }

        // 데이터베이스에 ID, 타입, UUID 설정
        boolean success = databaseManager.addUser(nameId, uuid);
        if (success) {
            sender.sendMessage(MessageUtils.getMessage("command.name.success") + nameId);
        } else {
            sender.sendMessage(MessageUtils.getMessage("command.name.setup.error"));
        }

        return true;
    }
}
