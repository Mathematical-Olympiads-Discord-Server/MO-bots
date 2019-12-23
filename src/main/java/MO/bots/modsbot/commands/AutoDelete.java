package MO.bots.modsbot.commands;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import MO.bots.shared.SheetsIntegration;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class AutoDelete extends ListenerAdapter {
	Timer mainTimer;
	static final long delay = 15 * 1000;
	static final long AUTODELETE_CHANNEL = 658511493691605008L;
	
	public AutoDelete() {
		mainTimer = new Timer();
	}
	
	public void onMessageReceived (MessageReceivedEvent event) {
		if (event.getChannel().getIdLong() != AUTODELETE_CHANNEL) 
			return;
		
		long delayToUse = delay;
		try {
			String identifier = event.getMessage().getContentRaw().substring(0, 2);
			int time = Integer.parseInt(event.getMessage().getContentRaw().split(" ")[1]);
			if (identifier.contentEquals("-s")) {
				delayToUse = time * 1000;
			} else if (identifier.contentEquals("-m")) {
				delayToUse = time * 60000;
			} else if (identifier.contentEquals("-h")) {
				delayToUse = time * 3600000;
			} else if (identifier.contentEquals("-d")) {
				delayToUse = time * 86400000;
			} 
		} catch (Exception e) {
			
		}
		
		TimerTask t = new DeleteMessageTask(event.getMessage(), event.getGuild());
		mainTimer.schedule(t, delayToUse);
	}
}

class DeleteMessageTask extends TimerTask {
	private Message messageToDelete;
	static final long LOG_CHANNEL_ID = 559964001724006400L;
	static final String LOG_SHEET_ID = "12YgBc7wd_N4x-FKW0qmbJKcXl2XshBcbWdsF1ZMSB2E";
	
	public DeleteMessageTask(Message m, Guild g) {
		this.messageToDelete = m;
	}
	
	public void run() {
		this.messageToDelete.delete().queue();
		List<Object> row = Arrays.asList((Object) this.messageToDelete.getCreationTime().toString(),
				(Object) this.messageToDelete.getAuthor().toString(), 
				this.messageToDelete.getAuthor().getId(), 
				this.messageToDelete.getContentRaw());
		try {
			SheetsIntegration.appendRow(LOG_SHEET_ID, row, "autodelete log");
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}