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
public class ShowContestsCommand extends Command {

	/**
	 * 
	 */
	public ShowContestsCommand() {
		this.name = "showcontests";
		this.aliases = new String[] {"list", "show", "showall"};
		this.help = "Shows all contests";
		this.guildOnly = false;
	}

	@Override
	protected void execute(CommandEvent event) {
		event.reply(ContestsManager.listContests());

	}

}
