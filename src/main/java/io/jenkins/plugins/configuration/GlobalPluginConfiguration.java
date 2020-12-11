package io.jenkins.plugins.configuration;

import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;

import hudson.Extension;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;

@Extension
public class GlobalPluginConfiguration extends GlobalConfiguration {

    private String keyhubURI;

    public GlobalPluginConfiguration() {
        load();
    }

    public GlobalPluginConfiguration(String keyhubURI) {
        this.keyhubURI = keyhubURI;
    }

    public static GlobalPluginConfiguration getInstance() {
        return all().get(GlobalPluginConfiguration.class);
    }

    public String getKeyhubURI() {
        return this.keyhubURI;
    }

    @DataBoundSetter
    @SuppressWarnings("unused")
    public void setKeyhubURI(String keyhubURI) {
        this.keyhubURI = keyhubURI;
        save();
    }

    @Override
    public synchronized boolean configure(StaplerRequest req, JSONObject json) {
        this.keyhubURI = null;
        req.bindJSON(this, json);
        save();
        return true;
    }
}
