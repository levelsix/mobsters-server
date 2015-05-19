/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.BoardPropertyConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IBoardPropertyConfig;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record8;
import org.jooq.Row;
import org.jooq.Row8;
import org.jooq.impl.UpdatableRecordImpl;


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
@Table(name = "board_property_config", schema = "mobsters")
public class BoardPropertyConfigRecord extends UpdatableRecordImpl<BoardPropertyConfigRecord> implements Record8<Integer, Integer, String, Byte, Byte, String, Integer, Integer>, IBoardPropertyConfig {

	private static final long serialVersionUID = -1315327120;

	/**
	 * Setter for <code>mobsters.board_property_config.id</code>.
	 */
	@Override
	public BoardPropertyConfigRecord setId(Integer value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.board_property_config.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getId() {
		return (Integer) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.board_property_config.board_id</code>.
	 */
	@Override
	public BoardPropertyConfigRecord setBoardId(Integer value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.board_property_config.board_id</code>.
	 */
	@Column(name = "board_id", precision = 10)
	@Override
	public Integer getBoardId() {
		return (Integer) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.board_property_config.name</code>.
	 */
	@Override
	public BoardPropertyConfigRecord setName(String value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.board_property_config.name</code>.
	 */
	@Column(name = "name", length = 45)
	@Size(max = 45)
	@Override
	public String getName() {
		return (String) getValue(2);
	}

	/**
	 * Setter for <code>mobsters.board_property_config.pos_x</code>.
	 */
	@Override
	public BoardPropertyConfigRecord setPosX(Byte value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.board_property_config.pos_x</code>.
	 */
	@Column(name = "pos_x", precision = 3)
	@Override
	public Byte getPosX() {
		return (Byte) getValue(3);
	}

	/**
	 * Setter for <code>mobsters.board_property_config.pos_y</code>.
	 */
	@Override
	public BoardPropertyConfigRecord setPosY(Byte value) {
		setValue(4, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.board_property_config.pos_y</code>.
	 */
	@Column(name = "pos_y", precision = 3)
	@Override
	public Byte getPosY() {
		return (Byte) getValue(4);
	}

	/**
	 * Setter for <code>mobsters.board_property_config.element</code>.
	 */
	@Override
	public BoardPropertyConfigRecord setElement(String value) {
		setValue(5, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.board_property_config.element</code>.
	 */
	@Column(name = "element", length = 45)
	@Size(max = 45)
	@Override
	public String getElement() {
		return (String) getValue(5);
	}

	/**
	 * Setter for <code>mobsters.board_property_config.value</code>.
	 */
	@Override
	public BoardPropertyConfigRecord setValue(Integer value) {
		setValue(6, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.board_property_config.value</code>.
	 */
	@Column(name = "value", precision = 10)
	@Override
	public Integer getValue() {
		return (Integer) getValue(6);
	}

	/**
	 * Setter for <code>mobsters.board_property_config.quantity</code>. Mostly used for jelly, as in break jelly twice kind of thing.
	 */
	@Override
	public BoardPropertyConfigRecord setQuantity(Integer value) {
		setValue(7, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.board_property_config.quantity</code>. Mostly used for jelly, as in break jelly twice kind of thing.
	 */
	@Column(name = "quantity", precision = 10)
	@Override
	public Integer getQuantity() {
		return (Integer) getValue(7);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Record1<Integer> key() {
		return (Record1) super.key();
	}

	// -------------------------------------------------------------------------
	// Record8 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row8<Integer, Integer, String, Byte, Byte, String, Integer, Integer> fieldsRow() {
		return (Row8) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row8<Integer, Integer, String, Byte, Byte, String, Integer, Integer> valuesRow() {
		return (Row8) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field1() {
		return BoardPropertyConfig.BOARD_PROPERTY_CONFIG.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field2() {
		return BoardPropertyConfig.BOARD_PROPERTY_CONFIG.BOARD_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field3() {
		return BoardPropertyConfig.BOARD_PROPERTY_CONFIG.NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Byte> field4() {
		return BoardPropertyConfig.BOARD_PROPERTY_CONFIG.POS_X;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Byte> field5() {
		return BoardPropertyConfig.BOARD_PROPERTY_CONFIG.POS_Y;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field6() {
		return BoardPropertyConfig.BOARD_PROPERTY_CONFIG.ELEMENT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field7() {
		return BoardPropertyConfig.BOARD_PROPERTY_CONFIG.VALUE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field8() {
		return BoardPropertyConfig.BOARD_PROPERTY_CONFIG.QUANTITY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value1() {
		return getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value2() {
		return getBoardId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value3() {
		return getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Byte value4() {
		return getPosX();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Byte value5() {
		return getPosY();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value6() {
		return getElement();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value7() {
		return getValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value8() {
		return getQuantity();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoardPropertyConfigRecord value1(Integer value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoardPropertyConfigRecord value2(Integer value) {
		setBoardId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoardPropertyConfigRecord value3(String value) {
		setName(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoardPropertyConfigRecord value4(Byte value) {
		setPosX(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoardPropertyConfigRecord value5(Byte value) {
		setPosY(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoardPropertyConfigRecord value6(String value) {
		setElement(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoardPropertyConfigRecord value7(Integer value) {
		setValue(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoardPropertyConfigRecord value8(Integer value) {
		setQuantity(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoardPropertyConfigRecord values(Integer value1, Integer value2, String value3, Byte value4, Byte value5, String value6, Integer value7, Integer value8) {
		value1(value1);
		value2(value2);
		value3(value3);
		value4(value4);
		value5(value5);
		value6(value6);
		value7(value7);
		value8(value8);
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IBoardPropertyConfig from) {
		setId(from.getId());
		setBoardId(from.getBoardId());
		setName(from.getName());
		setPosX(from.getPosX());
		setPosY(from.getPosY());
		setElement(from.getElement());
		setValue(from.getValue());
		setQuantity(from.getQuantity());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IBoardPropertyConfig> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached BoardPropertyConfigRecord
	 */
	public BoardPropertyConfigRecord() {
		super(BoardPropertyConfig.BOARD_PROPERTY_CONFIG);
	}

	/**
	 * Create a detached, initialised BoardPropertyConfigRecord
	 */
	public BoardPropertyConfigRecord(Integer id, Integer boardId, String name, Byte posX, Byte posY, String element, Integer value, Integer quantity) {
		super(BoardPropertyConfig.BOARD_PROPERTY_CONFIG);

		setValue(0, id);
		setValue(1, boardId);
		setValue(2, name);
		setValue(3, posX);
		setValue(4, posY);
		setValue(5, element);
		setValue(6, value);
		setValue(7, quantity);
	}
}
