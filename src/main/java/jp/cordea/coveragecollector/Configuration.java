package jp.cordea.coveragecollector;

import hudson.Extension;
import jenkins.model.GlobalConfiguration;
import lombok.Getter;
import lombok.Setter;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Created by Yoshihiro Tanaka on 2016/09/30.
 */
@Extension
public class Configuration extends GlobalConfiguration {

    @Getter
    @Setter
    private String pluginFileDir;

    public Configuration() {
        load();
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        req.bindJSON(this, json);
        save();

        return super.configure(req, json);
    }

    public static Configuration get() {
        return GlobalConfiguration.all().get(Configuration.class);
    }
}
