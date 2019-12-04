package MO.bots.cms.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import MO.bots.MainClass;
import MO.bots.cms.logic.Contest;
import MO.bots.cms.logic.ContestsManager;
import MO.bots.cms.logic.SheetsIntegration;

public class AutoLoadCommand extends CommandWithLogging {

	public AutoLoadCommand() {
		this.name = "autoload";
		this.help = "Loads contest from environment variable";
		this.aliases = new String[] {"al"};
		this.requiredRole = MainClass.managerRole;
	}

	@Override
	protected void exec(CommandEvent event) {
		String s = System.getenv("MO-bots-load-sheets");
		if (s == null) {
			event.reply("No sheets to load");
			return;
		}
		
		String[] sheetIds = s.split(" ");
		for (String id : sheetIds) {
			Contest c;
			try {
				c = SheetsIntegration.loadContest(id, event.getEvent());
			} catch (Exception e) {
				event.reply("Something went wrong");
				StringBuilder sb = new StringBuilder();
				sb.append(e.getMessage()).append("\n");
				for (StackTraceElement ste : e.getStackTrace()) {
					sb.append(ste.toString()).append("\n");
				}
				event.reply(sb.toString());
				continue;
			}
			event.reply("Successfully loaded contest from sheet " + id);
			ContestsManager.addNewContest(c);
		}
	}

}
