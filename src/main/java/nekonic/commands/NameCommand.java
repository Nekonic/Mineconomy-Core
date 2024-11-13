package nekonic.commands;

import nekonic.managers.DatabaseManager;
import org.bukkit.Bukkit;
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
        if (args.length < 3) {
            sender.sendMessage("Usage: /name <player|company|bank> <entity_name> <ID>");
            return false;
        }

        String type = args[0].toUpperCase();  // ID 유형 (PLAYER, COMPANY, BANK)
        String entityName = args[1];
        String nameId = args[2];

        // ID 중복 체크
        if (databaseManager.isNameIdDuplicate(nameId)) {
            sender.sendMessage("이미 사용 중인 ID입니다. 다른 ID를 선택하세요.");
            return true;
        }

        String uuid = null;
        if (type.equals("PLAYER")) {
            Player player = Bukkit.getPlayer(entityName);
            if (player == null) {
                sender.sendMessage("플레이어 " + entityName + "을(를) 찾을 수 없습니다. 온라인 상태인지 확인하세요.");
                return true;
            }
            uuid = player.getUniqueId().toString();

            // UUID 중복 체크
            if (databaseManager.isUUIDDuplicate(uuid)) {
                sender.sendMessage("해당 플레이어는 이미 등록되어 있습니다.");
                return true;
            }
        } else if (!type.equals("COMPANY") && !type.equals("BANK")) {
            sender.sendMessage("유효하지 않은 타입입니다. 'player', 'company', 'bank' 중 하나를 입력하세요.");
            return true;
        }

        // 데이터베이스에 ID, 타입, UUID 설정
        boolean success = databaseManager.addUser(nameId, type, uuid);
        if (success) {
            sender.sendMessage("ID가 성공적으로 설정되었습니다: " + nameId + " (Type: " + type + ")");
        } else {
            sender.sendMessage("ID 설정에 실패했습니다. 다시 시도하세요.");
        }

        return true;
    }
}
