package MO.bots.cms.commands;

import java.util.HashMap;
import java.util.Map;

import com.jagrosh.jdautilities.command.CommandEvent;

import MO.bots.MainClass;
import net.dv8tion.jda.core.entities.Role;

public class PingRoleCommand extends CommandWithLogging {
	private static Map<String, Long> convenientRoles;
	
	public PingRoleCommand() {
		this.requiredRole = MainClass.managerRole;
		this.name = "pingrole";
		this.arguments = "rolename";
		
		convenientRoles = new HashMap<String, Long>(); 
		convenientRoles.put("daily", 561876362776936459L);
	}

	@Override
	protected void exec(CommandEvent event) {
		Long l = convenientRoles.get(event.getArgs());
		Role r;
		if (l == null) {
			r = event.getGuild().getRoleById(Long.parseLong(event.getArgs()));
			if (r == null) {
				event.reply("No role with that id");
				return;
			}
		} else {
			r = event.getGuild().getRoleById(l);
		}
		r.getManager().setMentionable(true).complete();
		event.getChannel().sendMessage(r.getAsMention()).complete();
		r.getManager().setMentionable(false).complete();
		event.getMessage().delete().queue();
	}

}
