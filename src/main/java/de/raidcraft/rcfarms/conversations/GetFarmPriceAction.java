package de.raidcraft.rcfarms.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.rcconversations.api.action.*;
import de.raidcraft.rcconversations.api.conversation.Conversation;
import de.raidcraft.rcfarms.RCFarmsPlugin;

/**
 * @author Philip Urban
 */
@ActionInformation(name = "GET_FARM_PRICE")
public class GetFarmPriceAction extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws ActionArgumentException {

        String farmId = args.getString("farm-id");

        RCFarmsPlugin plugin = RaidCraft.getComponent(RCFarmsPlugin.class);

        // check farm id
        if(!plugin.getWorldGuardManager().isFarm(farmId)) {
            throw new WrongArgumentValueException("Wrong argument value in action '" + getName() + "': Farm '" + farmId + "' does not exist!");
        }

        // check players balance
        double price = plugin.getFarmManager().getFarmPrice(farmId);
        conversation.set("farm_price", price);
    }
}
