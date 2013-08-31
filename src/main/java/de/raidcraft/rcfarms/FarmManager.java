package de.raidcraft.rcfarms;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.rcfarms.tables.TFarm;
import de.raidcraft.rcfarms.tables.TFarmLocation;
import de.raidcraft.rcfarms.tables.TMaterial;
import de.raidcraft.util.ItemUtils;
import de.raidcraft.worldcontrol.WorldControlPlugin;
import de.raidcraft.worldcontrol.restricteditem.RestrictedItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;

import java.sql.Timestamp;
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
            generateRegion(tFarm);
        }
    }

    public void generateRegion(TFarm tFarm) {

        TFarmLocation[] keyPoints = tFarm.getKeyPoints().toArray(new TFarmLocation[tFarm.getKeyPoints().size()]);
        Map<String,ProtectedRegion> regions = plugin.getWorldGuard().getRegionManager(tFarm.getBukkitWorld()).getRegions();

        ProtectedRegion region;
        boolean create = false;
        if(regions.containsKey(getRegionName(tFarm.getId()))) {
            region = regions.get(getRegionName(tFarm.getId()));
        } else {
            region = new ProtectedCuboidRegion(getRegionName(tFarm.getId()),
                new BlockVector(keyPoints[0].getX(), keyPoints[0].getY(), keyPoints[0].getZ()),
                new BlockVector(keyPoints[1].getX(), keyPoints[1].getY(), keyPoints[1].getZ()));
            create = true;
        }

        region.setFlag(DefaultFlag.BUILD, StateFlag.State.ALLOW);
        region.setFlag(DefaultFlag.GREET_MESSAGE, getWelcomeMessage(tFarm));
        region.setFlag(DefaultFlag.FAREWELL_MESSAGE, getFarewellMessage(tFarm));
        if(create) {
            plugin.getWorldGuard().getRegionManager(tFarm.getBukkitWorld()).addRegion(region);
        }
    }

    public void deleteFarm(String farmId) throws RaidCraftException {


        TFarm tFarm = RaidCraft.getDatabase(RCFarmsPlugin.class).find(TFarm.class, farmId);
        if(tFarm == null) {
            throw new RaidCraftException("Es gibt keine Farm mit dieser Id");
        }

        // delete schematics
        plugin.getSchematicManager().deleteSchematic(tFarm);

        // delete region
        plugin.getWorldGuard().getRegionManager(tFarm.getBukkitWorld()).removeRegion(getRegionName(tFarm.getId()));

        // delete database entries
        RaidCraft.getDatabase(RCFarmsPlugin.class).delete(tFarm);

        // delete dynmap marker
        plugin.getDynmapManager().removeMarker(tFarm);
    }

    public void regenerateFarm(String farmId) {

        TFarm tFarm = RaidCraft.getDatabase(RCFarmsPlugin.class).find(TFarm.class, farmId);
        regenerateFarm(tFarm);
    }

    public void regenerateFarm(TFarm tFarm) {

        if(tFarm == null) return;

        int upgradeLevel = 0;
        //TODO get upgrade level
        try {
            tFarm.setLastRegeneration(new Timestamp(System.currentTimeMillis()));
            RaidCraft.getDatabase(RCFarmsPlugin.class).save(tFarm);
            plugin.getSchematicManager().pasteSchematic(tFarm, upgradeLevel);
        } catch (RaidCraftException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
        }
    }

    public boolean checkForRegeneration(TFarm tFarm) {

        for(TMaterial tMaterial : tFarm.getMaterials()) {
            Material material = ItemUtils.getItem(tMaterial.getName());
            if(material == null) continue;

            RestrictedItem restrictedItem = RaidCraft.getComponent(WorldControlPlugin.class)
                    .getRestrictedItemManager().getRestrictedItem(material);
            if(restrictedItem == null) continue;
            if(restrictedItem.getRegenerationTime() < ((System.currentTimeMillis() - tFarm.getLastRegeneration().getTime()) / 1000)) {
                // check if player is inside farm -> abort regeneration
                if(tFarm.isPlayerInside()) {
                    continue;
                }
                RaidCraft.LOGGER.info("Regenerate farm '" + tFarm.getName() + "' with ID '" + tFarm.getId() + "'");
                regenerateFarm(tFarm);
                return true;
            }
        }
        return false;
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

    public String getWelcomeMessage(TFarm tFarm) {

        String materialString = "";
        for(TMaterial tMaterial : tFarm.getMaterials()) {
            if(!materialString.isEmpty()) materialString += ", ";
            materialString += ItemUtils.getFriendlyName(tMaterial.getBukkitMaterial(), ItemUtils.Language.GERMAN);
        }
        return ChatColor.GOLD + "Farm '" + tFarm.getName() + "' betreten: " + materialString;
    }

    public String getFarewellMessage(TFarm tFarm) {

        return ChatColor.LIGHT_PURPLE + "Farm '" + tFarm.getName() + "' verlassen.";
    }
}
