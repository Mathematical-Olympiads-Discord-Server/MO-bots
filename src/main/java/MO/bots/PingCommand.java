package MO.bots;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

/**
 * Simple ping command
 * @author IcosahedralDice
 *
 */
public class PingCommand extends Command {
	public PingCommand() {
		this.name = "ping";
		this.aliases = new String[] {"pong", "ping!"};
		this.help = "Pings to check whether the bot is working. ";
		this.guildOnly = false;
		this.category = new Category("Misc");
	}
	
	@Override
	protected void execute(CommandEvent event) {
		// TODO Auto-generated method stub
		event.reply("Pong!");
	}

}
