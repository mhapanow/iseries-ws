/**
 * 
 */
package com.inodes.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author mhapanowicz
 * 
 * File loader utilities
 * 
 */
public class FileLoader {

	public static int PRECEDENCE_CLASSLOADER = 1;
	public static int PRECEDENCE_SYSTEMPATH = 2;

	/**
	 * Finds a resource using both, the Class loader and the System Path. This
	 * method is equivalent to getResource(resourceName,
	 * FileLoader.PRECEDENCE_CLASSLOADER).
	 * 
	 * @param resourceName
	 *            The resource to find
	 * @return The URL to the resource found, or null if it found nothing
	 */
	public static URL getResource(String resourceName) {
		return getResource(resourceName, PRECEDENCE_CLASSLOADER);
	}

	/**
	 * Finds a resource using both, the Class loader and the System Path
	 * 
	 * @param resourceName
	 *            The resource to find
	 * 
	 * @param precedence
	 *            If precedence is PRECEDENCE_CLASSLOADER, first search using
	 *            the Class loader and then, if it found nothing, search using
	 *            System Path. If precedence is PRECEDENCE_SYSTEMPATH, first
	 *            search using the System Path, and then, if it found nothing,
	 *            search using the default Class loader.
	 * @return The URL to the resource found, or null if it found nothing
	 */
	public static URL getResource(String resourceName, int precedence) {
		URL url = null;

		if (precedence == PRECEDENCE_CLASSLOADER) {
			url = findResourceInClassPath(resourceName);
			if (url == null)
				url = findResourceInLibraryPath(resourceName);
		} else {
			url = findResourceInLibraryPath(resourceName);
			if (url == null)
				url = findResourceInClassPath(resourceName);
		}

		return url;
	}

	/**
	 * Finds a system resource using the System Path
	 * 
	 * @param resourceName
	 *            The resource name to find
	 * @return The URL to the resource found, or null if it found nothing
	 */
	@SuppressWarnings("deprecation")
	public static URL findResourceInLibraryPath(String resourceName) {

		// Tries to locate the resource in the current directory
		File f = new File(resourceName);
		try {
			if (f.exists())
				return (f.toURL());
		} catch (MalformedURLException e) {
		}

		// Tries to locate the resource in the system path
		String libraryPath = System.getProperty("java.library.path");
		String pathElems[] = libraryPath.split(File.pathSeparator);
		for (int i = 0; i < pathElems.length; i++) {
			String res = new StringBuffer().append(pathElems[i])
					.append(File.separator).append(resourceName).toString();
			f = new File(res);
			try {
				if (f.exists())
					return (f.toURL());
			} catch (MalformedURLException e) {
			}
		}

		return null;

	}

	/**
	 * Finds a system resource using the Default Class loader
	 * 
	 * @param resourceName
	 *            The resource name to find
	 * @return The URL to the resource found, or null if it found nothing
	 */
	@SuppressWarnings("deprecation")
	public static URL findResourceInClassPath(String resourceName) {

		URL url;

		// Tries to locate the resource with the class loader
		url = ClassLoader.getSystemResource(resourceName);
		if (url != null)
			return url;

		// Tries to locate the resource in the main directory
		url = FileLoader.class.getResource(resourceName);
		if (url != null)
			return url;

		// Tries to locate the resource in the class list
		String path = FileLoader.class.getResource(FileLoader.class.getSimpleName() + ".class").getPath();
		if( path != null ) {
			String pathParts[] = path.split("!");
			path = pathParts[0];
			pathParts = path.split("file:");
			if( pathParts.length > 1 ) {
				path = pathParts[1];
			} else {
				path = pathParts[0];
			}
			if( path.contains("/lib/")) {
				File f = new File(path);
				f = f.getParentFile().getParentFile();
				f = new File(f.getPath() + File.separator + "classes" + File.separator + resourceName);
				if( f.exists()) {
					try {
						return f.toURL();
					} catch( Exception e) {}
				}
			} else {
				File f = new File(path);
				try {
					f = f.getParentFile().getParentFile().getParentFile();
					f = new File(f.getPath() + File.separator + resourceName);
					if( f.exists()) {
						try {
							return f.toURL();
						} catch(Exception e) {}
					}
				} catch(NullPointerException npe) {}
			}
		}
		
		return null;
	}
}
