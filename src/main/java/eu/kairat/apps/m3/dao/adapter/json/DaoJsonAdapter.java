package eu.kairat.apps.m3.dao.adapter.json;

public interface DaoJsonAdapter<T>
{
	String create(String instance) throws Exception;

	String readAll() throws Exception;

	String read(String id) throws Exception;

	String find(String searchInstance) throws Exception;

	String update(String instance) throws Exception;

	void delete(String instance) throws Exception;
}