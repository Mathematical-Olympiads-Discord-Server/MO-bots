package MO.bots.modsbot.commands;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jagrosh.jdautilities.command.CommandEvent;

import MO.bots.MainClass;
import MO.bots.cms.commands.CommandWithLogging;
import MO.bots.shared.SheetsIntegration;

public class PunishmentViewCommand extends CommandWithLogging {
	private static final String PUNISHMENT_SHEET = "1BztOVh1_VmtqbkkOwitUTnS_Pqaa3hs5wfHkDaie3FQ";
	
	public PunishmentViewCommand() {
		this.name = "punishments";
		this.help = "Shows punishments of a user. ";
		this.requiredRole = MainClass.managerRole;
	}
	
	@Override
	protected void exec(CommandEvent event) {
		List<List<Object>> punishments;
		try {
			punishments = SheetsIntegration.getSheet(PUNISHMENT_SHEET, "Log", "Log!A3:G");
		} catch (GeneralSecurityException | IOException e) {
			event.reply("Error, check logs");
			return;
		}
		
		String userId = event.getArgs();
		final String DEFAULT_RESPONSE = "Punishments for " + event.getGuild().getMemberById(userId).getUser().getName() + ":```";
		StringBuilder response = new StringBuilder(DEFAULT_RESPONSE);
		for (List<Object> punishment : punishments) {
			String currentUserId = (String) punishment.get(2);
			if (currentUserId.contentEquals(userId)) {
				response.append("Log #")
						.append(punishment.get(0))
						.append(":\t")
						.append(punishment.get(4))
						.append(" at ")
						.append(punishment.get(1))
						.append(" by ")
						.append(punishment.get(5))
						.append(" for ")
						.append(punishment.get(6));
			}
		}
		
		if (DEFAULT_RESPONSE.contentEquals(response)) {
			event.reply("No punishments found. ");
		} else {
			event.reply(response.toString().concat("```"));
		}
	}

}
