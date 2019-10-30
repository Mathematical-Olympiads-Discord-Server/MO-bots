package MO.bots.cms.logic;

import java.util.ArrayList;

public class ContestsManager {
	public static ArrayList<Contest> currentContests = new ArrayList<Contest>();
	
	/**
	 * Creates a new contest. 
	 * @param name Name of contest
	 * @param messageId message ID
	 * @return
	 */
	public static String newContest(String name, long messageId) {
		currentContests.add(new Contest(name, messageId));
		return "New contest successfully made";
	}
	
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
	
	public static String newTimeslot(String name, String start, String end, long reactID, int position) {
		try {
			currentContests.get(position).addTimeslot(name, start, end, reactID);
		} catch (IndexOutOfBoundsException e) {
			return "There is no contest at this position. ";
		} 
		return "Successfully created a new timeslot for contest " + currentContests.get(position).getName() + ".";
	}
}
