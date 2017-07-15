package org.dcm.services.cli;

import java.util.Map;

import org.dcm.services.exception.DCMExceptionHelper;
import org.dcm.services.model.FieldDescriptor;
import org.dcm.services.model.RecordDescriptor;
import org.dcm.services.model.WSDescriptor;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

import com.google.gson.Gson;
import com.inodes.util.CollectionFactory;

import joptsimple.OptionParser;

public class TestServiceOutput extends AbstractCLI {

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

			Map<String, String> headers = CollectionFactory.createMap();
			headers.put("ContentType", "application/json");
			
			JSONObject sendTemplate = new JSONObject();
			sendTemplate.put("identifier", "{{INUSER}}");
			sendTemplate.put("password", "{{INPASS}}");
			
			RecordDescriptor ird = new RecordDescriptor();
			ird.setName("WSI0100");
			ird.setDescription("Descripcion para WS de Pruebas");
			ird.getFields().add(new FieldDescriptor("INUSER", "Nombre de Usuario", "identifier", "CHAR", 20, 0));
			ird.getFields().add(new FieldDescriptor("INPASS", "Contraseña", "password", "CHAR", 20, 0));
			
			RecordDescriptor ord = new RecordDescriptor();
			ord.setName("WSO0100");
			ord.setDescription("Descripcion para WS de Pruebas");
			ord.getFields().add(new FieldDescriptor("OUFNAM", "Nombre", "firstname", "CHAR", 20, 0));
			ord.getFields().add(new FieldDescriptor("OULNAM", "Apellido", "lastname", "CHAR", 20, 0));
			ord.getFields().add(new FieldDescriptor("OUTOKN", "Token", "token", "CHAR", 64, 0));
			
			WSDescriptor wsd = new WSDescriptor();
			wsd.setName("TESTO");
			wsd.setDescription("Web Service Output de pruebas");
			wsd.setType("*OUT");
			wsd.setUrl("http://api.getin.mx/bdb/auth");
			wsd.setMethod("POST");
			wsd.setHeaders(headers);
			wsd.setSendTemplate(sendTemplate.toString());
			wsd.setInputRecordDescriptor(ird);
			wsd.setOutputRecordDescriptor(ord);
			
			Gson gson = new Gson();
			System.out.println(gson.toJson(wsd));
			
		} catch( Exception e ) {
			throw DCMExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);

	}

}
