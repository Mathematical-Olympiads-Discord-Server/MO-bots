package MO.bots.modsbot.commands;

import java.util.HashMap;
import java.util.Map;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Triggers extends ListenerAdapter {
	public Map<String, String> toReply;
	
	public Triggers() {
		toReply = new HashMap<String, String>();
		toReply.put("-hw", "Hello! We are a Mathematical Olympiad discord server. "
				+ "If you want homework help, please visit the **Homework Help** discord server at "
				+ "<https://discord.gg/YudDZtb> or the **Mathematics** discord server at "
				+ "<https://discord.sg/math>. Thank you!");
		
		toReply.put("-hdigh", "You can find the blog \"hdigh\" at https://how-did-i-get-here.com");
		
	}
	
	@Override
	public void onMessageReceived (MessageReceivedEvent event) {
		if (toReply.get(event.getMessage()) != null) {
			event.getChannel().sendMessage(toReply.get(event.getMessage())).complete();
		}
	}
}
