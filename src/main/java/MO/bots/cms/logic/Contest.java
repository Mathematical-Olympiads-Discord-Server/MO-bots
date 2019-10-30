package MO.bots.cms.logic;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.User;

public class Contest {
	/**
	 * The name of the contest
	 */
	private String name;
	public String getName() {return name;}
	
	/**
	 * When the contest starts
	 */
	private Instant start;
	
	/**
	 * When the contest ends
	 */
	private Instant end;
	
	/**
	 * ID of the channel holding the message. 
	 */
	private long channelID;
	
	/**
	 * The message which the Contest's reactions are tied to. 
	 */
	private long messageID;
	
	/**
	 * Holds information about the timeslots. 
	 */
	private ArrayList<Timeslot> timeslots = new ArrayList<Timeslot>();
	
	/**
	 * Normal constructor for the Contest class
	 * @param name Name of the contest
	 * @param messageId message ID to pull contest takers from
	 */
	public Contest(String name, long channelId, long messageId) {
		this.name = name;
		this.channelID = channelId;
		this.messageID = messageId;
	}

	/**
	 * Adds a timeslot to the contest. 
	 * @param start Start time of the timeslot
	 * @param end End time of the timeslot
	 * @param reaction reaction ID which users will pick
	 */
	public void addTimeslot(String name, String start, String end, long reaction) {
		Instant startTime = Instant.parse(start);
		Instant endTime = Instant.parse(end);
		timeslots.add(new Timeslot(name, startTime, endTime, reaction));
	}
	
	/**
	 * Gives information about this contest in a string format. 
	 * @param level level of information
	 * 1 - cursory, name etc. 
	 * 2 - name + timeslots
	 * 3 - name + timeslots + users in those timeslots
	 * @return the information
	 */
	public String getContestInfo(int level) {
		if (level == 1) {
			return this.name;
		} else if (level == 2 || level == 3) {
			StringBuilder sb = new StringBuilder();
			sb.append("Name: ");
			sb.append(this.name);
			sb.append("\nTimeslots:");
			for (Timeslot t : timeslots) {
				sb.append(t.getInfo(level - 1));
				sb.append("\n");
			}
			return sb.toString();
		} else {
			return "Invalid level";
		}
	}
	
	/**
	 * Updates the contestants sitting the contest at each timeslot. 
	 * @param event the event which triggered this update, to get 
	 * the guild, channel, message from which to pull users
	 */
	public void updateContestants(CommandEvent event) {
		Message signupMessage = event.getGuild().getTextChannelById(channelID).getMessageById(messageID).complete();
		List<MessageReaction> reactions = signupMessage.getReactions();
		long currentID = 0L;
		for (Timeslot t : timeslots) {
			t.clearUsers();
			currentID = t.getReactionId();
			for (MessageReaction r : reactions) {
				if (r.getReactionEmote().getIdLong() == currentID) {
					List<User> reactedUsers = r.getUsers().complete();
					for (User u : reactedUsers) {t.addUser(u);}
				} 
			}
		}
	}
}

class Timeslot {
	/**
	 * Start time of the contest (UTC)
	 */
	private Instant startTime;
	
	/**
	 * End time of the contest (UTC)
	 */
	private Instant endTime;
	
	/**
	 * Users sitting the contest at this time
	 */
	private ArrayList<User> users;
	
	private String name;
	
	/**
	 * Reaction representing users who wish to sit
	 * the contest at this timeslot
	 */
	private long reactionID;
	public long getReactionId() {return reactionID;}
	
	/**
	 * Creates a new timeslot. 
	 * @param startTime
	 * @param endTime
	 * @param reaction
	 */
	public Timeslot(String name, Instant startTime, Instant endTime, long reaction) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.reactionID = reaction;
		users = new ArrayList<User>();
	}
	
	/**
	 * Adds one user
	 * @param u the user to add
	 */
	public void addUser(User u) {
		users.add(u);
	}
	
	
	/**
	 * Prints information about this timeslot. 
	 */
	public void printInfo() {
		System.out.println(startTime.toString());
		System.out.println(endTime.toString());
		System.out.println(reactionID);
		for(User u : users) {
			System.out.println(u.toString());
		}
	}
	
	/**
	 * Clears the ArrayList of users. 
	 */
	public void clearUsers() {
		users.clear();
	}
	
	/**
	 * Gets information about the timeslot. 
	 * @param level 1 - no user information, 2 - user information as well
	 * @return information 
	 */
	public String getInfo(int level) {
		StringBuilder sb = new StringBuilder();
		sb.append("Timeslot ");
		sb.append(this.name);
		sb.append(" starts at ");
		sb.append(this.startTime.toString());
		sb.append(" ends at ");
		sb.append(this.endTime.toString());
		sb.append(" tied to reaction ");
		sb.append(this.reactionID);
		if (level == 2) {
			sb.append(" with participants:");
			for (User u : users) {
				sb.append(" ");
				sb.append(u.toString());
			}
		}
		sb.append(".");
		return sb.toString();
	}
}