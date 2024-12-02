package nekonic;

import nekonic.commands.MarkCommand;
import nekonic.commands.NameCommand;
import nekonic.listeners.ChatListener;
import nekonic.managers.DatabaseManager;
import nekonic.managers.EconomyManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private DatabaseManager databaseManager;
    private EconomyManager economyManager;

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            try {
                this.getDataFolder().mkdir();
            } catch (Exception e) {
                getServer().getConsoleSender().sendMessage(
                        Component.text("Failed!", NamedTextColor.DARK_RED)
                );
            }
        }

        // 데이터베이스와 EconomyManager 초기화
        databaseManager = new DatabaseManager(this);
        economyManager = new EconomyManager(databaseManager);

        // 명령어 등록
        getCommand("mark").setExecutor(new MarkCommand(economyManager, databaseManager));
        getCommand("name").setExecutor(new NameCommand(databaseManager));

        //
        getServer().getPluginManager().registerEvents(new ChatListener(databaseManager), this);

        getServer().getConsoleSender().sendMessage(
                Component.text("Mineconomy-Core has been enabled!", NamedTextColor.DARK_AQUA)
        );
    }

    @Override
    public void onDisable() {
        // 데이터베이스 연결 해제
        databaseManager.closeConnection();
        getServer().getConsoleSender().sendMessage(
                Component.text("Mineconomy-Core has been disabled!", NamedTextColor.DARK_AQUA)
        );
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }
}
