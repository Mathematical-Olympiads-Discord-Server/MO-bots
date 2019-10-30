/**
 * 
 */
package MO.bots.cms.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import MO.bots.cms.logic.ContestsManager;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

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
		this.arguments = "contest-number<optional>";
	}

	@Override
	protected void execute(CommandEvent event) {
		Member m = event.getGuild().getMember(event.getAuthor());		
		Role staff = event.getGuild().getRolesByName("Staff", false).get(0);
		if (!m.getRoles().contains(staff)) {
			event.reply("Error - no permissions");
			return;
		}
		
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
