package nekonic.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CurrencyItem {

    public static ItemStack createCurrencyItem(int amount) {
        ItemStack currencyItem = new ItemStack(Material.GOLD_INGOT, amount);
        ItemMeta meta = currencyItem.getItemMeta();

        // 아이템 이름 설정
        meta.displayName(Component.text("Gold Coin" + NamedTextColor.GOLD));

        // 아이템 설명 추가
        meta.lore(
                Component.text("Used as currency in Mineconomy", NamedTextColor.DARK_AQUA).children()
        );

        // 커스텀 모델 데이터 추가
        meta.setCustomModelData(1001); // 1001번 커스텀 모델 데이터 설정
        currencyItem.setItemMeta(meta);

        return currencyItem;
    }
}
