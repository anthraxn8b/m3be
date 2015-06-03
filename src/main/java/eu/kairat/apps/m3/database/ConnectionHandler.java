package eu.kairat.apps.m3.database;

import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import eu.kairat.apps.m3.properties.Properties;
import eu.kairat.apps.m3.properties.PropertiesFactory;
import eu.kairat.apps.m3.properties.PropertiesTypes;
import eu.kairat.apps.m3.properties.PropertiesTypesForConfig;

public enum ConnectionHandler
{
	INSTANCE;
	
	private JdbcPooledConnectionSource connectionSource;
	
	private ConnectionHandler() 
	{
		try
		{			
			final Properties config =
				PropertiesFactory.getInstance().provideProperties(PropertiesTypes.CONFIG);

			connectionSource =
				new JdbcPooledConnectionSource(config.getString(PropertiesTypesForConfig.DB_URL));
			connectionSource.setMaxConnectionAgeMillis(5 * 60 * 1000);
			connectionSource.setUsername(config.getString(PropertiesTypesForConfig.DB_USERNAME));
			connectionSource.setPassword(config.getString(PropertiesTypesForConfig.DB_PASSWORD));
		}
		catch(Exception e)
		{
			connectionSource = null;
			throw new ExceptionInInitializerError(e);
		}
	}

	public ConnectionSource provideConnectionSource() throws Exception
	{	
		return connectionSource;
	}
}
