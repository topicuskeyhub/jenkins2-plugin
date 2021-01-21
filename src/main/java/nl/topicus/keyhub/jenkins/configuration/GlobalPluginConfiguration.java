package nl.topicus.keyhub.jenkins.configuration;

import org.kohsuke.stapler.DataBoundSetter;

import hudson.Extension;
import jenkins.model.GlobalConfiguration;

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
    public void setKeyhubURI(String keyhubURI) {
        this.keyhubURI = keyhubURI;
    }
}
