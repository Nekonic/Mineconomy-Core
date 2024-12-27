package nekonic.commands;

import nekonic.managers.DatabaseManager;
import nekonic.managers.EconomyManager;
import nekonic.utils.MessageUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MarkCommand implements CommandExecutor {
    private final EconomyManager economyManager;
    private final DatabaseManager databaseManager;


    public MarkCommand(EconomyManager economyManager, DatabaseManager databaseManager) {
        this.economyManager = economyManager;
        this.databaseManager = databaseManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Component.text(MessageUtils.getMessage("command.mark.usage"), NamedTextColor.RED));
            return false;
        }

        String nameId = args[0];

        // 1. 잔액 조회 명령어: /mark <ID> balance
        if (args[1].equalsIgnoreCase("balance")) {
            int balance = economyManager.getBalance(nameId);
            sender.sendMessage(Component.text(NamedTextColor.GREEN + nameId + " Your current balance is: " + NamedTextColor.GOLD + balance + " Mark"));
            return true;
        }

        // 2. 입금 명령어 (플레이어만 사용 가능): /mark <ID> deposit
        if (args[1].equalsIgnoreCase("deposit")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can use this command.");
                return true;
            }

            // ID가 플레이어인지 확인
            if (!databaseManager.isPlayer(nameId)) {
                sender.sendMessage(ChatColor.RED + "Deposit command is only available to players.");
                return true;
            }

            Player player = (Player) sender;
            economyManager.deposit(player);
            return true;
        }

        // 3. 출금 명령어 (플레이어만 사용 가능): /mark <ID> withdraw <amount>
        if (args[1].equalsIgnoreCase("withdraw") && args.length == 3) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can use this command.");
                return true;
            }
            try {
                int amount = Integer.parseInt(args[2]);
                economyManager.withdraw(nameId, amount, ((Player) sender).getPlayer());
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid amount. Please enter a valid number.");
            }
            return true;
        }

        // 4. 송금 명령어: /mark <ID> send <targetID> <amount> (플레이어만 사용 가능)
        if (args[1].equalsIgnoreCase("send") && args.length == 4) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can use this command.");
                return true;
            }

            Player player = (Player) sender;
            String playerUuid = player.getUniqueId().toString();

            // 송신자 ID가 플레이어이며, 명령어 입력자와 일치하는지 확인
            if (!databaseManager.isPlayer(nameId) || !playerUuid.equals(databaseManager.getPlayerUUID(nameId))) {
                sender.sendMessage(ChatColor.RED + "You can only send money from your own account.");
                return true;
            }

            String targetId = args[2];
            try {
                int amount = Integer.parseInt(args[3]);
                economyManager.sendMoney(nameId, targetId, amount);
                sender.sendMessage(ChatColor.GREEN + "Successfully sent " + amount + " Mark to " + targetId + ".");
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid amount. Please enter a valid number.");
            }
            return true;
        }

        // 5. 관리자 잔액 설정 명령어: /mark <ID> setbalance <amount> (관리자만 사용 가능)
        if (args[1].equalsIgnoreCase("setbalance") && args.length == 3 && sender.isOp()) {
            try {
                int amount = Integer.parseInt(args[2]);
                economyManager.setBalance(nameId, amount);
                sender.sendMessage(ChatColor.GREEN + "Set " + nameId + "'s balance to " + amount + " Mark");
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid amount. Please enter a valid number.");
            }
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Usage: /mark balance or /mark setbalance <player> <amount>");
        return false;
    }
}
