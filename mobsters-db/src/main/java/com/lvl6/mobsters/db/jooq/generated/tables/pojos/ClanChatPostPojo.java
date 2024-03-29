/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IClanChatPost;

import java.sql.Timestamp;

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
@Table(name = "clan_chat_post", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"id", "time_of_post", "clan_id", "poster_id"})
})
public class ClanChatPostPojo implements IClanChatPost {

	private static final long serialVersionUID = 991836978;

	private String    id;
	private String    posterId;
	private String    clanId;
	private Timestamp timeOfPost;
	private String    content;

	public ClanChatPostPojo() {}

	public ClanChatPostPojo(ClanChatPostPojo value) {
		this.id = value.id;
		this.posterId = value.posterId;
		this.clanId = value.clanId;
		this.timeOfPost = value.timeOfPost;
		this.content = value.content;
	}

	public ClanChatPostPojo(
		String    id,
		String    posterId,
		String    clanId,
		Timestamp timeOfPost,
		String    content
	) {
		this.id = id;
		this.posterId = posterId;
		this.clanId = clanId;
		this.timeOfPost = timeOfPost;
		this.content = content;
	}

	@Column(name = "id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public ClanChatPostPojo setId(String id) {
		this.id = id;
		return this;
	}

	@Column(name = "poster_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getPosterId() {
		return this.posterId;
	}

	@Override
	public ClanChatPostPojo setPosterId(String posterId) {
		this.posterId = posterId;
		return this;
	}

	@Column(name = "clan_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getClanId() {
		return this.clanId;
	}

	@Override
	public ClanChatPostPojo setClanId(String clanId) {
		this.clanId = clanId;
		return this;
	}

	@Column(name = "time_of_post", nullable = false)
	@NotNull
	@Override
	public Timestamp getTimeOfPost() {
		return this.timeOfPost;
	}

	@Override
	public ClanChatPostPojo setTimeOfPost(Timestamp timeOfPost) {
		this.timeOfPost = timeOfPost;
		return this;
	}

	@Column(name = "content", length = 65535)
	@Size(max = 65535)
	@Override
	public String getContent() {
		return this.content;
	}

	@Override
	public ClanChatPostPojo setContent(String content) {
		this.content = content;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IClanChatPost from) {
		setId(from.getId());
		setPosterId(from.getPosterId());
		setClanId(from.getClanId());
		setTimeOfPost(from.getTimeOfPost());
		setContent(from.getContent());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IClanChatPost> E into(E into) {
		into.from(this);
		return into;
	}


	public String toString() {
		com.lvl6.mobsters.db.jooq.generated.tables.records.ClanChatPostRecord poop = new com.lvl6.mobsters.db.jooq.generated.tables.records.ClanChatPostRecord();
		poop.from(this);
		return "ClanChatPostPojo[" + poop.valuesRow() + "]";
	}
}
