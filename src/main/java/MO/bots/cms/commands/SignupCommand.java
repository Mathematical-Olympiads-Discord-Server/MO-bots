package MO.bots.cms.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import MO.bots.cms.logic.ContestsManager;

public class SignupCommand extends Command {

	public SignupCommand() {
		this.name = "signup";
		this.arguments = "contest timeslot";
		this.help = "Signs you up for a contest. ";
	}

	@Override
	protected void execute(CommandEvent event) {
		String[] args = event.getArgs().split(" ");
		try {
			ContestsManager.signupContestant(event.getAuthor(), args[0], args[1]);
		} catch (ArrayIndexOutOfBoundsException ae) {
			event.reply("Invalid argument format!");
			return;
		} catch (IllegalArgumentException ie) {
			event.reply(ie.getMessage());
			return;
		}
		
		event.reply("Done. Use +when to check your current signups. ");
	}

}
