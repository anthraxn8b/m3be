package eu.kairat.apps.m3.model;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;

public class User {
    public User(String username, String password)
    {
        this.username = username;
        this.password = password;
    }

    public User(Integer id, String username, String password, String portraitData)
    {
        this(username,password);
        this.id = id;
    }

    public User() {}
    
    @DatabaseField(generatedId = true, canBeNull = false, throwIfNull = true)
    public int id;
    
    @DatabaseField(canBeNull = false, unique = true, uniqueIndex = true)
    public String username;

	@DatabaseField(canBeNull = false)
	public String password;
	
	//@DatabaseField(canBeNull = false)
	//public UserType userType;
	
	@DatabaseField(canBeNull = false, version = true)
	public Date lastModified;

	
}
