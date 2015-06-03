package eu.kairat.apps.m3.dao.adapter.json;

import eu.kairat.apps.m3.tools.json.GsonFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.j256.ormlite.support.ConnectionSource;

import eu.kairat.apps.m3.dao.Dao;
import eu.kairat.apps.m3.dao.DaoFactory;

class DaoJsonAdapterAbstract<T> implements DaoJsonAdapter<T>
{
	protected Class<T>   theClass;
	protected Logger     logger;
	protected Dao<T>     dao;
	protected DaoFactory daoFactory;

	public DaoJsonAdapterAbstract(Class<T> theClass, ConnectionSource connectionSource) throws Exception
	{
		this.theClass = theClass;
		logger = LoggerFactory.getLogger(DaoJsonAdapterAbstract.class);
		daoFactory = DaoFactory.getInstance();
		dao = daoFactory.provideDao(theClass, connectionSource);
	}

	public String readAll() throws Exception
	{
		return GsonFactory.GSON.toJson(dao.readAll());
	}

	public String create(String instance) throws Exception
	{
		final T objInstance = GsonFactory.GSON_FOR_UNSECURE_USE.fromJson(instance, theClass);
		return GsonFactory.GSON.toJson(dao.create(objInstance));
	}

	public String read(String id) throws Exception
	{
		return GsonFactory.GSON.toJson(dao.read(id));
	}

	public String find(String searchInstance) throws Exception
	{
		final T searchObjInstance = GsonFactory.GSON_FOR_UNSECURE_USE.fromJson(searchInstance, theClass);
		return GsonFactory.GSON.toJson(dao.find(searchObjInstance));
	}

	public String update(String instance) throws Exception
	{
		final T objInstance = GsonFactory.GSON_FOR_UNSECURE_USE.fromJson(instance, theClass);
		return GsonFactory.GSON.toJson(dao.update(objInstance));
	}

	public void delete(String instance) throws Exception
	{
		final T objInstance = GsonFactory.GSON_FOR_UNSECURE_USE.fromJson(instance, theClass);
		dao.delete(objInstance);
	}
}
