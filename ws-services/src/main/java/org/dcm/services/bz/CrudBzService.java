package org.dcm.services.bz;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;

/**
 * User Service Class
 */
public interface CrudBzService extends BzService {
    @Get
    public String selectRetrieveOrList();
    
    @Put("json")
    public String add(JsonRepresentation entity);
    
    @Post("json")
    public String change(JsonRepresentation entity);

    @Delete("json")
    public String delete(JsonRepresentation entity);

}
