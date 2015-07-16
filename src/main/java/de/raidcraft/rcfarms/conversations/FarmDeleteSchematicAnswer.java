package de.raidcraft.rcfarms.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.api.conversations.answer.ConfiguredAnswer;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.conversation.ConversationEndReason;
import de.raidcraft.rcfarms.RCFarmsPlugin;
import de.raidcraft.rcfarms.tables.TFarm;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Philip Urban
 */
public class FarmDeleteSchematicAnswer extends ConfiguredAnswer {

    public FarmDeleteSchematicAnswer(String type, ConfigurationSection config) {

        super(type, config);
    }

    @Override
    protected void load(ConfigurationSection args) {


    }

    @Override
    public boolean processInput(Conversation conversation, String input) {

        if (!(conversation instanceof FarmConversation)) {
            RaidCraft.LOGGER.warning("farm conversation type not found!");
            conversation.end(ConversationEndReason.ERROR);
            return false;
        }

        if (((FarmConversation) conversation).getFarmToDelete() == null) {
            // lets take the first input as the farm name
            TFarm tFarm = RaidCraft.getComponent(RCFarmsPlugin.class).getFarmManager().getFarm(input);
            if (tFarm == null) {
                conversation.sendMessage(ChatColor.RED + "Bitte gebe die Datenbank ID oder den Namen der Farm an:");
                return false;
            }
            ((FarmConversation) conversation).setFarmToDelete(tFarm);
            conversation.sendMessage(ChatColor.DARK_AQUA + "Bitte gebe nun das Upgrade Level der Farm an (im Zweifel 1): ");
            // we are returning false so not actions are executed and the answer is reprocessed
            return false;
        } else {
            // we already have a farm so lets ask for the farm upgrade level
            try {
                int upgradeLevel = Integer.parseInt(input);
                if (upgradeLevel < 1) {
                    conversation.sendMessage(ChatColor.RED + "Das Upgrade Level muss > 1 sein.");
                    return false;
                }
                RaidCraft.getComponent(RCFarmsPlugin.class).getSchematicManager()
                        .deleteSchematic(((FarmConversation) conversation).getFarmToDelete(), upgradeLevel);
                return true;
            } catch (NumberFormatException e) {
                conversation.sendMessage(ChatColor.RED + "Bitte gebe eine Zahl als Upgrade Level an!");
                return false;
            } catch (RaidCraftException e) {
                conversation.sendMessage(ChatColor.RED + e.getMessage());
                conversation.end(ConversationEndReason.ERROR);
                return false;
            }
        }
    }
}
