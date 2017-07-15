package org.dcm.services.auth;

import java.util.List;
import java.util.logging.Logger;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.dcm.services.model.SystemConfiguration;
import org.dcm.services.model.User;
import org.dcm.services.tools.CollectionFactory;
import org.springframework.beans.factory.annotation.Autowired;


public class AuthHelper {
	private static final char HEXES[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	private static final int DEFAULT_PASSWORD_LEN = 6;
	private static final String ADMIN_IDENTIFIER = "admin";

	private static final Logger log = Logger.getLogger(AuthHelper.class.getName());
	
	@Autowired
	private SystemConfiguration systemConfiguration;
	
	private final List<String> validReferersList = CollectionFactory.createList();

	public SystemConfiguration getSystemConfiguration() {
		return this.systemConfiguration;
	}

	public void setSystemConfiguration(SystemConfiguration systemConfiguration) {
		this.systemConfiguration = systemConfiguration;
	}
	
	/**
	 * Encodes (Hashes) a String using Hmac SHA
	 * 
	 * @param input
	 *            The string to encode
	 * @return the Encoded (Hashed) String
	 */
	private String encodeString(String input) {
		 String output = "";
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			byte keyBytes[] = "MSIR33L264H1VVXSINHR".getBytes(); 
			SecretKey key = new SecretKeySpec(keyBytes, "HmacSHA256");
			mac.init(key);
			mac.update(input.getBytes());
			byte macBytes[] = mac.doFinal();

			StringBuilder hexString = new StringBuilder(macBytes.length * 2);
			for (byte b : macBytes) {
				hexString.append(HEXES[((b & 0xF0) >> 4)]).append(HEXES[(b & 0x0F)]);
			}
			output = hexString.toString();
		} catch (Exception e) {
		}
		return output;
	}
	
	/**
	 * Creates a new token for a user
	 * 
	 * @param user
	 *            The user on which the token will be created
	 * @return The string representation of the auth token
	 */
	public String generateTokenForUser(User user) {
//		Calendar calendar = Calendar.getInstance();	
//		Date now = calendar.getTime();
//		String input = user.getIdentifier() + "-" + now.getTime(); 
//		String token = this.encodeString(input);
//		
//		int days = user.getSecuritySettings().getRole().equals(UserSecurity.Role.APPLICATION) 
//				? this.getSystemConfiguration().getAppTokenValidityInDays() 
//				: this.getSystemConfiguration().getAuthTokenValidityInDays();
//		if (days > 0) {
//			calendar.add(Calendar.DAY_OF_MONTH, days);
//			Date validity = calendar.getTime();
//			user.getSecuritySettings().setAuthTokenValidity(validity);
//		}
//		user.getSecuritySettings().setAuthToken(token);
//		user.setLastLogin(new Date());
//		user.setActivityStatus(User.STATUS_ACTIVE);
//		
//		return token;
		return "";
	}

	/**
	 * Encodes a password using encodeString <br>
	 * 
	 * @see AuthHelper#encodeString(String)
	 * @param password
	 *            The password to encode
	 * @return A hashed form of the input password
	 */
	public String encodePassword(String password){
		return this.encodeString(password);
	}

	public List<String> getValidReferersList() {
		return validReferersList;
	}

	public void setValidReferersList(List<String> validReferersList) {
		this.validReferersList.clear();
		this.validReferersList.addAll(validReferersList);
	}
}
