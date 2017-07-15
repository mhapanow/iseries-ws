package org.dcm.services.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import com.inodes.util.FileLoader;

public class I18NUtils {

	private static Map<String, Properties> langWrappers = CollectionFactory.createMap();

	/**
	 * Obtains the internationalized message file
	 * 
	 * @param lang
	 *            The language to process
	 * @param message
	 *            The message to find
	 * @return An internationalized and formatted message
	 * @throws IOException
	 */
	public static String getI18NMessage(String lang, String message) throws IOException {
		return getI18NMessage(lang, message, null);
	}

	/**
	 * Obtains the internationalized message file
	 * 
	 * @param lang
	 *            The language to process
	 * @param message
	 *            The message to find
	 * @param replacementValues
	 *            A map with a value list to replace
	 * @return An internationalized and formatted message
	 * @throws IOException
	 */
	public static String getI18NMessage(String lang, String message, Map<String, String> replacementValues) throws IOException {

		if( langWrappers.get(lang) == null ) {
			// Tries to locate the resource with the class loader
			URL url = FileLoader.getResource(new StringBuffer("lang_").append(lang)
					.append(".properties").toString());
			InputStreamReader is = new InputStreamReader(new FileInputStream(new File(url.getFile())));

			// Opens the properties file and loads its contents
			Properties langWrapper = new Properties();
			langWrapper.load(is);
			is.close();
			langWrappers.put(lang, langWrapper);
		}

		String text = replaceValues(langWrappers.get(lang).getProperty(message), replacementValues)	;

		return text;
	}

	/**
	 * Replaces values in a text defined by {xxx} with replacement values
	 * arranged in a Map
	 * 
	 * @param text
	 *            The quoted text
	 * @param replacementValues
	 *            Map with replacement values
	 * @return
	 * @throws IOException
	 */
	public static String replaceValues( String text, Map<String, String> replacementValues ) throws IOException {
		if( replacementValues != null ) {
			Iterator<String> i = replacementValues.keySet().iterator();
			while( i.hasNext() ) {
				String key = i.next();
				String val = replacementValues.get(key);
				text = text.replace("{" + key + "}", val);
			}
		}
		
		return text;
	}
}
