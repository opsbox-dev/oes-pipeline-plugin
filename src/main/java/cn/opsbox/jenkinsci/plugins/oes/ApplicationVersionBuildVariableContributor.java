package cn.opsbox.jenkinsci.plugins.oes;

import cn.opsbox.jenkinsci.plugins.oes.ApplicationVersionContributor.ApplicationVersionAction;
import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.BuildVariableContributor;

import java.util.Map;

@Extension
public class ApplicationVersionBuildVariableContributor extends BuildVariableContributor {

    @Override
    public void buildVariablesFor(AbstractBuild build, Map<String, String> variablesOut) {
        ApplicationVersionAction applicationVersionAction = build.getAction(ApplicationVersionAction.class);

        if (applicationVersionAction != null) {
            variablesOut.put(ApplicationVersionContributor.VERSION_PARAMETER, applicationVersionAction.getVersion());
        }
    }
}
