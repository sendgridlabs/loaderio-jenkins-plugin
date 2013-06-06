package io.loader.jenkins;

import com.cloudbees.plugins.credentials.CredentialsProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Item;
import hudson.security.ACL;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Sample {@link Builder}.
 *
 * <p>
 * When the user configures the project and enables this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked
 * and a new {@link LoaderPublisher} is created. The created
 * instance is persisted to the project configuration XML by using
 * XStream, so this allows you to use instance fields (like {@link #name})
 * to remember the configuration.
 *
 * <p>
 * When a build is performed, the {@link #perform(AbstractBuild, Launcher, BuildListener)}
 * method will be invoked. 
 *
 * @author Kohsuke Kawaguchi
 */
public class LoaderPublisher extends Notifier {
	
	private String apiKey;
	
	@DataBoundConstructor
    public LoaderPublisher(String apiKey) {
        this.apiKey = apiKey;
    }
	
	@Override
    public boolean perform(AbstractBuild build, Launcher launcher,
            BuildListener listener) throws InterruptedException, IOException {
		return true;
	}

	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.BUILD;
	}
	
	public String getApiKey() {
        return apiKey;
    }
	
	@Override
    public LoaderioPerformancePublisherDescriptor getDescriptor() {
        return DESCRIPTOR;
    }

    @Extension
    public static final LoaderioPerformancePublisherDescriptor DESCRIPTOR = new LoaderioPerformancePublisherDescriptor();
	
	public static final class DescriptorImpl
    	extends LoaderioPerformancePublisherDescriptor {
	}
	
	public static class LoaderioPerformancePublisherDescriptor extends BuildStepDescriptor<Publisher> {
		private String apiKey;

        public LoaderioPerformancePublisherDescriptor() {
            super(LoaderPublisher.class);
            load();
        }
        
        public List<LoaderCredential> getCredentials(Object scope) {
            List<LoaderCredential> result = new ArrayList<LoaderCredential>();
            Set<String> apiKeys = new HashSet<String>();

            Item item = scope instanceof Item ? (Item) scope : null;
            for (LoaderCredential c : CredentialsProvider
                    .lookupCredentials(LoaderCredential.class, item, ACL.SYSTEM)) {
                String id = c.getId();
                if (!apiKeys.contains(id)) {
                    result.add(c);
                    apiKeys.add(id);
                }
            }
            return result;
        }
		
		@Override
		public boolean isApplicable(Class<? extends AbstractProject> jobType) {
			return true;
		}

		@Override
		public String getDisplayName() {
			return "Loader.io";
		}
		
		@Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            apiKey = formData.optString("apiKey");
            save();
            return true;
        }
		
		public String getApiKey() {
			return apiKey;
		}
		
		public void setApiKey(String apiKey) {
			this.apiKey = apiKey;
	    }
		
	}

   

}

