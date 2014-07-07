package org.atmosphere.plugin.jgroups;

import org.jgroups.JChannel;

/**
 * Creates a default, singleton JGroupsChannel object
 *
 */
public class DefaultJGroupsChannelFactory {
    private static JGroupsChannel jc;

    //public static String DEFAULT_JGROUPS_XML = "org/atmosphere/plugin/jgroups/JGroupsFilter.xml";
    public static String DEFAULT_JGROUPS_XML = "udp.xml";
    public static String DEFAULT_CHANNEL_NAME = "JGroupsChannel";

    private DefaultJGroupsChannelFactory() {
    }

    public static synchronized JGroupsChannel getDefaultJGroupsChannel() {
        return getDefaultJGroupsChannel(DEFAULT_JGROUPS_XML, DEFAULT_CHANNEL_NAME);
    }

    public static synchronized JGroupsChannel getDefaultJGroupsChannel(String jGroupsFilterLocation) {
        return getDefaultJGroupsChannel(jGroupsFilterLocation, DEFAULT_CHANNEL_NAME);
    }

    public static synchronized JGroupsChannel getDefaultJGroupsChannel(String jGroupsFilterLocation, String channelName) {

        if (jc == null) {
            try {
                JChannel channel = new JChannel(jGroupsFilterLocation);
                jc = new JGroupsChannel(channel, channelName);

                jc.init();
            } catch (Exception e) {
                throw new RuntimeException("Failed to create JGroupsChannel", e);
            }
        }

        return jc;
    }
}
