package eu.kairat.apps.m3.model;

import com.j256.ormlite.field.DatabaseField;

public class UserAndRole {

	public UserAndRole(String username, String rolename) {
		this.username = username;
		this.rolename = rolename;
	}

	public UserAndRole() {}

	@DatabaseField(canBeNull = false, unique = true, uniqueIndex = true)
	public String username;
	
	@DatabaseField()
	public String rolename;
}
