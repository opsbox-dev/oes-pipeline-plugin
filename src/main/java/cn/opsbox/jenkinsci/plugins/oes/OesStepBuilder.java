package cn.opsbox.jenkinsci.plugins.oes;

import cn.opsbox.jenkinsci.plugins.oes.pipeline.Step;
import cn.opsbox.jenkinsci.plugins.oes.registry.RegistryUtil;
import cn.opsbox.jenkinsci.plugins.oes.util.Constants;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.*;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.ListBoxModel;
import jenkins.tasks.SimpleBuildStep;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.IOException;
import java.util.*;

public class OesStepBuilder extends Builder implements SimpleBuildStep {

    public static final String STEPS_DIR = "steps";

    @Getter
    private String stepId;

    @Getter
    private List<OesStepProperty> stepProperties = new ArrayList<>();

    @DataBoundConstructor
    public OesStepBuilder(String stepId) {
        this.stepId = stepId;
    }

    @DataBoundSetter
    public void setStepProperties(List<OesStepProperty> stepProperties) {
        this.stepProperties = stepProperties;
    }
    @Override
    public void perform(@NonNull Run<?, ?> run, @NonNull FilePath ws, @NonNull EnvVars env,
                        @NonNull Launcher launcher, @NonNull TaskListener listener) throws IOException, InterruptedException {

        OesRunner runner = new OesRunner(run, ws, launcher, listener);
        runner.setEnvvars(env);
        boolean r = runner.runStep(new Step(stepId, convertStepProperties(env)));
        if (r) {
            run.setResult(Result.SUCCESS);
        } else {
            run.setResult(Result.FAILURE);
            throw new AbortException("--//step build failure...");
        }
    }

    private Map<String, String> convertStepProperties(EnvVars envVars) {
        Map<String, String> retRaw = new HashMap<>();
        for (OesStepProperty var : this.stepProperties) {
            retRaw.put(var.getKey(), envVars.expand(var.getValue()));
        }

        retRaw.put("step.id", stepId);
        return retRaw;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    @Extension(ordinal = 1)
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @SneakyThrows
        public ListBoxModel doFillStepIdItems() {

            ListBoxModel items = new ListBoxModel();
            try {
                List<String> stepIds = RegistryUtil.getStepRegistry().getStepList();
                Collections.sort(stepIds);
                for (String stepId : stepIds) {
                    if (StringUtils.equalsIgnoreCase(stepId, Constants.STEP_ASL)) {
                        continue;
                    }
                    items.add(stepId, stepId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return items;
        }

        @Override
        public String getDisplayName() {
            return "OES Step";
        }
    }
}