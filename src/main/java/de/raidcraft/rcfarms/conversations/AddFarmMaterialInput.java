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
public class AddFarmMaterialInput extends ConfiguredAnswer {

    public AddFarmMaterialInput(String type, ConfigurationSection config) {

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
            return false;
        }

        ((FarmConversation) conversation).getAllowedMaterials().add(material);
        conversation.sendMessage(ChatColor.GREEN + material.name() + " wurde der Farm als erlaubtes Material hinzugefügt.");
        return true;
    }
}
