package org.atmosphere.plugin.jgroups;

import java.io.Serializable;

public class BroadcastMessage implements Serializable {

	private static final long serialVersionUID = 1L;
	private final String topic;
	private final Object message;

	public BroadcastMessage(final String topic, final Object message) {
		this.topic = topic;
		this.message = message;
	}

	public String getTopic() {
		return this.topic;
	}

	public Object getMessage() {
		return this.message;
	}
}
