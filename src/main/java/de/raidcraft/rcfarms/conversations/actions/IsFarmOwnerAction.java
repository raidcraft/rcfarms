package de.raidcraft.rcfarms.conversations.actions;

import de.raidcraft.RaidCraft;
import de.raidcraft.rcconversations.api.action.AbstractAction;
import de.raidcraft.rcconversations.api.action.ActionArgumentException;
import de.raidcraft.rcconversations.api.action.ActionArgumentList;
import de.raidcraft.rcconversations.api.action.ActionInformation;
import de.raidcraft.rcconversations.api.conversation.Conversation;
import de.raidcraft.rcfarms.FarmManager;
import de.raidcraft.rcfarms.RCFarmsPlugin;

/**
 * @author Philip Urban
 */
@ActionInformation(name = "IS_FARM_OWNER")
public class IsFarmOwnerAction extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws ActionArgumentException {

        String farmId = args.getString("farm-id");
        String success = args.getString("onsuccess", null);
        String failure = args.getString("onfailure", null);

        RCFarmsPlugin plugin = RaidCraft.getComponent(RCFarmsPlugin.class);
        FarmManager farmManager = plugin.getFarmManager();

        // check farm id
        if(!plugin.getWorldGuardManager().isFarm(farmId)) {
            changeStage(conversation, failure);
            return;
        }

        // check if conversation player is farm owner
        if(!farmManager.getFarmOwner(farmId).equalsIgnoreCase(conversation.getPlayer().getName())) {
            conversation.set("farm_owner", farmManager.getFarmOwner(farmId));
            changeStage(conversation, failure);
            return;
        }
        changeStage(conversation, success);
    }

    private void changeStage(Conversation conversation, String newStage) {

        if(newStage == null) return;
        conversation.setCurrentStage(newStage);
        conversation.triggerCurrentStage();
    }
}
