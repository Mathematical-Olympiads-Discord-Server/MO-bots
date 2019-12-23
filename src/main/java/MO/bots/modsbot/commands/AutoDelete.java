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
	static final long delay = 2 * 1000;
	
	public AutoDelete() {
		mainTimer = new Timer();
	}
	
	public void onMessageReceived (MessageReceivedEvent event) {
		TimerTask t = new DeleteMessageTask(event.getMessage(), event.getGuild());
		mainTimer.schedule(t, delay);
	}
}

class DeleteMessageTask extends TimerTask {
	private Guild messageGuild;
	private Message messageToDelete;
	static final long LOG_CHANNEL_ID = 559964001724006400L;
	static final String LOG_SHEET_ID = "12YgBc7wd_N4x-FKW0qmbJKcXl2XshBcbWdsF1ZMSB2E";
	
	public DeleteMessageTask(Message m, Guild g) {
		this.messageToDelete = m;
		this.messageGuild = g;
	}
	
	public void run() {
		this.messageToDelete.delete().queue();
		List<Object> row = Arrays.asList((Object) Instant.now().toString(), 
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