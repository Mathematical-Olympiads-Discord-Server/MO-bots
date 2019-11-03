/**
 * 
 */
package MO.bots.cms.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import MO.bots.cms.logic.ContestsManager;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

/**
 * @author IcosahedralDice
 *
 */
public class SyncCommand extends Command {

	/**
	 * 
	 */
	public SyncCommand() {
		this.name = "sync";
		this.help = "Admin command - syncs with Sheet";
		this.arguments = "position <optional>";
	}

	@Override
	protected void execute(CommandEvent event) {
		Member m = event.getGuild().getMember(event.getAuthor());		
		Role staff = event.getGuild().getRolesByName("Staff", false).get(0);
		if (!m.getRoles().contains(staff)) {
			event.reply("Error - no permissions");
			return;
		}
		
		String s = ContestsManager.sync(event);
		event.reply(s);
	}

}
