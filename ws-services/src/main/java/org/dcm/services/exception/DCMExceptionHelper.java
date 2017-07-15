package org.dcm.services.exception;

import java.util.List;

public class DCMExceptionHelper {
	public static int AS_EXCEPTION_FORBIDDEN_CODE = 403;
	public static int AS_EXCEPTION_NOTFOUND_CODE = 404;
	public static int AS_EXCEPTION_ALREADYEXISTS_CODE = 405;
	public static int AS_EXCEPTION_NOTACCEPTED_CODE = 406;
	public static int AS_EXCEPTION_INVALIDARGUMENTS_CODE = 407;
	public static int AS_EXCEPTION_AUTHTOKENEXPIRED_CODE = 408;
	public static int AS_EXCEPTION_AUTHTOKENMISSING_CODE = 409;
	public static int AS_EXCEPTION_CONCURRENT_MODIFICATION_CODE = 410;
	public static int AS_EXCEPTION_MAILALREADYEXISTS_CODE = 411;
	public static int AS_EXCEPTION_NOT_UNIQUE_CODE = 412;
	public static int AS_EXCEPTION_PUSH_MESSAGE = 413;
	public static int AS_EXCEPTION_INTERNALERROR_CODE = 500;
	
	public static DCMException forbiddenException() {
		return new DCMException("Forbidden", AS_EXCEPTION_FORBIDDEN_CODE);
	}
	public static DCMException notFoundException() {
		return new DCMException("Not found", AS_EXCEPTION_NOTFOUND_CODE);
	}
	public static DCMException notFoundException(String data) {
		return new DCMException("Entity Id " + data + " Not found", AS_EXCEPTION_NOTFOUND_CODE);
	}
	public static DCMException alreadyExistsException() {
		return new DCMException("Entity already exists", AS_EXCEPTION_ALREADYEXISTS_CODE);
	}
	public static DCMException notAcceptedException() {
		return new DCMException("Not accepted", AS_EXCEPTION_NOTACCEPTED_CODE);
	}
	public static DCMException invalidArgumentsException(List<String> invalidFields) {
		return DCMExceptionHelper.invalidArgumentsException(invalidFields.toString());
	}
	public static DCMException invalidArgumentsException(String invalidField) {
		StringBuffer sb = new StringBuffer("Invalid Arguments on fields:");
		sb.append(invalidField.toString());	
		return new DCMException(sb.toString(), AS_EXCEPTION_INVALIDARGUMENTS_CODE);
	}
	public static DCMException invalidArgumentsException() {
		return new DCMException("Invalid Arguments", AS_EXCEPTION_INVALIDARGUMENTS_CODE);
	}
	public static DCMException tokenExpiredException() {
		return new DCMException("Auth token expired", AS_EXCEPTION_AUTHTOKENEXPIRED_CODE);
	}
	public static DCMException authTokenMissingException() {
		return new DCMException("Auth token missing", AS_EXCEPTION_AUTHTOKENMISSING_CODE);
	}
	public static DCMException defaultException(String message, Throwable cause) {
		return new DCMException(message, AS_EXCEPTION_INTERNALERROR_CODE, cause);
	}
	public static DCMException concurrentModificationException() {
		return new DCMException("Concurrent error", AS_EXCEPTION_CONCURRENT_MODIFICATION_CODE);
	}
	public static DCMException mailAlreadyExistsException() {
		return new DCMException("Mail already exists", AS_EXCEPTION_MAILALREADYEXISTS_CODE);
	}
	public static DCMException notUniqueException() {
		return new DCMException("Not Unique value found", AS_EXCEPTION_NOT_UNIQUE_CODE);
	}
	public static DCMException pushMessageException(String userId, String title) {
		return new DCMException("Push Message not delivered to user " + userId  
			+ " and title " + title, AS_EXCEPTION_PUSH_MESSAGE);
	}
}
