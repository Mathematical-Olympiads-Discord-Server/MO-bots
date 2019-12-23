package MO.bots.modsbot.commands;

import java.util.Timer;
import java.util.TimerTask;

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
		TimerTask t = new DeleteMessageTask(event.getMessage());
		mainTimer.schedule(t, delay);
	}
}

class DeleteMessageTask extends TimerTask {
	Message messageToDelete;
	
	public DeleteMessageTask(Message m) {
		this.messageToDelete = m;
	}
	
	public void run() {
		this.messageToDelete.delete().queue();
	}
}