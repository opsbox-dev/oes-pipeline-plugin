package cn.k8ops.jenkinsci.plugins.asl.config;

import hudson.Extension;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

@Extension(ordinal = 6)
public class GlobalConfig extends GlobalConfiguration {

    public GlobalConfig() {
        load();
    }

    public boolean configure(StaplerRequest req, JSONObject json) {
        req.bindJSON(this, json);
        save();
        return true;
    }

    public static GlobalConfig get() {
        return GlobalConfiguration.all().get(GlobalConfig.class);
    }
}
