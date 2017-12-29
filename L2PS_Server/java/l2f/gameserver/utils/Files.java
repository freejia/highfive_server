package l2f.gameserver.utils;

import l2f.gameserver.model.Player;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class Files
{
	private static final Logger LOG = LoggerFactory.getLogger(Files.class);

	/**
	 * Сохраняет строку в файл в кодировке UTF-8.<br>
	 * Если такой файл существует, то перезаписывает его.
	 * @param path путь к файлу
	 * @param string сохраняемая строка
	 */
	public static void writeFile(String path, String string)
	{
		try
		{
			FileUtils.writeStringToFile(new File(path), string, "UTF-8");
		}
		catch (IOException e)
		{
			LOG.error("Error while saving file : " + path, e);
		}
	}

	public static boolean copyFile(String srcFile, String destFile)
	{
		try
		{
			FileUtils.copyFile(new File(srcFile), new File(destFile), false);
			return true;
		}
		catch (IOException e)
		{
			LOG.error("Error while copying file : " + srcFile + " to " + destFile, e);
		}

		return false;
	}

	public static String read(String name)
	{
		if (name == null)
			return null;

		File file = new File("./" + name);

		if (!file.exists())
			return null;

		String content = null;

		try (BufferedReader br = new BufferedReader(new UnicodeReader(new FileInputStream(file), "UTF-8")))
		{
			StringBuffer sb = new StringBuffer();
			String s = "";
			while ((s = br.readLine()) != null)
				sb.append(s).append('\n');
			content = sb.toString();
		}
		catch (IOException e)
		{
			LOG.error("Error while reading \"Files\"!", e);
		}

		return content;
	}

    /**
     * Сохраняет строку в файл в кодировке UTF-8.<br>
     * Если такой файл существует, то перезаписывает его.
     *
     * @param path   путь к файлу
     * @param string сохраняемая строка
     */
    public static void writeFile1(String path, String string) {
        if (string == null || string.length() == 0)
            return;

        File target = new File(path);

        if (!target.exists())
            try {
                target.createNewFile();
            } catch (IOException e) {
                e.printStackTrace(System.err);
            }

        try {
            FileOutputStream fos = new FileOutputStream(target);
            fos.write(string.getBytes("UTF-8"));
            fos.close();
        }
        catch (FileNotFoundException | UnsupportedEncodingException e)
        {
	        LOG.error("Error while writing File1 in Files", e);
        }
        catch (IOException e)
        {
	        LOG.error("IOException while writing File1 in Files", e);
        }
    }

    public static String read(String name, Player player) {
        if (player == null)
            return "";
        return read(name, player.getLang());
    }

    public static String read(String name, String lang) {
        String tmp = langFileName(name, lang);

        long last_modif = lastModified(tmp); // время модификации локализованного файла
        if (last_modif > 0) // если он существует
        {
            if (last_modif >= lastModified(name)) // и новее оригинального файла
                return Strings.bbParse(read(tmp)); // то вернуть локализованный

            LOG.warn("Last modify of " + name + " more then " + tmp); // если он существует но устарел - выругаться в лог
        }

        return Strings.bbParse(read(name)); // если локализованный файл отсутствует вернуть оригинальный
    }

    public static String langFileName(String name, String lang) {
        if (lang == null || lang.equalsIgnoreCase("en"))
            lang = "";

        String tmp;

        tmp = name.replaceAll("(.+)(\\.htm)", "$1-" + lang + "$2");
        if (!tmp.equals(name) && lastModified(tmp) > 0)
            return tmp;

        tmp = name.replaceAll("(.+)(/[^/].+\\.htm)", "$1/" + lang + "$2");
        if (!tmp.equals(name) && lastModified(tmp) > 0)
            return tmp;

        tmp = name.replaceAll("(.+?/html)/", "$1-" + lang + "/");
        if (!tmp.equals(name) && lastModified(tmp) > 0)
            return tmp;

        if (lastModified(name) > 0)
            return name;

        return null;
    }

    public static long lastModified(String name) {
        if (name == null)
            return 0;

        return new File(name).lastModified();
    }
}