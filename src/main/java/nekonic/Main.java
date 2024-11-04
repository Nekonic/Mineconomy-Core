package nekonic;

import nekonic.commands.MarkCommand;
import nekonic.managers.DatabaseManager;
import nekonic.managers.EconomyManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private DatabaseManager databaseManager;
    private EconomyManager economyManager;

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        // 데이터베이스와 EconomyManager 초기화
        databaseManager = new DatabaseManager(this);
        economyManager = new EconomyManager(databaseManager);

        // 명령어 등록
        getCommand("mark").setExecutor(new MarkCommand(economyManager));

        getLogger().info("Mineconomy-Core enabled!");
    }

    @Override
    public void onDisable() {
        // 데이터베이스 연결 해제
        databaseManager.closeConnection();
        getLogger().info("Mineconomy-Core disabled!");
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }
}
