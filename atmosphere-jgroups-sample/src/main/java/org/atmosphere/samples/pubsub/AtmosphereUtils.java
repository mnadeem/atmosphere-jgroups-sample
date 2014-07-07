package org.atmosphere.samples.pubsub;

import javax.servlet.http.HttpServletRequest;

import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.FrameworkConfig;
import org.springframework.util.Assert;

public final class AtmosphereUtils {

	private AtmosphereUtils() {
		
	}

	public static AtmosphereResource getAtmosphereResource(HttpServletRequest request) {
		AtmosphereResource resource = (AtmosphereResource) request.getAttribute(FrameworkConfig.ATMOSPHERE_RESOURCE);

		Assert.notNull(resource, "AtmosphereResource could not be located for the request. Check that AtmosphereServlet is configured correctly in web.xml");
		return resource;
	}
}
