package util.credentials;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;

public class CredentialManager {
	
	private static AWSCredentials credentials = new ProfileCredentialsProvider().getCredentials();
	
	public static AWSCredentials getCredentials() {
		return credentials;
	}

}
