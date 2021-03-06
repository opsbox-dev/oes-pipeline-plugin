package cn.opsbox.jenkinsci.plugins.oes.config;

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.cloudbees.plugins.credentials.domains.URIRequirementBuilder;
import hudson.Extension;
import hudson.security.ACL;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import lombok.Getter;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.interceptor.RequirePOST;

import java.util.Collections;
import java.util.List;

public class OesGitlabStepRegistryProvider extends StepRegistryProvider{

    @Getter
    private String gitlabUrl;

    @Getter
    private String credentialsId;

    @Getter
    private String stepsGroup;

    @DataBoundConstructor
    public OesGitlabStepRegistryProvider(String gitlabUrl, String credentialsId) {
        this.gitlabUrl = gitlabUrl;
        this.credentialsId = credentialsId;
    }

    @DataBoundSetter
    public void setStepsGroup(String stepsGroup) {
        this.stepsGroup = stepsGroup;
    }

    public StandardCredentials getCredentials() {
        return CredentialsMatchers.firstOrNull(
                CredentialsProvider.lookupCredentials(StandardCredentials.class,
                        Jenkins.getInstanceOrNull(), ACL.SYSTEM, Collections.emptyList()),
                CredentialsMatchers.withId(credentialsId)
        );
    }

    @Extension(ordinal = 2)
    public static class DescriptorImpl extends StepRegistryProviderDescriptor {

        @Override
        public String getDisplayName() {
            return "Gitlab Server";
        }

        @RequirePOST
        public ListBoxModel doFillCredentialsIdItems(@QueryParameter String endpoint) {
            Jenkins.getInstanceOrNull().checkPermission(Jenkins.ADMINISTER);

            List<DomainRequirement> domainRequirements;
            if (endpoint == null) {
                domainRequirements = Collections.emptyList();
            } else {
                domainRequirements = URIRequirementBuilder.fromUri(endpoint.trim()).build();
            }

            return new StandardListBoxModel()
                    .includeEmptyValue()
                    .withMatching(
                            CredentialsMatchers.anyOf(
                                    CredentialsMatchers.instanceOf(StandardUsernamePasswordCredentials.class)
                            ),
                            CredentialsProvider.lookupCredentials(StandardCredentials.class,
                                    Jenkins.getInstanceOrNull(), ACL.SYSTEM, domainRequirements)
                    );
        }
    }

}
