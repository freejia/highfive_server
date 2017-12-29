package l2f.gameserver.cache;

import l2f.gameserver.Config;
import l2f.gameserver.model.Player;
import l2f.gameserver.network.serverpackets.PledgeCrest;
import l2f.gameserver.taskmanager.AutoImageSenderManager;
import l2f.gameserver.utils.Util;
import l2f.gameserver.vote.DDSConverter;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 * Class containing Map of Images sent from Server to Client
 * Images are located in ./data/images/id_by_name
 * They are sent to client by PledgeCrest packet
 */
public class ImagesCache
{
	private static final Logger _log = LoggerFactory.getLogger(ImagesCache.class);
	private static final String CREST_IMAGE_KEY_WORD = "Crest.crest_4_";

	private final Map<Integer, byte[]> images = new HashMap<>();

	/**
	 * Loading Images
	 */
	public ImagesCache()
	{
		loadImages();
	}

	/**
	 * Loading all the images from ./data/images/id_by_name Path and adding them to images map
	 */
	private void loadImages()
	{
		Map<Integer, File> imagesToLoad = getImagesToLoad();

		for (Map.Entry<Integer, File> image : imagesToLoad.entrySet())
		{
			File file = image.getValue();
			byte[] data = DDSConverter.convertToDDS(file).array();
			images.put(image.getKey(), data);
		}

		_log.info("Loaded "+imagesToLoad.size()+" Images!");
	}

	/**
	 * Getting Map<Id, File> of all .png files from ./data/images/id_by_name Path
	 * @return Getting map of all images
	 */
	private static Map<Integer, File> getImagesToLoad()
	{
		Map<Integer, File> files = new HashMap<>();
		File folder = new File("./data/images/id_by_name");
		if (folder.exists())
		{
			File[] listOfFiles = folder.listFiles();
			if (listOfFiles == null)
				return files;

			for (File file : listOfFiles)
			{
				if (file.getName().endsWith(".png"))
				{
					int id = -1;
					try
					{
						String name = FilenameUtils.getBaseName(file.getName());
						id = Integer.parseInt(name);
					}
					catch (NumberFormatException e)
					{
						_log.error("File "+file.getName()+" in id_by_name folder have invalid name!", e);
					}

					if (id != -1)
					{
						files.put(id, file);
					}
				}
			}
		}
		else
		{
			_log.error("Path \"./data/images/id_by_name\" doesn't exist!", new FileNotFoundException());
		}

		return files;
	}

	/**
	 * Sending All Images that are needed to open HTML to the player
	 * @param html page that may contain images
	 * @param player that will receive images
	 */
	public void sendUsedImages(String html, Player player)
	{
		if (!Config.ALLOW_SENDING_IMAGES)
			return;
		char[] charArray = html.toCharArray();
		int lastIndex = 0;

		while (lastIndex != -1)
		{
			lastIndex = html.indexOf(CREST_IMAGE_KEY_WORD, lastIndex);

			if (lastIndex != -1)
			{
				int start = lastIndex + CREST_IMAGE_KEY_WORD.length();
				int end = getFileNameEnd(charArray, start);
				lastIndex = end;
				int imageId = Integer.parseInt(html.substring(start, end));

				//Checking if images are sent automatically(then player needs to wait for sending Thread) or in real time
				if (!AutoImageSenderManager.isImageAutoSendable(imageId))
					sendImageToPlayer(player, imageId);
			}
		}
	}

	/**
	 * Getting end of Image File Name(name is always numbers)
	 * @param charArray whole text
	 * @param start place
	 * @return whole name
	 */
	private static int getFileNameEnd(char[] charArray, int start)
	{
		int stop = start;
		for (; stop<charArray.length;stop++)
		{
			if (!Util.isInteger(charArray[stop]))
			{
				return stop;
			}
		}
		return stop;
	}

	/**
	 * Sending Image as PledgeCrest to a player
	 * If image was already sent once to the player, it's skipping this part
	 * Saved images data is in player Quick Vars as Key: "Image"+imageId Value: true
	 * @param player that will receive image
	 * @param imageId Id of the image
	 */
	public void sendImageToPlayer(Player player, int imageId)
	{
		if (!Config.ALLOW_SENDING_IMAGES)
			return;

		if (player.wasImageLoaded(imageId))
			return;

		player.addLoadedImage(imageId);

		if (images.containsKey(imageId))
		{
			player.sendPacket(new PledgeCrest(imageId, images.get(imageId)));
		}
		/*else
		{
			_log.warn("Trying to send image that doesn't exist, id:"+imageId);
		}*/
	}

	/**
	 * @return the only instance of ImagesCache
	 */
	public static ImagesCache getInstance()
	{
		return ImagesCacheHolder.instance;
	}

	private static class ImagesCacheHolder
	{
		protected static final ImagesCache instance = new ImagesCache();
	}
}
