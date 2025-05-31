package nekonic;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import static org.bukkit.Bukkit.getServer;

public class VaultHook {
    private static Economy econ = null;
    private static Permission perms = null;
    private static Chat chat = null;

    public void init(){
        setupEconomy();
        setupPermissions();
        setupChat();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if(!(sender instanceof Player)) {
            getServer().getConsoleSender().sendMessage(
                    Component.text("Only players are supported for this Example Plugin, but you should not do this!!!", NamedTextColor.RED)
            );
            return true;
        }

        Player player = (Player) sender;

        if(command.getLabel().equals("mark")) {
            // Lets give the player 1.05 currency (note that SOME economic plugins require rounding!)
            sender.sendMessage(
                    Component.text(String.format("You have %s", econ.format(econ.getBalance(player.getName()))),
                            NamedTextColor.GREEN)
            );
            EconomyResponse r = econ.depositPlayer(player, 1.05);
            if(r.transactionSuccess()) {
                sender.sendMessage(
                        Component.text(String.format("You were given %s and now have %s", econ.format(r.amount), econ.format(r.balance)),
                                NamedTextColor.GREEN)
                        );
            } else {
                sender.sendMessage(
                        Component.text(String.format("An error occured: %s", r.errorMessage),
                                NamedTextColor.RED)
                );
            }
            return true;
        } else if(command.getLabel().equals("test-permission")) {
            // Lets test if user has the node "example.plugin.awesome" to determine if they are awesome or just suck
            if(perms.has(player, "example.plugin.awesome")) {
                sender.sendMessage(
                        Component.text("You are awesome!", NamedTextColor.BLUE)
                );
            } else {
                sender.sendMessage(
                        Component.text("You suck!", NamedTextColor.RED)
                );
            }
            return true;
        } else {
            return false;
        }
    }


    public static Economy getEconomy() {
        return econ;
    }

    public static Permission getPermissions() {
        return perms;
    }

    public static Chat getChat() {
        return chat;
    }
}
