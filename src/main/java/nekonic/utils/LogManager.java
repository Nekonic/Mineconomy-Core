package nekonic.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogManager {

    // 로그 파일 경로 설정 (logs/mineconomy.log)
    private static final String LOG_FILE_PATH = "logs/mineconomy.log";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 로그 메시지를 기록하는 메소드
    public static void log(String level, String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE_PATH, true))) {
            // 현재 시간과 로그 레벨 및 메시지를 기록
            String timestamp = LocalDateTime.now().format(formatter);
            writer.write("[" + timestamp + "] [" + level + "] " + message);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 정보 로그 기록
    public static void info(String message) {
        log("INFO", message);
    }

    // 경고 로그 기록
    public static void warning(String message) {
        log("WARNING", message);
    }

    // 오류 로그 기록
    public static void error(String message) {
        log("ERROR", message);
    }
}
