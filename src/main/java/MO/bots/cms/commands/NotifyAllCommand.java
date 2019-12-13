package MO.bots.cms.commands;

import java.util.List;

import com.jagrosh.jdautilities.command.CommandEvent;

import MO.bots.MainClass;
import MO.bots.cms.logic.ContestsManager;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.User;

public class NotifyAllCommand extends CommandWithLogging {

	public NotifyAllCommand() {
		this.name = "notifyall";
		this.arguments = "position message";
		this.help = "Position is 0-indexed, and message is the message to send";
		this.requiredRole = MainClass.managerRole;
		this.aliases = new String[] {"notify"};
	}

	@Override
	protected void exec(CommandEvent event) {
		int position = 0;
		try {
			position = Integer.parseInt(event.getArgs().split(" ")[0]);
		} catch (NumberFormatException nfe) {
			event.reply("Invalid position! Please enter an integer");
			return;
		}
		
		List<User> users;
		try {
			users = ContestsManager.getUsersInContest(position);
		} catch (ArrayIndexOutOfBoundsException ae) {
			event.reply("No contest at position " + position);
			return;
		}
		
		String message = event.getArgs().substring(event.getArgs().indexOf(' '));
		for (User u : users) {
			u.openPrivateChannel().queue((PrivateChannel pc) -> {
				pc.sendMessage(message);
			});
		}
	}

}
