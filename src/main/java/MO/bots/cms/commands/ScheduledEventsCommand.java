package MO.bots.cms.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

public class ScheduledEventsCommand extends Command {

	public ScheduledEventsCommand() {
		this.name = "scheduledevents";
		this.aliases = new String[] {"sched", "events"};
		this.help = "Admin command - checks for scheduled events. ";
	}

	@Override
	protected void execute(CommandEvent event) {
		Member m = event.getGuild().getMember(event.getAuthor());		
		Role staff = event.getGuild().getRolesByName("Staff", false).get(0);
		if (!m.getRoles().contains(staff)) {
			event.reply("Error - no permissions");
			return;
		}
		
		

	}

}
