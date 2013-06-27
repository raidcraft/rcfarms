package de.raidcraft.rcfarms.util;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
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
    private World world;

    public WorldGuardManager(RCFarmsPlugin plugin, WorldGuardPlugin worldGuard) {

        this.plugin = plugin;
        this.worldGuard = worldGuard;
        this.world = Bukkit.getWorld(plugin.getConfig().world);
    }

    public String getFarmName(Location location) {

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

    public boolean isFarm(String regionId) {

        if(!regionId.startsWith(plugin.getConfig().farmPrefix)) return false;

        ProtectedRegion region = worldGuard.getRegionManager(world).getRegion(regionId);
        return (region != null);
    }

    public double getFarmVolume(String farmId) {

        ProtectedRegion region = worldGuard.getRegionManager(world).getRegion(farmId);
        if(region == null) return 0;

        BlockVector max = region.getMaximumPoint();
        BlockVector min = region.getMinimumPoint();
        int xLength = max.getBlockX() - min.getBlockX();
        int zWidth = max.getBlockZ() - min.getBlockZ();
        return xLength * zWidth * (max.getBlockY() - min.getBlockY());
    }
}
