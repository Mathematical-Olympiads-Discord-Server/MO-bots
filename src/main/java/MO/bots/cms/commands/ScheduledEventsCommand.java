package MO.bots.cms.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import MO.bots.MainClass;
import MO.bots.cms.logic.ContestsManager;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

public class ScheduledEventsCommand extends Command {

	public ScheduledEventsCommand() {
		this.name = "scheduledevents";
		this.aliases = new String[] {"sched", "events"};
		this.requiredRole = MainClass.managerRole;
		this.arguments = "time-sorted or by-timeslot <optional, defaults to time-sorted>";
		this.help = "Admin command - checks for scheduled events. ";
	}

	@Override
	protected void execute(CommandEvent event) {
		boolean byTimeslot = event.getArgs().contentEquals("by-timeslot");
		
		for (String s : CommandEvent.splitMessage(ContestsManager.getSchedule(byTimeslot))) {
			event.reply(s);
		}
	}

}
