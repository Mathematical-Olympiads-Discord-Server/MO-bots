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

import MO.bots.MainClass;
import MO.bots.cms.commands.CommandWithLogging;
import MO.bots.shared.SheetsIntegration;
import net.dv8tion.jda.core.EmbedBuilder;

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
			toTex.append("\\textbf{Day ")
				.append(requestedPotd)
				.append("} - ")
				.append((String) potdRow.get(2))
				.append(" ")
				.append((String) potdRow.get(1))
				.append("\\\\\n")
				//.append("\n\\begin{flushleft}\n")
				.append((String) potdRow.get(8));
				//.append("\n\\end{flushleft}");
			System.out.println(toTex.toString());
			StringBuilder newToTex = new StringBuilder();
			
			int last$ = -1;
			boolean inMathMode = false;
			newToTex.append("\\text{");
			for (int i = 0; i < toTex.length(); i++) {
				char c = toTex.charAt(i);
				if (c == '$') {
					if (inMathMode) {
						inMathMode = false;
						newToTex.append("\\text{");
					} else {
						inMathMode = true;
						newToTex.append("}");
					}
				} else {
					newToTex.append(c);
				}
			}
			newToTex.append("}");
			System.out.println(newToTex.toString());
			
		    BufferedImage image = (BufferedImage) TeXFormula.createBufferedImage(newToTex.toString(), TeXFormula.SERIF, 14.0f, new Color(0xff, 0xff, 0xff), new Color(0x00,0x00,0x00));
		    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		    ImageIO.write(image, "png", bytes);
		    event.getTextChannel().sendFile(bytes.toByteArray(), requestedPotd + ".png").queue();
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
