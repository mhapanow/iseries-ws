package org.dcm.services.auth.spi;


import java.util.Set;
import java.util.logging.Logger;

import org.dcm.services.auth.AuthBzService;
import org.dcm.services.auth.AuthHelper;
import org.dcm.services.bz.RestBaseServerResource;
import org.dcm.services.exception.DCMException;
import org.dcm.services.exception.DCMExceptionHelper;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.springframework.beans.factory.annotation.Autowired;


public class AuthBzServiceJSONImpl extends RestBaseServerResource implements AuthBzService {
	private static final String[] DEFAULT_FIELDS = { "identifier", "firstname", "lastname", "avatarId", "tokenValidity" };
	private static Set<String> invalidIdentifierNames = null;
	private static final Logger log = Logger.getLogger(AuthBzServiceJSONImpl.class.getName());
	private static final String PASSWORD_FIELD = "password";

	@Autowired
	private AuthHelper authHelper;

	/**
	 * Corresponding to /app/auth POST method.<br>
	 * Issues a login and return the user token if the login was valid, or an
	 * error if not
	 */
	@Override
	public String login(JsonRepresentation entity) {
		JSONObject jsonOut;
		JSONObject jsonIn = null;
		long start = markStart();

		return "OK";
	}

	/**
	 * Logs a user out, disposing its token
	 */
	@Override
	public String logout() {
		JSONObject jsonOut = new JSONObject();
		try {
			this.invalidateToken();
			jsonOut = this.generateJSONOkResponse();

		} catch (DCMException e) {
			if (e.getErrorCode() == DCMExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE ||
					e.getErrorCode() == DCMExceptionHelper.AS_EXCEPTION_AUTHTOKENEXPIRED_CODE ||
					e.getErrorCode() == DCMExceptionHelper.AS_EXCEPTION_AUTHTOKENMISSING_CODE ) {
				// does not exists
				jsonOut = this.generateJSONOkResponse();
			} else {
				// other error
				jsonOut = this.getJSONRepresentationFromException(e);
			}
		}catch (Exception e) {
			jsonOut = this.getJSONRepresentationFromException(e);
		}


		return jsonOut.toString();
	}
}
