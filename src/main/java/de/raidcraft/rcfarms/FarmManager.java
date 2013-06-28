package de.raidcraft.rcfarms;

import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.MCEditSchematicFormat;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.rcfarms.tables.TFarm;
import de.raidcraft.util.ItemUtils;
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

        if(RaidCraft.getDatabase(RCFarmsPlugin.class).find(TFarm.class).where().eq("regionId", farmId).findUnique() != null) {
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

            TFarm tFarm = new TFarm();
            tFarm.setMaterial(material.name());
            tFarm.setRegionId(farmId);
            tFarm.setLastRegeneration(new Timestamp(System.currentTimeMillis()));
            RaidCraft.getDatabase(RCFarmsPlugin.class).save(tFarm);
        }
        catch(EmptyClipboardException e) {
            throw new RaidCraftException("Keine Auswahl selektiert!");
        }
        catch(IOException | DataException e) {
            throw new RaidCraftException("Fehler beim speichern der Schematic!");
        }
    }

    public double getFarmPrice(String farmId) {

        return plugin.getConfig().pricePerBlock * plugin.getWorldGuardManager().getFarmVolume(farmId);
    }

    public void buyFarm(String player, String farmId) {

        //TODO: implement
    }

    public boolean isForSale(String farmId) {

        //TODO: implement
    }

    public String getFarmOwner(String farmId) {

        //TODO: implement
    }

    public void dropFarm(String farmId) {

        //TODO: implement
    }
}
