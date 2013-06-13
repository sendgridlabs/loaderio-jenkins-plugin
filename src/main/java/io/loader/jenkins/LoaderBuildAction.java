package io.loader.jenkins;

import hudson.model.HealthReport;
import hudson.model.HealthReportingAction;
import hudson.model.AbstractBuild;

public class LoaderBuildAction implements HealthReportingAction {
	private final AbstractBuild<?, ?> build;
	
	private String testId;
	private String testResultId = null;
	
	public LoaderBuildAction(AbstractBuild<?, ?> build, String testId, String testResultId) {
		this.build = build;
		this.testId = testId;
		this.testResultId = testResultId;
	}
	
	public AbstractBuild<?, ?> getOwner() {
        return build;
    }
	
	public String getTestId() {
		return this.testId;
	}
	
	public String getTestResultId() {
		return this.testResultId;
	}

	public String getIconFileName() {
		return "/plugin/loaderio-jenkins-plugin/images/24x24/24.png";
	}

	public String getDisplayName() {
		return "loader.io Report";
	}

	public String getUrlName() {
		return "loaderio";
	}

	public HealthReport getBuildHealth() {
		return null;
	}

}
