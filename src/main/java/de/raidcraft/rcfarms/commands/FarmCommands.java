package de.raidcraft.rcfarms.commands;

import com.sk89q.minecraft.util.commands.*;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.rcfarms.FarmBuilder;
import de.raidcraft.rcfarms.RCFarmsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * 17.12.11 - 11:30
 *
 * @author Silthus
 */
public class FarmCommands {

    private RCFarmsPlugin plugin;

    public FarmCommands(RCFarmsPlugin plugin) {

        this.plugin = plugin;
    }

    @Command(
            aliases = {"rcfarms", "farm", "farms"},
            desc = "Region Commands"
    )
    @NestedCommand(value = NestedCommands.class)
    public void farms(CommandContext args, CommandSender sender) {
    }

    public static class NestedCommands {

        private final RCFarmsPlugin plugin;

        public NestedCommands(RCFarmsPlugin plugin) {

            this.plugin = plugin;
        }

        @Command(
                aliases = {"reload"},
                desc = "Reloads the plugin"
        )
        @CommandPermissions("rcfarms.reload")
        public void reload(CommandContext args, CommandSender sender) {

            plugin.reload();
            sender.sendMessage(ChatColor.GREEN + "Die Config wurde neugeladen.");
        }

        @Command(
                aliases = {"create"},
                desc = "Creates a farm",
                min = 1,
                usage = "<farm name>"
        )
        @CommandPermissions("rcfarms.create")
        public void create(CommandContext args, CommandSender sender) throws CommandException {

            if(sender instanceof ConsoleCommandSender) throw new CommandException("Players only!");
            Player player = (Player)sender;

            try {
                FarmBuilder farmBuilder = new FarmBuilder();
                farmBuilder.addMaterial(Material.WOOD);
                farmBuilder.setCreator(player.getName());
                farmBuilder.setMinimumPoint(plugin.getWorldEdit().getSelection(player).getMinimumPoint());
                farmBuilder.setMaximumPoint(plugin.getWorldEdit().getSelection(player).getMaximumPoint());
                farmBuilder.setName(args.getString(0));
                farmBuilder.createFarm();
                player.sendMessage(ChatColor.GREEN + "Du hast erfolgreich eine Farm erstellt!");
            } catch (RaidCraftException e) {
                throw new CommandException("Fehler beim erstellen der Farm: " + e.getMessage());
            }
        }

        @Command(
                aliases = {"delete"},
                desc = "Delete a farm",
                min = 1,
                usage = "<farm name>"
        )
        @CommandPermissions("rcfarms.delete")
        public void delete(CommandContext args, CommandSender sender) throws CommandException {

            try {
                plugin.getFarmManager().deleteFarm(args.getString(0));
                sender.sendMessage(ChatColor.GREEN + "Du hast erfolgreich die Farm " + args.getString(0) + " gelöscht!");
            } catch (RaidCraftException e) {
                throw new CommandException(e.getMessage());
            }
        }
    }
}
