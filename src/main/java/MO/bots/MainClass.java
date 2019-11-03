package MO.bots;

import java.util.Scanner;

import javax.security.auth.login.LoginException;

import com.jagrosh.jdautilities.command.Command.Category;
import com.jagrosh.jdautilities.command.CommandClientBuilder;

import MO.bots.cms.commands.*;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;

public class MainClass {
	public static Scanner sc = new Scanner(System.in);
	public static Category[] categories = {new Category("Admin"),
											new Category("Misc"),
											new Category("User help")
	};
	
	public static void main (String[] args) throws LoginException {
		CommandClientBuilder cmsBuilder = new CommandClientBuilder();
		cmsBuilder.setPrefix("+");
		cmsBuilder.useDefaultGame();
		cmsBuilder.setOwnerId("281300961312374785");
		cmsBuilder.addCommands(new PingCommand(),
								new ShowContestsCommand(),
								new NewContestCommand(),
								new NewTimeslotCommand(),
								new NewTimeslotUCommand(),
								new UpdateContestantsCommand(),
								new LoadContestCommand(),
								new SyncCommand());
		JDABuilder cms = new JDABuilder(AccountType.BOT);
		String token = System.getenv("APITOKEN");
		if (token == null) {
			System.out.print("Input API Token: ");
			token = sc.nextLine();
		}
		cms.setToken(token);
		cms.addEventListener(cmsBuilder.build());
		cms.addEventListener(new AutoAddContestant());
		cms.build();
	}
}
