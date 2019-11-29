package MO.bots.cms.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import MO.bots.cms.logic.ContestsManager;

public class CancelCommand extends Command {

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
	protected void execute(CommandEvent event) {
		try {
			ContestsManager.removeContestant(event.getAuthor(), event.getArgs().split(" ")[0], event.getArgs().split(" ")[1]);
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
	}

}
