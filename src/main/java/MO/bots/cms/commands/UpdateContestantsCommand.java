/**
 * 
 */
package MO.bots.cms.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import MO.bots.MainClass;
import MO.bots.cms.logic.ContestsManager;

/**
 * @author IcosahedralDice
 *
 */
public class UpdateContestantsCommand extends Command {

	/**
	 * 
	 */
	public UpdateContestantsCommand() {
		this.name = "updatecontestants";
		this.help = "Updates the contestants based on reactions. Admin-only command. ";
		this.guildOnly = true;
		this.requiredRole = MainClass.managerRole;
		this.arguments = "contest-number<optional>";
	}

	@Override
	protected void execute(CommandEvent event) {
		if (event.getArgs().contentEquals("")) {
			//No arguments provided
			event.reply(ContestsManager.updateContestants(0, event));
		} else {
			try {
				event.reply(ContestsManager.updateContestants(Integer.parseInt(event.getArgs()), event));
			} catch (Exception e) {
				event.reply("Invalid input - check your arguments. ");
			}
		}
	}

}
