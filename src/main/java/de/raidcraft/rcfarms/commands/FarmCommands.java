package de.raidcraft.rcfarms.commands;

import com.sk89q.minecraft.util.commands.*;
import com.sk89q.worldedit.bukkit.selections.Selection;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.rcfarms.RCFarmsPlugin;
import de.raidcraft.rcfarms.conversations.host.FarmHost;
import de.raidcraft.rcfarms.tables.TFarm;
import org.bukkit.ChatColor;
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
                desc = "Creates a farm"
        )
        @CommandPermissions("rcfarms.create")
        public void create(CommandContext args, CommandSender sender) throws CommandException {

            if(sender instanceof ConsoleCommandSender) throw new CommandException("Players only!");
            Player player = (Player)sender;

            Selection selection = plugin.getWorldEdit().getSelection(player);
            if(selection == null || selection.getMinimumPoint() == null || selection.getMaximumPoint() == null) {
                throw new CommandException("Kein Bereich markiert!");
            }

            RaidCraft.getConversationProvider().triggerConversation(player, plugin.getConfig().creatingConversationName, new FarmHost(player.getLocation()));
        }

        @Command(
                aliases = {"delete"},
                desc = "Delete a farm",
                min = 1,
                usage = "<farm id>"
        )
        @CommandPermissions("rcfarms.delete")
        public void delete(CommandContext args, CommandSender sender) throws CommandException {

            try {
                plugin.getFarmManager().deleteFarm(args.getString(0));
                sender.sendMessage(ChatColor.GREEN + "Du hast erfolgreich die Farm mit der ID " + args.getString(0) + " gelöscht!");
            } catch (RaidCraftException e) {
                throw new CommandException(e.getMessage());
            }
        }

        @Command(
                aliases = {"check"},
                desc = "Check all farms for regeneration"
        )
        @CommandPermissions("rcfarms.delete")
        public void check(CommandContext args, CommandSender sender) throws CommandException {

            sender.sendMessage(ChatColor.GREEN + "Start regeneration check...");
            int regenerated = 0;
            int farmCount = 0;
            for(TFarm tFarm : RaidCraft.getDatabase(RCFarmsPlugin.class).find(TFarm.class).findList()) {
                if(plugin.getFarmManager().checkForRegeneration(tFarm)) {
                    regenerated++;
                }
                farmCount++;
            }
            sender.sendMessage(ChatColor.GREEN + "Checked all farms! " + regenerated + "/" + farmCount + " regenerated");
        }
    }
}
