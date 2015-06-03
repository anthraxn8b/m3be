package eu.kairat.apps.m3.service.rest;

import java.util.LinkedList;
import java.util.List;

import eu.kairat.apps.m3.service.rest.errorCodes.UserAndRoleRestServiceErrorCodes;
import eu.kairat.apps.m3.tools.json.GsonFactory;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.j256.ormlite.support.ConnectionSource;

import eu.kairat.apps.m3.dao.Dao;
import eu.kairat.apps.m3.dao.DaoFactory;
import eu.kairat.apps.m3.database.ConnectionHandler;
import eu.kairat.apps.m3.exception.SpecialException;
import eu.kairat.apps.m3.model.User;
import eu.kairat.apps.m3.model.UserAndRole;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/relation/userAndRole")
public class UserAndRoleRestService {

	@Context
	private HttpServletRequest request;

	//private Logger logger;
	//private DaoJsonAdapterFactory daoJsonAdapterFactory;
	//private DaoJsonAdapter<UserAndRole> userAndRoleDaoJsonAdapter;
	private Dao<UserAndRole> userAndRoleDao;
	private Dao<User> userDao;

	public UserAndRoleRestService() throws Exception {

		ConnectionSource connectionSource = ConnectionHandler.INSTANCE.provideConnectionSource();
		//logger = LoggerFactory.getLogger(UserAndRoleRestService.class);
		//daoJsonAdapterFactory = DaoJsonAdapterFactory.getInstance();
		//userAndRoleDaoJsonAdapter = daoJsonAdapterFactory.provideDaoJsonAdapter(UserAndRole.class, connectionSource);
		

		userAndRoleDao = DaoFactory.getInstance().provideDao(UserAndRole.class, connectionSource);
		userDao = DaoFactory.getInstance().provideDao(User.class, connectionSource);
		
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	//@RolesAllowed("secured")
	public String getRolesForUsernamePassword(String jsonString) throws Exception {

		// transfer json object to java user object
		final User userSearchInstance = GsonFactory.GSON_FOR_UNSECURE_USE.fromJson(jsonString, User.class);

		// check if username and password are set
		if(null == userSearchInstance.username || null == userSearchInstance.password ) {
			throw new SpecialException(UserAndRoleRestServiceErrorCodes.USER_AND_PASSWORD_HAVE_TO_BE_SET);
		}

		// search for the user
		final List<User> users = userDao.find(userSearchInstance);
		
		// handle the cases if the number of found users is not 1
		if(users.size() < 1) {
			throw new SpecialException(UserAndRoleRestServiceErrorCodes.USER_OR_PASSWORD_MISMATCH);
		}
		if (users.size() > 1) {
			throw new Exception("No single user found. Found " + users.size() + " users!");
		}

		// search for the roles assigned to the user
		final List<UserAndRole> userAndRoles = userAndRoleDao.find(new UserAndRole(users.get(0).username, null));

		// return the role names only
		final List<String> roles = new LinkedList<String>();
		userAndRoles.forEach(userAndRole -> roles.add(userAndRole.rolename));
		return GsonFactory.GSON.toJson(roles);
	}
}