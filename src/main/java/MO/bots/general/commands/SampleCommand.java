package MO.bots.general.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import MO.bots.MainClass;

public class SampleCommand extends Command {

	public SampleCommand() {
		this.name = "sample";
		this.help = "Starts a sample";
		this.requiredRole = MainClass.managerRole;
		this.arguments = "number sheetId probability";
	}

	@Override
	protected void execute(CommandEvent event) {
		String[] args = event.getArgs().split(" ");
		try {
			MainClass.sm.addSample(Integer.parseInt(args[0]), 
					args[1], Double.parseDouble(args[2]), event.getTextChannel());
		} catch (Exception e) {
			event.reply("An error occurred");
			return;
		}
		event.reply("Begin sampling");
	}

}