/**
 * 
 */
package MO.bots.cms.commands;

import com.jagrosh.jdautilities.command.CommandEvent;

import MO.bots.MainClass;
import MO.bots.cms.logic.ContestsManager;


/**
 * @author IcosahedralDice
 *
 */
public class NewTimeslotCommand extends CommandWithLogging {

	/**
	 * 
	 */
	public NewTimeslotCommand() {
		this.name = "newtimeslot";
		this.aliases = new String[] {"addtimeslot"};
		this.requiredRole = MainClass.managerRole;
		this.help = "Adds a timeslot. Note start and end have to be YYYY-MM-DDTHH:mm:ssZ, e.g. 2019-11-11T13:00:00Z";
		this.arguments = "name start end reactionID position<optional>";
	}

	@Override
	protected void exec(CommandEvent event) {
		
		try {
			String[] args = event.getArgs().split(" ");
			if (args.length < 4 || args.length > 5) {
				event.reply("Invalid argument format. ");
			} else if (args.length == 4) {
				event.reply(ContestsManager.newTimeslot(args[0], args[1], args[2], Long.parseLong(args[3])));
			} else {
				event.reply(ContestsManager.newTimeslot(args[0], args[1], args[2], Long.parseLong(args[3]), Integer.parseInt(args[4])));
			}
		} catch (Exception e) {
			event.reply("Invalid format. ");
		}
	}

}
