package MO.bots.cms.commands;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import MO.bots.shared.SheetsIntegration;
import net.dv8tion.jda.core.entities.PrivateChannel;

public abstract class CommandWithLogging extends Command {
	private static final String LOGGING_SHEET_ID = "12YgBc7wd_N4x-FKW0qmbJKcXl2XshBcbWdsF1ZMSB2E";
	
	public CommandWithLogging() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void execute(CommandEvent event) {
		long startTime = System.currentTimeMillis();
		this.exec(event);
		long endTime = System.currentTimeMillis();
		
		List<Object> row = Arrays.asList(
			this.name, 
			Instant.now().toString(),
			event.getAuthor().getName(),
			event.getAuthor().getId(),
			endTime - startTime,
			System.getenv("MO-bots-platform")
		);
		
		//Log to sheet
		try {
			SheetsIntegration.appendRow(LOGGING_SHEET_ID, row, "logs");
		} catch (GeneralSecurityException | IOException e) {
			e.printStackTrace();
			event.getGuild().getMemberById(event.getClient().getOwnerId())
				.getUser().openPrivateChannel().queue((PrivateChannel pc) -> {
					StringBuilder sb = new StringBuilder();
					sb.append(e.getMessage()).append("\n");
					for (StackTraceElement ste : e.getStackTrace()) {
						sb.append(ste.toString()).append("\n");
					}
					pc.sendMessage(sb.toString()).complete();
				});
		}

	}
	
	/**
	 * Method to be run when the command executes
	 * @param event 
	 */
	abstract protected void exec(CommandEvent event);

}
