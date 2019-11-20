package MO.bots.cms.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import MO.bots.MainClass;
import net.dv8tion.jda.core.entities.Role;

public class PingRoleCommand extends Command {

	public PingRoleCommand() {
		this.requiredRole = MainClass.managerRole;
		this.name = "pingrole";
		this.arguments = "rolename";
	}

	@Override
	protected void execute(CommandEvent event) {
		Role r = event.getGuild().getRoleById(Long.parseLong(event.getArgs()));
		if (r == null) {
			event.reply("No role with that id");
			return;
		}
		System.out.println("Got here");
		r.getManager().setMentionable(true).complete();
		event.getChannel().sendMessage(r.getAsMention()).complete();
		r.getManager().setMentionable(false).complete();
		event.getMessage().delete().queue();
		System.out.println("got here");
	}

}
