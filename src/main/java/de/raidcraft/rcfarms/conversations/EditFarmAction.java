package de.raidcraft.rcfarms.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.conversations.answer.Answer;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.conversation.ConversationEndReason;
import de.raidcraft.api.conversations.stage.Stage;
import de.raidcraft.rcfarms.RCFarmsPlugin;
import de.raidcraft.rcfarms.tables.TFarm;
import de.raidcraft.rcfarms.tables.TMaterial;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;
import java.util.Set;

/**
 * @author Philip Urban
 */
public class EditFarmAction implements Action<Conversation> {

    @Override
    @Information(
            value = "farm.edit",
            desc = "Updates the given farm with all values set in the conversation.",
            aliases = {"EDIT_FARM"}
    )
    public void accept(Conversation conversation, ConfigurationSection config) {

        if (!(conversation instanceof FarmConversation)) {
            RaidCraft.LOGGER.warning("farm conversation type not found!");
            conversation.end(ConversationEndReason.ERROR);
            return;
        }

        FarmConversation farmConversation = (FarmConversation) conversation;

        if (farmConversation.getFarm() == null) {
            Optional<Answer> answer = Answer.of(SelectFarmInput.class, Action.changeStage(conversation.getStageHistory().peek()));
            if (answer.isPresent()) {
                Stage.of(conversation, "Keine Farm ausgewählt. Bitte wähle zuerst eine Farm aus:", answer.get()).changeTo();
            } else {
                printError(conversation, "Keine Farm ausgewählt. Bitte fange von vorne an.");
                return;
            }
        }

        TFarm farm = farmConversation.getFarm();

        // update name
        if(farmConversation.getFarmName() != null) {
            farm.setName(farmConversation.getFarmName());
        }

        // collect new material
        if(!farm.isAllMaterialFarm()) {
            Set<Material> materials = farmConversation.getAllowedMaterials();
            if (materials.isEmpty()) {
                printError(conversation, "Der Farm ist kein gültiges Material zugewiesen!");
                return;
            }

            // delete old material
            for (TMaterial tMaterial : farm.getMaterials()) {
                RaidCraft.getDatabase(RCFarmsPlugin.class).delete(tMaterial);
            }
            farm.setMaterials(null);

            // save new
            for (Material material : materials) {
                TMaterial tMaterial = new TMaterial(material.name(), farm);
                RaidCraft.getDatabase(RCFarmsPlugin.class).save(tMaterial);
                farm.addMaterial(tMaterial);
            }
        }

        RaidCraft.getDatabase(RCFarmsPlugin.class).update(farm);

        RaidCraft.getComponent(RCFarmsPlugin.class).getFarmManager().generateRegion(farm);

        conversation.sendMessage(ChatColor.GREEN + "Die Farm wurde erfolgreich aktualisiert!");
    }

    private void printError(Conversation conversation, String msg) {

        conversation.sendMessage(ChatColor.RED + msg);
        conversation.sendMessage(ChatColor.RED + "Das aktualisieren der Farm wurde abgebrochen!");
        conversation.end(ConversationEndReason.ERROR);
    }
}
