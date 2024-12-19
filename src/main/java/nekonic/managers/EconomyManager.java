package nekonic.managers;

import nekonic.utils.CurrencyItem;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EconomyManager {
    private final DatabaseManager databaseManager;
    private static final int COIN_VALUE = 100; // Gold Coin 1개당 100 Mark

    public EconomyManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public int getBalance(String nameId) {
        return databaseManager.getBalance(nameId);
    }

    public void setBalance(String nameId, int amount) {
        databaseManager.updateBalance(nameId, amount);
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
            int totalDeposit = totalCoins * COIN_VALUE;
            String playerId = player.getUniqueId().toString();
            int newBalance = getBalance(playerId) + totalDeposit;
            setBalance(playerId, newBalance);

            player.sendMessage(ChatColor.GREEN + "Deposited " + ChatColor.GOLD + totalCoins + " Gold Coins"
                    + ChatColor.GREEN + " (worth " + totalDeposit + " Mark) into your account.");
        } else {
            player.sendMessage(ChatColor.RED + "You have no Gold Coins to deposit.");
        }
    }

    // 출금 기능 구현
    public void withdraw(String nameId, int amount, Player player) {
        int currentBalance = getBalance(nameId);

        if (amount > currentBalance) {
            if (player != null) {
                player.sendMessage(ChatColor.RED + "Insufficient balance. Your current balance is " + ChatColor.GOLD + currentBalance + ChatColor.RED + " Mark.");
            }
            return;
        }

        int coinCount = (int) (amount / COIN_VALUE);
        if (coinCount <= 0) {
            if (player != null) {
                player.sendMessage(ChatColor.RED + "Please withdraw at least " + COIN_VALUE + " Mark (1 Gold Coin).");
            }
            return;
        }

        int totalWithdraw = coinCount * COIN_VALUE;
        setBalance(nameId, currentBalance - totalWithdraw);

        if (player != null) {
            ItemStack coins = CurrencyItem.createCurrencyItem(coinCount);
            player.getInventory().addItem(coins);

            player.sendMessage(ChatColor.GREEN + "Withdrew " + ChatColor.GOLD + coinCount + " Gold Coins"
                    + ChatColor.GREEN + " (worth " + totalWithdraw + " Mark) from your account.");
        }
    }

    // 송금 기능 구현
    public void sendMoney(String senderId, String receiverId, int amount) {
        int senderBalance = getBalance(senderId);

        if (amount > senderBalance) {
            Player senderPlayer = getPlayerById(senderId); // player 객체를 얻어온다
            if (senderPlayer != null) {
                senderPlayer.sendMessage(ChatColor.RED + "Insufficient balance. You have " + ChatColor.GOLD + senderBalance + ChatColor.RED + " Mark available.");
            }
            return;
        }

        setBalance(senderId, senderBalance - amount);
        int receiverBalance = getBalance(receiverId);
        setBalance(receiverId, receiverBalance + amount);

        Player senderPlayer = getPlayerById(senderId);
        Player receiverPlayer = getPlayerById(receiverId);

        // 송금 및 수신 확인 메시지
        if (senderPlayer != null) {
            senderPlayer.sendMessage(ChatColor.GREEN + "Successfully sent " + ChatColor.GOLD + amount + " Mark"
                    + ChatColor.GREEN + " to " + receiverId + ".");
        }
        if (receiverPlayer != null) {
            receiverPlayer.sendMessage(ChatColor.GREEN + "You have received " + ChatColor.GOLD + amount + " Mark"
                    + ChatColor.GREEN + " from " + senderId + ".");
        }
    }

    // nameId를 통해 플레이어 객체를 찾는 헬퍼 메서드 (플레이어가 아닌 경우 null 반환)
    private Player getPlayerById(String nameId) {
        if (databaseManager.isPlayer(nameId)) {  // DatabaseManager에서 플레이어 확인
            String uuid = databaseManager.getPlayerUUID(nameId);
            if (uuid != null) {
                return org.bukkit.Bukkit.getPlayer(java.util.UUID.fromString(uuid));
            }
        }
        return null; // ID가 플레이어가 아니거나 UUID가 없는 경우 null 반환
    }
}