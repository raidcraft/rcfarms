package de.raidcraft.rcfarms.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.conversations.answer.ConfiguredAnswer;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.conversation.ConversationEndReason;
import de.raidcraft.util.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Philip Urban
 */
public class RemoveFarmMaterialInput extends ConfiguredAnswer {

    public RemoveFarmMaterialInput(String type, ConfigurationSection config) {

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

        Material material = ItemUtils.getItem(input);
        if (material == null) {
            conversation.sendMessage(ChatColor.RED + "Unbekanntes Material '" + input + "'! Eingabe wiederholen:");
            return true;
        }

        if (!((FarmConversation) conversation).containsMaterial(material)) {
            conversation.sendMessage("Das Material " + material.name() + " ist aktuell nicht erlaubt und kann daher auch nicht entfernt werden.");
            return true;
        }

        ((FarmConversation) conversation).removeMaterial(material);
        conversation.sendMessage(ChatColor.GREEN + material.name() + " wurde als erlaubtes Material von der Farm entfernt.");
        return true;
    }
}
