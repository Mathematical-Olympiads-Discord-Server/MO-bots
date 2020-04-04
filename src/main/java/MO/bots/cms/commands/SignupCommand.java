package MO.bots.cms.commands;

import java.util.List;

import com.jagrosh.jdautilities.command.CommandEvent;

import MO.bots.cms.logic.ContestsManager;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

public class SignupCommand extends CommandWithLogging {

	public SignupCommand() {
		this.name = "signup";
		this.arguments = "contest timeslot";
		this.help = "Signs you up for a contest. ";
	}

	@Override
	protected void exec(CommandEvent event) {
		String[] args = event.getArgs().split(" ");
		if (args.length == 0)
			args = new String[] {"", ""};
		if (args.length == 1)
			args = new String[] {args[0], ""};
		
		try {
			ContestsManager.signupContestant(event.getAuthor(), args[0], args[1], !event.getArgs().contains("-unofficial"));
			event.reply("Done. Use +when to check your current signups. ");
			//Assign roles
			Member contestant = event.getGuild().getMember(event.getAuthor());
			List<Role> timeslotRoles = event.getGuild().getRolesByName(args[1], true);
			if (timeslotRoles.isEmpty()) {	//No role matches the timeslot. 
				event.reply("Signup complete but role could not be assigned. ");
				System.out.println("No role with name " + args[1]);
				return;
			}
			event.getGuild().getController()
				.addRolesToMember(contestant, timeslotRoles.get(0)).queue();
			
		} catch (ArrayIndexOutOfBoundsException ae) {
			event.reply("Invalid argument format!");
			return;
		} catch (IllegalArgumentException ie) {
			event.reply(ie.getMessage());
			return;
		}
		
	}

}
