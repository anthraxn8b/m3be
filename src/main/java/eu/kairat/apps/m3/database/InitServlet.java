package eu.kairat.apps.m3.database;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.j256.ormlite.support.ConnectionSource;

import eu.kairat.apps.m3.dao.Dao;
import eu.kairat.apps.m3.dao.DaoFactory;
import eu.kairat.apps.m3.model.Role;
import eu.kairat.apps.m3.model.User;
import eu.kairat.apps.m3.model.UserAndRole;

@SuppressWarnings("serial")
public class InitServlet extends HttpServlet
{
	public void init() throws ServletException
	{
		try
		{
			final Logger logger = LoggerFactory.getLogger(InitServlet.class);

			logger.info("----------");
			logger.info("---------- Setting up the database ----------");
			logger.info("----------");

			final ConnectionSource connectionSource = ConnectionHandler.INSTANCE.provideConnectionSource();
			final Dao<User> userDao = DaoFactory.getInstance().provideDao(User.class, connectionSource);
			final Dao<UserAndRole> userAndRoleDao = DaoFactory.getInstance().provideDao(UserAndRole.class,
			        connectionSource);
			
			User userBoris = new User("bka", "s3cret");
			userBoris = userDao.create(userBoris);
			
			UserAndRole userAndRoleBorisAdministrator = new UserAndRole("bka", Role.ADMINISTRATOR.name());
			userAndRoleBorisAdministrator = userAndRoleDao.create(userAndRoleBorisAdministrator);
		}
		catch (Exception e)
		{
			throw new ServletException(e);
		}

	}
}