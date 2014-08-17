package de.raidcraft.rcfarms.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;
import com.sk89q.worldedit.bukkit.selections.Selection;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.rcfarms.RCFarmsPlugin;
import de.raidcraft.rcfarms.conversations.host.FarmHost;
import de.raidcraft.rcfarms.tables.TFarm;
import de.raidcraft.rcfarms.tables.TFarmLocation;
import de.raidcraft.rcfarms.tables.TMaterial;
import de.raidcraft.util.ItemUtils;
import de.raidcraft.util.UUIDUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

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
        @CommandPermissions("rcfarms.admin")
        public void reload(CommandContext args, CommandSender sender) {

            // refresh farm marker
            for (World world : Bukkit.getWorlds()) {
                for (TFarm tFarm : RaidCraft.getComponent(RCFarmsPlugin.class).getFarmManager().getFarms(world.getName())) {
                    tFarm.loadChildren();
                    plugin.getDynmapManager().addFarmMarker(tFarm);
                }
            }

            plugin.reload();
            sender.sendMessage(ChatColor.GREEN + "Die Config wurde neugeladen.");
        }

        @Command(
                aliases = {"create"},
                desc = "Creates a farm"
        )
        @CommandPermissions("rcfarms.admin")
        public void create(CommandContext args, CommandSender sender) throws CommandException {

            if (sender instanceof ConsoleCommandSender) throw new CommandException("Players only!");
            Player player = (Player) sender;

            Selection selection = plugin.getWorldEdit().getSelection(player);
            if (selection == null || selection.getMinimumPoint() == null || selection.getMaximumPoint() == null) {
                throw new CommandException("Kein Bereich markiert!");
            }

            RaidCraft.getConversationProvider().triggerConversation(player, new FarmHost(player.getLocation(), plugin.getConfig().creatingConversationName));
        }

        @Command(
                aliases = {"edit"},
                desc = "Edit a farm"
        )
        @CommandPermissions("rcfarms.admin")
        public void edit(CommandContext args, CommandSender sender) throws CommandException {

            if (sender instanceof ConsoleCommandSender) throw new CommandException("Players only!");
            Player player = (Player) sender;

            RaidCraft.getConversationProvider().triggerConversation(player, new FarmHost(player.getLocation(), plugin.getConfig().editingConversationName));
        }

        @Command(
                aliases = {"delete"},
                desc = "Delete a farm",
                min = 1,
                usage = "<farm id/name>"
        )
        @CommandPermissions("rcfarms.admin")
        public void delete(CommandContext args, CommandSender sender) throws CommandException {

            try {
                plugin.getFarmManager().deleteFarm(args.getString(0));
                sender.sendMessage(ChatColor.GREEN + "Du hast erfolgreich die Farm '" + args.getString(0) + "' gelöscht!");
            } catch (RaidCraftException e) {
                throw new CommandException(e.getMessage());
            }
        }

        @Command(
                aliases = {"check"},
                desc = "Check all farms for regeneration"
        )
        @CommandPermissions("rcfarms.admin")
        public void check(CommandContext args, CommandSender sender) throws CommandException {

            sender.sendMessage(ChatColor.GREEN + "Start regeneration check...");
            int regenerated = 0;
            int farmCount = 0;
            for (World world : Bukkit.getWorlds()) {
                for (TFarm tFarm : plugin.getFarmManager().getFarms(world.getName())) {
                    if (plugin.getFarmManager().checkForRegeneration(tFarm)) {
                        regenerated++;
                    }
                    farmCount++;
                }
            }
            sender.sendMessage(ChatColor.GREEN + "Checked all farms! " + regenerated + "/" + farmCount + " regenerated");
        }

        @Command(
                aliases = {"update"},
                desc = "Updates all farm regions"
        )
        @CommandPermissions("rcfarms.admin")
        public void update(CommandContext args, CommandSender sender) throws CommandException {

            sender.sendMessage(ChatColor.GREEN + "Start farm region update...");
            for (World world : Bukkit.getWorlds()) {
                plugin.getFarmManager().generateRegions(world);
            }
            sender.sendMessage(ChatColor.GREEN + "Finished farm region update!");
        }

        @Command(
                aliases = {"list"},
                desc = "List all farms"
        )
        public void list(CommandContext args, CommandSender sender) throws CommandException {

            if (sender instanceof ConsoleCommandSender) throw new CommandException("Players only!");
            Player player = (Player) sender;

            StringBuilder farmNameList = new StringBuilder();
            for (TFarm tFarm : plugin.getFarmManager().getFarms(player.getLocation().getWorld().getName())) {
                if (farmNameList.length() > 0) farmNameList.append(ChatColor.WHITE).append(", ");
                farmNameList.append(ChatColor.YELLOW).append(tFarm.getName()).append(" (").append(tFarm.getId()).append(")");
            }
            sender.sendMessage(ChatColor.GREEN + "Alle verfügbaren Farmen:");
            sender.sendMessage(farmNameList.toString());
        }

        @Command(
                aliases = {"info"},
                min = 1,
                desc = "Info about an specific farm"
        )
        public void info(CommandContext args, CommandSender sender) throws CommandException {

            TFarm tFarm = plugin.getFarmManager().getFarm(args.getString(0));
            if (tFarm == null) {
                throw new CommandException("Die Farm '" + args.getString(0) + "' wurde nicht gefunden! (ID oder Name möglich)");
            }
            StringBuilder materialList = new StringBuilder();
            for (TMaterial tMaterial : tFarm.getMaterials()) {
                if (materialList.length() > 0) materialList.append(ChatColor.WHITE).append(", ");
                materialList.append(ChatColor.YELLOW).append(ItemUtils.getFriendlyName(tMaterial.getBukkitMaterial()));
            }

            sender.sendMessage(ChatColor.GREEN + "Informationen zur Farm '" + ChatColor.YELLOW + tFarm.getName() + ChatColor.GREEN + "' ("
                    + tFarm.getId() + "):");
            sender.sendMessage(ChatColor.GREEN + "Erstellt am: " + ChatColor.YELLOW + tFarm.getCreationDate().toString());
            sender.sendMessage(ChatColor.GREEN + "Von: " + ChatColor.YELLOW + tFarm.getCreator());
            sender.sendMessage(ChatColor.GREEN + "Materialien: " + materialList.toString());
        }

        @Command(
                aliases = {"warp", "tp"},
                desc = "Warp player to specific farm",
                usage = "<Farm ID/Name> [Spieler]"
        )
        @CommandPermissions("rcfarms.admin")
        public void warp(CommandContext args, CommandSender sender) throws CommandException {

            Player player;
            if (args.argsLength() < 2) {
                if (sender instanceof ConsoleCommandSender) throw new CommandException("Players only!");
                player = (Player) sender;
            } else {
                UUID uuid = UUIDUtil.convertPlayer(args.getString(1));
                if (uuid == null) {
                    throw new CommandException("Keinen passenden Spieler gefunden!");
                }
                player = Bukkit.getPlayer(uuid);
                if (player == null) {
                    throw new CommandException("Spieler ist nicht online!");
                }
            }

            TFarm tFarm = plugin.getFarmManager().getFarm(args.getString(0));
            if (tFarm == null) {
                throw new CommandException("Die Farm '" + args.getString(0) + "' wurde nicht gefunden! (ID oder Name möglich)");
            }

            TFarmLocation[] keyPoints = tFarm.getKeyPointArray();
            player.teleport(new Location(tFarm.getBukkitWorld(), keyPoints[0].getX(), keyPoints[0].getY() + 20, keyPoints[0].getZ()));
            sender.sendMessage(ChatColor.GREEN + "Du wurdest zu Farm '" + tFarm.getName() + "' teleportiert!");
        }
    }
}
