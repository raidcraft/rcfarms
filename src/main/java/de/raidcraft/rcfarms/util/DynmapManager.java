package de.raidcraft.rcfarms.util;

import de.raidcraft.RaidCraft;
import de.raidcraft.rcfarms.tables.TFarm;
import de.raidcraft.rcfarms.tables.TFarmLocation;
import org.bukkit.Bukkit;
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

    private DynmapAPI api;
    private MarkerAPI markerAPI = null;
    private MarkerSet farmsSet = null;

    public DynmapManager() {

        api = (DynmapAPI) Bukkit.getServer().getPluginManager().getPlugin("dynmap");
        if(api == null) {
            return;
        }
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

        farmsSet.createMarker(tFarm.getName().toLowerCase().replace(" ", "_")
                , tFarm.getName()
                , tFarm.getBukkitWorld().getName()
                , keyPoints[0].getX()
                , keyPoints[0].getY()
                , keyPoints[0].getZ()
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
