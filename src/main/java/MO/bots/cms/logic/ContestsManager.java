package MO.bots.cms.logic;

import java.util.ArrayList;

import com.jagrosh.jdautilities.command.CommandEvent;

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
	
	public static String updateContestants(int position, CommandEvent triggerEvent) {
		try {
			currentContests.get(position).updateContestants(triggerEvent);
		} catch (Exception e) {
			e.printStackTrace();
			return "An error occurred. ";
		}
		return "Successfully updated contestants for contest " + currentContests.get(position).getName() + ".";
	}
}
