package de.raidcraft.rcfarms.upgrades;

import de.raidcraft.rcupgrades.api.upgrade.AbstractUpgrade;
import de.raidcraft.rcupgrades.api.upgrade.UpgradeInformation;

/**
 * @author Philip Urban
 */
@UpgradeInformation(name = "FARM_RESTORE_FREQUENCY")
public class FarmRestoreFrequencyUpgrade extends AbstractUpgrade {

    private int currentLevel;

    @Override
    public int getCurrentLevel() {

        return currentLevel;
    }

    @Override
    public void increaseLevel() {
        //TODO: implement
    }

    @Override
    public void decreaseLevel() {
        //TODO: implement
    }

    @Override
    public void setLevel(int level) {
        //TODO: implement
    }
}
