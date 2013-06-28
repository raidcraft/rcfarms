package de.raidcraft.rcfarms;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.MCEditSchematicFormat;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.rcfarms.tables.TFarm;
import de.raidcraft.rcfarms.tables.TFarmLocation;
import de.raidcraft.util.ItemUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;

/**
 * @author Philip Urban
 */
public class FarmManager {

    private RCFarmsPlugin plugin;

    public FarmManager(RCFarmsPlugin plugin) {

        this.plugin = plugin;
    }

    public void createFarm(Player player, String farmId, String materialName) throws RaidCraftException {

        Material material = ItemUtils.getItem(materialName);
        if(material == null) {
            throw new RaidCraftException("Es gibt kein Material mit dem Namen oder ID: " + materialName);
        }

        if(RaidCraft.getDatabase(RCFarmsPlugin.class).find(TFarm.class).where().eq("name", farmId).findUnique() != null) {
            throw new RaidCraftException("Es gibt bereits eine Farm mit diesem Namen");
        }

        try {
            String schematicDirPath = plugin.getDataFolder().getCanonicalPath() + "\\schematics";
            File dir = new File(schematicDirPath);
            if (!dir.exists()) {
                if (!dir.mkdir()) {
                    throw new RaidCraftException("Der Schematics Ordner konnte nicht erstellt werden!");
                }
            }
            File file = new File(schematicDirPath + "\\farm_" + farmId + "_original.schematic");
            MCEditSchematicFormat.MCEDIT.save(plugin.getWorldEdit().getSession(player).getClipboard(), file);
        }
        catch(EmptyClipboardException e) {
            throw new RaidCraftException("Keine Auswahl selektiert!");
        }
        catch(IOException | DataException e) {
            throw new RaidCraftException("Fehler beim speichern der Schematic!");
        }

        // save farm in database
        Location minimumPoint = plugin.getWorldEdit().getSelection(player).getMinimumPoint();
        Location maximumPoint = plugin.getWorldEdit().getSelection(player).getMaximumPoint();

        TFarm tFarm = new TFarm();
        tFarm.setMaterial(material.name());
        tFarm.setName(farmId);
        tFarm.setLastRegeneration(new Timestamp(System.currentTimeMillis()));
        RaidCraft.getDatabase(RCFarmsPlugin.class).save(tFarm);

        TFarmLocation tFarmLocationMinimum = new TFarmLocation(minimumPoint, tFarm.getId());
        RaidCraft.getDatabase(RCFarmsPlugin.class).save(tFarmLocationMinimum);
        TFarmLocation tFarmLocationMaximum = new TFarmLocation(maximumPoint, tFarm.getId());
        RaidCraft.getDatabase(RCFarmsPlugin.class).save(tFarmLocationMaximum);

        // create region
        ProtectedCuboidRegion region = new ProtectedCuboidRegion(plugin.getConfig().farmPrefix + "_" + farmId,
               new BlockVector(minimumPoint.getBlockX(), minimumPoint.getBlockY(), minimumPoint.getBlockZ()),
               new BlockVector(maximumPoint.getBlockX(), maximumPoint.getBlockY(), maximumPoint.getBlockZ()));
        plugin.getWorldGuard().getRegionManager(maximumPoint.getWorld()).addRegion(region);
    }

    public void deleteFarm(String farmId) throws RaidCraftException {

        TFarm tFarm = RaidCraft.getDatabase(RCFarmsPlugin.class).find(TFarm.class).where().eq("name", farmId).findUnique();
        if(tFarm == null) {
            throw new RaidCraftException("Es gibt keine Farm mit diesem Namen");
        }

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
}
