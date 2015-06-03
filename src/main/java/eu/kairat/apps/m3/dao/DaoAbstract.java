package eu.kairat.apps.m3.dao;

import java.sql.SQLException;
import java.util.List;

import eu.kairat.apps.m3.properties.Properties;
import eu.kairat.apps.m3.properties.PropertiesFactory;
import eu.kairat.apps.m3.properties.PropertiesTypesForConfig;
import eu.kairat.apps.m3.properties.PropertiesTypes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

class DaoAbstract<T> implements eu.kairat.apps.m3.dao.Dao<T>
{
	protected Logger           logger;
	protected Dao<T, String>   dao;
	protected ConnectionSource connectionSource;
	protected Class<T>         theClass;

	public DaoAbstract(Class<T> theClass, ConnectionSource connectionSource) throws Exception
	{
		this.theClass = theClass;
		this.connectionSource = connectionSource;

		try
		{
			// setup logger
			logger = LoggerFactory.getLogger(theClass);

			final Properties config = PropertiesFactory.getInstance().provideProperties(PropertiesTypes.CONFIG);

			// setup orm-persistence
			dao = DaoManager.createDao(connectionSource, theClass);
			if (config.getBoolean(PropertiesTypesForConfig.ORM_DROPTABLE))
				// ignores errors
				TableUtils.dropTable(connectionSource, theClass, true);
			if (config.getBoolean(PropertiesTypesForConfig.ORM_CREATETABLE))
				// throws an error if the table still exists
				TableUtils.createTable(connectionSource, theClass);
		} catch (java.lang.Exception e)
		{
			throw new Exception("Initializing the DAO for " + theClass + " failed.", e);
		}
	}

	public List<T> readAll() throws Exception
	{
		logger.info("Returning all instances of " + theClass.getSimpleName() + "...");
		try
		{
			final List<T> instances = dao.queryForAll();
			logger.info("Returning " + instances.size() + " instances...");
			return instances;
		} catch (SQLException e)
		{
			throw new Exception("Reading all instances of " + theClass.getSimpleName() + " failed.", e);
		}
	}

	public T read(String id) throws Exception
	{
		logger.info("Returning the instance with id " + id + " of " + theClass.getSimpleName() + "...");
		try
		{
			final T instance = dao.queryForId(id);
			logger.info("Returning " + (null == instance ? "0" : "1") + " instances...");
			return instance;

		} catch (SQLException e)
		{
			throw new Exception("Reading all instances of " + theClass.getSimpleName() + " failed.", e);
		}
	}

	public List<T> find(T searchInstance) throws Exception
	{
		logger.info("Returning all instances matching the search instance of " + theClass.getSimpleName() + "...");
		try
		{
			final List<T> instances = dao.queryForMatching(searchInstance);
			logger.info("Returning " + instances.size() + " instances...");
			return instances;
		} catch (SQLException e)
		{
			throw new Exception("Reading all instances matching the search instance of " + theClass.getSimpleName()
			        + " failed.", e);
		}
	}

	public T create(T instance) throws Exception
	{
		logger.info("Creating new instance of type " + theClass.getSimpleName() + "...");
		try
		{
			final Integer returnValue = dao.create(instance);
			if (1 != returnValue)
				throw new Exception("Creating an instance of type " + theClass.getSimpleName()
				        + " failed. Database rows affected: " + returnValue);
			logger.info("Created instance: " + instance.toString());
			return instance;
		} catch (SQLException e)
		{
			throw new Exception("Creating a new instance of type " + theClass.getSimpleName() + " failed.", e);
		}
	}

	public T update(T instance) throws Exception
	{
		logger.info("Updating an instance of type " + theClass.getSimpleName() + "...");
		try
		{
			final Integer returnValue = dao.update(instance);
			if (1 != returnValue)
				throw new Exception("Updating an instance of type " + theClass.getSimpleName()
				        + " failed. Database rows affected: " + returnValue);
			logger.info("Updated instance: " + instance.toString());
			return instance;
		} catch (SQLException e)
		{
			throw new Exception("Updating an instance of type " + theClass.getSimpleName() + " failed.", e);
		}
	}

	public void delete(T instance) throws Exception
	{
		logger.info("Deleting an instance of type " + theClass.getSimpleName() + "...");
		try
		{
			final Integer returnValue = dao.delete(instance);
			if (1 != returnValue)
				throw new Exception("Deleting an instance of type " + theClass.getSimpleName()
				        + " failed. Database rows affected: " + returnValue);
			logger.info("Deleted instance: " + instance.toString());
		} catch (SQLException e)
		{
			throw new Exception("Deleting an instance of type " + theClass.getSimpleName() + " failed.", e);
		}
	}
}
