package nekonic.commands;

import nekonic.managers.EconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MarkCommand implements CommandExecutor {
    private final EconomyManager economyManager;

    public MarkCommand(EconomyManager economyManager) {
        this.economyManager = economyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        // 잔액 확인 명령어
        if (args.length == 1 && args[0].equalsIgnoreCase("balance")) {
            double balance = economyManager.getBalance(player);
            player.sendMessage(ChatColor.GREEN + "Your current balance is: " + ChatColor.GOLD + balance + " Mark");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("deposit")) {
            economyManager.deposit(player);
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("withdraw")) {
            try {
                double amount = Double.parseDouble(args[1]);
                if (amount <= 0) {
                    player.sendMessage(ChatColor.RED + "Please enter a positive amount to withdraw.");
                    return true;
                }
                economyManager.withdraw(player, amount);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Invalid amount. Please enter a valid number.");
            }
            return true;
        }

        // 관리자가 사용할 수 있는 setbalance 명령어
        if (args.length == 3 && args[0].equalsIgnoreCase("setbalance") && player.isOp()) {
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer == null) {
                player.sendMessage(ChatColor.RED + "The specified player is not online.");
                return true;
            }

            try {
                double amount = Double.parseDouble(args[2]);
                economyManager.setBalance(targetPlayer, amount);
                player.sendMessage(ChatColor.GREEN + "Set " + targetPlayer.getName() + "'s balance to " + amount + " Mark");
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Invalid amount. Please enter a valid number.");
            }
            return true;
        }

        player.sendMessage(ChatColor.RED + "Usage: /mark balance or /mark setbalance <player> <amount>");
        return false;
    }
}
