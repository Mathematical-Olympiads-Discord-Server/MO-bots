package MO.bots.general.sampling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Random;

import MO.bots.shared.SheetsIntegration;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class SampleManager extends ListenerAdapter {

	private static ArrayList<Sample> samples;
	private static Random r;
	
	public SampleManager() {
		r = new Random();
		samples = new ArrayList<Sample>();
	}
	
	public void addSample(int n, String i, double p, TextChannel c) {
		Sample s = new Sample(n, i, p, c);
		samples.add(s);
	}
	
	@Override
	public void onMessageReceived (MessageReceivedEvent event) {
		try {
			for (Sample s : samples) {
				if (r.nextDouble() < s.getProbability()) {
					//Then we have selected this message to be included as a sample
					if (s.recordMessage(event)) {
						//Then this sample is over
						s.getReplyChannel().sendMessage("Sample has finished. Check it at https://docs.google.com/spreadsheets/d/" +
								s.getSpreadsheetId() + "/edit#gid=0").queue();
						samples.remove(s);
					}
				}
			}
		} catch (ConcurrentModificationException cme) {
			System.out.println("Attempting to modify concurrently");
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.onMessageReceived(event);
		}
	}
}

class Sample {
	public static final String SAMPLE_SHEET = "Samples";
	private int numMessages;
	private int currentMessageNumber;
	private String spreadsheetId;
	private double probability;
	private TextChannel replyChannel;
	public String getSpreadsheetId() {return this.spreadsheetId;}
	public double getProbability() {return this.probability;}
	public TextChannel getReplyChannel() {return this.replyChannel;}
	
	public Sample (int n, String i, double p, TextChannel c) {
		this.numMessages = n;
		this.currentMessageNumber = 0;
		this.spreadsheetId = i;
		this.probability = p;
		this.replyChannel = c;
	}
	
	public boolean recordMessage (MessageReceivedEvent e) {
		List<Object> row = Arrays.asList(
				e.getChannel().toString(),
				e.getAuthor().toString(),
				e.getMessage().getCreationTime().toString(),
				e.getMessage().getContentRaw().toString());
		try {
			SheetsIntegration.appendRow(spreadsheetId, row, "Samples");
		} catch (Exception e1) {
			System.out.println("Erorr with sampling. ");
		}
		++this.currentMessageNumber;
		if (this.currentMessageNumber == this.numMessages) {
			return true;
		} else {
			return false;
		}
	}
}