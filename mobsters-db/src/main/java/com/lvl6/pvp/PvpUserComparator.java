package com.lvl6.pvp;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;

import com.hazelcast.util.IterationType;
//used to order the PvpUser objects returned by hazelcast's querying system
//via the Paging Predicate

@SuppressWarnings("rawtypes")
//idk why, could just use Map<String, PvpUser>
public class PvpUserComparator implements Comparator<Map.Entry>, Serializable {

	private static final long serialVersionUID = 673698211756933678L;

	int ascending = 1;

	IterationType iterationType = IterationType.ENTRY;

	PvpUserComparator() {
	}

	PvpUserComparator(boolean ascending, IterationType iterationType) {
		this.ascending = ascending ? 1 : -1;
		this.iterationType = iterationType;
	}

	//Compares its two arguments for order. Returns a negative integer, zero, or
	//a positive integer as the first argument is less than, equal to, or
	//greater than the second.
	@Override
	public int compare(Map.Entry e1, Map.Entry e2) {
		@SuppressWarnings("unchecked")
		Map.Entry<String, PvpUser> o1 = e1;
		@SuppressWarnings("unchecked")
		Map.Entry<String, PvpUser> o2 = e2;

		switch (iterationType) {
		case KEY:
			//don't forsee being used, string userIds won't make sense 
			//if udids are used, but if just stringified int it will
			//so do whatever
			return (o1.getKey().compareTo(o2.getKey())) * ascending;
		default: //encapsulates IterationTypes: VALUE and ENTRY 
			PvpUser left = o1.getValue();
			int leftElo = left.getElo();

			PvpUser right = o2.getValue();
			int rightElo = right.getElo();
			//order by elo
			if (leftElo != rightElo) {
				return (leftElo - rightElo) * ascending;
			}

			//order by num battles won
			int leftWins = left.getBattlesWon();
			int rightWins = right.getBattlesWon();
			if (leftWins != rightWins) {
				return (leftWins - rightWins) * ascending;
			}

			//doesn't matter at this point
			return (o1.getKey().compareTo(o2.getKey())) * ascending;
		}
	}
}