package MO.bots;

import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.security.auth.login.LoginException;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.Command.Category;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.command.CommandEvent;

import MO.bots.cms.commands.*;
import MO.bots.general.sampling.SampleManager;
import MO.bots.modsbot.commands.AutoDelete;
import MO.bots.modsbot.commands.AutoResponse;
import MO.bots.modsbot.commands.EchoCommand;
import MO.bots.modsbot.commands.PunishmentViewCommand;
import MO.bots.modsbot.commands.RecordStatsCommand;
import MO.bots.modsbot.commands.RetSampleCommand;
import MO.bots.modsbot.commands.SampleCommand;
import MO.bots.modsbot.commands.Triggers;
import MO.bots.modsbot.commands.Verification;
import MO.bots.modsbot.commands.VerifyCommand;
import MO.bots.potd.commands.PotdCommand;
import MO.bots.potd.commands.PotdViewCommand;
import MO.bots.shared.Initialisation;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

public class MainClass {
	public static final String managerRole = "CMS-Authorised";
	
	
	public static Scanner sc = new Scanner(System.in);
	public static Category[] categories = {new Category("Admin"),
											new Category("Misc"),
											new Category("User help")
	};
	
	public static Consumer<CommandEvent> help = (CommandEvent event) -> {
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
	};
	
	public static SampleManager sm;
	
	public static JDA MODSbot;
	public static JDA CMSbot;
	
	public static void main (String[] args) throws LoginException {
		sm = new SampleManager();
		CommandClientBuilder cmsBuilder = new CommandClientBuilder();
		cmsBuilder.setPrefix("+");
		cmsBuilder.useDefaultGame();
		cmsBuilder.setOwnerId("281300961312374785");
		cmsBuilder.addCommands(new PingCommand(),
								new WhenSittingCommand(),
								new SignupCommand(),
								new CancelCommand(),
								new ShowContestsCommand(),
								new NewContestCommand(),
								new NewTimeslotCommand(),
								new NewTimeslotUCommand(),
								new UpdateContestantsCommand(),
								new LoadContestCommand(),
								new SyncCommand(),
								new ScheduledEventsCommand(),
								new ReloadContestCommand(),
								new RemoveRoleCommand(),
								new NotifyAllCommand(),
								new RemoveContestCommand(),
								new AutoLoadCommand(),
								new PingAllInContestCommand(),
								new NotifyUnRegisteredCommand());
		cmsBuilder.setHelpConsumer(help);
		JDABuilder cms = new JDABuilder(AccountType.BOT);
		String token = System.getenv("CMSAPITOKEN");
		if (token == null) {
			System.out.print("Input API Token: ");
			token = sc.nextLine();
		}
		cms.setToken(token);
		cms.addEventListener(cmsBuilder.build());
		cms.addEventListener(sm);
		cms.addEventListener(new AutoLoad());
		//cms.addEventListener(new AutoAddContestant());
		CMSbot = cms.build();
		
		CommandClientBuilder modsBotBuilder = new CommandClientBuilder();
		modsBotBuilder.setPrefix("-");
		modsBotBuilder.useDefaultGame();
		modsBotBuilder.setOwnerId("281300961312374785");
		modsBotBuilder.setHelpConsumer(help);
		modsBotBuilder.addCommands(
				new PingCommand(),
				new PotdCommand(),
				new PingRoleCommand(),
				new RecordStatsCommand(),
				new SampleCommand(),
				new VerifyCommand(),
				new RetSampleCommand(),
				new EchoCommand(),
				new PotdViewCommand(),
				new PunishmentViewCommand()
			);
		JDABuilder modsBot = new JDABuilder(AccountType.BOT);
		token = System.getenv("MO-bots-MODSBOTTOKEN");
		modsBot.setToken(token);
		modsBot.addEventListener(modsBotBuilder.build());
		modsBot.addEventListener(new AutoResponse());
		modsBot.addEventListener(new Initialisation());
		modsBot.addEventListener(new Verification());
		modsBot.addEventListener(new Triggers());
		//modsBot.addEventListener(new AutoDelete());
		//modsBot.build();
	}
}
