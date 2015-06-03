package eu.kairat.apps.m3.dao.adapter.json;

import java.util.HashMap;
import java.util.Map;

import com.j256.ormlite.support.ConnectionSource;

import eu.kairat.apps.m3.dao.DaoFactory;
import eu.kairat.apps.m3.model.Player;

public class DaoJsonAdapterFactory
{
	private static DaoJsonAdapterFactory           singleton;

	private final Map<Class<?>, DaoJsonAdapter<?>> cache = new HashMap<Class<?>, DaoJsonAdapter<?>>();

	private DaoJsonAdapterFactory()
	{
	}

	public static DaoJsonAdapterFactory getInstance()
	{
		if (singleton == null)
		{
			synchronized (DaoFactory.class)
			{
				if (singleton == null)
				{
					singleton = new DaoJsonAdapterFactory();
				}
			}
		}
		return singleton;
	}

	@SuppressWarnings("unchecked")
	public synchronized <T> DaoJsonAdapter<T> provideDaoJsonAdapter(Class<T> theClass, ConnectionSource connectionSource)
	        throws Exception
	{
		DaoJsonAdapter<T> daoJsonAdapter = (DaoJsonAdapter<T>) cache.get(theClass);
		if (null == daoJsonAdapter)
		{
			if (theClass.equals(Player.class))
			{
				daoJsonAdapter = (DaoJsonAdapter<T>) new PlayerDaoJsonAdapter(connectionSource);
			} else
			{
				daoJsonAdapter = new DaoJsonAdapterAbstract<T>(theClass, connectionSource);
			}

			cache.put(theClass, daoJsonAdapter);
		}
		return daoJsonAdapter;
	}
}
