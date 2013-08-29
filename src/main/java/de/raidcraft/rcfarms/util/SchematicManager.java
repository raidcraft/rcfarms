package de.raidcraft.rcfarms.util;

import com.google.common.io.PatternFilenameFilter;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
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
        RaidCraft.LOGGER.info("DS -1");
        TFarmLocation[] keyPoints = tFarm.getKeyPoints().toArray(new TFarmLocation[tFarm.getKeyPoints().size()]);

        try {
            RaidCraft.LOGGER.info("DS 0");
            World world = tFarm.getBukkitWorld();
            RaidCraft.LOGGER.info("DS 1: " + world.getName());
            String schematicName = getSchematicName(tFarm.getId(), upgradeLevel);
            RaidCraft.LOGGER.info("DS 2: " + schematicName);
            String filePath = getSchematicDirPath(world) + "/" + schematicName;
            RaidCraft.LOGGER.info(filePath);
            File file = new File(filePath);
            Vector origin = keyPoints[0].getSk89qVector();
            Vector size = keyPoints[1].getSk89qVector().subtract(keyPoints[0].getSk89qVector());
            CuboidClipboard clipboard = new CuboidClipboard(size, origin);
            MCEditSchematicFormat.MCEDIT.save(clipboard, file);
        }
        catch(IOException | DataException e) {
            throw new RaidCraftException("Fehler beim speichern der Schematic!");
        }
    }

    public void deleteSchematic(TFarm tFarm) throws RaidCraftException {

        final File folder = getSchematicDir(tFarm.getBukkitWorld());
        final File[] files = folder.listFiles(new PatternFilenameFilter(SCHEMATIC_PREFIX + tFarm.getId() + "*\\.schematic"));

        // loop through the files
        for ( final File file : files ) {
            if ( !file.delete() ) {
                System.err.println( "Can't remove schematic file " + file.getAbsolutePath() );
            }
        }
    }

    public String getSchematicName(int farmId, int upgradeLevel) {

        return SCHEMATIC_PREFIX + farmId + "_level_" + upgradeLevel + ".schematic";
    }

    public String getSchematicDirPath(World world) throws RaidCraftException{

        try {
            String path = plugin.getDataFolder().getCanonicalPath() + "/schematics/" + world.getName();
            RaidCraft.LOGGER.info("DEBUG: S0: " + path);
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
