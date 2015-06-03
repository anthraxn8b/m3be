package eu.kairat.apps.m3.service.rest;

import java.sql.SQLException;
import java.util.concurrent.Callable;

import eu.kairat.apps.m3.model.Role;
import eu.kairat.apps.m3.model.UserAndRole;
import eu.kairat.apps.m3.service.rest.errorCodes.PlayerRestServiceErrorCodes;
import eu.kairat.apps.m3.tools.json.GsonFactory;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.atmosphere.client.TrackMessageSizeInterceptor;
import org.atmosphere.config.service.AtmosphereService;
import org.atmosphere.cpr.ApplicationConfig;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResourceEventListenerAdapter;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;

import eu.kairat.apps.m3.dao.Dao;
import eu.kairat.apps.m3.dao.DaoFactory;
import eu.kairat.apps.m3.dao.adapter.json.DaoJsonAdapter;
import eu.kairat.apps.m3.dao.adapter.json.DaoJsonAdapterFactory;
import eu.kairat.apps.m3.database.ConnectionHandler;
import eu.kairat.apps.m3.exception.SpecialException;
import eu.kairat.apps.m3.model.Player;
import eu.kairat.apps.m3.model.User;
import eu.kairat.apps.m3.model.dto.PlayerDto;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/entity/player")
@AtmosphereService(
        dispatch = true,
        interceptors = {AtmosphereResourceLifecycleInterceptor.class, TrackMessageSizeInterceptor.class},
        path = "/rest",
        servlet = "org.glassfish.jersey.servlet.ServletContainer")
public class PlayerRestService {

    @Context
    private HttpServletRequest request;

    private Logger logger;
    private DaoJsonAdapterFactory daoJsonAdapterFactory;
    private DaoJsonAdapter<Player> playerDaoJsonAdapter;
    
    private Dao<User> daoUser;
    private Dao<Player> daoPlayer;
    private Dao<UserAndRole> daoUserAndRole;
    
	private ConnectionSource connectionSource;
	
    public PlayerRestService() throws Exception {
		
		// pooled connection source
		connectionSource = ConnectionHandler.INSTANCE.provideConnectionSource();
    	logger = LoggerFactory.getLogger(PlayerRestService.class);
    	daoJsonAdapterFactory = DaoJsonAdapterFactory.getInstance();
    	playerDaoJsonAdapter = daoJsonAdapterFactory.provideDaoJsonAdapter(Player.class, connectionSource);
    	
    	daoUser = DaoFactory.getInstance().provideDao(User.class, connectionSource);
    	daoPlayer = DaoFactory.getInstance().provideDao(Player.class, connectionSource);
        daoUserAndRole = DaoFactory.getInstance().provideDao(UserAndRole.class, connectionSource);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getAll() throws Exception {
        return playerDaoJsonAdapter.readAll();
    }

    
    
    
    
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String create(String jsonString) throws Exception {
    	logger.info("\n\nCRUD | CREATE STARTED...\n\n" + GsonFactory.prettifyJson(jsonString));
    	
    	final PlayerDto playerDtoIn  = GsonFactory.GSON_FOR_UNSECURE_USE.fromJson(jsonString, PlayerDto.class);
    	final PlayerDto playerDtoOut = new PlayerDto();
        final UserAndRole userAndRole = new UserAndRole(playerDtoIn.user.username, Role.PLAYER.name());
    	
        try {
    		TransactionManager.callInTransaction(
    				connectionSource,
    				new Callable<Void>() {
    					public Void call() throws Exception {
    						// first player then user shows that the rollback works locally
    						playerDtoOut.player = daoPlayer.create(playerDtoIn.player);
    						// maybe using sleep can help checking if multiuser also works with rollback here...
    						//Thread.sleep(15000);
    						playerDtoOut.user   = daoUser.create(playerDtoIn.user);
                            daoUserAndRole.create(userAndRole);
    						return null;
    					}
			});
        }
        catch(Exception e) {
        	Throwable th = ExceptionUtils.getRootCause(e);
        	if(th instanceof SQLException) {
        		final SQLException sqle = (SQLException)th;
        
            	logger.debug("SQL-ERROR-CODE: " + sqle.getErrorCode());
            	logger.debug("SQL-STATE: " + sqle.getSQLState());
            	logger.debug("SQL-MESSAGE: " + sqle.getMessage());
            	
            	switch(((SQLException)th).getErrorCode()) {
            		case 1062:
            			throw new SpecialException(PlayerRestServiceErrorCodes.USERNAME_ALREADY_IN_USE);
            	}
        	}
        	throw e;
        }
		
        final String playerDtoAsJsonOut = GsonFactory.GSON.toJson(playerDtoOut);
        final String playerAsJsonOut = GsonFactory.GSON.toJson(playerDtoOut.player);

        AtmosphereResource r = (AtmosphereResource) request.getAttribute(ApplicationConfig.ATMOSPHERE_RESOURCE);
        if (r == null) {
            logger.error("\n\nCRUD | CREATE FAILED.\n\n");
            throw new IllegalStateException();
        }

        logger.info("\n\nCRUD | SUCCEEDED. Trying to broadcast...");
        r.getBroadcaster().broadcast(playerAsJsonOut);
        logger.info("\n\nCRUD | Broadcasting finished.\n\n");
        return playerDtoAsJsonOut;
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