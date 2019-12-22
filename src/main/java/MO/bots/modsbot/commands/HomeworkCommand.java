package MO.bots.modsbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class HomeworkCommand extends Command{

	private static final String message = "Hello! We are a Mathematical Olympiad discord server. "
			+ "If you want homework help, please visit the **Homework Help** discord server at "
			+ "<https://discord.gg/YudDZtb> or the **Mathematics** discord server at "
			+ "<https://discord.sg/math>. Thank you!";
	
	public HomeworkCommand() {
		this.name = "homework";
		this.aliases = new String[] {"hw"};
		this.help = "Tells people where to look for homework help. ";
		this.helpBiConsumer = (CommandEvent event, Command c) -> {
			StringBuilder sb = new StringBuilder();
			sb.append("Prints the following message:\n").append(message);
			event.getAuthor().openPrivateChannel().complete().sendMessage(sb.toString()).queue();
		};
	}
	
	
	@Override
	public void execute (CommandEvent event) {
		event.getMessage().delete().queue();
		event.reply(message);
	}

}
