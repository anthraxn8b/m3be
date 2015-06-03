package eu.kairat.apps.m3.dao.adapter.json;

import eu.kairat.apps.m3.model.Player;
import eu.kairat.apps.m3.properties.Properties;
import eu.kairat.apps.m3.properties.PropertiesFactory;
import eu.kairat.apps.m3.properties.PropertiesTypes;
import eu.kairat.apps.m3.properties.PropertiesTypesForConfig;
import eu.kairat.apps.m3.tools.json.GsonFactory;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

import com.j256.ormlite.support.ConnectionSource;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

class PlayerDaoJsonAdapter extends DaoJsonAdapterAbstract<Player>
{
	public PlayerDaoJsonAdapter(ConnectionSource connectionSource) throws Exception
	{
		super(Player.class, connectionSource);
	}

	public String create(String jsonString) throws Exception
	{
		final String jsonReturnValue = super.create(jsonString);

		final Player player = GsonFactory.GSON.fromJson(jsonReturnValue, Player.class);

		final Properties configuration = PropertiesFactory.getInstance().provideProperties(PropertiesTypes.CONFIG);
		final String staticFilesDirectory = configuration.getString(PropertiesTypesForConfig.FILES_STATICFILES) + "/portraits/";

		final String portrait = player.portraitData;

		if (null != portrait)
		{
			try
			{
				final int lastPositionToRemove = portrait.indexOf(",");
				final String portraitBase64 = portrait.substring(lastPositionToRemove);

				final byte[] decodedBytes = DatatypeConverter.parseBase64Binary(portraitBase64);
				final BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(decodedBytes));

				final File outputfile = new File(staticFilesDirectory + "playerImages/player_" + player.id + ".jpg");

				ImageIO.write(bufferedImage, "jpg", outputfile);
				bufferedImage.flush();
			} catch (IOException e)
			{
				throw new Exception("Writing image to disk failed.", e);
			}
		}

		return jsonReturnValue;
	}
}
