package eu.kairat.apps.m3.dao;

import java.util.List;

public interface Dao<T>
{
	T create(T instance) throws Exception;

	List<T> readAll() throws Exception;

	T read(String id) throws Exception;

	List<T> find(T searchInstance) throws Exception;

	T update(T instance) throws Exception;

	void delete(T instance) throws Exception;
}
