/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IUserPrivateChatPost;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
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
@Table(name = "user_private_chat_post", schema = "mobsters")
public class UserPrivateChatPostPojo implements IUserPrivateChatPost {

	private static final long serialVersionUID = -1877947906;

	private String    id;
	private String    posterId;
	private String    recipientId;
	private Timestamp timeOfPost;
	private String    content;
	private String    contentLanguage;

	public UserPrivateChatPostPojo() {}

	public UserPrivateChatPostPojo(UserPrivateChatPostPojo value) {
		this.id = value.id;
		this.posterId = value.posterId;
		this.recipientId = value.recipientId;
		this.timeOfPost = value.timeOfPost;
		this.content = value.content;
		this.contentLanguage = value.contentLanguage;
	}

	public UserPrivateChatPostPojo(
		String    id,
		String    posterId,
		String    recipientId,
		Timestamp timeOfPost,
		String    content,
		String    contentLanguage
	) {
		this.id = id;
		this.posterId = posterId;
		this.recipientId = recipientId;
		this.timeOfPost = timeOfPost;
		this.content = content;
		this.contentLanguage = contentLanguage;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public UserPrivateChatPostPojo setId(String id) {
		this.id = id;
		return this;
	}

	@Column(name = "poster_id", length = 36)
	@Size(max = 36)
	@Override
	public String getPosterId() {
		return this.posterId;
	}

	@Override
	public UserPrivateChatPostPojo setPosterId(String posterId) {
		this.posterId = posterId;
		return this;
	}

	@Column(name = "recipient_id", length = 36)
	@Size(max = 36)
	@Override
	public String getRecipientId() {
		return this.recipientId;
	}

	@Override
	public UserPrivateChatPostPojo setRecipientId(String recipientId) {
		this.recipientId = recipientId;
		return this;
	}

	@Column(name = "time_of_post", nullable = false)
	@NotNull
	@Override
	public Timestamp getTimeOfPost() {
		return this.timeOfPost;
	}

	@Override
	public UserPrivateChatPostPojo setTimeOfPost(Timestamp timeOfPost) {
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
	public UserPrivateChatPostPojo setContent(String content) {
		this.content = content;
		return this;
	}

	@Column(name = "content_language", length = 45)
	@Size(max = 45)
	@Override
	public String getContentLanguage() {
		return this.contentLanguage;
	}

	@Override
	public UserPrivateChatPostPojo setContentLanguage(String contentLanguage) {
		this.contentLanguage = contentLanguage;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IUserPrivateChatPost from) {
		setId(from.getId());
		setPosterId(from.getPosterId());
		setRecipientId(from.getRecipientId());
		setTimeOfPost(from.getTimeOfPost());
		setContent(from.getContent());
		setContentLanguage(from.getContentLanguage());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IUserPrivateChatPost> E into(E into) {
		into.from(this);
		return into;
	}
}