package MO.bots.potd.commands;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import javax.imageio.ImageIO;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import MO.bots.MainClass;
import MO.bots.cms.commands.CommandWithLogging;
import MO.bots.shared.SheetsIntegration;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class PotdCommand extends CommandWithLogging {
	static final String SPREADSHEET_ID = "10X0-CCSv7FenZP1YaKlDSE-PD4LSZAgpzT5Rs9F8hvA"; //CONFIG
	
	public PotdCommand() {
		this.name = "potd";
		this.arguments = "number";
		this.requiredRole = MainClass.managerRole;
	}

	@Override
	protected void exec(CommandEvent event) {
		try {
			List<List<Object>> potdSheet = SheetsIntegration
					.getSheet(SPREADSHEET_ID, "History", "A2:I");
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
			final long dailyId = 561876362776936459L;
			
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
					source.append("Source: ||`");
					int sourceLength = ((String) potdRow.get(4)).length();
					source.append((String) potdRow.get(4));
					source.append(" ");
					for (int i = 0; i < 48 - sourceLength; i++) {
						source.append(" ");		//Pad to 49 chars
					}
					source.append((String) potdRow.get(5)).append((String) potdRow.get(6));
					source.append("`||");
					sentChannel.sendMessage(source).complete();
					
					Role r = mre.getGuild().getRoleById(dailyId);
					if (r == null) {
						event.reply("No role with that id");
						return;
					}
					r.getManager().setMentionable(true).complete();
					mre.getChannel().sendMessage(r.getAsMention()).complete();
					r.getManager().setMentionable(false).complete();
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
