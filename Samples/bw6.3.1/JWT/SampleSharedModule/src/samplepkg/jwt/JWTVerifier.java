package samplepkg.jwt;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;

public class JWTVerifier {
	public static String resolveAuthenticationToken(String jwtSecret,
			String authHeader) throws IllegalAccessException {

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			// Extract the user part from the header
			String jwt = authHeader.substring("Bearer ".length());

			String[] jwtParts = jwt.split("\\.");
			String payload = jwtParts[1];

			try {	
				// Parse the JWS and verify its HMAC
				SignedJWT signedJWT = SignedJWT.parse(jwt);

				JWSVerifier verifier = new MACVerifier(jwtSecret);

				signedJWT.verify(verifier);
				
				// Token is valid..
				return pad(payload);

			} catch (Exception e) {
				throw new IllegalAccessException("Invalid JWT Token - " + e.getMessage());
			}
		}

		return null;
	}
	
	private static String pad(String base64EncodedString){
		if ((base64EncodedString.length() % 4) == 0){
			return base64EncodedString;
		}
		else {
			return pad(base64EncodedString+"=");
		}
	}
}
