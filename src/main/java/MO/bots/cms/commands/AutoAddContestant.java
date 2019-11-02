package MO.bots.cms.commands;

import MO.bots.cms.logic.ContestsManager;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class AutoAddContestant extends ListenerAdapter {

	public AutoAddContestant() {
		
	}

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {
		ContestsManager.autoReactionAdd(event);
	}


}
