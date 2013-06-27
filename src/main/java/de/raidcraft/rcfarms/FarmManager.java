package de.raidcraft.rcfarms;

/**
 * @author Philip Urban
 */
public class FarmManager {

    private RCFarmsPlugin plugin;

    public FarmManager(RCFarmsPlugin plugin) {

        this.plugin = plugin;
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
