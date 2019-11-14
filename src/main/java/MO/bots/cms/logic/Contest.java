package MO.bots.cms.logic;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.Role;
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
	 * The guild/server the contest is held in. 
	 */
	private Guild contestGuild;
	
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
	
	private long finishedRoleId;
	public long getFinishedRoleId() {return this.finishedRoleId;}
	
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
	 * @param g The guild this contest is in. 
	 */
	public Contest(String name,
			long channelId,
			long messageId,
			long roleId,
			long pcbChannelId,
			String formLink,
			long staffMailId,
			long finishedRoleId,
			Guild g) {
		this.name = name;
		this.channelID = channelId;
		this.messageID = messageId;
		this.roleId = roleId;
		this.pcbChannelId = pcbChannelId;
		this.formLink = formLink;
		this.staffMailId = staffMailId;
		this.finishedRoleId = finishedRoleId;
		this.contestGuild = g;
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
		timeslots.add(new Timeslot(name, startTime, endTime, reaction,
				this.contestGuild, this.roleId, this.formLink, this.finishedRoleId));
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
		timeslots.add(new Timeslot(name, startTime, endTime, reaction, 
				this.contestGuild, this.roleId, this.formLink, this.finishedRoleId));
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

	/**
	 * Gets the schedule for this contest. 
	 * @return
	 */
	public String getSchedule(boolean byTimeslot) {
		ArrayList<TimerTaskWithSchedule> l = new ArrayList<TimerTaskWithSchedule>(100); //10 slots, 10 per slot
		for (Timeslot t : timeslots) {
			l.addAll(t.getSchedule());
		}
		if (!byTimeslot) 
			Collections.sort(l);
		
		StringBuilder sb = new StringBuilder(10000);
		for (TimerTaskWithSchedule t : l) {
			sb.append(t.getInfo()).append("\n");
		}
		return sb.toString();
	}

	/**
	 * Gets information about a particular contestant. 
	 * @param u The 'User' object representing the contestant to query. 
	 * @return A string representing all the user's involvements in the current
	 * contest. 
	 */
	public String contestantInfo(User u) {
		StringBuilder sb = new StringBuilder();
		for (Timeslot t : timeslots) {
			if (t.getUsers().contains(u)) {
				sb.append("Timeslot " + t.getName() + " starting at " + 
						t.getStartInstant().toString() + " which is in " + 
						Instant.now().until(t.getStartInstant(), ChronoUnit.SECONDS) +
						" seconds, or approximately " + 
						Instant.now().until(t.getStartInstant(), ChronoUnit.MINUTES) + 
						" minutes. (" + 
						Instant.now().until(t.getStartInstant(), ChronoUnit.HOURS) + 
					    " hours and " + 
						(Instant.now().until(t.getStartInstant(), ChronoUnit.MINUTES)%60) + 
						" minutes)");
				sb.append("\n");
						
						
			}
		}
		
		return sb.toString();
	}
}

class Timeslot {
	/**
	 * Start time of the contest (UTC)
	 */
	private Instant startTime;
	public Instant getStartInstant() {return this.startTime;}
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
	
	private Guild contestGuild;
	public Guild getContestGuild() {return contestGuild;}
	
	/**
	 * Reaction representing users who wish to sit
	 * the contest at this timeslot
	 */
	private long reactionID;
	public long getReactionId() {return reactionID;}
	
	private long roleId;
	public long getRoleId() {return roleId;}
	
	private long finishedRoleId;
	public long getFinishedRoleId() {return finishedRoleId;}
	
	private boolean isCustomTimeslot;
	public boolean getIsCustomTimeslot() {return isCustomTimeslot;}
	
	private static final String CONTEST_ROOM_NAME = "Contest Room";
	
	/*
	 * Timers to execute tasks when needed. 
	 */
	private Timer mainTimer;
	private ArrayList<TimerTaskWithSchedule> schedule = new ArrayList<TimerTaskWithSchedule>();
	
	/**
	 * Creates a new timeslot. 
	 * @param startTime
	 * @param endTime
	 * @param reaction
	 */
	public Timeslot(String name, Instant startTime, Instant endTime, long reaction, Guild g, long roleId, String formLink, long finishedRoleId) {
		this.name = name;
		this.startTime = startTime;
		this.endTime = endTime;
		this.reactionID = reaction;
		users = new ArrayList<User>();
		this.contestGuild = g;
		this.roleId = roleId;
		this.finishedRoleId = finishedRoleId;
		this.isCustomTimeslot = (reaction == 0) ? true : false;
		
		//Set up tasks
		mainTimer = new Timer();
		schedule.add(new ReminderTask(this, "15 minutes left before the contest starts. Please head "
				+ "to the Contest Room VC soon. Please also prepare the following materials: "
				+ "Blank A4 Paper, Pen, Pencil, Compass and Ruler. You can now join Contest Room VC. ", 
				"before contest reminder", this.startTime.minus(Duration.ofMinutes(15))));
		schedule.add(new ReminderTask(this, "5 minutes left before the contest starts. Please head "
				+ "to the Contest Room VC soon. ", "before contest reminder 2", this.startTime.minus(Duration.ofMinutes(5))));
		schedule.add(new ReminderTask(this, "The contest has started - you may now look at the contest paper and "
				+ "begin working on the problems. If there is any issue please contact Staff Mail at the "
				+ "top of the server memberlist. ", "Contest Start Reminder", this.startTime));
		schedule.add(new ReminderTask(this, "30 minutes left before the contest ends. ", 
				"30 minutes left reminder", this.endTime.minus(Duration.ofMinutes(30))));
		schedule.add(new ReminderTask(this, "5 minutes left before the contest ends. Please make sure to "
				+ "have written the question and page number on each sheet of your contest paper, and "
				+ "that your User ID/Username is not written anywhere on your contest paper. ", 
				"5 minutes left reminder", this.endTime.minus(Duration.ofMinutes(5))));
		schedule.add(new ReminderTask(this, "The contest is over. Please submit your solutions to the form "
				+ "given in " + formLink + ". Thank you for participating in this contest! Further instructions "
				+ "are available in the form. ", "Contest end reminder",
				this.endTime));
		if (!this.isCustomTimeslot) {
			schedule.add(new AllowConnectionTask(this, "Allow participants to join VC", this.startTime.minus(Duration.ofMinutes(15)),
					this.name, CONTEST_ROOM_NAME));
			schedule.add(new DisAllowConnectionTask(this, "Remove VC Connection permissions", this.startTime.plus(Duration.ofMinutes(5)),
					this.name, CONTEST_ROOM_NAME));
			
		}
		schedule.add(new AssignRolesTask(this, this.roleId, "Assign Now Competing roles", this.startTime));
		schedule.add(new AssignRolesTask(this, this.finishedRoleId, "Assign finished roles", this.endTime));
		schedule.add(new RemoveRolesTask(this, "Remove now competing roles", this.endTime));
		
		/*
		beforeContestReminder = new ReminderTask(this, "15 minutes left before the contest starts. "
				+ "Please head to the Contest Room VC soon. ");
		beforeContestReminder2 = new ReminderTask(this, "5 minutes left before the contest starts. "
				+ "Please head to the Contest Room VC soon. ");
		startContestReminder = new ReminderTask(this, "The contest has started - you may now open the "
				+ "image and begin working on the problems. If there is any issue please contact Staff "
				+ "Mail at the top of the server memberlist. ");
		thirtyMinutesLeftReminder = new ReminderTask(this, "30 minutes left before the contest ends. ");
		contestOverReminder = new ReminderTask(this, "The contest is over. Please submit your solutions to the form"
				+ "given in " + formLink + ". Thank you for participating in this contest!");
		assignRoles = new AssignRolesTask(this, this.roleId);
		assignFinishedRoles = new AssignRolesTask(this, this.finishedRoleId);
		removeRoles = new RemoveRolesTask(this);*/

		long startDelay = startTime.toEpochMilli() - Instant.now().toEpochMilli();
		long endDelay   = endTime.toEpochMilli() - Instant.now().toEpochMilli();
		System.out.println(startDelay);
		System.out.println(endDelay);
		
		for (TimerTaskWithSchedule t : schedule) {
			try {
				mainTimer.schedule(t, t.schedule.toEpochMilli() - Instant.now().toEpochMilli());
				System.out.println("Scheduled " + t.name + " at time " + t.schedule.toString());
			} catch (IllegalArgumentException e) {
				System.out.println("Unable to schedule " + t.name + " due to it being in the past. ");
			}
		}
		
		/*
		mainTimer.schedule(beforeContestReminder, startDelay - 900000); // 15 minutes = 900 000 ms
		mainTimer.schedule(beforeContestReminder2, startDelay - 300000);// 5 minutes = 300 000 ms
		mainTimer.schedule(startContestReminder, startDelay);
		mainTimer.schedule(assignRoles, startDelay);
		mainTimer.schedule(thirtyMinutesLeftReminder, endDelay - 1800000); // 30 minues = 1 800 000 ms
		mainTimer.schedule(contestOverReminder, endDelay);
		mainTimer.schedule(assignFinishedRoles, endDelay);
		mainTimer.schedule(removeRoles, endDelay);*/
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

	/**
	 * Gets the current schedule in this timeslot. 
	 * @return The current schedule in this timeslot. 
	 */
	public ArrayList<TimerTaskWithSchedule> getSchedule() {
		return this.schedule;
	}

	
}

abstract class TimerTaskWithSchedule extends TimerTask implements Comparable<TimerTaskWithSchedule> {
	protected Timeslot tiedTimeslot;
	protected String name;
	protected Instant schedule;
	public Instant getScheduledTime() {return schedule;}
	
	@Override
	public int compareTo(TimerTaskWithSchedule t) {
		return this.schedule.compareTo(t.schedule);
	}

	public String getInfo() {
		return (this.schedule.compareTo(Instant.now()))>0 ? 
				this.tiedTimeslot.getName() + ": Scheduled " + this.name + " at " + this.schedule.toString() : 
				this.tiedTimeslot.getName() + ": Scheduled " + this.name + " in the past. ";
		
	}
}

class ReminderTask extends TimerTaskWithSchedule {
	private String remindMessage;
	
	public ReminderTask(Timeslot t, String remindMessage, String name, Instant schedule) {
		this.tiedTimeslot = t;
		this.remindMessage = remindMessage;
		this.name = name;
		this.schedule = schedule;
	}
	
	@Override
	public void run() {
		System.out.print("Reminder task is running. Printing ");
		System.out.println(remindMessage);
		for (User u : tiedTimeslot.getUsers()) {
			u.openPrivateChannel().complete().sendMessage(remindMessage).queue();
		}
	}
}

class ChannelMessageTask extends TimerTaskWithSchedule {
	private String message;
	private long channelId;
	
	public ChannelMessageTask(Timeslot t, String m , long c, String name, Instant schedule) {
		this.tiedTimeslot = t;
		this.message = m;
		this.channelId = c;
		this.name = name;
		this.schedule = schedule;
	}
	
	@Override
	public void run() {
		tiedTimeslot.getContestGuild().getTextChannelById(channelId).sendMessage(message).queue();
	}
}

class AssignRolesTask extends TimerTaskWithSchedule {
	private long roleId;
	
	public AssignRolesTask(Timeslot t, long r, String name, Instant schedule) {
		this.tiedTimeslot = t;
		this.roleId = r;
		this.name = name;
		this.schedule = schedule;
	}

	@Override
	public void run() {
		Role toAdd = this.tiedTimeslot.getContestGuild().getRoleById(roleId);
		for (User u : tiedTimeslot.getUsers()) {
			tiedTimeslot.getContestGuild().getController()
				.addRolesToMember(tiedTimeslot.getContestGuild().getMember(u), toAdd).queue();
		}
	}
	
	@Override
	public String getInfo() {
		return super.getInfo().concat(" Role Id: " + this.roleId);
	}
}

class RemoveRolesTask extends TimerTaskWithSchedule {
	public RemoveRolesTask(Timeslot t, String name, Instant schedule) {
		this.tiedTimeslot = t;
		this.name = name;
		this.schedule = schedule;
	}
	
	@Override
	public void run() {
		Role toRemove = this.tiedTimeslot.getContestGuild().getRoleById(tiedTimeslot.getRoleId());
		for (User u : tiedTimeslot.getUsers()) {
			tiedTimeslot.getContestGuild().getController()
				.removeRolesFromMember(tiedTimeslot.getContestGuild().getMember(u), toRemove).queue();
		}
	}
	
	@Override
	public String getInfo() {
		return super.getInfo().concat(" Role Id: " + this.tiedTimeslot.getRoleId());
	}
}

class AllowConnectionTask extends TimerTaskWithSchedule {
	private String role;
	private String vcToAllow;
	
	public AllowConnectionTask(Timeslot t, String name, Instant schedule,
			String role, String vcName) {
		this.tiedTimeslot = t;
		this.name = name;
		this.schedule = schedule;
		this.role = role;
		this.vcToAllow = vcName;
	}
	
	@Override
	public void run() {
		Guild g = this.tiedTimeslot.getContestGuild();
		Role r = g.getRolesByName(this.role, true).get(0);
		g.getVoiceChannelsByName(vcToAllow, true).get(0).putPermissionOverride(r).complete()
			.getManager().grant(Permission.VOICE_CONNECT).deny(Permission.VOICE_SPEAK).queue();
	}
	
	@Override
	public String getInfo() {
		return super.getInfo().concat("Role: " + this.role + " VC: " + this.vcToAllow);
	}
}

class DisAllowConnectionTask extends TimerTaskWithSchedule {
	private String role;
	private String vcToAllow;
	
	public DisAllowConnectionTask (Timeslot t, String name, Instant schedule,
			String role, String vcName) {
		this.tiedTimeslot = t;
		this.name = name;
		this.schedule = schedule;
		this.role = role;
		this.vcToAllow = vcName;
	}
	
	@Override
	public void run() {
		Guild g = this.tiedTimeslot.getContestGuild();
		Role r = g.getRolesByName(this.role, true).get(0);
		g.getVoiceChannelsByName(vcToAllow, true).get(0).putPermissionOverride(r).complete()
			.getManager().deny(Permission.VOICE_CONNECT).deny(Permission.VOICE_SPEAK).queue();
	}
	
	@Override
	public String getInfo() {
		return super.getInfo().concat("Role: " + this.role + " VC: " + this.vcToAllow);
	}
}