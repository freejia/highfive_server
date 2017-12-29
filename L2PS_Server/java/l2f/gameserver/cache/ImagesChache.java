package l2f.gameserver.cache;

import gnu.trove.map.hash.TIntObjectHashMap;
import gov.nasa.worldwind.formats.dds.DDSConverter;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import l2f.gameserver.Config;
import l2f.gameserver.idfactory.IdFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImagesChache
{
	private static final Logger _log = LoggerFactory.getLogger(ImagesChache.class);
	private static final int[] SIZES =
	{
		1,
		2,
		4,
		8,
		16,
		32,
		64,
		128,
		256,
		512,
		1024
	};
	private static final int MAX_SIZE = SIZES[(SIZES.length - 1)];

	public static final Pattern HTML_PATTERN = Pattern.compile("%image:(.*?)%", 32);

	private static final ImagesChache _instance = new ImagesChache();

	private final Map<String, Integer> _imagesId = new HashMap<String, Integer>();

	private final TIntObjectHashMap<byte[]> _images = new TIntObjectHashMap<byte[]>();

	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final Lock readLock = this.lock.readLock();

	public static final ImagesChache getInstance()
	{
		return _instance;
	}

	private ImagesChache()
	{
		load();
	}

	public void load()
	{
		_log.info("ImagesChache: Loading images...");

		File dir = new File(Config.DATAPACK_ROOT, "images");
		if ((!dir.exists()) || (!dir.isDirectory()))
		{
			_log.info("ImagesChache: Files missing, loading aborted.");
			return;
		}

		int count = 0;
		for (File file : dir.listFiles())
		{
			if (!file.isDirectory())
			{
				file = resizeImage(file);
				if (file != null)
				{
					count++;

					String fileName = file.getName();
					try
					{
						ByteBuffer bf = DDSConverter.convertToDDS(file);
						byte[] image = bf.array();
						int imageId = IdFactory.getInstance().getNextId();

						this._imagesId.put(fileName.toLowerCase(), Integer.valueOf(imageId));
						this._images.put(imageId, image);

						_log.info("ImagesChache: Loaded " + fileName + " image.");
					}
					catch (IOException e)
					{
						_log.error("ImagesChache: Error while loading " + fileName + " image.", e);
					}
				}
			}
		}
		_log.info("ImagesChache: Loaded " + count + " images");
	}

	private static File resizeImage(File file)
	{
		BufferedImage image;
		try
		{
			image = ImageIO.read(file);
		}
		catch (IOException e)
		{
			_log.error("ImagesChache: Error while resizing " + file.getName() + " image.", e);
			return null;
		}

		if (image == null)
		{
			return null;
		}
		int width = image.getWidth();
		int height = image.getHeight();

		boolean resizeWidth = true;
		if (width > MAX_SIZE)
		{
			image = image.getSubimage(0, 0, MAX_SIZE, height);
			resizeWidth = false;
		}

		boolean resizeHeight = true;
		if (height > MAX_SIZE)
		{
			image = image.getSubimage(0, 0, width, MAX_SIZE);
			resizeHeight = false;
		}

		int resizedWidth = width;
		if (resizeWidth)
		{
			for (int size : SIZES)
			{
				if (size >= width)
				{
					resizedWidth = size;
					break;
				}
			}
		}
		int resizedHeight = height;
		if (resizeHeight)
		{
			for (int size : SIZES)
			{
				if (size >= height)
				{
					resizedHeight = size;
					break;
				}
			}
		}
		if ((resizedWidth != width) || (resizedHeight != height))
		{
			for (int x = 0; x < resizedWidth; x++)
			{
				for (int y = 0; y < resizedHeight; y++)
				{
					image.setRGB(x, y, Color.BLACK.getRGB());
				}
			}
			String filename = file.getName();
			String format = filename.substring(filename.lastIndexOf("."));
			try
			{
				ImageIO.write(image, format, file);
			}
			catch (IOException e)
			{
				_log.error("ImagesChache: Error while resizing " + file.getName() + " image.", e);
				return null;
			}
		}
		return file;
	}

	public int getImageId(String val)
	{
		int imageId = 0;

		this.readLock.lock();
		try
		{
			if (this._imagesId.get(val.toLowerCase()) != null)
			{
				imageId = this._imagesId.get(val.toLowerCase()).intValue();
			}
		}
		finally
		{
			this.readLock.unlock();
		}

		return imageId;
	}

	public byte[] getImage(int imageId)
	{
		byte[] image = null;

		this.readLock.lock();
		try
		{
			image = this._images.get(imageId);
		}
		finally
		{
			this.readLock.unlock();
		}

		return image;
	}
}