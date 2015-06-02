package de.raidcraft.rcfarms.conversations.actions.wizzard;

import de.raidcraft.rcconversations.api.action.AbstractAction;
import de.raidcraft.rcconversations.api.action.ActionArgumentException;
import de.raidcraft.rcconversations.api.action.ActionArgumentList;
import de.raidcraft.rcconversations.api.action.ActionInformation;
import de.raidcraft.rcconversations.api.conversation.Conversation;

/**
 * @author Philip Urban
 */
@ActionInformation(name = "FARM_DISALLOW_ALL_MATERIALS")
public class DisalowAllFarmMaterialsAction extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws ActionArgumentException {

        conversation.set("all_materials", false);
    }
}
