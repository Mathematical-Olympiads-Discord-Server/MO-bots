package MO.bots.cms.commands;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jagrosh.jdautilities.command.CommandEvent;

import MO.bots.MainClass;
import MO.bots.cms.logic.ContestsManager;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.User;

public class NotifyAllCommand extends CommandWithLogging {

	public NotifyAllCommand() {
		this.name = "notifyoftimeslot";
		this.arguments = "position message";
		this.help = "Position is 0-indexed, and message is the message to send";
		this.requiredRole = MainClass.managerRole;
		this.aliases = new String[] {"notify"};
	}

	@Override
	protected void exec(CommandEvent event) {
		String message = event.getArgs();
		
		Set<User> allContestants = new HashSet<User>();
		for (int i = 0; i < ContestsManager.numContests(); i++) {
			allContestants.addAll(ContestsManager.getUsersInContest(i));
		}
		
		for (User u : allContestants) {
			u.openPrivateChannel().queue((PrivateChannel pc) -> {
				String messageToSend = message + "\n" + ContestsManager.userSchedule(u);
				System.out.println(messageToSend);
				pc.sendMessage(messageToSend).queue();
			});
		}
	}

}
