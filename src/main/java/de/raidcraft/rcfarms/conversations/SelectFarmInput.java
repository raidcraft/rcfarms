package de.raidcraft.rcfarms.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.conversations.answer.ConfiguredAnswer;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.rcfarms.RCFarmsPlugin;
import de.raidcraft.rcfarms.tables.TFarm;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Philip Urban
 */
public class SelectFarmInput extends ConfiguredAnswer {

    public SelectFarmInput(String type, ConfigurationSection config) {

        super(type, config);
    }

    @Override
    protected void load(ConfigurationSection args) {


    }

    @Override
    public boolean processInput(Conversation conversation, String input) {

        TFarm tFarm = RaidCraft.getComponent(RCFarmsPlugin.class).getFarmManager().getFarm(input);
        if(tFarm == null) {
            printError(conversation, "Es wurde keine passende Farm gefunden!");
            return false;
        }
        conversation.set("farm_id", String.valueOf(tFarm.getId()));

        if (conversation instanceof FarmConversation) {
            ((FarmConversation) conversation).setFarm(tFarm);
        }
        return true;
    }

    private void printError(Conversation conversation, String msg) {

        conversation.sendMessage(ChatColor.RED + msg);
        conversation.sendMessage(ChatColor.AQUA + "Probiere es nochmal: ");
    }
}
