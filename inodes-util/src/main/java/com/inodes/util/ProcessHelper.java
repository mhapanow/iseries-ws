/**
 * 
 */
package com.inodes.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;


public class ProcessHelper {
	private static Logger log = Logger.getLogger(ProcessHelper.class);
	private List<Process> processes;


	public ProcessHelper() {
		processes = new ArrayList< Process >();

	}

	public Process startNewJavaProcess(final String optionsAsString, final String mainClass, final String[] arguments)
			throws IOException {

		ProcessBuilder processBuilder = createProcess(optionsAsString, mainClass, arguments);
		processBuilder.redirectErrorStream(true);
		final Process process = processBuilder.start();
		new Thread(new Runnable() {
			public void run() {
				try {
					InputStream is = process.getInputStream();
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader br = new BufferedReader(isr);
					String line; 
					while ((line = br.readLine()) != null) {
						System.out.println(line);
					}
				} catch( Throwable t ) {}
			}
		}).start();
		processes.add(process);
		log.debug("Process " + process.toString() + " has started");

		return process;
	}

	public Process startNewProcess(final String[] arguments, boolean wait)
			throws IOException {

		ProcessBuilder processBuilder = createNativeProcess(arguments, null);
		processBuilder.redirectErrorStream(true);
		final Process process = processBuilder.start();
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					InputStream is = process.getInputStream();
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader br = new BufferedReader(isr);
					String line; 
					while ((line = br.readLine()) != null) {
						System.out.println(line);
					}
				} catch( Throwable t ) {}
			}
		}, "Process Helper");
		t.start();
		if( wait ) {
			try {
				t.join();
			} catch (InterruptedException e) {
			}
		}
		processes.add(process);
		log.debug("Process " + process.toString() + " has started");

		return process;
	}

	public String runProcess(final String[] arguments, boolean wait)
			throws IOException {

		String stdout = "";
		ProcessBuilder processBuilder = createNativeProcess(arguments, null);
		processBuilder.redirectErrorStream(true);
		final Process process = processBuilder.start();
		try {
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line; 
			while ((line = br.readLine()) != null) {
				stdout += line + "\n";
			}
		} catch( Throwable t ) {}
		processes.add(process);
		log.debug("Process " + process.toString() + " has started");

		return stdout;
	}

	private String getTomcatClasspath() {
		if( System.getProperty("catalina.base") != null ) {
			File tomcatBase = new File(System.getProperty("catalina.base"));
			if(tomcatBase.isDirectory() && tomcatBase.exists()) {
				File[] f = tomcatBase.listFiles();
				Vector<File> v = new Vector<File>();
				for( int i = 0; i < f.length; i++ ) {
					v.addAll(getJars(f[i]));
				}

				StringBuffer sb = new StringBuffer();
				Iterator<File> i = v.iterator();
				while(i.hasNext()) {
					File x = i.next();
					sb.append(x.getAbsolutePath());
					if( i.hasNext()) {
						sb.append(File.pathSeparator);
					}
				}
				return sb.toString();
			} else {
				return "";
			}
		} else {
			return "";
		}
	}

	private Vector<File> getJars(File f) {
		Vector<File> v = new Vector<File>();
		if(f.isFile()) {
			String n[] = f.getName().split("\\.");
			if( n[n.length - 1].equalsIgnoreCase("jar")) {
				v.add(f);
			}
		} else {
			if( f.isDirectory()) {
				File f2[] = f.listFiles();
				for(int i = 0; i < f2.length; i++ ) {
					v.addAll(getJars(f2[i]));
				}
			}
		}
		return v;
	}

	private ProcessBuilder createNativeProcess(final String[] arguments, String workingDirectory) {
		List < String > command = new ArrayList <String>();
		command.addAll(Arrays.asList(arguments));

		ProcessBuilder processBuilder = new ProcessBuilder(command);
		if( workingDirectory != null ) {
			processBuilder.directory(new File(workingDirectory));
		}
		return processBuilder;
	}

	private ProcessBuilder createProcess(final String optionsAsString, final String mainClass, final String[] arguments) {
		String jvm = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
		String classpath = getTomcatClasspath();
		String path = "";
		String workingDirectory = "";
		boolean useTomcat = false;
		if( classpath != "") {
			useTomcat = true;
			path = System.getProperty("java.home") + File.separator + "bin" + File.pathSeparator +
					System.getProperty("java.home") + File.separator + "conf";
			workingDirectory = System.getProperty("catalina.base");
		} else {
			classpath = System.getProperty("java.class.path");
			workingDirectory = System.getProperty("user.dir");
		}

		String[] options = optionsAsString.split(" ");
		List < String > command = new ArrayList <String>();
		command.add(jvm);
		command.addAll(Arrays.asList(options));
		command.add(mainClass);
		command.addAll(Arrays.asList(arguments));

		ProcessBuilder processBuilder = new ProcessBuilder(command);
		processBuilder.directory(new File(workingDirectory));
		Map< String, String > environment = processBuilder.environment();
		if( useTomcat ) {
			environment.put("PATH", path);
		}
		environment.put("CLASSPATH", classpath);
		return processBuilder;
	}


	public void killProcess(final Process process) {
		process.destroy();
	}

	/**
	 * Kill all processes.
	 */
	public void shutdown() {
		log.debug("Killing " + processes.size() + " processes.");    
		for (Process process : processes) {
			killProcess(process);
		}
	}
}