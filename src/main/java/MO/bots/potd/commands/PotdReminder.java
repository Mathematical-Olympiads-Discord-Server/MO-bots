package MO.bots.potd.commands;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import MO.bots.MainClass;
import MO.bots.shared.Initialisation;
import MO.bots.shared.SheetsIntegration;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class PotdReminder {
	public static ScheduledExecutorService executor;
	
	public static void init() {
		executor = Executors.newScheduledThreadPool(2);
		
	    long time = Instant.now().getEpochSecond();
	    long timeUntilNextDay = 86400 - (time % 86400);
	    executor.scheduleAtFixedRate(
	    		new Reminder(), timeUntilNextDay, 1, TimeUnit.DAYS);
	}
}

class Reminder implements Runnable {
	private static final long POTD_START_TIME = 1553558400L;
	final long paradoxId = 419356082981568522L;
	final long dailyId = 561876362776936459L;
	final long potdChannelId = 537763636161150976L;
	long day = -1;
	
	public Reminder() {this.day = -1;}
	
	public Reminder(long day) {
		this.day = day;
	}
	
	public void run() {

		List<List<Object>> potdSheet = null;
		try {
			potdSheet = SheetsIntegration
					.getSheet(PotdCommand.SPREADSHEET_ID, "History!A2:I");
		} catch (GeneralSecurityException | IOException e) {
			e.printStackTrace();
		}
		
		long curDay;
		if (this.day == -1) {
			curDay = (Instant.now().getEpochSecond() - POTD_START_TIME) / 86400 + 1;
		} else {
			curDay = this.day;
		}
		//Get current potd number

		//Get current potd number
		int currentPotd = Integer.parseInt((String) potdSheet.get(0).get(0));
		if (curDay > currentPotd) {
			return;
		}
		
		int rowNumber = (int) (currentPotd - curDay);
		List<Object> potdRow = potdSheet.get(rowNumber);
		
		if (potdRow.size() < 9) {
			//Then this row has not been filled in yet...
			long idtoDm = PotdCommand.curators.get(potdRow.get(3));
			User toDm = Initialisation.tiedGuild.getMemberById(idtoDm).getUser();
			toDm.openPrivateChannel().complete().sendMessage("oi potd please").queue();
			System.out.println("Sent a reminder to " + toDm.getName() + " for potd");
			PotdReminder.executor.schedule(new Reminder(), 1, TimeUnit.HOURS);
		}
		
		
		
		
		StringBuilder toTex = new StringBuilder();
		toTex.append("```tex\n\\textbf{Day ")
			.append(curDay)
			.append("} --- ")
			.append((String) potdRow.get(2))
			.append(" ")
			.append((String) potdRow.get(1))
			.append("\n\\begin{flushleft}\n")
			.append((String) potdRow.get(8))
			.append("\n\\end{flushleft}```");
		System.out.println(toTex.toString());
		
		Message texMessage = Initialisation.tiedGuild.getTextChannelById(potdChannelId).sendMessage(toTex.toString()).complete();
		EventWaiter ew = new EventWaiter();
		MainClass.MODSbot.addEventListener(ew);
		ew.waitForEvent(MessageReceivedEvent.class, 
				
			(MessageReceivedEvent mre) -> {
				System.out.println(mre.getAuthor().getIdLong());
				return (mre.getAuthor().getIdLong() == paradoxId);// && (mre.getChannel().equals(sentChannel));						
			}, 
			
			(MessageReceivedEvent mre) -> {
				Initialisation.tiedGuild.getTextChannelById(potdChannelId).deleteMessageById(texMessage.getIdLong()).queue();
				
				//Construct source message
				StringBuilder source = new StringBuilder();
				String curator = (String) potdRow.get(3);
				Long curatorId = PotdCommand.curators.get(curator);
				if (curatorId == null) {
					//No curator found
					source.append("Unknown curator. ");
				} else {
					source.append("Problem chosen by ")
						.append(mre.getGuild().getMemberById(curatorId).getAsMention())
						.append(". ");
				}
				
				source.append("Source: ||`");
				int sourceLength = ((String) potdRow.get(4)).length();
				source.append((String) potdRow.get(4));
				source.append(" ");
				for (int i = 0; i < 48 - sourceLength; i++) {
					source.append(" ");		//Pad to 49 chars
				}
				source.append((String) potdRow.get(5)).append((String) potdRow.get(6));
				source.append("`|| ");
				
				/*
				Role r = mre.getGuild().getRoleById(dailyId);
				if (r == null) {
					event.reply("No role with that id");
					return;
				}
				r.getManager().setMentionable(true).complete();
				source.append(r.getAsMention());*/
				mre.getTextChannel().sendMessage(source).complete();
				//r.getManager().setMentionable(false).complete();
			});
		
	}
}