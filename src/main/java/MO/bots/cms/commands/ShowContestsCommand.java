/**
 * 
 */
package MO.bots.cms.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import MO.bots.cms.logic.ContestsManager;

/**
 * @author IcosahedralDice
 *
 */
public class ShowContestsCommand extends CommandWithLogging {

	/**
	 * 
	 */
	public ShowContestsCommand() {
		this.name = "showcontests";
		this.aliases = new String[] {"list", "show", "showall"};
		this.help = "Shows all contests. Level 1 - just names, Level 2 - timeslots, Level 3 - timeslots + contestants";
		this.guildOnly = false;
		this.arguments = "level";
	}

	@Override
	protected void exec(CommandEvent event) {
		try {
			int level = Integer.parseInt(event.getArgs());
			event.reply(ContestsManager.showContests(level));
		} catch (Exception e) {
			e.printStackTrace();
			event.reply("There was an error - check your arguments. ");
		}
	}

}
