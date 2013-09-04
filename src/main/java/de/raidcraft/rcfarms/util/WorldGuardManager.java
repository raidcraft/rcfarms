package de.raidcraft.rcfarms.util;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.raidcraft.RaidCraft;
import de.raidcraft.rcfarms.RCFarmsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * @author Philip Urban
 */
public class WorldGuardManager {

    private RCFarmsPlugin plugin;
    private WorldGuardPlugin worldGuard;

    public WorldGuardManager(RCFarmsPlugin plugin, WorldGuardPlugin worldGuard) {

        this.plugin = plugin;
        this.worldGuard = worldGuard;
    }

    public String getFarmName(Location location) {

        //TODO: use database farms
        ApplicableRegionSet regions = worldGuard.getRegionManager(location.getWorld()).getApplicableRegions(location);
        if(regions.size() == 0) {
            return null;
        }
        for (ProtectedRegion region : regions) {
            if(region.getId().startsWith(plugin.getConfig().farmPrefix)) {
                return region.getId();
            }
        }
        return null;
    }

    public boolean isFarm(World world, String regionId) {

        if(!regionId.startsWith(plugin.getConfig().farmPrefix)) return false;

        ProtectedRegion region = worldGuard.getRegionManager(world).getRegion(regionId);
        return (region != null);
    }

    public String getFarm(Location location) {
        ApplicableRegionSet regions = worldGuard.getRegionManager(location.getWorld()).getApplicableRegions(location);
        if(regions.size() == 0) {
            return null;
        }
        for (ProtectedRegion region : regions) {
            if(region.getId().startsWith(plugin.getConfig().farmPrefix)) {
                return region.getId();
            }
        }
        return null;
    }

    public double getFarmVolume(World world, String farmId) {

        ProtectedRegion region = worldGuard.getRegionManager(world).getRegion(farmId);
        if(region == null) return 0;

        BlockVector max = region.getMaximumPoint();
        BlockVector min = region.getMinimumPoint();
        int xLength = max.getBlockX() - min.getBlockX();
        int zWidth = max.getBlockZ() - min.getBlockZ();
        return xLength * zWidth * (max.getBlockY() - min.getBlockY());
    }

    public void save() {

        for (World world : Bukkit.getServer().getWorlds()) {
            try {
                worldGuard.getRegionManager(world).save();
            } catch (ProtectionDatabaseException e) {
                RaidCraft.LOGGER.warning(e.getMessage());
            }
        }
    }
}
