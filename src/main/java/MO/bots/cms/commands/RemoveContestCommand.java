package MO.bots.cms.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import MO.bots.MainClass;
import MO.bots.cms.logic.ContestsManager;

public class RemoveContestCommand extends CommandWithLogging {

	public RemoveContestCommand() {
		this.name = "remove";
		this.help = "Removes a contest. ";
		this.requiredRole = MainClass.managerRole;
		this.aliases = new String[] {"rem"};
	}

	@Override
	protected void exec(CommandEvent event) {
		try {
			ContestsManager.removeContest(Integer.parseInt(event.getArgs()));
		} catch (NumberFormatException e ) {
			event.reply("Enter a number!");
			return;
		} catch (IndexOutOfBoundsException e) {
			event.reply("No contest at this position");
			return;
		}
		
		event.reply("Done!");

	}

}
