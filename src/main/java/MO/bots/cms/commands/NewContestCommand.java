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
public class NewContestCommand extends CommandWithLogging {

	/**
	 * 
	 */
	public NewContestCommand() {
		this.name = "newcontest";
		this.arguments = "name messageID";
		this.requiredRole = MainClass.managerRole;
		this.aliases = new String[] {"new"};
		this.guildOnly = true;
	}

	@Override
	protected void exec(CommandEvent event) {
		
		String[] args = event.getArgs().split(" ");
		long l = 1;
		try {
			l = Long.parseLong(args[1]);
		} catch (Exception e) {
			event.reply("Error - invalid input format");
			return;
		}
		ContestsManager.newContest(args[0], event.getChannel().getIdLong(), l);
		event.reply("New contest created with name **" + args[0] + "** tied to message ID **" + args[1] + "**");
	}

}
