package MO.bots.cms.logic;

import java.time.Instant;
import java.util.ArrayList;

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
	 * The message which the Contest's reactions are tied to. 
	 */
	private long messageID;
	
	private ArrayList<Timeslot> timeslots = new ArrayList<Timeslot>();
	
	public Contest(String name, long messageId) {
		this.name = name;
		this.messageID = messageId;
	}
	
	
	public void addTimeslot(String start, String end, long reaction) {
		Instant startTime = Instant.parse(start);
		Instant endTime = Instant.parse(end);
		timeslots.add(new Timeslot(startTime, endTime, reaction));
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
	
	/**
	 * Reaction representing users who wish to sit
	 * the contest at this timeslot
	 */
	private long reactionID;
	
	
	/**
	 * Creates a new timeslot. 
	 * @param startTime
	 * @param endTime
	 * @param reaction
	 */
	public Timeslot(Instant startTime, Instant endTime, long reaction) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.reactionID = reaction;
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
}