package MO.bots.cms.commands;

import MO.bots.cms.logic.Contest;
import MO.bots.cms.logic.ContestsManager;
import MO.bots.cms.logic.SheetsIntegration;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class AutoLoad extends ListenerAdapter {
	private boolean loaded;
	
	public AutoLoad() {
		loaded = false;
	}

	public void onMessageReceived (MessageReceivedEvent event) {
		if (!loaded) {
			String s = System.getenv("MO-bots-load-sheets");
			if (s == null) {
				return;
			}
			
			String[] sheetIds = s.split(" ");
			for (String id : sheetIds) {
				Contest c;
				try {
					c = SheetsIntegration.loadContest(id, event);
				} catch (Exception e) {
					continue;
				}
				ContestsManager.addNewContest(c);
			}
		}
		loaded = true;
	}
}
