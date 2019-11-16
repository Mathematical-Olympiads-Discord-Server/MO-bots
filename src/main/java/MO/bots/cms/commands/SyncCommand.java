/**
 * 
 */
package MO.bots.cms.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import MO.bots.MainClass;
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
		this.requiredRole = MainClass.managerRole;
	}

	@Override
	protected void execute(CommandEvent event) {
		String s = ContestsManager.sync(event);
		event.reply(s);
	}

}
