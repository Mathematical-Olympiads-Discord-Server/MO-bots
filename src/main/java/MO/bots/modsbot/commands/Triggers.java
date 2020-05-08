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
		
		toReply.put("-resources", "-  MODS Resources — http://bit.ly/mods-resources\r\n" + 
				"-  A list of useful theorems and proof techniques — http://bit.ly/MO_things_to_know\r\n" + 
				"-  how did I get here? — https://how-did-i-get-here.com/olympiad\r\n" + 
				"-  An IMO results visualiser — http://olivernash.org/2017/08/05/visualising-imo-results/index.html?2017-1\r\n" + 
				"-  Study Groups Portal by @Jem — https://discord.gg/BfUysmw");
		
		toReply.put("-dropbox", "Dropbox containing handouts from national camps and elsewhere: "
				+ "https://www.dropbox.com/sh/w9mfy9qtjs68xzc/AADnnQKWONBsboMGVDiuS-kAa?dl=0");
	}
	
	@Override
	public void onMessageReceived (MessageReceivedEvent event) {
		if (toReply.get(event.getMessage().getContentRaw()) != null) {
			event.getChannel().sendMessage(toReply.get(event.getMessage().getContentRaw())).complete();
			return;
		}
		String s = event.getMessage().getContentRaw().toLowerCase();
		if (s.contains("help") && s.contains("with") && (s.contains("homework") || s.contains("hw"))) {
			event.getChannel().sendMessage("Hello! We are a Mathematical Olympiad discord server. "
				+ "If you want homework help, please visit the **Homework Help** discord server at "
				+ "<https://discord.gg/YudDZtb> or the **Mathematics** discord server at "
				+ "<https://discord.sg/math>. Thank you!").complete();
			return;
		}
	}
}
