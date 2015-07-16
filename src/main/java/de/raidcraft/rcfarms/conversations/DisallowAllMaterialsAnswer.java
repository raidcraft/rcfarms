package de.raidcraft.rcfarms.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.conversations.answer.ConfiguredAnswer;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.conversation.ConversationEndReason;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Philip Urban
 */
public class DisallowAllMaterialsAnswer extends ConfiguredAnswer {

    public DisallowAllMaterialsAnswer(String type, ConfigurationSection config) {

        super(type, config);
    }

    @Override
    protected void load(ConfigurationSection args) {
        //TODO: implement
    }

    @Override
    public boolean processInput(Conversation conversation, String input) {

        if (!(conversation instanceof FarmConversation)) {
            RaidCraft.LOGGER.warning("farm conversation type not found!");
            conversation.end(ConversationEndReason.ERROR);
            return false;
        }
        ((FarmConversation) conversation).setAllowAllMaterials(false);
        return true;
    }
}
