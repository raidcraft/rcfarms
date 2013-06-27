package de.raidcraft.rcfarms;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.rcconversations.actions.ActionManager;
import de.raidcraft.rcfarms.conversations.FindFarmAction;
import de.raidcraft.rcfarms.tables.TFarms;
import de.raidcraft.rcfarms.upgrades.FarmRestoreFrequencyUpgrade;
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

    @Override
    public void enable() {

        // register conversation actions
        ActionManager.registerAction(new FindFarmAction());

        // register upgrades
        RaidCraft.getComponent(RCUpgradesPlugin.class).getUpgradeManager().registerUpgrade(FarmRestoreFrequencyUpgrade.class);

        worldGuard = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");

        reload();
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

    public class LocalConfiguration extends ConfigurationBase<RCFarmsPlugin> {

        @Setting("farm-region-prefix")
        public String farmPrefix = "itemfarm_";

        @Setting("max-farm-per-player")
        public int playerMaxFarmCount = 3;

        public LocalConfiguration(RCFarmsPlugin plugin) {

            super(plugin, "config.yml");
        }
    }
}
