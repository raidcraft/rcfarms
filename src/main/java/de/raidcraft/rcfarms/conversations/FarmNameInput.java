package de.raidcraft.rcfarms.conversations;

import de.raidcraft.RaidCraft;
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
public class FarmNameInput extends ConfiguredAnswer {

    public FarmNameInput(String type, ConfigurationSection config) {

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
            return true;
        }

        String name = input.trim();

        if(name.isEmpty() || name.length() < 3) {
            conversation.sendMessage(ChatColor.RED + "Der Name muss mindestens 3 Zeichen lang sein!");
            return false;
        }

        if(name.contains("_")) {
            conversation.sendMessage(ChatColor.RED + "Der Name darf keine Unterstriche enthalten!");
            return false;
        }

        try {
            Integer.parseInt(name);
            conversation.sendMessage(ChatColor.RED + "Der Name darf keine Zahl sein!");
            return false;
        } catch(NumberFormatException ignored) {}

        TFarm tFarm = RaidCraft.getComponent(RCFarmsPlugin.class).getFarmManager().getFarm(name);
        if(tFarm != null) {
            conversation.sendMessage(ChatColor.RED + "Eine Farm mit dem Namen '" + tFarm.getName() + "' existiert bereits!");
            return false;
        }

        ((FarmConversation) conversation).setFarmName(name);
        return true;
    }
}
