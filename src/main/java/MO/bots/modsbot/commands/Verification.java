package MO.bots.modsbot.commands;

import java.time.Instant;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Verification extends ListenerAdapter {
	private static final long MESSAGE_ID = 672964993146421249L;
	private static final long WELCOME_CHANNEL_ID = 533153217119387660L;
	private static final long ROLES_ID = 671639229293395978L;
	
	
	@Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
		if (event.getMessageIdLong() == MESSAGE_ID) {
			event.getGuild().getController().removeRolesFromMember(
					event.getMember(), event.getGuild().getRolesByName("unverified", true)).complete();
			
			Message m = event.getTextChannel().getMessageById(event.getMessageId()).complete();
			m.getReactions().get(0).removeReaction(event.getUser());
			
			event.getGuild().getTextChannelById(WELCOME_CHANNEL_ID).sendMessage(
					"Welcome to the Mathematical Olympiads Discord server "
					+ event.getUser().getAsMention() 
					+ "! Check out the self-assignable roles in " 
					+ event.getGuild().getTextChannelById(ROLES_ID).getAsMention()
					+ "and enjoy your time here. :smile:");
			
			System.out.println("Verified " + event.getUser().toString() + " at " + Instant.now().toString());
			
		}
		
	}
	
}
