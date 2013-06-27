package de.raidcraft.rcfarms.util;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.raidcraft.RaidCraft;
import de.raidcraft.rcfarms.RCFarmsPlugin;
import org.bukkit.Location;

/**
 * @author Philip Urban
 */
public class WorldGuardUtil {

    public static String getFarmName(Location location) {

        WorldGuardPlugin worldGuard = RaidCraft.getComponent(RCFarmsPlugin.class).getWorldGuard();

        ApplicableRegionSet regions = worldGuard.getRegionManager(location.getWorld()).getApplicableRegions(location);
        if(regions.size() == 0) {
            return null;
        }
        for (ProtectedRegion region : regions) {
            if(region.getId().startsWith(RaidCraft.getComponent(RCFarmsPlugin.class).getConfig().farmPrefix)) {
                return region.getId();
            }
        }
        return null;
    }
}
