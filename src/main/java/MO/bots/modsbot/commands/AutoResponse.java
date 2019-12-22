package MO.bots.modsbot.commands;

import java.util.HashMap;
import java.util.Map;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class AutoResponse extends ListenerAdapter {
	private static Map<Long, String> autoResponses;
	
	public AutoResponse() {
		autoResponses = new HashMap<Long, String>();
		autoResponses.put(652813198667284483L, "yes");
	}
	
	public void onMessageReceived (MessageReceivedEvent event) {
		if (event.getAuthor().isBot()) return;
		
		if (autoResponses.containsKey(event.getChannel().getIdLong())) {
			event.getChannel().sendMessage(autoResponses.get(event.getChannel().getIdLong())).queue();
		}
	}
}