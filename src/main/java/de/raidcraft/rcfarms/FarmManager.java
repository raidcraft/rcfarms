package de.raidcraft.rcfarms;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.rcfarms.tables.TFarm;
import de.raidcraft.rcfarms.tables.TFarmLocation;
import org.bukkit.World;

import java.util.Map;
import java.util.Set;

/**
 * @author Philip Urban
 */
public class FarmManager {

    private RCFarmsPlugin plugin;

    public FarmManager(RCFarmsPlugin plugin) {

        this.plugin = plugin;
    }

    public void generateRegions(World world) {

        Map<String,ProtectedRegion> regions = plugin.getWorldGuard().getRegionManager(world).getRegions();
        Set<TFarm> farms = RaidCraft.getDatabase(RCFarmsPlugin.class).find(TFarm.class).findSet();
        for(TFarm tFarm : farms) {

            TFarmLocation[] keyPoints = tFarm.getKeyPoints().toArray(new TFarmLocation[tFarm.getKeyPoints().size()]);
            // check world
            if(!keyPoints[0].getWorld().equalsIgnoreCase(world.getName())) continue;

            // check if region exists
            if(regions.containsKey(getRegionName(tFarm.getId()))) continue;

            // create region
            ProtectedCuboidRegion region = new ProtectedCuboidRegion(getRegionName(tFarm.getId()),
                    new BlockVector(keyPoints[0].getX(), keyPoints[0].getY(), keyPoints[0].getZ()),
                    new BlockVector(keyPoints[1].getX(), keyPoints[1].getY(), keyPoints[1].getZ()));
            plugin.getWorldGuard().getRegionManager(world).addRegion(region);
        }
    }

    public void deleteFarm(String farmId) throws RaidCraftException {


        TFarm tFarm = RaidCraft.getDatabase(RCFarmsPlugin.class).find(TFarm.class).where().eq("name", farmId).findUnique();
        if(tFarm == null) {
            throw new RaidCraftException("Es gibt keine Farm mit diesem Namen");
        }

        // delete schematics
        plugin.getSchematicManager().deleteSchematic(tFarm.getId());

        // delete region
        plugin.getWorldGuard().getRegionManager(tFarm.getBukkitWorld()).removeRegion(getRegionName(tFarm.getId()));

        // delete database entries
        RaidCraft.getDatabase(RCFarmsPlugin.class).delete(tFarm);

    }

    public double getFarmPrice(String farmId) {

        return plugin.getConfig().pricePerBlock * plugin.getWorldGuardManager().getFarmVolume(farmId);
    }

    public void buyFarm(String player, String farmId) {

        //TODO: implement
    }

    public boolean isForSale(String farmId) {

        //TODO: implement
        return false;
    }

    public String getFarmOwner(String farmId) {

        //TODO: implement
        return null;
    }

    public void dropFarm(String farmId) {

        //TODO: implement
    }

    public String getRegionName(int farmId) {

        return plugin.getConfig().farmPrefix + farmId;
    }
}
