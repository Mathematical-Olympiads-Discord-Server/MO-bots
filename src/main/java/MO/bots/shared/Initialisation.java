package MO.bots.shared;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Initialisation extends ListenerAdapter {
	public static Guild tiedGuild;
	public static boolean initialised;
	
	public Initialisation() {
		initialised = false;
	}
	
	public void onMessageReceived (MessageReceivedEvent event) {
		if (initialised) return;
		
		if (event.getGuild() == null) return;
		
		initialised = true;
		tiedGuild = event.getGuild();
	}
}
