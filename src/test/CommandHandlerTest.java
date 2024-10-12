package tests;

import nekonic.CommandHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

// Mockito를 통해 CommandHandler와 의존성을 테스트
@ExtendWith(MockitoExtension.class)
public class CommandHandlerTest {

    @Mock
    CommandSender sender;  // Mock 객체로 CommandSender를 생성

    @InjectMocks
    CommandHandler commandHandler;  // CommandHandler에 필요한 의존성을 주입

    @BeforeEach
    public void setup() {
        // 테스트 전에 필요한 설정을 할 수 있는 공간
    }

    @Test
    public void testMarkCommand_withValidPlayer() {
        // 명령어 mock 객체 생성
        Command command = mock(Command.class);
        when(command.getName()).thenReturn("mark");  // "mark" 명령어를 사용할 경우

        // 명령어에 대한 매개변수 설정 (플레이어 자본 조회)
        String[] args = {"player", "testPlayer"};

        // 명령어 처리 실행
        commandHandler.onCommand(sender, command, "mark", args);

        // sender가 적절한 메시지를 받았는지 검증
        verify(sender).sendMessage(contains("testPlayer님의 자본"));
    }

    @Test
    public void testStockCommand_buyStock() {
        // 명령어 mock 객체 생성
        Command command = mock(Command.class);
        when(command.getName()).thenReturn("stock");  // "stock" 명령어를 사용할 경우

        // 명령어에 대한 매개변수 설정 (주식 구매)
        String[] args = {"testCorp", "buy", "10"};

        // 명령어 처리 실행
        commandHandler.onCommand(sender, command, "stock", args);

        // sender가 적절한 메시지를 받았는지 검증
        verify(sender).sendMessage(contains("10주를 구매했습니다."));
    }
}
