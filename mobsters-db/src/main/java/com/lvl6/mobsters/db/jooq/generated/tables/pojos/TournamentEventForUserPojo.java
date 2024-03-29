/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ITournamentEventForUser;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


/**
 * This class is generated by jOOQ.
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.1"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
@Entity
@Table(name = "tournament_event_for_user", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"tournament_event_id", "user_id"})
})
public class TournamentEventForUserPojo implements ITournamentEventForUser {

	private static final long serialVersionUID = -614308;

	private Integer tournamentEventId;
	private String  userId;
	private Integer battlesWon;
	private Integer battlesLost;
	private Integer battlesFled;

	public TournamentEventForUserPojo() {}

	public TournamentEventForUserPojo(TournamentEventForUserPojo value) {
		this.tournamentEventId = value.tournamentEventId;
		this.userId = value.userId;
		this.battlesWon = value.battlesWon;
		this.battlesLost = value.battlesLost;
		this.battlesFled = value.battlesFled;
	}

	public TournamentEventForUserPojo(
		Integer tournamentEventId,
		String  userId,
		Integer battlesWon,
		Integer battlesLost,
		Integer battlesFled
	) {
		this.tournamentEventId = tournamentEventId;
		this.userId = userId;
		this.battlesWon = battlesWon;
		this.battlesLost = battlesLost;
		this.battlesFled = battlesFled;
	}

	@Column(name = "tournament_event_id", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getTournamentEventId() {
		return this.tournamentEventId;
	}

	@Override
	public TournamentEventForUserPojo setTournamentEventId(Integer tournamentEventId) {
		this.tournamentEventId = tournamentEventId;
		return this;
	}

	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getUserId() {
		return this.userId;
	}

	@Override
	public TournamentEventForUserPojo setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	@Column(name = "battles_won", precision = 10)
	@Override
	public Integer getBattlesWon() {
		return this.battlesWon;
	}

	@Override
	public TournamentEventForUserPojo setBattlesWon(Integer battlesWon) {
		this.battlesWon = battlesWon;
		return this;
	}

	@Column(name = "battles_lost", precision = 10)
	@Override
	public Integer getBattlesLost() {
		return this.battlesLost;
	}

	@Override
	public TournamentEventForUserPojo setBattlesLost(Integer battlesLost) {
		this.battlesLost = battlesLost;
		return this;
	}

	@Column(name = "battles_fled", precision = 10)
	@Override
	public Integer getBattlesFled() {
		return this.battlesFled;
	}

	@Override
	public TournamentEventForUserPojo setBattlesFled(Integer battlesFled) {
		this.battlesFled = battlesFled;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(ITournamentEventForUser from) {
		setTournamentEventId(from.getTournamentEventId());
		setUserId(from.getUserId());
		setBattlesWon(from.getBattlesWon());
		setBattlesLost(from.getBattlesLost());
		setBattlesFled(from.getBattlesFled());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends ITournamentEventForUser> E into(E into) {
		into.from(this);
		return into;
	}


	public String toString() {
		com.lvl6.mobsters.db.jooq.generated.tables.records.TournamentEventForUserRecord poop = new com.lvl6.mobsters.db.jooq.generated.tables.records.TournamentEventForUserRecord();
		poop.from(this);
		return "TournamentEventForUserPojo[" + poop.valuesRow() + "]";
	}
}
