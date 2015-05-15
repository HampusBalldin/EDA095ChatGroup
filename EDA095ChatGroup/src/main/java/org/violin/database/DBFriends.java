package org.violin.database;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.management.Attribute;
import org.violin.database.generated.Friend;
import org.violin.database.generated.Friends;
import org.violin.database.generated.ObjectFactory;

public class DBFriends extends DBObject<Friends> {

	public DBFriends(Database db) {
		super(db);
	}

	@Override
	protected Class<?> factoryType() {
		return ObjectFactory.class;
	}

	@Override
	protected Class<?> rowType() {
		return Friend.class;
	}

	@Override
	protected Class<Friends> rootType() {
		return Friends.class;
	}

	@Override
	protected String rootName() {
		return "Friends";
	}

	@Override
	protected String rowName() {
		return "Friend";
	}

	@Override
	protected List<Attribute> primaryKey(Object o) throws ClassCastException {
		if (!(o instanceof Friend))
			throw new ClassCastException();
		return attributes(o);
	}

	@Override
	protected List<Attribute> attributes(Object o) throws ClassCastException {
		if (!(o instanceof Friend))
			throw new ClassCastException();
		Friend f = (Friend) o;
		Attribute p1 = new Attribute("uid_1", f.getUid1());
		Attribute p2 = new Attribute("uid_2", f.getUid2());
		ArrayList<Attribute> list = new ArrayList<Attribute>();
		list.add(p1);
		list.add(p2);
		return list;
	}

	@Override
	protected Iterator<?> iterator(Friends element) {
		return element.getFriend().iterator();
	}
}
