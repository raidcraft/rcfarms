package de.raidcraft.rcfarms;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.api.conversations.Conversations;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.rcfarms.commands.FarmCommands;
import de.raidcraft.rcfarms.conversations.AddFarmMaterialInput;
import de.raidcraft.rcfarms.conversations.AllowAllMaterialsAnswer;
import de.raidcraft.rcfarms.conversations.CreateFarmAction;
import de.raidcraft.rcfarms.conversations.DisallowAllMaterialsAnswer;
import de.raidcraft.rcfarms.conversations.FarmConversation;
import de.raidcraft.rcfarms.conversations.FarmDeleteSchematicAnswer;
import de.raidcraft.rcfarms.conversations.FarmNameInput;
import de.raidcraft.rcfarms.conversations.RemoveFarmMaterialInput;
import de.raidcraft.rcfarms.conversations.SelectFarmInput;
import de.raidcraft.rcfarms.conversations.SetExplicitRegenerationDelayInput;
import de.raidcraft.rcfarms.conversations.UpgradeFarmSchematicAction;
import de.raidcraft.rcfarms.listener.BlockListener;
import de.raidcraft.rcfarms.tables.TFarm;
import de.raidcraft.rcfarms.tables.TFarmLocation;
import de.raidcraft.rcfarms.tables.TMaterial;
import de.raidcraft.rcfarms.util.DynmapManager;
import de.raidcraft.rcfarms.util.SchematicManager;
import de.raidcraft.rcfarms.util.WorldGuardManager;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

        registerConversationAPI();
        registerActionAPI();

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

        // check all farms each 30min for regeneration (only between 4am and 7am)
        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {

                // get current time
                Calendar cal = Calendar.getInstance();
                cal.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("HH");
                if(Integer.parseInt(sdf.format(cal.getTime())) < 4 || Integer.parseInt(sdf.format(cal.getTime())) > 7) {
                    return;
                }

                for(World world : Bukkit.getWorlds()) {
                    for(TFarm tFarm : RaidCraft.getComponent(RCFarmsPlugin.class).getFarmManager().getFarms(world.getName())) {
                        tFarm.loadChildren();
                        farmManager.checkForRegeneration(tFarm);
                    }
                }
            }
        }, 0, 30 * 60 * 20);
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

//        worldGuardManager.save();
    }

    private void registerConversationAPI() {

        Conversations.registerConversationType("farm", FarmConversation.class);
        Conversations.registerAnswer("farm-add-material", AddFarmMaterialInput.class);
        Conversations.registerAnswer("farm-remove-material", RemoveFarmMaterialInput.class);
        Conversations.registerAnswer("farm-name", FarmNameInput.class);
        Conversations.registerAnswer("farm-allow-all-materials", AllowAllMaterialsAnswer.class);
        Conversations.registerAnswer("farm-disallow-all-materials", DisallowAllMaterialsAnswer.class);
        Conversations.registerAnswer("farm-delete-schematic", FarmDeleteSchematicAnswer.class);
        Conversations.registerAnswer("farm-select", SelectFarmInput.class);
        Conversations.registerAnswer("farm-set-regenration-delay", SetExplicitRegenerationDelayInput.class);
    }

    private void registerActionAPI() {

        ActionAPI.register(this)
                .action(new UpgradeFarmSchematicAction())
                .action(new CreateFarmAction(), Conversation.class);
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
