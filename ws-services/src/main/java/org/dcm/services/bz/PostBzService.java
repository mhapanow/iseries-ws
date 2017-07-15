package org.dcm.services.bz;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Post;

/**
 * User Service Class
 */
public interface PostBzService extends BzService {

    @Post("json")
    public String change(JsonRepresentation entity);
    
}
