package io.loader.jenkins;

import hudson.model.HealthReport;
import hudson.model.HealthReportingAction;
import hudson.model.AbstractBuild;

public class LoaderBuildAction implements HealthReportingAction {
	private final AbstractBuild<?, ?> build;
	
	public LoaderBuildAction(AbstractBuild<?, ?> build) {
		this.build = build;
	}
	
	public AbstractBuild<?, ?> getOwner() {
        return build;
    }
	
	public String getTestId() {
		return "669c8560ba8b94eb68fd8f5c19c354ce";
	}

	public String getIconFileName() {
		return "loader_icon.gif";
	}

	public String getDisplayName() {
		return "Loader.io Report";
	}

	public String getUrlName() {
		return "loaderio";
	}

	public HealthReport getBuildHealth() {
		return null;
	}

}
