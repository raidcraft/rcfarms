package de.raidcraft.rcfarms.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.rcconversations.api.action.AbstractAction;
import de.raidcraft.rcconversations.api.action.ActionArgumentException;
import de.raidcraft.rcconversations.api.action.ActionArgumentList;
import de.raidcraft.rcconversations.api.action.ActionInformation;
import de.raidcraft.rcconversations.api.conversation.Conversation;
import de.raidcraft.rcfarms.RCFarmsPlugin;

/**
 * @author Philip Urban
 */
@ActionInformation(name = "FIND_FARM")
public class FindFarmAction extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws ActionArgumentException {

        String success = args.getString("onsuccess", null);
        String failure = args.getString("onfailure", null);

        String farmName = RaidCraft.getComponent(RCFarmsPlugin.class).getWorldGuardManager().getFarmName(conversation.getHost().getLocation());

        if(farmName == null) {
            if(failure != null) {
                conversation.setCurrentStage(failure);
                conversation.triggerCurrentStage();
            }
            return;
        }
        conversation.set("farm_id", farmName);
        if(success != null) {
            conversation.setCurrentStage(success);
            conversation.triggerCurrentStage();
        }
    }
}
