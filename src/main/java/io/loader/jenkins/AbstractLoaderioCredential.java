package io.loader.jenkins;

import com.cloudbees.plugins.credentials.BaseCredentials;
import com.cloudbees.plugins.credentials.CredentialsScope;
import org.apache.commons.lang.StringUtils;

/**
 * @author
 */
public abstract  class AbstractLoaderioCredential extends BaseCredentials implements LoaderCredential {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7426700608553371938L;

	protected AbstractLoaderioCredential() {
        super(CredentialsScope.GLOBAL);
    }

    protected AbstractLoaderioCredential(CredentialsScope scope) {
        super(scope);
    }

    public String getId() {
        final String apiKey = getApiKey().getPlainText();
        return StringUtils.left(apiKey,4) + "..." + StringUtils.right(apiKey, 6);
    }
}
