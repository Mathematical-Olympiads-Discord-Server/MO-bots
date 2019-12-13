package MO.bots.general.commands;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import MO.bots.MainClass;
import MO.bots.shared.SheetsIntegration;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

public class RecordStatsCommand extends Command {
	public static final String statsSheetId = "1kjzY_D0M6RufjVoNd13vUsBsvP5GLEnEYLavn4HVyG4";
	
	public RecordStatsCommand() {
		this.name = "record";
		this.help = "Records stats";
		this.requiredRole = MainClass.managerRole;
	}

	@Override
	protected void execute(CommandEvent event) {
		//Get date in format required
		DateTimeFormatter formatter =
			    DateTimeFormatter.ofPattern("d MMM")
			                     .withZone( ZoneId.systemDefault() );
		String date = formatter.format(Instant.now().minus(Duration.ofDays(1)));
		String time = Instant.now().toString();
		
		//Calculate number of members
		int total = event.getGuild().getMembers().size();
		
		//Calculate number of verified
		int verified = 0;
		Role unverifiedRole = event.getGuild().getRolesByName("unverified", true).get(0);
		for (Member m : event.getGuild().getMembers()) {
			verified += (!m.getRoles().contains(unverifiedRole) && !m.getUser().isBot()) ? 1 : 0;
		}
		
		//Calculate rct%
		double rct = 100 * (double) verified/total;
		
		int unrct = total - verified;
		
		List<Object> row = Arrays.asList(
			date,
			total,
			verified,
			"",
			"",
			rct,
			"",
			unrct,
			"",
			"",
			time
		);
		
		try {
			SheetsIntegration.appendRow(statsSheetId, row, "Activity");
			StringBuilder sb = new StringBuilder();
			sb.append("Stats recorded. Total: ").append(total).append(", Verified: ")
				.append(verified).append(", rct%: ").append(rct).append(", unrct: ").append(unrct);
			event.reply(sb.toString());
		} catch (Exception e) {
			event.reply("Something went wrong...");
			StringBuilder sb = new StringBuilder();
			sb.append(e.getMessage());
			for (StackTraceElement ste : e.getStackTrace()) {
				sb.append(ste.toString()).append("\n");
			}
			event.reply(sb.toString());
		}
	}
}
