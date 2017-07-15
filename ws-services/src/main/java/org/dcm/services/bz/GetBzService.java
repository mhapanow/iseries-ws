package org.dcm.services.bz;

import org.restlet.resource.Get;

/**
 * User Service Class
 */
public interface GetBzService extends BzService {
    @Get
    public String retrieve();
}
