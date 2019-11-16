package MO.bots.cms.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import MO.bots.MainClass;
import MO.bots.cms.logic.ContestsManager;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

public class ReloadContestCommand extends Command {

	public ReloadContestCommand() {
		this.name = "reload";
		this.arguments = "position(0-indexed)";
		this.requiredRole = MainClass.managerRole;
		this.help = "Admin Command - Re-loads a contest from a sheet. Note that this will over"
				+ "ride **all** data in the contest - so please +sync first if"
				+ "you do not want the data to be overridden. ";
	}

	@Override
	protected void execute(CommandEvent event) {
		try {
			if (event.getArgs().contentEquals("")) {
				ContestsManager.reloadContest(0, event);
				event.reply("Successfully updated the contest at position 0. ");
			} else {
				ContestsManager.reloadContest(Integer.parseInt(event.getArgs()), event);
				event.reply("Successfully updated the contest at position " + Integer.parseInt(event.getArgs()));
			}
		} catch (Exception e) {
			event.reply(e.toString());
		}
	}

}
