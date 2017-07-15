/**
 * 
 */
package com.inodes.util;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author mhapanowicz
 *
 */
public class DynamicLoader {

	private HashMap<String, String> versions = new HashMap<String, String>();
	private HashMap<String, File> locations = new HashMap<String, File>();


	@SuppressWarnings("rawtypes")
	public static void main(String args[]) {
		if (System.getProperty("dynamic.loader") != null ) {
			String[] directories = System.getProperty("dynamic.loader").split(File.pathSeparator);
			DynamicLoader dl = new DynamicLoader();
			dl.adaptClassPath(directories);
		}
		
		String parms[] = new String[args.length - 1];
		for( int i = 1; i < args.length; i++ ) {
			parms[i-1] = args[i];
		}
		
		try {
			int cnt = Thread.activeCount();
			Class c = Class.forName(args[0]);
			Class[] parameterTypes = new Class[]{String[].class};
			@SuppressWarnings("unchecked")
			Method  method = c.getDeclaredMethod ("main", parameterTypes);
			method.invoke(c, new Object[] {parms});
			while( Thread.activeCount() > cnt + 1 ) {
				Thread.sleep(5000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void adaptClassPath(String[] directories) {

		String[] currentClassPath = System.getProperty("java.class.path").split(File.pathSeparator);
		for(int i = 0; i < directories.length; i++) {
			scan(new File(directories[i]));
		}
		for(int i = 0; i < currentClassPath.length; i++) {
			scan(new File(currentClassPath[i]));
		}

		Iterator<File> it = locations.values().iterator();
		while(it.hasNext()) {
			File f = it.next();
			try {
				ClassPathUpdater.add(f);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void scan(File f) {
		if( f.isDirectory() ) {
			File[] sub = f.listFiles();
			for( int i = 0; i < sub.length; i++ ) {
				scan(sub[i]);
			}
		} else {
			String fparts[] = getJarVersion(f);
			String name = fparts[0];
			String version = fparts[1];
			if( version != null ) {
				String oldVersion = versions.get(name);
				if( oldVersion == null ) {
					versions.put(name, version);
					locations.put(name, f);
				} else {
					if( oldVersion.compareTo(version) < 0 ) {
						versions.put(name, version);
						locations.put(name, f);
					}
				}
			}
		}
	}

	public String[] getJarVersion(File jar) {
		String ret[] = new String[2];
		String name = jar.getName();
		String parts[] = name.split("\\.");
		if( parts[parts.length - 1].equalsIgnoreCase("jar")) {
			String elems[] = name.toLowerCase().split("-");
			if( elems[elems.length - 1].endsWith("snapshot.jar")) {
				try {
					ret[1] = elems[elems.length - 2] + "-SNAPSHOT";
					ret[0] = elems[0];
					for( int x = 1; x < elems.length - 2; x++) {
						ret[0] = ret[0] + "-" + elems[x];
					}
				} catch (StringIndexOutOfBoundsException e ) {
					ret[0] = name;
					ret[1] = "1.0.0";
				}
				return ret;
			} else {
				if( elems[elems.length - 1].endsWith(".jar")) {
					try {
						ret[1] = elems[elems.length - 1].substring(0, elems[elems.length - 1].length() - 4);
						ret[0] = name.substring(0, name.length() - ret[1].length() - 5);
					} catch (StringIndexOutOfBoundsException e ) {
						ret[0] = name;
						ret[1] = "1.0.0";
					}
					return ret;
				} else {
					return ret;
				}
			}
		} else {
			return ret;
		}
	}

}
