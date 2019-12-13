/**
 * 
 */
package MO.bots.cms.commands;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.jagrosh.jdautilities.command.CommandEvent;

import MO.bots.MainClass;
import MO.bots.cms.logic.Contest;
import MO.bots.cms.logic.ContestsManager;
import MO.bots.shared.SheetsIntegration;

/**
 * @author IcosahedralDice
 *
 */
public class LoadContestCommand extends CommandWithLogging {

	/**
	 * 
	 */
	public LoadContestCommand() {
		this.name = "loadcontest";
		this.aliases = new String[] {"load"};
		this.arguments = "spreadsheet ID";
		this.requiredRole = MainClass.managerRole;
		this.help = "Admin command. Loads a contest's data from a spreadsheet. ";
	}

	@Override
	protected void exec(CommandEvent event) {
		try {
			Contest c = SheetsIntegration.loadContest(event.getArgs().split(" ")[0], event.getEvent());
			ContestsManager.addNewContest(c);
		} catch (GeneralSecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			event.reply(e.getMessage());
			return;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			event.reply(e.getMessage());
			return;
		}
		event.reply("Successfully loaded a contest from sheet" + event.getArgs().split(" ")[0]);
	}

}
