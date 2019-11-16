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
public class NewTimeslotUCommand extends Command {

	/**
	 * 
	 */
	public NewTimeslotUCommand() {
		this.name = "newtimeslotu";
		this.help = "Admin command. Adds a new timeslot to the contest. Note that the start and"
				+ "end arguments must be in Unix time. ";
		this.requiredRole = MainClass.managerRole;
		this.aliases = new String[] {"addtimeslotu"};
		this.arguments = "name start end reaction position";
	}

	@Override
	protected void execute(CommandEvent event) {
		
		try {	
			String[] args = event.getArgs().split(" ");
			if (args.length != 5) {
				event.reply("Invalid format. ");
				return;
			} else {
				long s = Long.parseLong(args[1]);
				long e = Long.parseLong(args[2]);
				long r = Long.parseLong(args[3]);
				int p = Integer.parseInt(args[4]);
				event.reply(ContestsManager.newTimeslot(args[0], s, e, r, p));
			}
		} catch (Exception e) {
			event.reply("Invalid format. ");
		}
	}

}
