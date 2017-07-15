package org.dcm.services.auth;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Post;

public interface AuthBzService {
	
	@Post("json")
    public String login(JsonRepresentation entity);
    
    @Delete
    public String logout();
    
}
