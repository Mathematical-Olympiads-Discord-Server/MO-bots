package MO.bots.cms.logic;

import java.util.Arrays;

import net.dv8tion.jda.core.entities.User;

public class Contestant {
	private User contestantUser;
	public User getUser() {return this.contestantUser;}
	
	private int[] scores;
	public int[] getScores() {return this.scores;}
	
	
	public Contestant(User u, int numberQuestions) {
		this.contestantUser = u;
		this.scores = new int[numberQuestions];
		Arrays.fill(this.scores, -1);
	}

	public Contestant(User u, int numberQuestions, int... scores) {
		assert(numberQuestions == scores.length);
		this.contestantUser = u;
		this.scores = scores;
	}
}
