package MO.bots.cms.logic;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import com.jagrosh.jdautilities.command.CommandEvent;

import MO.bots.shared.SheetsIntegration;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

public class ContestsManager {
	public static ArrayList<Contest> currentContests = new ArrayList<Contest>();
	
	/**
	 * Creates a new contest. 
	 * @param name Name of contest
	 * @param messageId message ID
	 * @return
	 */
	public static String newContest(String name, long channelId, long messageId) {
		currentContests.add(new Contest(name, channelId, messageId));
		return "New contest successfully made";
	}
	
	/**
	 * Adds a new contest based on a pre-loaded contest. If 
	 * the contest is not pre-loaded, please use newContest(String, long, long). 
	 * @param c The pre-loaded contest to add. 
	 */
	public static void addNewContest(Contest c) {
		currentContests.add(c);
	}
	
	/**
	 * Lists all the contests (equivalent to calling showContests(1)
	 * @return a String to display to the user. 
	 */
	public static String listContests() {
		StringBuilder sb = new StringBuilder();
		if (currentContests.isEmpty()) {return "No contests currently. ";}
		
		sb.append("Current Contests: ");
		sb.append("\n");
		for (Contest c : currentContests) {
			sb.append(c.getName());
			sb.append("\n");
		}
		return sb.toString();
	}
	
	/**
	 * Displays information about the contest. 
	 * @param level The level of detail given to the information. <br>
	 * 1 - just a list of contest names <br>
	 * 2 - list of contest names + timeslots <br>
	 * 3 - list of contest names + timeslots + contestants in timeslots. 
	 * @return a String to display to the user. 
	 */
	public static String showContests(int level) {
		StringBuilder sb = new StringBuilder();
		for (Contest c : currentContests) {
			sb.append(c.getContestInfo(level));
			sb.append("\n\n");
		}
		return sb.toString();
	}

	/**
	 * Adds a new timeslot to a specific contest. This version of the
	 * method is the one without a specified position and thus only works
	 * when only one contest is active currently. 
	 * @param name The name of the new timeslot. 
	 * @param start Starting time of timeslot
	 * @param end Ending time of timeslot
	 * @param reactID ID of the reaction to tie the timeslot to. 
	 * @return a String detailing whether the creation was successful and should
	 * be displayed to the user. <br>
	 * If empty, it returns "No contests currently available". <br>
	 * If more than one contest is running it returns "More than one contest currently
	 * created. Please specify the contest number." In this case please use the second
	 * version of this method. 
	 */
	public static String newTimeslot(String name, String start, String end, long reactID) {
		if (currentContests.size() == 1) {
			currentContests.get(0).addTimeslot(name, start, end, reactID);
			return "Successfully created a new timeslot for contest " + currentContests.get(0).getName() + ".";
		} else if (currentContests.isEmpty()) {
			return "No contests currently available. ";
		} else {
			return "More than one contest currently created. Please specify the contest number from the list below \n" + listContests();
		}
	}
	
	/**
	 * Adds a new timeslot to a specified contest. This version of the method
	 * takes long inputs from start and end, representing the use of Unix time
	 * as opposed to the ISO8601 time used by the other methods. 
	 * @param name Name of the timeslot. 
	 * @param start Start time (in Unix time)
	 * @param end End time (in Unix time)
	 * @param reactID Reaction ID
	 * @param position Position (note 0-indexing)
	 * @return
	 */
	public static String newTimeslot(String name, long start, long end, long reactID, int position) {
		try {
			currentContests.get(position).addTimeslot(name, start, end, reactID);
		} catch (IndexOutOfBoundsException e) {
			return "There is no contest at this position. ";
		} 
		return "Successfully created a new timeslot for contest " + currentContests.get(position).getName() + ".";
	}
	
	/**
	 * The version of newTimeslot with a position parameter. Use this
	 * version when there is more than one currently running contest as 
	 * the position parameter helps to differentiate between them. 
	 * @param name The name of the new timeslot. 
	 * @param start Starting time of timeslot
	 * @param end Ending time of timeslot
	 * @param reactID ID of the reaction to tie the timeslot to. 
	 * @param position position of the contest to add this timeslot to. Note
	 * the contests are 0-indexed (i.e. the first contest would be specified by 
	 * position = 0). 
	 * @return a String detailing whether the creation was successful and should
	 * be displayed to the user. <br>
	 * If empty, it returns "No contests currently available". <br>
	 */
	public static String newTimeslot(String name, String start, String end, long reactID, int position) {
		try {
			currentContests.get(position).addTimeslot(name, start, end, reactID);
		} catch (IndexOutOfBoundsException e) {
			return "There is no contest at this position. ";
		} 
		return "Successfully created a new timeslot for contest " + currentContests.get(position).getName() + ".";
	}
	
	/**
	 * This method loops through all current contests and adds a 
	 * user if the MessageReactionAddEvent matches what the contest is
	 * looking for. 
	 * @param event
	 */
	public static void autoReactionAdd(MessageReactionAddEvent event) {
		for (Contest c : currentContests) {
			c.autoReactionAdd(event);
		}
	}
	
	/**
	 * Updates the contestants for a specified contest
	 * @param position Position of the contest (0-indexed)
	 * @param triggerEvent Event that triggered this method
	 * being called. 
	 * @return A string (message to reply to the user with)
	 */
	public static String updateContestants(int position, CommandEvent triggerEvent) {
		try {
			currentContests.get(position).updateContestants(triggerEvent);
		} catch (Exception e) {
			e.printStackTrace();
			return "An error occurred. ";
		}
		return "Successfully updated contestants for contest " + currentContests.get(position).getName() + ".";
	}

	public static String sync(CommandEvent triggerEvent) {
		int contestNumber = 0;
		try {
			if (triggerEvent.getArgs().contentEquals("")) {
				//Default to first contest
				SheetsIntegration.saveContest(currentContests.get(0));
			} else {
				contestNumber = Integer.parseInt(triggerEvent.getArgs().split(" ")[0]);
				SheetsIntegration.saveContest(currentContests.get(contestNumber));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
		return "Successfully saved contest " + currentContests.get(contestNumber).getName() + " to sheet with ID "
				+ currentContests.get(contestNumber).getSpreadsheetId();
	}

	/**
	 * Gets the current schedule for all the contests. 
	 * @return A string representing the current schedule for
	 * all the contests. 
	 */
	public static String getSchedule(boolean byTimeslot) {
		StringBuilder sb = new StringBuilder();
		for (Contest c : currentContests) {
			sb.append(c.getSchedule(byTimeslot)).append("\n").append("\n");
		}
		return sb.toString();
	}

	/**
	 * Gets all the timeslots a user will be sitting. 
	 * @param u The 'User' object representing the user to query. 
	 * @return
	 */
	public static String userSchedule(User u) {
		final String startString = "**YOUR TIMESLOTS**: \n```\n";
		StringBuilder sb = new StringBuilder(startString);
		String s;
		for (Contest c : currentContests) {
			s = c.contestantInfo(u);
			if (!s.equals("")) {
				sb.append(c.getName())
				  .append(" ")
				  .append(s)
				  .append("\n");
			}
		}
		if (!sb.toString().contentEquals(startString)) {
			return sb.append("```").toString();
		} else {
			return "You have not signed up for any timeslots. ";
		}
	}

	/**
	 * Reloads a contest from the Google Sheet. 
	 * @param pos Position of the contest 
	 * @param triggerEvent Event that triggered this reload
	 * @throws GeneralSecurityException 
	 * @throws IOException
	 * @throws ArrayIndexOutOfBoundsException if there is no
	 * contest at that position. 
	 */
	public static void reloadContest (int pos, CommandEvent triggerEvent) throws GeneralSecurityException, IOException,
		ArrayIndexOutOfBoundsException {
		Contest newContest = SheetsIntegration.loadContest(
				currentContests.get(pos).getSpreadsheetId(), triggerEvent.getEvent());
		currentContests.get(pos).cancelSchedule();
		currentContests.set(pos, newContest);
	}

	/**
	 * Signs up a user for the contest. 
	 * @param u The User to sign up
	 * @param contest Name of contest
	 * @param timeslot Name of timeslot
	 * @throws IllegalArgumentException if any of the parameters
	 * do not make sense (i.e. already signed up, no matching contest
	 * name, etc)
	 */
	public static void signupContestant (User u, String contest, String timeslot) throws IllegalArgumentException {
		for (Contest c : currentContests) {
			if (c.getName().contentEquals(contest)) {
				c.signupUser(u, timeslot);
				return;
			}
		}
		
		StringBuilder exceptionString = new StringBuilder();
		exceptionString.append("No contest with that name found. Active contests: ```\n");
		for (Contest c : currentContests) {
			exceptionString.append(c.getName()).append("\n");
		}
		exceptionString.append("```");
		throw new IllegalArgumentException(exceptionString.toString());
	}
	
	public static void removeContestant (User u, String contest, String timeslot) {
		for (Contest c : currentContests) {
			if (c.getName().contentEquals(contest)) {
				c.removeUser(u, timeslot);
				return;
			}
		}
		throw new IllegalArgumentException("No contest with that name found. ");
	}
	
	/**
	 * Gets a List<User> of all users signed up to this contest. 
	 * @param pos Position of the contest
	 * @return
	 * @throws ArrayIndexOutOfBoundsException if there is no contest at pos. 
	 */
	public static List<User> getUsersInContest (int pos) 
		throws ArrayIndexOutOfBoundsException {
		return currentContests.get(pos).getUsersAsList();
	}

	/**
	 * Removes a contest from the list. 
	 * @param pos position
	 */
	public static void removeContest (int pos) {
		currentContests.get(pos).cancelSchedule();
		currentContests.remove(pos);
	}
}
