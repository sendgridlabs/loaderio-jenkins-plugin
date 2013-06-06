package io.loader.jenkins;

import com.cloudbees.plugins.credentials.Credentials;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.util.Secret;

/**
 * @author Loader.io
 */
public interface LoaderCredential extends Credentials {

    public String getDescription();

    public String getId();
    
    @NonNull
    public Secret getApiKey();

}