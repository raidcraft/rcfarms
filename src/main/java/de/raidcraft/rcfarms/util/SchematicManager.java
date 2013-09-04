package de.raidcraft.rcfarms.util;

import com.google.common.io.PatternFilenameFilter;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.MCEditSchematicFormat;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.rcfarms.RCFarmsPlugin;
import de.raidcraft.rcfarms.tables.TFarm;
import de.raidcraft.rcfarms.tables.TFarmLocation;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;

/**
 * @author Philip Urban
 */
public class SchematicManager {

    private final static String SCHEMATIC_PREFIX = "farm_";

    private RCFarmsPlugin plugin;

    public SchematicManager(RCFarmsPlugin plugin) {

        this.plugin = plugin;
    }

    public void createSchematic(TFarm tFarm, int upgradeLevel) throws RaidCraftException {

        TFarmLocation[] keyPoints = tFarm.getKeyPointArray();

        try {
            String schematicName = getSchematicName(tFarm.getId(), upgradeLevel);
            String filePath = getSchematicDirPath(tFarm.getBukkitWorld()) + "/" + schematicName;

            File file = new File(filePath);
            Vector pos1 = keyPoints[0].getSk89qVector();
            Vector pos2 = keyPoints[1].getSk89qVector();

            Vector min = new Vector(Math.min(pos1.getX(), pos2.getX()),
                                    Math.min(pos1.getY(), pos2.getY()),
                                    Math.min(pos1.getZ(), pos2.getZ()));
            Vector max = new Vector(Math.max(pos1.getX(), pos2.getX()),
                                    Math.max(pos1.getY(), pos2.getY()),
                                    Math.max(pos1.getZ(), pos2.getZ()));

            // create clipboard
            CuboidClipboard clipboard = new CuboidClipboard(max.subtract(min).add(new Vector(1, 1, 1)), min);
            BukkitWorld bukkitWorld = new BukkitWorld(tFarm.getBukkitWorld());
            // store blocks
            clipboard.copy(new EditSession(bukkitWorld, Integer.MAX_VALUE));
            // store entities
//            for (LocalEntity entity : bukkitWorld.getEntities(new CuboidRegion(min, max))) {
//                clipboard.storeEntity(entity);
//            }
            // save schematic
            MCEditSchematicFormat.MCEDIT.save(clipboard, file);
        }
        catch(IOException | DataException e) {
            throw new RaidCraftException("Fehler beim speichern der Schematic!");
        }
    }

    public void pasteSchematic(TFarm tFarm, int upgradeLevel) throws RaidCraftException {

        String schematicName = getSchematicName(tFarm.getId(), upgradeLevel);
        String filePath = getSchematicDirPath(tFarm.getBukkitWorld()) + "/" + schematicName;
        File file = new File(filePath);
        try {
            CuboidClipboard clipboard = MCEditSchematicFormat.MCEDIT.load(file);
            clipboard.paste(new EditSession(new BukkitWorld(tFarm.getBukkitWorld()), Integer.MAX_VALUE), clipboard.getOrigin(), false);
//            clipboard.pasteEntities(clipboard.getOrigin());
        } catch (IOException | DataException e) {
            throw new RaidCraftException("Fehler beim laden der Schematic!");
        }
        catch (MaxChangedBlocksException e) {
            throw new RaidCraftException("Fehler beim pasten der Schematic! (Zu viele Blöcke)");
        }
    }

    public void deleteSchematic(TFarm tFarm) throws RaidCraftException {

        final File folder = getSchematicDir(tFarm.getBukkitWorld());
        final File[] files = folder.listFiles(new PatternFilenameFilter(SCHEMATIC_PREFIX + tFarm.getId() + ".*\\.schematic"));

        if(files.length == 0) {
            RaidCraft.LOGGER.info("No schematic found to delete!");
        }
        // loop through the files
        for ( final File file : files ) {
            if (!file.delete() ) {
                throw new RaidCraftException("Can't remove schematic file " + file.getAbsolutePath());
            }
        }
    }

    public void deleteSchematic(TFarm tFarm, int upgradeLevel) throws RaidCraftException {

        String schematicName = getSchematicName(tFarm.getId(), upgradeLevel);
        String filePath = getSchematicDirPath(tFarm.getBukkitWorld()) + "/" + schematicName;
        File file = new File(filePath);

        if (!file.delete() ) {
            throw new RaidCraftException("Can't remove schematic file " + file.getAbsolutePath());
        }
    }

    public String getSchematicName(int farmId, int upgradeLevel) {

        return SCHEMATIC_PREFIX + farmId + "_level_" + upgradeLevel + ".schematic";
    }

    public String getSchematicDirPath(World world) throws RaidCraftException{

        try {
            String path = plugin.getDataFolder().getCanonicalPath() + "/../../schematics/" + world.getName();
            return path;
        }
        catch(IOException e) {
            throw new RaidCraftException("Schematic Ordner konnte nicht geöffnet werden!");
        }
    }

    public File getSchematicDir(World world) throws RaidCraftException {

        File dir;
        dir = new File(getSchematicDirPath(world));
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                throw new RaidCraftException("Der Schematics Ordner konnte nicht erstellt werden!");
            }
        }
        return dir;
    }
}
