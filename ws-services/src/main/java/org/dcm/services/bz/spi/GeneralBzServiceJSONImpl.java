package org.dcm.services.bz.spi;

import java.util.Iterator;
import java.util.Map;

import org.dcm.services.bz.RestBaseServerResource;
import org.dcm.services.exception.DCMException;
import org.dcm.services.exception.DCMExceptionHelper;
import org.dcm.services.impl.WSBrokerImpl;
import org.dcm.services.model.WSDescriptor;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * This is a dummy service made as a place holder for certain old API URLs
 */
public class GeneralBzServiceJSONImpl extends RestBaseServerResource {

	@Autowired
	private WSBrokerImpl broker;
	
	@Get
	public String doGet() {
		return executeService("GET", null);
	}

	@Put("json")
	public String doPut(JsonRepresentation entity) {
		return executeService("PUT", entity);
	}

	@Post("json")
	public String doPost(JsonRepresentation entity) {
		return executeService("POST", entity);
	}

	@Delete
	public String doDelete() {
		return executeService("DELETE", null);
	}

	public String executeService(String method, JsonRepresentation entity)  {

		try {
			// Obtains the service
			String serviceName = obtainIdentifier(GENERAL_IDENTIFIER_KEY);
			WSDescriptor ws = broker.getWSUsingName(serviceName.trim().toUpperCase());

			// General Validations
			if( ws == null )
				throw DCMExceptionHelper.notFoundException(serviceName);
			
			if(!ws.getType().trim().equals("*IN"))
				throw DCMExceptionHelper.invalidArgumentsException(serviceName);
			
			if(!ws.getMethod().equalsIgnoreCase(method))
				throw DCMExceptionHelper.invalidArgumentsException(serviceName);
			
			// Parameters join
			
			JsonParser jsonParser = new JsonParser();
			
			
			
			JsonObject obj = entity != null ? jsonParser.parse(entity.getJsonArray().toString()).getAsJsonObject() : new JsonObject();
			Map<String,?> attr = getRequest().getAttributes();
			Iterator<String> i = attr.keySet().iterator();
			while( i.hasNext() ) {
				String key = i.next();
				Object o = attr.get(key);
				
				if(o instanceof Number){
					obj.addProperty(key, (Number) o);
				}
				
				if(o instanceof Boolean){
					obj.addProperty(key, (Boolean) o);
				}
				
				if(o instanceof String){
					obj.addProperty(key, (String) o);
				}
				
				
			}

			// Executing the service and returning
			JsonObject response = broker.executeServerWS(ws, obj);
			return response.toString();

		} catch( DCMException e ) {
			return getJSONRepresentationFromException(e).toString();
		} catch( Exception e ) {
			return getJSONRepresentationFromException(e).toString();
		}

	}
}
