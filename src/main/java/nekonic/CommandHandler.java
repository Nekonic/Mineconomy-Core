package nekonic;

import nekonic.DB.DatabaseManager;
import nekonic.DB.PlayerStock;
import nekonic.DB.Stock;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandHandler implements CommandExecutor, TabCompleter {

    private final DatabaseManager db = Mineconomy.getInstance().getDatabaseManager();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("mark")) {
            // 자본 조회 명령어: /mark player <name> 또는 /mark corporation <name>
            if (args.length == 2) {
                String entityType = args[0];  // player 또는 corporation
                String name = args[1];        // 플레이어 이름 또는 기업 이름

                double balance = db.getBalance(entityType, name);
                if (entityType.equalsIgnoreCase("player")) {
                    sender.sendMessage(ChatColor.GREEN + name + "님의 자본: " + balance + " Mark");
                } else if (entityType.equalsIgnoreCase("corporation")) {
                    sender.sendMessage(ChatColor.GREEN + name + "의 자본: " + balance + " Mark");
                } else {
                    sender.sendMessage(ChatColor.RED + "잘못된 엔터티 유형입니다. player 또는 corporation을 사용하세요.");
                }
                return true;
            }

            // 자본 추가 또는 제거: /mark player|corporation <name> add|remove <amount>
            if (args.length == 4) {
                String entityType = args[0]; // player 또는 corporation
                String name = args[1];       // 플레이어 또는 기업 이름
                String action = args[2];     // add 또는 remove
                double amount;

                try {
                    amount = Double.parseDouble(args[3]); // 추가 또는 제거할 금액
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "금액은 숫자여야 합니다.");
                    return false;
                }

                // 자본 처리 로직
                if (entityType.equalsIgnoreCase("player") || entityType.equalsIgnoreCase("corporation")) {
                    if (action.equalsIgnoreCase("add")) {
                        db.updateBalance(entityType, name, amount); // 자본 추가
                        sender.sendMessage(ChatColor.GREEN + name + "의 자본에 " + amount + " Mark가 추가되었습니다.");
                    } else if (action.equalsIgnoreCase("remove")) {
                        db.updateBalance(entityType, name, -amount); // 자본 제거
                        sender.sendMessage(ChatColor.GREEN + name + "의 자본에서 " + amount + " Mark가 제거되었습니다.");
                    } else {
                        sender.sendMessage(ChatColor.RED + "사용법: /mark player|corporation <name> add|remove <amount>");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "사용법: /mark player|corporation <name> add|remove <amount>");
                }
                return true;
            }

            // 사용법 안내
            sender.sendMessage(ChatColor.RED + "사용법: /mark player|corporation <name> [add|remove] <amount>");
            return false;
        }

        // stock
        if (command.getName().equalsIgnoreCase("stock")) {
            // 플레이어가 소유한 주식 목록 보기
            if (args.length == 0) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    List<PlayerStock> playerStocks = db.getPlayerAllStocks(player.getUniqueId().toString());

                    if (playerStocks.isEmpty()) {
                        sender.sendMessage(ChatColor.RED + "보유한 주식이 없습니다.");
                    } else {
                        sender.sendMessage(ChatColor.GREEN + player.getName() + "님의 주식 보유 목록:");
                        for (PlayerStock playerStock : playerStocks) {
                            sender.sendMessage("기업: " + playerStock.getCorporation() + ", 수량: " + playerStock.getQuantity() + "주, 평균 매입가: " + playerStock.getAvgPrice() + " Mark");
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "콘솔에서는 사용할 수 없습니다.");
                }
                return true;
            }

            // 특정 기업의 주식 정보 보기
            if (args.length == 1) {
                String corporation = args[0];
                List<Stock> stocks = db.getCorporationStocks(corporation);

                if (stocks.isEmpty()) {
                    sender.sendMessage(ChatColor.RED + corporation + "의 주식이 존재하지 않습니다.");
                } else {
                    int totalQuantity = 0;
                    for (Stock stock : stocks) {
                        totalQuantity += stock.getQuantity();
                    }
                    sender.sendMessage(ChatColor.GREEN + corporation + "의 주식 정보:");
                    sender.sendMessage("총 발행량: " + totalQuantity + "주");
                    for (Stock stock : stocks) {
                        sender.sendMessage("가격: " + stock.getPrice() + " Mark, 수량: " + stock.getQuantity() + "주");
                    }
                }
                return true;
            }

            // 주식 구매
            if (args.length == 3 && args[1].equalsIgnoreCase("buy")) {
                String corporation = args[0];
                int amount;
                try {
                    amount = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "수량은 숫자여야 합니다.");
                    return false;
                }

                // 주식 구매 처리
                db.buyStock(sender.getName(), corporation, amount);
                sender.sendMessage(ChatColor.GREEN + String.valueOf(amount) + "주를 구매했습니다.");
                return true;
            }

            // 주식 판매
            if (args.length == 4 && args[1].equalsIgnoreCase("sell")) {
                String corporation = args[0];
                int amount;
                double price;
                try {
                    amount = Integer.parseInt(args[2]);
                    price = Double.parseDouble(args[3]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "수량과 가격은 숫자여야 합니다.");
                    return false;
                }

                // 주식 판매 처리
                db.sellStock(sender.getName(), corporation, amount, price);
                sender.sendMessage(ChatColor.GREEN + String.valueOf(amount) + "주를 " + price + " Mark에 판매 등록했습니다.");
                return true;
            }

            // 주식 발행
            if (args.length == 4 && args[1].equalsIgnoreCase("listed")) {
                String corporation = args[0];
                int amount;
                double price;
                try {
                    amount = Integer.parseInt(args[2]);
                    price = Double.parseDouble(args[3]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "수량과 가격은 숫자여야 합니다.");
                    return false;
                }

                // 주식 발행 처리
                db.issueStock(corporation, amount, price);
                sender.sendMessage(ChatColor.GREEN + String.valueOf(amount) + "주를 " + price + " Mark에 발행했습니다.");
                return true;
            }

            // 상장 폐지
            if (args.length == 2 && args[1].equalsIgnoreCase("delist")) {
                String corporation = args[0];

                // 상장폐지 처리
                db.delistCorporation(corporation);
                sender.sendMessage(ChatColor.GREEN + corporation + "의 주식이 상장폐지되었습니다.");
                return true;
            }

            // 잘못된 명령어 사용
            sender.sendMessage(ChatColor.RED + "사용법: /stock <corporation name> buy <amount>");
            sender.sendMessage(ChatColor.RED + "사용법: /stock <corporation name> sell <amount> <price>");
            sender.sendMessage(ChatColor.RED + "사용법: /stock <corporation name> listed <amount> <price>");
            sender.sendMessage(ChatColor.RED + "사용법: /stock <corporation name> delist");
            return true;
        }

        return false;
    }



    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (command.getName().equalsIgnoreCase("mark")) {
            if (args.length == 1) {
                completions.addAll(Arrays.asList("player", "corporation")); // player 또는 corporation 선택
            } else if (args.length == 2 && args[0].equalsIgnoreCase("corporation")) {
                completions.addAll(db.getCorporationNames()); // 기업 목록 자동완성
            } else if (args.length == 2 && args[0].equalsIgnoreCase("player")) {
                completions.addAll(db.getPlayerNames()); // 플레이어 목록 자동완성
            } else if (args.length == 3) {
                completions.addAll(Arrays.asList("add", "remove"));
            }
        }


        // stock
        if (command.getName().equalsIgnoreCase("stock")) {
            if (args.length == 1) {
                // 기업 이름 자동완성
                completions.addAll(db.getCorporationNames());
            } else if (args.length == 2) {
                completions.addAll(List.of("buy", "sell", "listed", "delist"));
            }
        }


        return completions;
    }
}
