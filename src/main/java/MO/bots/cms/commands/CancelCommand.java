package MO.bots.cms.commands;

import java.util.List;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import MO.bots.cms.logic.ContestsManager;
import net.dv8tion.jda.core.entities.Role;

public class CancelCommand extends CommandWithLogging {

	public CancelCommand() {
		this.name = "cancel";
		this.help = "Cancels a timeslot of yours. ";
		this.arguments = "contest timeslot";
		this.helpBiConsumer = (CommandEvent event, Command command) -> {
			StringBuilder sb = new StringBuilder();
			sb.append("Cancels a timeslot. Your current schedule: \n")
				.append(ContestsManager.userSchedule(event.getAuthor()));
			event.reply(sb.toString());
		};
	}

	@Override
	protected void exec(CommandEvent event) {
		String contest, timeslot;
		try {
			contest = event.getArgs().split(" ")[0];
			timeslot = event.getArgs().split(" ")[1];
			ContestsManager.removeContestant(event.getAuthor(), contest, timeslot);
		} catch (IndexOutOfBoundsException e) {
			event.reply("Not enough arguments!");
			return;
		} catch (IllegalArgumentException e) {
			event.reply(e.getMessage());
			return;
		} catch (Exception e) {
			event.reply("Something went wrong.");
			event.reply(event.getGuild().getMemberById(event.getClient().getOwnerId()).getAsMention());
			return;
		}
		event.reply("Succesfully removed you from the contest. ");
		
		//Remove the timeslot role
		List<Role> tsRoles = event.getGuild().getRolesByName(timeslot, true);
		if (tsRoles.isEmpty())
			return;
		
		event.getGuild().getController().removeRolesFromMember(event.getMember(), tsRoles).queue();
	}

}
