package MO.bots.cms.logic;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

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
	public long getChannelID() {return this.channelID;}
	
	/**
	 * The message which the Contest's reactions are tied to. 
	 */
	private long messageID;
	public long getMessageId() {return this.messageID;}
	
	/**
	 * ID of the Google Sheets Spreadsheet which this contest backs
	 * up to. 
	 */
	private String spreadsheetId;
	public String getSpreadsheetId() {return this.spreadsheetId;}
	public void setSpreadsheetId(String spreadsheetId) {this.spreadsheetId = spreadsheetId;}
	
	public long roleId;
	public long getRoleId() {return this.roleId;}
	public void setRoleId(long roleId) {this.roleId = roleId;}
	
	private long pcbChannelId;
	/**
	 * ID of the channel where contestants can discuss the contest in. 
	 * @return
	 */
	public long getPcbChannelId() {return this.pcbChannelId;}
	
	private String formLink;
	/**
	 * The link to the form where contestants should submit their 
	 * solutions to. 
	 * @return
	 */
	public String getFormLink() {return this.formLink;}
	
	private long staffMailId;
	/**
	 * Gets the ID of the Staff Mail bot in the server. Contestants
	 * will DM the staff mail bot if they have any questions to ask. 
	 * @return
	 */
	public long getStaffMailId() {return this.staffMailId;}
	
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
	 * Full constructor. 
	 * @param name
	 * @param channelId
	 * @param messageId
	 * @param roleId
	 * @param pcbChannelId
	 * @param formLink
	 * @param staffMailId
	 */
	public Contest(String name,
			long channelId,
			long messageId,
			long roleId,
			long pcbChannelId,
			String formLink,
			long staffMailId) {
		this.name = name;
		this.channelID = channelId;
		this.messageID = messageId;
		this.roleId = roleId;
		this.pcbChannelId = pcbChannelId;
		this.formLink = formLink;
		this.staffMailId = staffMailId;
	}
	
	/**
	 * Returns a {@code List<List<Object>>} representing data about
	 * the timeslots held in this Contest object. 
	 * @return a {@code List<List<Object>>} in the following form: <br>
	 * one {@code List<Object>} per timeslot (let this be {@code t}). 
	 * {@code t.get(0)} is the timeslot name, {@code t.get(1)} is the timeslot 
	 * start time (in unix time), {@code t.get(2)} is the timeslot end time, and 
	 * {@code t.get(3)} is the timeslot reaction. 
	 */
	public List<List<Object>> getTimeslotInfoAsList() {
		List<List<Object>> toReturn = new ArrayList<>();
		for (Timeslot t : timeslots) {
			toReturn.add(Arrays.asList(t.getName(), t.getStartLong(), t.getEndLong(), "" + t.getReactionId()));
		}
		
		return toReturn;
	}
	
	/**
	 * Returns a {@code List<List<Object>>} representing data about 
	 * all the contestants registered for the current contest. 
	 * @return A {@code List<List<Object>>}, with one {@code List<List<Object>>}
	 * per user. Entry is Username + "#" + discriminator (e.g. asdf#1234), ID, timeslot name. 
	 */
	public List<List<Object>> getUserInfoAsList() {
		List<List<Object>> toReturn = new ArrayList<>();
		for (Timeslot t : timeslots) {
			for (User u : t.getUsers()) {
				toReturn.add(Arrays.asList(u.getName() + "#" + u.getDiscriminator(), 
						u.getId(), t.getName()));
			}
		}
		
		return toReturn;
	}
	
	/**
	 * Adds a contestant to a contest based on a reaction
	 * they made to an event. This only happens if the reaction
	 * and message is linked to the contest or timeslots. 
	 * @param event Event that triggers this method. Used to get
	 * information about the user such as ID, Guild, etc. 
	 * @return true if it was added, false otherwise. 
	 */
	public boolean autoReactionAdd(MessageReactionAddEvent event) {
		if (this.messageID != event.getMessageIdLong()) {return false;}
		boolean addedAUser = false;
		for (Timeslot t : timeslots) {
			if (t.getReactionId() == event.getReactionEmote().getIdLong()) {
				t.addUser(event.getUser());
				if (spreadsheetId != null) 
				try {
					SheetsIntegration.appendUser(this, event.getUser(), t.getName());
				} catch (GeneralSecurityException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				addedAUser = true;
			}
		}
		return addedAUser;
		
		
	}
	
	/**
	 * Adds a timeslot to the contest. 
	 * @param name Name of the timeslot
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
	 * Adds a single contestant to a specified timeslot. 
	 * @param event Event which triggered the addition
	 * @param timeslotName Name of the timeslot
	 * @param userID The ID of the user to be added. 
	 */
	public void addContestant(CommandEvent event, String timeslotName, long userID) {
		User contestant = event.getGuild().getMemberById(userID).getUser();
		for (Timeslot t : timeslots) {
			if (t.getName().contentEquals(timeslotName)) {
				t.addUser(contestant);
				return;
			}
		}
		throw new IllegalArgumentException("No timeslot with that name found while attempting to add user " + userID);
	}
	
	/**
	 * Adds a new timeslot to this contest. Uses
	 * longs for start and end instead of strings,
	 * because this method uses Unix time. 
	 * @param name Name of the timeslot
	 * @param start Start time (in Unix time)
	 * @param end End time (in Unix time)
	 * @param reaction reaction ID
	 */
	public void addTimeslot(String name, long start, long end, long reaction) {
		Instant startTime = Instant.ofEpochSecond(start);
		Instant endTime = Instant.ofEpochSecond(end);
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
		StringBuilder sb = new StringBuilder();
		sb.append(this.name);
		sb.append(" ");
		sb.append(this.channelID);
		sb.append(" ");
		sb.append(this.messageID);
		sb.append(" ");
		sb.append(this.roleId);
		sb.append(" ");
		sb.append(this.pcbChannelId);
		sb.append(" ");
		sb.append(this.formLink);
		sb.append(" ");
		sb.append(this.staffMailId);
		sb.append("\n");
		if (level == 1) {
			return sb.toString();
		} else if (level == 2 || level == 3) {
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
			if (t.getReactionId() == 0) continue;	//Custom timeslots will have reactionId set to 0.
			t.clearUsers();							//We don't want to update those. 
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
	public long getStartLong() {return this.startTime.getEpochSecond();}
	
	/**
	 * End time of the contest (UTC)
	 */
	private Instant endTime;
	public long getEndLong() {return this.endTime.getEpochSecond();}
	
	/**
	 * Users sitting the contest at this time
	 */
	private ArrayList<User> users;
	public ArrayList<User> getUsers() {return this.users;}
	
	private String name;
	public String getName() {return name;}
	
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
		this.name = name;
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