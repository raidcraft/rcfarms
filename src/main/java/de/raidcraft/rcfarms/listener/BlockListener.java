package de.raidcraft.rcfarms.listener;

import de.raidcraft.RaidCraft;
import de.raidcraft.rcfarms.RCFarmsPlugin;
import de.raidcraft.rcfarms.tables.TFarm;
import de.raidcraft.rcfarms.tables.TMaterial;
import de.raidcraft.rcfarms.util.WorldGuardManager;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * @author Philip Urban
 */
public class BlockListener implements Listener {

    // ignore cancelled events and call this handler as latest
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {

        // ignoring creative players
        if(event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }

        RCFarmsPlugin plugin = RaidCraft.getComponent(RCFarmsPlugin.class);
        WorldGuardManager worldGuardManager = plugin.getWorldGuardManager();
        String farmRegion = worldGuardManager.getFarm(event.getBlock().getLocation());
        if(farmRegion == null) {
            return;
        }

        int farmId;
        String prefix = plugin.getConfig().farmPrefix;
        try {
            String farmIdString = farmRegion.substring(prefix.length(), farmRegion.length());
            RaidCraft.LOGGER.info("farmIdString: " + farmIdString);
            farmId = Integer.valueOf(farmIdString);
        }
        catch(NumberFormatException e) {
            printRegionErrorLog(event.getBlock().getLocation(), "Corrupt farm region id!");
            return;
        }

        TFarm tFarm = RaidCraft.getDatabase(RCFarmsPlugin.class).find(TFarm.class, farmId);
        if(tFarm == null) {
            printRegionErrorLog(event.getBlock().getLocation(), "Farm region but no comparing database entry!");
            return;
        }

        boolean found = false;
        for(TMaterial tMaterial : tFarm.getMaterials()) {
            if(tMaterial.getBukkitMaterial() == event.getBlock().getType()) {
                found = true;
                break;
            }
        }
        if(!found) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "Dieses Material kannst du in dieser Farm nicht abbauen!");
        }
    }

    private void printRegionErrorLog(Location location, String msg) {

        RaidCraft.LOGGER.warning(msg + " (x:" + location.getBlockX()+ " y:" + location.getBlockY() + " z:" + location.getBlockZ() + ")");
    }
}
