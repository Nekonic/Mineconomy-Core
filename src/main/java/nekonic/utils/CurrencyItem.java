package nekonic.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CurrencyItem {

    public static ItemStack createCurrencyItem(int amount) {
        ItemStack currencyItem = new ItemStack(Material.GOLD_INGOT, amount);
        ItemMeta meta = currencyItem.getItemMeta();

        // 아이템 이름 설정
        meta.setDisplayName(ChatColor.GOLD + "Gold Coin");

        // 아이템 설명 추가
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Used as currency in Mineconomy");
        meta.setLore(lore);

        // 커스텀 모델 데이터 추가
        meta.setCustomModelData(1001); // 1001번 커스텀 모델 데이터 설정
        currencyItem.setItemMeta(meta);

        return currencyItem;
    }
}
