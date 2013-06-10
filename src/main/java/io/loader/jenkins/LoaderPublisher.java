package io.loader.jenkins;

import com.cloudbees.plugins.credentials.CredentialsProvider;

import io.loader.jenkins.api.LoaderAPI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jenkins.model.Jenkins;

import org.apache.commons.lang.StringUtils;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Job;
import hudson.model.BuildListener;
import hudson.model.Item;
import hudson.security.ACL;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;
import hudson.util.Secret;
import hudson.util.ListBoxModel;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.Stapler;
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
        
     // Used by config.jelly to display the test list.
        public ListBoxModel doFillTestIdItems(@QueryParameter String apiKey) throws FormValidation {
            if (StringUtils.isBlank(apiKey)) {
                apiKey = getApiKey();
            }

            Secret apiKeyValue = null;
            Item item = Stapler.getCurrentRequest().findAncestorObject(Item.class);
            for (LoaderCredential c : CredentialsProvider
                    .lookupCredentials(LoaderCredential.class, item, ACL.SYSTEM)) {
                if (StringUtils.equals(apiKey, c.getId())) {
                	apiKeyValue = c.getApiKey();
                    break;
                }
            }
            ListBoxModel items = new ListBoxModel();
            if (apiKeyValue == null) {
                items.add("No API Key", "-1");
            } else {
	            LoaderAPI lda = new LoaderAPI(apiKeyValue.getPlainText());
	
	            try {
	                Map<String, String> testList = lda.getTestList();
	                if (testList == null){
	                    items.add("Invalid API key ", "-1");
	                } else if (testList.isEmpty()){
	                    items.add("No tests", "-1");
	                } else {
	                    for (Map.Entry<String, String> test : testList.entrySet()) {
	                        items.add(test.getValue(), test.getKey());
	                    }
	                }
	            } catch (Exception e) {
	                throw FormValidation.error(e.getMessage(), e);
	            }
            }
            return items;
        }
        
        public ListBoxModel doFillApiKeyItems() {
            ListBoxModel items = new ListBoxModel();
            Set<String> apiKeys = new HashSet<String>();

            Item item = Stapler.getCurrentRequest().findAncestorObject(Item.class);
            if (item instanceof Job) {
                List<LoaderCredential> global = CredentialsProvider
                        .lookupCredentials(LoaderCredential.class, Jenkins.getInstance(), ACL.SYSTEM);
                if (!global.isEmpty() && !StringUtils.isEmpty(getApiKey())) {
                    items.add("Default API Key", "");
                }
            }
            for (LoaderCredential c : CredentialsProvider
                    .lookupCredentials(LoaderCredential.class, item, ACL.SYSTEM)) {
                String id = c.getId();
                if (!apiKeys.contains(id)) {
                    items.add(StringUtils.defaultIfEmpty(c.getDescription(), id), id);
                    apiKeys.add(id);
                }
            }
            return items;
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
            List<LoaderCredential> credentials = CredentialsProvider
                    .lookupCredentials(LoaderCredential.class, Jenkins.getInstance(), ACL.SYSTEM);
            if (StringUtils.isBlank(apiKey) && !credentials.isEmpty()) {
                return credentials.get(0).getId();
            }
            if (credentials.size() == 1) {
                return credentials.get(0).getId();
            }
            for (LoaderCredential c: credentials) {
                if (StringUtils.equals(c.getId(), apiKey)) {
                    return apiKey;
                }
            }
            // API key is not valid any more
            return "";
        }
		
		public void setApiKey(String apiKey) {
			this.apiKey = apiKey;
	    }
		
	}

   

}

