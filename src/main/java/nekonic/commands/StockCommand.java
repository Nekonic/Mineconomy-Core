package nekonic.commands;

import nekonic.managers.StockManager;
import nekonic.models.Stock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StockCommand implements CommandExecutor {
    private final StockManager stockManager;

    public StockCommand(StockManager stockManager) {
        this.stockManager = stockManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("Usage: /stock <list|info>");
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            sender.sendMessage("Available Stocks:");
            stockManager.getAllStocks().forEach(stock -> sender.sendMessage("- " + stock.getTicker()));
            return true;
        }

        if (args[0].equalsIgnoreCase("info") && args.length == 2) {
            Stock stock = stockManager.getStock(args[1]);
            if (stock != null) {
                sender.sendMessage("Ticker: " + stock.getTicker());
                sender.sendMessage("Current Price: " + stock.getCurrentPrice());
                sender.sendMessage("Volume: " + stock.getVolume());
            } else {
                sender.sendMessage("Stock not found.");
            }
            return true;
        }

        return false;
    }
}
