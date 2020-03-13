package MO.bots.potd.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import MO.bots.cms.commands.CommandWithLogging;
import MO.bots.shared.SheetsIntegration;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class PotdViewCommand extends CommandWithLogging {
	static final String SPREADSHEET_ID = "10X0-CCSv7FenZP1YaKlDSE-PD4LSZAgpzT5Rs9F8hvA"; //CONFIG
	protected Map<String, Long> curators = new HashMap<String, Long>(); 
	
	@Override
	protected void execute(CommandEvent event) {
		this.name = "potdview";
		this.arguments = "day";
		this.help = "Displays a particular problem of the day. ";
		
		curators.put("nya10", 174175867268759553L);
		curators.put("brainysmurfs", 281300961312374785L);
		curators.put("Daniel", 118831126239248397L);
		curators.put("Tony", 541318134699786272L);
		curators.put("Sharky", 268970368524484609L);
		curators.put("Will", 429525897167765505L);
		curators.put("tanyushi", 300065144333926400L);
	}
	
	@Override
	public void exec (CommandEvent event) {
		try {
			List<List<Object>> potdSheet = SheetsIntegration
					.getSheet(SPREADSHEET_ID, "History!A2:I");
			int requestedPotd = Integer.parseInt(event.getArgs());
			
			//Get current potd number
			int currentPotd = Integer.parseInt((String) potdSheet.get(0).get(0));
			if (requestedPotd > currentPotd) {
				event.reply("That PoTD has not come out yet!");
				return;
			}
			
			int rowNumber = currentPotd - requestedPotd;
			List<Object> potdRow = potdSheet.get(rowNumber);
			
			StringBuilder toTex = new StringBuilder();
			toTex.append("```tex\n\\textbf{Day ")
				.append(requestedPotd)
				.append("} --- ")
				.append((String) potdRow.get(2))
				.append(" ")
				.append((String) potdRow.get(1))
				.append("\n\\begin{flushleft}\n")
				.append((String) potdRow.get(8))
				.append("\n\\end{flushleft}```");
			System.out.println(toTex.toString());
			
			MessageChannel sentChannel = event.getChannel();
			final long paradoxId = 419356082981568522L;
			
			Message texMessage = event.getTextChannel().sendMessage(toTex.toString()).complete();
			EventWaiter ew = new EventWaiter();
			event.getJDA().addEventListener(ew);
			ew.waitForEvent(MessageReceivedEvent.class, 
					
				(MessageReceivedEvent mre) -> {
					System.out.println(mre.getAuthor().getIdLong());
					return (mre.getAuthor().getIdLong() == paradoxId);// && (mre.getChannel().equals(sentChannel));						
				}, 
				
				(MessageReceivedEvent mre) -> {
					sentChannel.deleteMessageById(texMessage.getIdLong()).queue();
					
					//Construct source message
					StringBuilder source = new StringBuilder();
					String curator = (String) potdRow.get(3);
					source.append("Problem chosen by ")
						.append(curator)
						.append(". ");
					
					source.append("Source: ||`");
					int sourceLength = ((String) potdRow.get(4)).length();
					source.append((String) potdRow.get(4));
					source.append(" ");
					for (int i = 0; i < 48 - sourceLength; i++) {
						source.append(" ");		//Pad to 49 chars
					}
					source.append((String) potdRow.get(5)).append((String) potdRow.get(6));
					source.append("`|| ");
					
					mre.getTextChannel().sendMessage(source).complete();
				});
			
		} catch (Exception e) {
			e.printStackTrace();
			event.reply("Something went wrong!");
			//event.reply(event.getGuild()
							//.getMemberById(event.getClient().getOwnerId())
							//.getAsMention()
							//+ " fix please");
		}
	}
}
