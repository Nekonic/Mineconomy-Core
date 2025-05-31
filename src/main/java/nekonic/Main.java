package nekonic;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {


    @Override
    public void onEnable() {
        getServer().getConsoleSender().sendMessage(
                Component.text("plugin enabled", NamedTextColor.DARK_AQUA)
        );
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(
                Component.text("plugin disable", NamedTextColor.DARK_AQUA)
        );
    }
}
