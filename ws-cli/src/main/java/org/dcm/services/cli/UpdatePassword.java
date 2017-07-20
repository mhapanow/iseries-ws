package org.dcm.services.cli;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Scanner;

import org.dcm.services.exception.DCMExceptionHelper;
import org.dcm.services.impl.ConnectionHelperImpl;
import org.dcm.services.model.SystemConfiguration;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.context.ApplicationContext;

import com.inodes.util.FileLoader;

import joptsimple.OptionParser;

public class UpdatePassword extends AbstractCLI {

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}

	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		return parser;
	}


	public static void main(String args[]) throws Exception {

		try {
			ConnectionHelperImpl conn = (ConnectionHelperImpl)getApplicationContext().getBean("conn.helper");
			SystemConfiguration systemConfiguration = (SystemConfiguration)getApplicationContext().getBean("system.configuration");

			StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
			encryptor.setPassword("ws400");

			Scanner in = new Scanner(System.in);  

			System.out.println(
					"Ingrese la contraseña de iSeries para el systema " + systemConfiguration.getiSeriesServer()
					+ " con el usuario " + systemConfiguration.getiSeriesUser());
			System.out.print("Contraseña: ");
			String password = in.nextLine();
			System.out.print("Confirmación: ");
			String password2 = in.nextLine();
			in.close();

			if(!password.equals(password2)) {
				System.out.println("La contraseña no coincide con la confirmación");
				System.exit(-1);
			}

			String encrypted = encryptor.encrypt(password);
			String credentialsfile = FileLoader.getResource("credentials", FileLoader.PRECEDENCE_SYSTEMPATH).getFile();
			File credentials = new File(credentialsfile);
			FileOutputStream fos = new FileOutputStream(credentials);
			fos.write(encrypted.getBytes());
			fos.flush();
			fos.close();

			System.out.println("Archivo guardado con éxito... comprobando contraseña...");

			try {
				conn.connect();
				System.out.println(
						"Contraseña comprobada con éxito para el sistema " + systemConfiguration.getiSeriesServer()
								+ " y el usuario " + systemConfiguration.getiSeriesUser() + "!");
				conn.disconnect();
			} catch( Exception e ) {
				System.out.println("No se pudo establecer conexión con el sistema iSeries "
						+ systemConfiguration.getiSeriesServer());
				e.printStackTrace();
			}

		} catch( Exception e ) {
			throw DCMExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);

	}

}
