package eu.kairat.apps.m3.model;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;

public class Player {

	@DatabaseField(generatedId = true, canBeNull = false, throwIfNull = true)
    public int id;

	@DatabaseField(canBeNull = false)
	public String firstname;
	
	@DatabaseField(canBeNull = false)
	public String lastname;

	@DatabaseField()
	public String portraitData;
	
	@DatabaseField(canBeNull = false, version = true)
	public Date lastModified;
}
