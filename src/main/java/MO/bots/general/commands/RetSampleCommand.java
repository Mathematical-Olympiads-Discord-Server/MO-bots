package MO.bots.general.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import MO.bots.MainClass;
import MO.bots.shared.SheetsIntegration;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.TextChannel;

public class RetSampleCommand extends Command {

	public RetSampleCommand() {
		this.name = "sampleret";
		this.aliases = new String[] {"retsample"};
		this.help = "Samples retroactively from a particular channel";
		this.arguments = "numberToSample numberToSampleFrom sheetId sheetName";
		this.requiredRole = MainClass.managerRole;
	}

	@Override
	protected void execute(CommandEvent event) {
		int num, numFrom; String spreadsheetId, sheetName;
		try {
			String[] args = event.getArgs().split(" ");
			num = Integer.parseInt(args[0]);
			numFrom = Integer.parseInt(args[1]);
			spreadsheetId = args[2];
			sheetName = args[3];
		} catch (Exception e) {
			event.reply("Check your arguments!");
			return;
		}

		TextChannel channelToSample = event.getTextChannel();
		int numHistories = numFrom/100 + 1;		//100 is maximum a history holds
		MessageHistory[] histories = new MessageHistory[numHistories];
		
		histories[0] = MessageHistory.getHistoryBefore(channelToSample, 
				event.getMessage().getId()).limit(100).complete();
		for (int i = 1; i < numHistories - 1; i++) {
			histories[i] = MessageHistory.getHistoryBefore(channelToSample,
					histories[i-1]
							.getRetrievedHistory()
							.get(99)
							.getId())
					.limit(100)
					.complete();
		}
		if (numHistories > 1)
			histories[numHistories-1] = MessageHistory.getHistoryBefore(channelToSample,
				histories[numHistories-2].getRetrievedHistory().get(99).getId()).limit(numFrom % 99).complete();
		
		double probability = (double) num / numFrom;
		if (probability > 1) {
			event.reply("num > numFrom!");
			return;
		}
		
		Random r = new Random();
		List<Message> sample = new ArrayList<Message>((int) ((double) num * 1.05d));
		for (MessageHistory h : histories) {
			for (Message m : h.getRetrievedHistory()) {
				if (r.nextDouble() < probability) {
					sample.add(m);
				}
			}
		}
		
		//Create the list of objects to write to the sheet
		List<List<Object>> toWrite = new ArrayList<> (sample.size());
		for (Message m : sample) {
			toWrite.add(Arrays.asList(
					m.getAuthor().toString(),
					m.getContentRaw(),
					m.getCreationTime().toString()));
		}
		try {
		SheetsIntegration.appendRange(spreadsheetId, sheetName, toWrite);
		} catch (Exception e) {
			event.reply("Something went wrong while trying to access the sheet. ");
			return;
		}
		event.getMessage().delete().complete();
		Message m = event.getTextChannel().sendMessage(String.format("Done! Sampled a total of %d messages. ", sample.size())).complete();
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		m.delete().queue();
	}

}
