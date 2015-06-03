package eu.kairat.apps.m3.service.rest;

import eu.kairat.apps.m3.model.Role;
import eu.kairat.apps.m3.model.UserAndRole;
import eu.kairat.apps.m3.tools.json.GsonFactory;

import org.atmosphere.client.TrackMessageSizeInterceptor;
import org.atmosphere.config.service.AtmosphereService;
import org.atmosphere.cpr.ApplicationConfig;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResourceEventListenerAdapter;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.j256.ormlite.support.ConnectionSource;

import eu.kairat.apps.m3.dao.adapter.json.DaoJsonAdapter;
import eu.kairat.apps.m3.dao.adapter.json.DaoJsonAdapterFactory;
import eu.kairat.apps.m3.database.ConnectionHandler;
import eu.kairat.apps.m3.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/entity/user")
@AtmosphereService(
        dispatch = true,
        interceptors = {AtmosphereResourceLifecycleInterceptor.class, TrackMessageSizeInterceptor.class},
        path = "/rest",
        servlet = "org.glassfish.jersey.servlet.ServletContainer")
public class UserRestService {

    @Context
    private HttpServletRequest request;

    private Logger logger;
    private DaoJsonAdapterFactory daoJsonAdapterFactory;
    private DaoJsonAdapter<User> userDaoJsonAdapter;
    private DaoJsonAdapter<UserAndRole> userAndRoleDaoJsonAdapter;
    
    public UserRestService() throws Exception {
    	ConnectionSource connectionSource = ConnectionHandler.INSTANCE.provideConnectionSource();
		
    	logger = LoggerFactory.getLogger(UserRestService.class);
    	daoJsonAdapterFactory = DaoJsonAdapterFactory.getInstance();
    	userDaoJsonAdapter = daoJsonAdapterFactory.provideDaoJsonAdapter(User.class, connectionSource);
        userAndRoleDaoJsonAdapter = daoJsonAdapterFactory.provideDaoJsonAdapter(UserAndRole.class, connectionSource);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getAll() throws Exception {
        return userDaoJsonAdapter.readAll();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String create(String jsonString) throws Exception {
    	logger.info("\n\nCRUD | CREATE STARTED...\n\n" + GsonFactory.prettifyJson(jsonString));
    	
        final String userAsJson = userDaoJsonAdapter.create(jsonString);

        final User user = GsonFactory.GSON.fromJson(jsonString, User.class);
        userAndRoleDaoJsonAdapter.create("{\"username\": \"" + user.username + "\", \"rolename\":\"" + Role.PLAYER.name() + "\"}");

        AtmosphereResource r = (AtmosphereResource) request.getAttribute(ApplicationConfig.ATMOSPHERE_RESOURCE);
        if (r == null) {
            logger.error("\n\nCRUD | CREATE FAILED.\n\n");
            throw new IllegalStateException();
        }

        logger.info("\n\nCRUD | SUCCEEDED. Trying to broadcast...");
        r.getBroadcaster().broadcast(userAsJson);
        logger.info("\n\nCRUD | Broadcasting finished.\n\n");
        return userAsJson;
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public void delete(String jsonString) throws Exception {
        logger.info("\n\nCRUD | DELETION STARTED...\n\n" + GsonFactory.prettifyJson(jsonString));

        userDaoJsonAdapter.delete(jsonString);

        AtmosphereResource r = (AtmosphereResource) request.getAttribute(ApplicationConfig.ATMOSPHERE_RESOURCE);
        if (r == null) {
            logger.error("\n\nCRUD | DELETION FAILED.\n\n");
            throw new IllegalStateException();
        }

        logger.info("\n\nCRUD | SUCCEEDED. Trying to broadcast...");
        //r.getBroadcaster().broadcast("{\"deleted\":USERID....");
        logger.info("\n\nCRUD | Broadcasting finished.\n\n");
    }







    @GET
    @Path("websocket")
    public void configureAtmosphereResource() {
        AtmosphereResource r = (AtmosphereResource) request.getAttribute(ApplicationConfig.ATMOSPHERE_RESOURCE);

        if (r != null) {
            r.addEventListener(new AtmosphereResourceEventListenerAdapter.OnDisconnect() {
                @Override
                public void onDisconnect(AtmosphereResourceEvent event) {
                    if (event.isCancelled()) {
                        logger.info("Browser {} unexpectedly disconnected", event.getResource().uuid());
                    } else if (event.isClosedByClient()) {
                        logger.info("Browser {} closed the connection", event.getResource().uuid());
                    }
                }
            });
        } else {
            throw new IllegalStateException();
        }
    }

}