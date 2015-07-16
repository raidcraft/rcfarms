package de.raidcraft.rcfarms.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.conversation.ConversationEndReason;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Philip Urban
 */
public class SetAllowAllMaterialsAction implements Action<Conversation> {

    @Override
    @Information(
            value = "farm.allow-materials",
            desc = "Sets the farm conversation to allow all or not all materials"
    )
    public void accept(Conversation conversation, ConfigurationSection config) {

        if (!(conversation instanceof FarmConversation)) {
            RaidCraft.LOGGER.warning("farm conversation type not found!");
            conversation.end(ConversationEndReason.ERROR);
            return;
        }
        ((FarmConversation) conversation).setAllowAllMaterials(config.getBoolean("all", true));
    }
}
