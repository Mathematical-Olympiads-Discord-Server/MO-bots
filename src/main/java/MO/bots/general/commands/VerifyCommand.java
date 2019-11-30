package MO.bots.general.commands;

import java.time.Instant;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.core.entities.PrivateChannel;

public class VerifyCommand extends Command{

	public VerifyCommand() {
		this.name = "verify";
		this.help = "Verifies a new person";
		this.requiredRole = "unverified";
	}
	
	@Override
	public void execute (CommandEvent event) {
		event.getGuild().getController().removeRolesFromMember(event.getMember(),
				event.getGuild().getRolesByName(this.requiredRole, true)).queue();
		event.getMessage().delete().queue();
		event.getAuthor().openPrivateChannel().queue((PrivateChannel pc) -> {
			pc.sendMessage("Thanks! You have been verified").queue();
		});
		System.out.println("Verified " + event.getAuthor().getName() + " at " + Instant.now().toString());
		
		StringBuilder sb = new StringBuilder();
		sb.append("Welcome to the Mathematical Olympiads Discord Server ")
			.append(event.getAuthor().getAsMention())
			.append("! Check out the self-assignable roles in #roles and enjoy your time here. :)");
		event.getGuild().getTextChannelsByName("lounge", true).get(0)
			.sendMessage(sb.toString()).queue();
	}

}
