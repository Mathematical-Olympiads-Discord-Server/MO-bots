package MO.bots.cms.commands;

import java.util.ArrayList;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.utils.tuple.ImmutablePair;

public class RemoveRoleCommand extends CommandWithLogging {

	private static ArrayList<ImmutablePair<String, Long[]>> shortcuts = null;
	
	public RemoveRoleCommand() {
		this.name = "removeroles";
		this.help = "Admin command - removes all particular roles";
		this.arguments = "id";
		this.requiredRole = "Staff";
		
		//Initialise the shortcuts ArrayList. If you are using this on your own server
		//you may want to change these shortcuts to match the correct roles. 
		shortcuts = new ArrayList<ImmutablePair<String, Long[]>>(3);
		shortcuts.add(new ImmutablePair<>("Finished", new Long[] {575263156562296842L}));
		shortcuts.add(new ImmutablePair<>("Timeslots", new Long[] {575410334417027076L, 
						 575410708221657100L, 575410724138909697L, 575410731466620938L,
						 575410738638749727L, 584490941998432394L
				 }));
		shortcuts.add(new ImmutablePair<>("Signup", new Long[] {575008925058072596L}));
		
		this.helpBiConsumer = (CommandEvent event, Command c) -> {
			StringBuilder sb = new StringBuilder();
			sb.append("Removes a specified role (or a list of roles) from all users within the server. \nShortcuts:\n");
			
			for (ImmutablePair<String, Long[]> p : shortcuts) {
				sb.append(p.left).append(": " );
				for (Long l : p.right) {
					sb.append(event.getGuild().getRoleById(l).getName())
					  .append(" (id: ").append(l).append(") ");
				}
				sb.append("\n");
			}
			
			event.reply(sb.toString());
		};
	}

	@Override
	protected void exec(CommandEvent event) {
		//Check whether this is a "shortcut"
		for (ImmutablePair<String, Long[]> p : shortcuts) {
			if (p.left.equals(event.getArgs())) {
				//Inform user of roles to be removed
				StringBuilder sb = new StringBuilder();
				sb.append("Shortcut ").append(p.left).append(" activated. Removing the following roles: ");
				for (Long l : p.right) {
					sb.append(event.getGuild().getRoleById(l).getName())
					  .append(" (id: ").append(l).append(") ");
				}
				event.reply(sb.toString());
				
				//Actually remove those roles
				for (long l : p.right) {
					Role toRemove = event.getGuild().getRoleById(l);
					if (toRemove == null) {
						event.reply("No role with id" + l);
						continue;
					}
					
					for (Member m : event.getGuild().getMembersWithRoles(toRemove)) {
						event.getGuild().getController().removeRolesFromMember(m, toRemove).queue();
					}
					event.reply("Removed all members from role " + toRemove.getName() + " (id: " + toRemove.getId() + ")");
				}
				return;  //We are done
			}
		}
		
		
		//If not shortcut, then get the specified role:
		try {
			Role toRemove = event.getGuild().getRoleById(Long.parseLong(event.getArgs()));
			if (toRemove == null) {
				event.reply("No role found with that id!");
				return;
			}
			
			for (Member m : event.getGuild().getMembersWithRoles(toRemove)) {
				event.getGuild().getController().removeRolesFromMember(m, toRemove).queue();
			}
			event.reply("Done!");
		} catch (NumberFormatException n) {
			event.reply("Invalid ID");
		}
	}

}
