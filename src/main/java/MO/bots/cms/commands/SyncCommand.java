/**
 * 
 */
package MO.bots.cms.commands;

import com.jagrosh.jdautilities.command.CommandEvent;

import MO.bots.MainClass;
import MO.bots.cms.logic.ContestsManager;

/**
 * @author IcosahedralDice
 *
 */
public class SyncCommand extends CommandWithLogging {

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
	protected void exec(CommandEvent event) {
		String s = ContestsManager.sync(event);
		event.reply(s);
	}

}
