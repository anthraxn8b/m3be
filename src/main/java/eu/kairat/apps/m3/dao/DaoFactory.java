package eu.kairat.apps.m3.dao;

import java.util.HashMap;
import java.util.Map;

import com.j256.ormlite.support.ConnectionSource;

public class DaoFactory
{
	private static DaoFactory           singleton;

	private final Map<Class<?>, Dao<?>> cache = new HashMap<Class<?>, Dao<?>>();

	private DaoFactory()
	{
	}

	public static DaoFactory getInstance()
	{
		if (singleton == null)
		{
			synchronized (DaoFactory.class)
			{
				if (singleton == null)
				{
					singleton = new DaoFactory();
				}
			}
		}
		return singleton;
	}

	public synchronized <T> Dao<T> provideDao(Class<T> theClass, ConnectionSource connectionSource) throws Exception
	{
		@SuppressWarnings("unchecked")
		Dao<T> dao = (Dao<T>) cache.get(theClass);
		if (null == dao)
		{
			// if(theClass.equals(User.class)) {
			// dao = (DaoAbstract<T>)new DaoUser();
			// } else {
			dao = new DaoAbstract<T>(theClass, connectionSource);
			// }

			cache.put(theClass, dao);
		}
		return dao;
	}
}
