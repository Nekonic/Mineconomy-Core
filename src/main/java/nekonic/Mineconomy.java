package nekonic;

import nekonic.DB.DatabaseManager;
import nekonic.commands.CommandHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class Mineconomy extends JavaPlugin {

    private static Mineconomy instance;  // 싱글톤 인스턴스
    private DatabaseManager databaseManager;  // 데이터베이스 매니저

    @Override
    public void onEnable() {
        instance = this;

        // 플러그인 데이터 폴더 생성 (없으면 자동 생성)
        if (!getDataFolder().exists()) {
            boolean created = getDataFolder().mkdirs();
            if (created) {
                getLogger().info("플러그인 데이터 폴더가 생성되었습니다: " + getDataFolder().getPath());
            } else {
                getLogger().severe("플러그인 데이터 폴더를 생성하지 못했습니다!");
            }
        }

        // 데이터베이스 매니저 초기화 및 데이터베이스 설정
        databaseManager = new DatabaseManager();
        databaseManager.setupDatabase();

        // 명령어 등록
        getCommand("mark").setExecutor(new CommandHandler());
        getCommand("stock").setExecutor(new CommandHandler());

        getLogger().info("Mineconomy 플러그인이 활성화되었습니다!");
    }

    @Override
    public void onDisable() {
        // 데이터베이스 연결 종료
        databaseManager.closeConnection();
        getLogger().info("Mineconomy 플러그인이 비활성화되었습니다.");
    }

    // Mineconomy 싱글톤 인스턴스 반환
    public static Mineconomy getInstance() {
        return instance;
    }

    // DatabaseManager 반환
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}
