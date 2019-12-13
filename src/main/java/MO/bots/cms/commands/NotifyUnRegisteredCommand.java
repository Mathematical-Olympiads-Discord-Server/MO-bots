package MO.bots.cms.commands;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;

import com.jagrosh.jdautilities.command.CommandEvent;

import MO.bots.MainClass;
import MO.bots.cms.logic.ContestsManager;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.User;

public class NotifyUnRegisteredCommand extends CommandWithLogging {

	public NotifyUnRegisteredCommand() {
		this.requiredRole = MainClass.managerRole;
		this.name = "notify-notimeslot";
		this.arguments = "channelId messageId reactionId";
	}

	final static String noTimeslot = "Hello! Our records show that you have signed up for the MODS Mathematical Olympiad, but have not signed up to both contest days. To sign up, simply choose one timeslot from each day, then copy and paste the corresponding commands into <#537818427675377677>\r\n" + 
			"\r\n" + 
			"**Day 1**\r\n" + 
			"*All following times are in UTC and refer to the 14th of December*\r\n" + 
			"**1A**   SAT 0130 - 0600      `+signup 2019-12-modsmo-d1 Timeslot-1A`\r\n" + 
			"**1B**   SAT 0830 - 1300      `+signup 2019-12-modsmo-d1 Timeslot-1B`\r\n" + 
			"**1C**   SAT 1330 - 1800      `+signup 2019-12-modsmo-d1 Timeslot-1C`\r\n" + 
			"**1D**   SAT 1930 - 2400      `+signup 2019-12-modsmo-d1 Timeslot-1D`\r\n" + 
			"\r\n" + 
			"**Day 2**\r\n" + 
			"*All following times are in UTC and refer to the 15th of December*\r\n" + 
			"**2A**   SUN 0130 - 0600      `+signup 2019-12-modsmo-d2 Timeslot-2A`\r\n" + 
			"**2B**   SUN 0830 - 1300      `+signup 2019-12-modsmo-d2 Timeslot-2B`\r\n" + 
			"**2C**   SUN 1330 - 1800      `+signup 2019-12-modsmo-d2 Timeslot-2C`\r\n" + 
			"**2D**   SUN 1930 - 2400      `+signup 2019-12-modsmo-d2 Timeslot-2D`\r\n" + 
			"\r\n" + 
			"If you wish not to receive any further notifications about the contest, you can unregister by unreacting to the :mods: react in <#575001494731096074>.\r\n" + 
			"\r\n" + 
			"Have a nice day!\r\n" + 
			"MODS";
	
	final static String timeslotOne = "Hello! Our records show that you have signed up for the MODS Mathematical Olympiad, but have not signed up for day 2. To sign up, simply choose one of the timeslots below, then copy and paste the corresponding command into <#537818427675377677>\r\n" + 
			"\r\n" + 
			"**Day 2**\r\n" + 
			"*All following times are in UTC and refer to the 15th of December*\r\n" + 
			"**2A**   SUN 0130 - 0600      `+signup 2019-12-modsmo-d2 Timeslot-2A`\r\n" + 
			"**2B**   SUN 0830 - 1300      `+signup 2019-12-modsmo-d2 Timeslot-2B`\r\n" + 
			"**2C**   SUN 1330 - 1800      `+signup 2019-12-modsmo-d2 Timeslot-2C`\r\n" + 
			"**2D**   SUN 1930 - 2400      `+signup 2019-12-modsmo-d2 Timeslot-2D`\r\n" + 
			"\r\n" + 
			"Have a nice day!\r\n" + 
			"MODS";
	
	final static String timeslotTwo = "Hello! Our records show that you have signed up for the MODS Mathematical Olympiad, but have not signed up for day 1. To sign up, simply choose one of the timeslots below, then copy and paste the corresponding command into <#537818427675377677>\r\n" + 
			"\r\n" + 
			"**Day 1**\r\n" + 
			"*All following times are in UTC and refer to the 14th of December*\r\n" + 
			"**1A**   SAT 0130 - 0600      `+signup 2019-12-modsmo-d1 Timeslot-1A`\r\n" + 
			"**1B**   SAT 0830 - 1300      `+signup 2019-12-modsmo-d1 Timeslot-1B`\r\n" + 
			"**1C**   SAT 1330 - 1800      `+signup 2019-12-modsmo-d1 Timeslot-1C`\r\n" + 
			"**1D**   SAT 1930 - 2400      `+signup 2019-12-modsmo-d1 Timeslot-1D`\r\n" + 
			"\r\n" + 
			"Have a nice day!\r\n" + 
			"MODS";
	
	@Override
	protected void exec(CommandEvent event) {
		String[] args = event.getArgs().split(" ");
		long channelId = Long.parseLong(args[0]);
		long messageId = Long.parseLong(args[1]);
		String reactionName = args[2];
		
		//Get registered users
		Set<User> registeredUsers = new HashSet<User>();
		List<MessageReaction> reactedUsers = event.getGuild()
											.getTextChannelById(channelId)
											.getMessageById(messageId)
											.complete()
											.getReactions();
		for (MessageReaction mr : reactedUsers) {
			//System.out.println(mr.getReactionEmote().getName());
			if (mr.getReactionEmote().getName().contentEquals(reactionName)) {
				registeredUsers.addAll(mr.getUsers().complete());
			}
		}
		Set<User> firstDay = new HashSet<>(ContestsManager.getUsersInContest(0));
		Set<User> secondDay= new HashSet<>(ContestsManager.getUsersInContest(1));
		
		System.out.println("Registered: ");
		for (User u: registeredUsers) {
			System.out.println(u.toString());
		}
		System.out.println();
		
		
		//get those that haven't registered
		Set<User> pickedTimeslot = new HashSet<>();
		pickedTimeslot.addAll(firstDay);
		pickedTimeslot.addAll(secondDay);
		registeredUsers.removeAll(pickedTimeslot);
		for (User u : registeredUsers) {
			System.out.println(u.toString());
			if (!u.isBot())
				u.openPrivateChannel().queue((PrivateChannel pc) -> {
					pc.sendMessage(noTimeslot).queue();
				});
		}
		System.out.println();
		
		//registered for day 1 but not day 2
		Set<User> dayOneCopy = new HashSet<>();
		dayOneCopy.addAll(firstDay);
		dayOneCopy.removeAll(secondDay);
		for (User u: dayOneCopy) {
			System.out.println(u.toString());
			if (!u.isBot())
				u.openPrivateChannel().queue((PrivateChannel pc) -> {
					pc.sendMessage(timeslotOne).queue();
				});
		}
		System.out.println();
		
		//registered for day 2 but not day 1
		Set<User> dayTwoCopy = new HashSet<>();
		dayTwoCopy.addAll(secondDay);
		dayTwoCopy.removeAll(firstDay);
		for (User u: dayTwoCopy) {
			System.out.println(u.toString());
			if (!u.isBot())
				u.openPrivateChannel().queue((PrivateChannel pc) -> {
					pc.sendMessage(timeslotTwo).queue();
				});
		}
		
	}

}
