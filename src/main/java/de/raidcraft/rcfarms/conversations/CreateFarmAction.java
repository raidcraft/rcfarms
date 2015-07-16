package de.raidcraft.rcfarms.conversations;

import com.sk89q.worldedit.bukkit.selections.Selection;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.conversation.ConversationEndReason;
import de.raidcraft.rcfarms.FarmBuilder;
import de.raidcraft.rcfarms.RCFarmsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Philip Urban
 */
public class CreateFarmAction implements Action<Conversation> {

    @Override
    @Information(
            value = "farm.create",
            desc = "Creates the given farm from the parameters of the FarmConversation",
            aliases = "CREATE_FARM"
    )
    public void accept(Conversation conversation, ConfigurationSection config) {

        if (!(conversation instanceof FarmConversation)) {
            RaidCraft.LOGGER.warning("farm conversation type not found!");
            conversation.end(ConversationEndReason.ERROR);
            return;
        }

        FarmConversation farmConversation = (FarmConversation) conversation;


        if (!farmConversation.isAllowAllMaterials() && farmConversation.getAllowedMaterials().isEmpty()) {
            printError(conversation, "Der Farm ist kein gültiges Material zugewiesen!");
            return;
        }

        Selection selection = RaidCraft.getComponent(RCFarmsPlugin.class).getWorldEdit().getSelection(conversation.getOwner());

        if(selection == null || selection.getMaximumPoint() == null || selection.getMinimumPoint() == null) {
            printError(conversation, "Kein Bereich selektiert!");
            return;
        }

        conversation.sendMessage(ChatColor.GREEN + "Die Farm '" + farmConversation.getFarmName() + "' wird erstellt...");
        // create farm
        FarmBuilder farmBuilder = new FarmBuilder();
        farmBuilder.setName(farmConversation.getFarmName());
        farmBuilder.setCreator(conversation.getOwner().getUniqueId());
        farmBuilder.setWorld(conversation.getOwner().getWorld().getName());
        farmBuilder.addMaterials(farmConversation.getAllowedMaterials());
        farmBuilder.setAllMaterials(farmConversation.isAllowAllMaterials());
        farmBuilder.setExplicitRegenerationInterval(farmConversation.getRegenerationInterval());
        farmBuilder.setMinimumPoint(selection.getMinimumPoint());
        farmBuilder.setMaximumPoint(selection.getMaximumPoint());
        try {
            farmBuilder.createFarm();
        } catch (RaidCraftException e) {
            printError(conversation, e.getMessage());
            return;
        }
        conversation.sendMessage(ChatColor.GREEN + "Der Erstellvorgang wure erfolgreich abgeschlossen!");
    }

    private void printError(Conversation conversation, String msg) {

        conversation.sendMessage(ChatColor.RED + msg);
        conversation.sendMessage(ChatColor.RED + "Das erstellen der Farm wurde abgebrochen!");
        conversation.end(ConversationEndReason.ERROR);
    }
}
