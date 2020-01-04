package MO.bots.cms.commands;

import java.util.List;

import com.jagrosh.jdautilities.command.CommandEvent;

import MO.bots.MainClass;
import MO.bots.cms.logic.ContestsManager;
import net.dv8tion.jda.core.entities.User;

public class PingAllInContestCommand extends CommandWithLogging {
	public PingAllInContestCommand() {
		this.name = "pingallincontest";
		this.help = "pings all in contest";
		this.requiredRole = MainClass.managerRole;
	}
	@Override
	protected void exec(CommandEvent event) {
		StringBuilder pingString = new StringBuilder();
		List<User> usersToPing;
		try {
			usersToPing = ContestsManager.getUsersInContest(Integer.parseInt(event.getArgs()));
		} catch (Exception e) {
			event.reply("Please enter a valid contest");
			return;
		}
		for (User u: usersToPing) {
			pingString.append(u.getAsMention())
				.append(" ");
		}
		event.reply(pingString.toString());

	}

}
