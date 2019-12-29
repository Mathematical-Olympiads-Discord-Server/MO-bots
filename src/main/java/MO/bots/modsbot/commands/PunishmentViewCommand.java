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
	
	private static Map<Integer, String> punishmentNames;
	
	public PunishmentViewCommand() {
		this.name = "punishments";
		this.help = "Shows punishments of a user. ";
		this.requiredRole = MainClass.managerRole;
		
		punishmentNames = new HashMap<Integer, String>();
		punishmentNames.put(0, "Unofficial Warning");
		punishmentNames.put(1, "Official Warning");
		punishmentNames.put(2, "Mute");
		punishmentNames.put(3, "Kick");
		punishmentNames.put(4, "Ban");
	}
	
	@Override
	protected void exec(CommandEvent event) {
		List<List<Object>> punishments;
		try {
			punishments = SheetsIntegration.getSheet(PUNISHMENT_SHEET, "Sheet1", "Sheet1!A1:G");
		} catch (GeneralSecurityException | IOException e) {
			event.reply("Error, check logs");
			return;
		}
		
		String userId = event.getArgs();
		final String DEFAULT_RESPONSE = "Punishments for " + event.getGuild().getMemberById(userId).getUser().getName() + ":```";
		StringBuilder response = new StringBuilder(DEFAULT_RESPONSE);
		for (List<Object> punishment : punishments) {
			String currentUserId = (String) punishment.get(1);
			if (currentUserId.contentEquals(userId)) {
				response.append(punishmentNames.get(Integer.parseInt((String) punishment.get(2))))
						.append(" at ")
						.append((String) punishment.get(3))
						.append(" by ")
						.append((String) punishment.get(5))
						.append(". Reason: ")
						.append((String) punishment.get(6))
						.append("\n");
			}
		}
		
		if (DEFAULT_RESPONSE.contentEquals(response)) {
			event.reply("No punishments found. ");
		} else {
			event.reply(response.toString().concat("```"));
		}
	}

}
