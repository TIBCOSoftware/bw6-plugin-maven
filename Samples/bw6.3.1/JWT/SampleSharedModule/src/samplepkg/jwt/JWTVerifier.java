package samplepkg.jwt;

import io.jsonwebtoken.Jwts;

public class JWTVerifier {
	public static String resolveAuthenticationToken(String jwtSecret,
			String authHeader) throws IllegalAccessException {

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			// Extract the user part from the header
			String jwt = authHeader.substring("Bearer ".length());

			String[] jwtParts = jwt.split("\\.");
			String payload = jwtParts[1];

			try {
				// Throws an JwtException in case of error (e.g. expired)
				Jwts.parser().setSigningKey(jwtSecret.getBytes("UTF-8"))
						.parseClaimsJws(jwt).getBody();

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
