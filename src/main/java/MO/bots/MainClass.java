package MO.bots;

import java.util.Scanner;

import javax.security.auth.login.LoginException;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.Command.Category;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.command.CommandEvent;

import MO.bots.cms.commands.*;
import MO.bots.general.commands.*;
import MO.bots.general.sampling.SampleManager;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;

public class MainClass {
	public static final String managerRole = "CMS-Authorised";
	
	
	public static Scanner sc = new Scanner(System.in);
	public static Category[] categories = {new Category("Admin"),
											new Category("Misc"),
											new Category("User help")
	};
	
	public static SampleManager sm;
	
	public static void main (String[] args) throws LoginException {
		sm = new SampleManager();
		CommandClientBuilder cmsBuilder = new CommandClientBuilder();
		cmsBuilder.setPrefix("+");
		cmsBuilder.useDefaultGame();
		cmsBuilder.setOwnerId("281300961312374785");
		cmsBuilder.addCommands(new PingCommand(),
								new WhenSittingCommand(),
								new ShowContestsCommand(),
								new VerifyCommand(),
								new HomeworkCommand(),
								new NewContestCommand(),
								new NewTimeslotCommand(),
								new NewTimeslotUCommand(),
								new UpdateContestantsCommand(),
								new LoadContestCommand(),
								new SyncCommand(),
								new ScheduledEventsCommand(),
								new ReloadContestCommand(),
								new SignupCommand(),
								new RemoveRoleCommand(),
								new NotifyAllCommand(),
								new PingRoleCommand(),
								new RecordStatsCommand(),
								new SampleCommand(),
								new RemoveContestCommand(),
								new AutoLoadCommand());
		cmsBuilder.setHelpConsumer((CommandEvent event) -> {
			final String managerRole = MainClass.managerRole;
			
			CommandClient client = event.getClient();
			
			if (event.getSelfMember() == null) {	//Check to see if it is in a guild
				event.reply("Please use this command in a server. ");
				return;
			}
			
			//Checks whether the author is a staff member or not. 
			boolean seeStaffCommands = event.getGuild().getMemberById(event.getAuthor().getId())
					.getRoles().contains(event.getGuild().getRolesByName("Staff", true).get(0));
			
			StringBuilder sb = new StringBuilder("**Contest Management System** commands:\n\n");
			sb.ensureCapacity(2000);
			if (!seeStaffCommands)
				sb.append("___Note: only showing non-staff commands___\n");
			for (Command c : client.getCommands()) {
				if (c.getRequiredRole() == null) {
					sb.append("``").append(client.getPrefix()).append(c.getName())
					.append("``").append(" - ").append(c.getHelp()).append("\n");
					
				} else if (c.getRequiredRole().contentEquals(managerRole)) {
					//This is a staff command - only show to staff members
					if (seeStaffCommands) {
						sb.append("``").append(client.getPrefix()).append(c.getName())
							.append("``").append(" - ").append(c.getHelp()).append("\n");
					}
				} else {
					sb.append("``").append(client.getPrefix()).append(c.getName())
						.append("``").append(" - ").append(c.getHelp()).append("\n");
				}
			}
			event.getAuthor().openPrivateChannel().complete().sendMessage(sb.toString()).queue();
		});
		JDABuilder cms = new JDABuilder(AccountType.BOT);
		String token = System.getenv("CMSAPITOKEN");
		if (token == null) {
			System.out.print("Input API Token: ");
			token = sc.nextLine();
		}
		cms.setToken(token);
		cms.addEventListener(cmsBuilder.build());
		cms.addEventListener(sm);
		//cms.addEventListener(new AutoAddContestant());
		cms.build();
	}
}
