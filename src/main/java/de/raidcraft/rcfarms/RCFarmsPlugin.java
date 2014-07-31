package de.raidcraft.rcfarms;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.rcconversations.actions.ActionManager;
import de.raidcraft.rcfarms.commands.FarmCommands;
import de.raidcraft.rcfarms.conversations.actions.wizzard.*;
import de.raidcraft.rcfarms.listener.BlockListener;
import de.raidcraft.rcfarms.tables.TFarm;
import de.raidcraft.rcfarms.tables.TFarmLocation;
import de.raidcraft.rcfarms.tables.TMaterial;
import de.raidcraft.rcfarms.util.DynmapManager;
import de.raidcraft.rcfarms.util.SchematicManager;
import de.raidcraft.rcfarms.util.WorldGuardManager;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Philip Urban
 */
public class RCFarmsPlugin extends BasePlugin {

    private LocalConfiguration config;
    private WorldGuardPlugin worldGuard;
    private WorldEditPlugin worldEdit;
    private WorldGuardManager worldGuardManager;
    private FarmManager farmManager;
    private SchematicManager schematicManager;
    private DynmapManager dynmapManager;

    @Override
    public void enable() {

        // register conversation actions
        // wizard actions
        ActionManager.registerAction(new AddFarmMaterialAction());
        ActionManager.registerAction(new AddFarmNameAction());
        ActionManager.registerAction(new CreateFarmAction());
        ActionManager.registerAction(new SelectFarmAction());
        ActionManager.registerAction(new RemoveFarmMaterialAction());
        ActionManager.registerAction(new UpgradeFarmSchematicAction());
        ActionManager.registerAction(new DeleteFarmSchematicAction());
        ActionManager.registerAction(new EditFarmAction());

        // register commands
        registerCommands(FarmCommands.class);

        // register listener
        registerEvents(new BlockListener());

        // register upgrades
        //RaidCraft.getComponent(RCUpgradesPlugin.class).getUpgradeManager().registerUpgrade(FarmRestoreFrequencyUpgrade.class);

        reload(); // RELOAD BEFORE MANAGERS GET INITIALIZED!!!

        worldGuard = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
        worldEdit = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
        worldGuardManager = new WorldGuardManager(this, worldGuard);
        farmManager = new FarmManager(this);
        schematicManager = new SchematicManager(this);
        dynmapManager = new DynmapManager();

        // regenerate regions at startup (if some are deleted)
        for(World world : Bukkit.getWorlds()) {
            farmManager.generateRegions(world);
        }

        // check each hour all farms for regeneration
        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                for(World world : Bukkit.getWorlds()) {
                    for(TFarm tFarm : RaidCraft.getComponent(RCFarmsPlugin.class).getFarmManager().getFarms(world.getName())) {
                        tFarm.loadChildren();
                        farmManager.checkForRegeneration(tFarm);
                    }
                }
            }
        }, 0, 60 * 60 * 20);
    }

    @Override
    public void reload() {

        config = configure(new LocalConfiguration(this));
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {

        List<Class<?>> databases = new ArrayList<>();
        databases.add(TFarm.class);
        databases.add(TFarmLocation.class);
        databases.add(TMaterial.class);
        return databases;
    }

    @Override
    public void disable() {

        worldGuardManager.save();
    }

    public LocalConfiguration getConfig() {

        return config;
    }

    public WorldGuardPlugin getWorldGuard() {

        return worldGuard;
    }

    public WorldEditPlugin getWorldEdit() {

        return worldEdit;
    }

    public WorldGuardManager getWorldGuardManager() {

        return worldGuardManager;
    }

    public FarmManager getFarmManager() {

        return farmManager;
    }

    public SchematicManager getSchematicManager() {

        return schematicManager;
    }

    public DynmapManager getDynmapManager() {

        return dynmapManager;
    }

    public class LocalConfiguration extends ConfigurationBase<RCFarmsPlugin> {

        @Setting("farm-region-prefix")
        public String farmPrefix = "itemfarm_";

        @Setting("max-farm-per-player")
        public int playerMaxFarmCount = 3;

        @Setting("farm-price-per-block")
        public double pricePerBlock = 0.1;

        @Setting("creating-conversation-name")
        public String creatingConversationName = "farm_create_wizard";

        @Setting("editing-conversation-name")
        public String editingConversationName = "farm_edit_wizard";

        public LocalConfiguration(RCFarmsPlugin plugin) {

            super(plugin, "config.yml");
        }
    }
}
