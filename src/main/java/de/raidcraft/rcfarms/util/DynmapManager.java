package de.raidcraft.rcfarms.util;

import com.sk89q.worldedit.Vector;
import de.raidcraft.RaidCraft;
import de.raidcraft.rcfarms.tables.TFarm;
import de.raidcraft.rcfarms.tables.TFarmLocation;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

/**
 * Author: Philip
 * Date: 12.12.12 - 22:16
 * Description:
 */
public class DynmapManager {

    private MarkerAPI markerAPI = null;
    private MarkerSet farmsSet = null;

    public DynmapManager() {

        Plugin dynmap = Bukkit.getServer().getPluginManager().getPlugin("dynmap");
        if(dynmap == null) {
            return;
        }
        DynmapAPI api = (DynmapAPI) dynmap;
        markerAPI = api.getMarkerAPI();
        farmsSet = markerAPI.getMarkerSet("farmen");
    }

    public void addFarmMarker(TFarm tFarm) {


        if (markerAPI == null || farmsSet == null) {
            RaidCraft.LOGGER.warning("Dynmap not installed or 'farmen' marker set not available!");
            return;
        }

        removeMarker(tFarm);

        TFarmLocation[] keyPoints = tFarm.getKeyPointArray();

        Vector min = new Vector(Math.min(keyPoints[0].getX(), keyPoints[1].getX()),
                Math.min(keyPoints[0].getY(), keyPoints[1].getY()),
                Math.min(keyPoints[0].getZ(), keyPoints[1].getZ()));
        Vector max = new Vector(Math.max(keyPoints[1].getX(), keyPoints[1].getX()),
                Math.max(keyPoints[0].getY(), keyPoints[1].getY()),
                Math.max(keyPoints[0].getZ(), keyPoints[1].getZ()));

        farmsSet.createMarker(tFarm.getName().toLowerCase().replace(" ", "_")
                , tFarm.getName()
                , tFarm.getBukkitWorld().getName()
                , min.getX() + ((max.getX() - min.getX()) / 2)
                , min.getY()
                , min.getZ() + ((max.getZ() - min.getZ()) / 2)
                , markerAPI.getMarkerIcon("tree")
                , true);
    }

    public void removeMarker(TFarm tFarm) {

        if (farmsSet == null) {
            return;
        }
        for (Marker marker : farmsSet.getMarkers()) {
            if (marker.getLabel().equalsIgnoreCase(tFarm.getName())) {
                marker.deleteMarker();
            }
        }
    }
}
