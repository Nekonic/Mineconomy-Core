package nekonic.managers;

import nekonic.utils.CurrencyItem;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EconomyManager {
    private final DatabaseManager databaseManager;
    private static final double COIN_VALUE = 100.0; // Gold Coin 1개당 100 Mark

    public EconomyManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public double getBalance(Player player) {
        return databaseManager.getBalance(player.getUniqueId().toString());
    }

    public void setBalance(Player player, double amount) {
        databaseManager.updateBalance(player.getUniqueId().toString(), amount);
    }

    public void deposit(Player player) {
        int totalCoins = 0;

        // 인벤토리에서 Gold Coin 감지
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.isSimilar(CurrencyItem.createCurrencyItem(1))) {
                totalCoins += item.getAmount();
                player.getInventory().remove(item);
            }
        }

        if (totalCoins > 0) {
            // 총 Gold Coin 가치 계산
            double totalDeposit = totalCoins * COIN_VALUE;

            // 플레이어 잔액 업데이트
            double newBalance = getBalance(player) + totalDeposit;
            setBalance(player, newBalance);

            // 플레이어에게 알림 메시지
            player.sendMessage(ChatColor.GREEN + "Deposited " + ChatColor.GOLD + totalCoins + " Gold Coins"
                    + ChatColor.GREEN + " (worth " + totalDeposit + " Mark) into your account.");
        } else {
            player.sendMessage(ChatColor.RED + "You have no Gold Coins to deposit.");
        }
    }

    // 출금 기능 구현
    public void withdraw(Player player, double amount) {
        double currentBalance = getBalance(player);

        // 요청한 출금 금액이 잔액보다 많으면 출금 불가
        if (amount > currentBalance) {
            player.sendMessage(ChatColor.RED + "Insufficient balance. Your current balance is " + currentBalance + " Mark.");
            return;
        }

        // 출금할 Gold Coin 개수 계산
        int coinCount = (int) (amount / COIN_VALUE);

        // 출금할 수 있는 최소 단위인지 확인 (1 Gold Coin 이상이어야 함)
        if (coinCount <= 0) {
            player.sendMessage(ChatColor.RED + "Please withdraw at least " + COIN_VALUE + " Mark (1 Gold Coin).");
            return;
        }

        double totalWithdraw = coinCount * COIN_VALUE;

        // 새로운 잔액 계산 및 데이터베이스에 반영
        setBalance(player, currentBalance - totalWithdraw);

        // 출금할 Gold Coin 아이템 생성 및 인벤토리에 추가
        ItemStack coins = CurrencyItem.createCurrencyItem(coinCount);
        player.getInventory().addItem(coins);

        player.sendMessage(ChatColor.GREEN + "Withdrew " + ChatColor.GOLD + coinCount + " Gold Coins"
                + ChatColor.GREEN + " (worth " + totalWithdraw + " Mark) from your account.");
    }

    // 송금 기능 구현
    public void sendMoney(Player sender, Player receiver, double amount) {
        double senderBalance = getBalance(sender);

        // 송금 가능 여부 확인
        if (amount > senderBalance) {
            sender.sendMessage(ChatColor.RED + "Insufficient balance. You have " + senderBalance + " Mark available.");
            return;
        }

        // 송금자의 잔액 차감 및 수신자 잔액 증가
        setBalance(sender, senderBalance - amount);
        setBalance(receiver, getBalance(receiver) + amount);

        // 송금 및 수신 확인 메시지
        sender.sendMessage(ChatColor.GREEN + "Successfully sent " + ChatColor.GOLD + amount + " Mark"
                + ChatColor.GREEN + " to " + receiver.getName() + ".");
        receiver.sendMessage(ChatColor.GREEN + "You have received " + ChatColor.GOLD + amount + " Mark"
                + ChatColor.GREEN + " from " + sender.getName() + ".");
    }
}