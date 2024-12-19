package nekonic.commands;

import nekonic.managers.DatabaseManager;
import org.bukkit.ChatColor;
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
            sender.sendMessage("Usage: /name <ID>");
            return false;
        }

        // player만 사용가능 명령어
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        String nameId = args[0];

        // ID 중복 체크
        if (databaseManager.isNameIdDuplicate(nameId)) {
            sender.sendMessage("이미 사용 중인 ID입니다. 다른 ID를 선택하세요.");
            return true;
        }

        Player player = (Player) sender;

        String uuid = player.getUniqueId().toString();

        // UUID 중복 체크
        if (databaseManager.isUUIDDuplicate(uuid)) {
            sender.sendMessage("당신은 이미 등록되어 있습니다.");
            return true;
        }

        // 데이터베이스에 ID, 타입, UUID 설정
        boolean success = databaseManager.addUser(nameId, uuid);
        if (success) {
            sender.sendMessage("ID가 성공적으로 설정되었습니다: " + nameId);
        } else {
            sender.sendMessage("ID 설정에 실패했습니다. 다시 시도하세요.");
        }

        return true;
    }
}
