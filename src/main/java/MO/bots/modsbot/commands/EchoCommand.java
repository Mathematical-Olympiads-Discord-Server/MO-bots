package MO.bots.modsbot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;

import MO.bots.MainClass;
import MO.bots.cms.commands.CommandWithLogging;

public class EchoCommand extends CommandWithLogging{
	public EchoCommand() {
		this.name = "echo";
		this.help = "Echos";
		this.requiredRole = MainClass.managerRole;
	}
	
	public void exec (CommandEvent event) {
		event.reply(event.getArgs());
	}
}
