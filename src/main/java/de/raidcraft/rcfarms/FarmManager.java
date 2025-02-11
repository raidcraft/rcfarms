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

        Map<String, ProtectedRegion> regions = plugin.getWorldGuard().getRegionManager(world).getRegions();
        Set<TFarm> farms = RaidCraft.getDatabase(RCFarmsPlugin.class).find(TFarm.class).findSet();
        for(TFarm tFarm : farms) {
            tFarm.loadChildren();
            TFarmLocation[] keyPoints = tFarm.getKeyPointArray();
            // check world
            if(!keyPoints[0].getWorld().equalsIgnoreCase(world.getName())) continue;

            // check if region exists
            String regionName = getRegionName(tFarm.getId());
            if(regions.containsKey(regionName)) {
                updateRegionFlags(tFarm, regions.get(regionName));
            }

            // create region
            generateRegion(tFarm);
        }
    }

    public void generateRegion(TFarm tFarm) {

        TFarmLocation[] keyPoints = tFarm.getKeyPointArray();
        ProtectedRegion existingRegion = plugin.getWorldGuard().getRegionManager(tFarm.getBukkitWorld()).getRegion(getRegionName(tFarm.getId()));

        ProtectedRegion region;
        boolean create = false;
        if(existingRegion != null) {
            region = existingRegion;
        } else {
            region = new ProtectedCuboidRegion(getRegionName(tFarm.getId()),
                new BlockVector(keyPoints[0].getX(), keyPoints[0].getY(), keyPoints[0].getZ()),
                new BlockVector(keyPoints[1].getX(), keyPoints[1].getY(), keyPoints[1].getZ()));
            create = true;
        }
        if(create) {
            plugin.getWorldGuard().getRegionManager(tFarm.getBukkitWorld()).addRegion(region);
        }

        updateRegionFlags(tFarm, region);
    }

    private void updateRegionFlags(TFarm tFarm, ProtectedRegion region) {

        region.setFlag(DefaultFlag.BUILD, StateFlag.State.ALLOW);
        region.setFlag(DefaultFlag.GREET_MESSAGE, getWelcomeMessage(tFarm));
        region.setFlag(DefaultFlag.FAREWELL_MESSAGE, getFarewellMessage(tFarm));
    }

    public void deleteFarm(String farmKeyword) throws RaidCraftException {


        TFarm tFarm = getFarm(farmKeyword);
        if(tFarm == null) {
            throw new RaidCraftException("Es gibt keine Farm mit dieser ID");
        }

        // delete schematics
        plugin.getSchematicManager().deleteSchematic(tFarm);

        // delete region
        plugin.getWorldGuard().getRegionManager(tFarm.getBukkitWorld()).removeRegion(getRegionName(tFarm.getId()));

        // save regions
        plugin.getWorldGuardManager().save();

        // delete database entries
        RaidCraft.getDatabase(RCFarmsPlugin.class).delete(tFarm);

        // delete dynmap marker
        plugin.getDynmapManager().removeMarker(tFarm);
    }

    public void regenerateFarm(String farmKeyword) throws RaidCraftException {

        TFarm tFarm = getFarm(farmKeyword);
        if(tFarm == null) {
            throw new RaidCraftException("Es gibt keine Farm mit dieser ID");
        }
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

        // check explicit regeneration delay
        if(tFarm.getExplicitRegenerationInterval() > 0 || tFarm.isAllMaterialFarm())  {

            if (tFarm.getExplicitRegenerationInterval() < ((System.currentTimeMillis() - tFarm.getLastRegeneration().getTime()) / 1000)) {
                // check if player is inside farm -> abort regeneration
                if (tFarm.isPlayerInside()) {
                    return false;
                }
                RaidCraft.LOGGER.info("Regenerate farm '" + tFarm.getName() + "' with ID '" + tFarm.getId() + "'");
                regenerateFarm(tFarm);
                return true;
            }

        // check delay of materials
        } else {
            for (TMaterial tMaterial : tFarm.getMaterials()) {
                Material material = ItemUtils.getItem(tMaterial.getName());
                if (material == null) continue;

                RestrictedItem restrictedItem = RaidCraft.getComponent(WorldControlPlugin.class)
                        .getRestrictedItemManager().getRestrictedItem(material);
                if (restrictedItem == null) continue;
                if (restrictedItem.getRegenerationTime() < ((System.currentTimeMillis() - tFarm.getLastRegeneration().getTime()) / 1000)) {
                    // check if player is inside farm -> abort regeneration
                    if (tFarm.isPlayerInside()) {
                        continue;
                    }
                    RaidCraft.LOGGER.info("Regenerate farm '" + tFarm.getName() + "' with ID '" + tFarm.getId() + "'");
                    regenerateFarm(tFarm);
                    return true;
                }
            }
        }
        return false;
    }

    public String getRegionName(int farmId) {

        return plugin.getConfig().farmPrefix + farmId;
    }

    public String getWelcomeMessage(TFarm tFarm) {

        String materialString = "";
        if(tFarm.isAllMaterialFarm()) {
            materialString = ChatColor.DARK_AQUA + "Alle Materialien abbaubar!";
        } else {
            for (TMaterial tMaterial : tFarm.getMaterials()) {
                if (!materialString.isEmpty()) materialString += ChatColor.GOLD + ", ";
                materialString += ChatColor.DARK_GREEN + ItemUtils.getFriendlyName(tMaterial.getBukkitMaterial(), ItemUtils.Language.GERMAN);
            }
        }
        return ChatColor.GOLD + "Farm '" + tFarm.getName() + "' betreten: " + materialString;
    }

    public String getFarewellMessage(TFarm tFarm) {

        return ChatColor.GRAY + "Farm verlassen.";
    }

    public TFarm getFarm(String keyword) {

        TFarm tFarm;
        try {
            int id = Integer.parseInt(keyword);
            tFarm = RaidCraft.getDatabase(RCFarmsPlugin.class).find(TFarm.class, id);
        }
        catch (NumberFormatException e) {
            tFarm = RaidCraft.getDatabase(RCFarmsPlugin.class).find(TFarm.class).where().ieq("name", keyword).findUnique();
        }
        if(tFarm != null) {
            tFarm.loadChildren();
        }
        return tFarm;
    }

    public Iterable<TFarm> getFarms(String world) {

        return RaidCraft.getDatabase(RCFarmsPlugin.class).find(TFarm.class).where().ieq("world", world).findList();
    }
}
