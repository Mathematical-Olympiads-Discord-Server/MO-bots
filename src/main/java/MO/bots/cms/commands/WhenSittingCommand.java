package MO.bots.cms.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import MO.bots.cms.logic.ContestsManager;

public class WhenSittingCommand extends CommandWithLogging {

	public WhenSittingCommand() {
		this.name = "when";
		this.aliases = new String[] {"personalinfo"};
		this.help = "Tells you all the contests you are sitting and when "
				+ "they are starting. ";
		this.guildOnly = false;
	}

	@Override
	protected void exec(CommandEvent event) {
		event.reply(ContestsManager.userSchedule(event.getAuthor()));

	}

}
