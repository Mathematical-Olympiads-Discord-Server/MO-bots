package MO.bots.cms.logic;

import java.util.ArrayList;

public class ContestsManager {
	public static ArrayList<Contest> currentContests = new ArrayList<Contest>();
	
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
}
