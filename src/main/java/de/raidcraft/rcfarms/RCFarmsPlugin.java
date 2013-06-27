package de.raidcraft.rcfarms;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.rcconversations.actions.ActionManager;
import de.raidcraft.rcfarms.conversations.BuyFarmAction;
import de.raidcraft.rcfarms.conversations.FindFarmAction;
import de.raidcraft.rcfarms.conversations.GetFarmPriceAction;
import de.raidcraft.rcfarms.tables.TFarms;
import de.raidcraft.rcfarms.upgrades.FarmRestoreFrequencyUpgrade;
import de.raidcraft.rcfarms.util.WorldGuardManager;
import de.raidcraft.rcupgrades.RCUpgradesPlugin;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Philip Urban
 */
public class RCFarmsPlugin extends BasePlugin {

    private LocalConfiguration config;
    private WorldGuardPlugin worldGuard;
    private WorldGuardManager worldGuardManager;
    private FarmManager farmManager;

    @Override
    public void enable() {

        // register conversation actions
        ActionManager.registerAction(new FindFarmAction());
        ActionManager.registerAction(new GetFarmPriceAction());
        ActionManager.registerAction(new BuyFarmAction());

        // register upgrades
        RaidCraft.getComponent(RCUpgradesPlugin.class).getUpgradeManager().registerUpgrade(FarmRestoreFrequencyUpgrade.class);

        reload();

        worldGuard = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
        worldGuardManager = new WorldGuardManager(this, worldGuard);
        farmManager = new FarmManager(this);
    }

    @Override
    public void reload() {

        config = configure(new LocalConfiguration(this));
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {

        List<Class<?>> databases = new ArrayList<>();
        databases.add(TFarms.class);
        return databases;
    }

    @Override
    public void disable() {
    }

    public LocalConfiguration getConfig() {

        return config;
    }

    public WorldGuardPlugin getWorldGuard() {

        return worldGuard;
    }

    public WorldGuardManager getWorldGuardManager() {

        return worldGuardManager;
    }

    public FarmManager getFarmManager() {

        return farmManager;
    }

    public class LocalConfiguration extends ConfigurationBase<RCFarmsPlugin> {

        @Setting("farm-world")
        public String world = "world";

        @Setting("farm-region-prefix")
        public String farmPrefix = "itemfarm_";

        @Setting("max-farm-per-player")
        public int playerMaxFarmCount = 3;

        @Setting("farm-price-per-block")
        public double pricePerBlock = 0.1;

        public LocalConfiguration(RCFarmsPlugin plugin) {

            super(plugin, "config.yml");
        }
    }
}
