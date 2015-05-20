package org.violin.database;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.management.Attribute;
import org.violin.database.generated.ObjectFactory;
import org.violin.database.generated.Status;
import org.violin.database.generated.User;
import org.violin.database.generated.Users;

public class DBUsers extends DBObject<Users> {

	public DBUsers(Database db) {
		super(db);
	}

	public User createUser(String uid, String pwd, Status status) {
		User user = new User();
		user.setUid(uid);
		user.setPwd(pwd);
		user.setStatus(status);
		return user;
	}

	public Users getOnlineFriends(User user) {
		return query("SELECT * " + " FROM Users"
				+ " WHERE (status = ? OR status = ?) AND (uid IN"
				+ " (SELECT uid_1 FROM friends"
				+ " WHERE ? = uid_2) OR uid IN " + " (SELECT uid_2"
				+ " FROM friends " + " WHERE ? = uid_1))",
				Status.ONLINE.value(), Status.AWAY.value(), "" + user.getUid(),
				"" + user.getUid());
	}

	public Users getFriends(User user) {
		return query("SELECT * " + " FROM Users"
				+ " WHERE (status = ? OR status = ?) AND (uid IN"
				+ " (SELECT uid_1 FROM friends"
				+ " WHERE ? = uid_2) OR uid IN " + " (SELECT uid_2"
				+ " FROM friends " + " WHERE ? = uid_1))",
				Status.ONLINE.value(), Status.AWAY.value(), "" + user.getUid(),
				"" + user.getUid());
	}

	public void update(User user) {
		Users users = new Users();
		users.getUser().add(user);
		update(users);
	}

	@Override
	protected Class<?> factoryType() {
		return ObjectFactory.class;
	}

	@Override
	protected Class<?> rowType() {
		return User.class;
	}

	@Override
	protected Class<Users> rootType() {
		return Users.class;
	}

	@Override
	protected String rootName() {
		return "Users";
	}

	@Override
	protected String rowName() {
		return "User";
	}

	@Override
	protected List<Attribute> primaryKey(Object o) throws ClassCastException {
		if (!(o instanceof User))
			throw new ClassCastException();
		User u = (User) o;
		Attribute p1 = new Attribute("uid", u.getUid());
		ArrayList<Attribute> list = new ArrayList<Attribute>();
		list.add(p1);
		return list;
	}

	@Override
	protected List<Attribute> attributes(Object o) throws ClassCastException {
		if (!(o instanceof User))
			throw new ClassCastException();
		User u = (User) o;
		Attribute p1 = new Attribute("uid", u.getUid());
		Attribute p2 = new Attribute("pwd", u.getPwd());
		Attribute p3 = new Attribute("status", u.getStatus());

		ArrayList<Attribute> list = new ArrayList<Attribute>();
		list.add(p1);
		list.add(p2);
		list.add(p3);
		return list;
	}

	@Override
	protected Iterator<?> iterator(Users element) {
		return element.getUser().iterator();
	}
}
