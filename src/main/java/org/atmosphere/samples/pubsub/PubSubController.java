/*
 * Copyright 2014 Jeanfrancois Arcand
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.atmosphere.samples.pubsub;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.atmosphere.cpr.ApplicationConfig;
import org.atmosphere.cpr.AtmosphereConfig;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEventListenerAdapter;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.cpr.ClusterBroadcastFilter;
import org.atmosphere.cpr.HeaderConfig;
import org.atmosphere.plugin.jgroups.JGroupsFilter;
import org.atmosphere.util.XSSHtmlFilter;
import org.atmosphere.websocket.WebSocketEventListenerAdapter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class PubSubController {

	private static final int FORE_EVER = -1;
	private static final String SLASH = "/";

	public PubSubController() {

	}

	/**
	 * This method takes a request to subscribe to the topic
	 * 
	 * @param request
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/{topic}/**", method = RequestMethod.GET)
	public ModelAndView subscribe(@PathVariable("topic") String topic, HttpServletRequest request, AtmosphereResource resource) {

		this.processGet(topic, request, resource);

		// A NoOpView is returned to tell Spring Dispatcher framework not to render anything
		// since it is all Atmosphere-related code
		ModelAndView mv = new ModelAndView(NoOpView.DEFAULT);
		return mv;
	}

	/**
	 * Takes a request to post data and broadcasts it to everyone else.
	 * 
	 * @param request
	 * @return String
	 */
	@RequestMapping(value = "/{topic}/**", method = RequestMethod.POST)
	public ModelAndView broadcastMessage(@PathVariable("topic") String topic, HttpServletRequest request, AtmosphereResource resource)
			throws Exception {

		this.processPost(topic, request, resource);

		// A NoOpView is returned to tell Spring Dispatcher framework not to render anything
		// since it is all Atmosphere-related code
		ModelAndView mv = new ModelAndView(NoOpView.DEFAULT);
		return mv;
	}

	// See AtmosphereHandlerPubSub example - same code as GET
	private void processGet(
			String topic,
			HttpServletRequest req,  AtmosphereResource event) {

		event.addEventListener(new WebSocketEventListenerAdapter());
		event.addEventListener(new AtmosphereResourceEventListenerAdapter());

		String restOfTheUrl = (String) req.getAttribute(
				HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);

		Broadcaster b = lookupBroadcaster(topic, restOfTheUrl);

		String header = req.getHeader(HeaderConfig.X_ATMOSPHERE_TRANSPORT);
		if (HeaderConfig.LONG_POLLING_TRANSPORT.equalsIgnoreCase(header)) {
			req.setAttribute(ApplicationConfig.RESUME_ON_BROADCAST, Boolean.TRUE);
		} 

		event.suspend(FORE_EVER);
		b.addAtmosphereResource(event);

		ClusterBroadcastFilter filter = newFilter(b, event.getAtmosphereConfig());
		b.getBroadcasterConfig().addFilter(filter);
		b.getBroadcasterConfig().addFilter(new XSSHtmlFilter());
	}

	private ClusterBroadcastFilter newFilter(Broadcaster bc, AtmosphereConfig atmosphereConfig) {
		ClusterBroadcastFilter filter = new JGroupsFilter(bc);
		return filter;
	}

	private void processPost(String topic, HttpServletRequest req, AtmosphereResource resource) throws IOException {

		String restOfTheUrl = (String) req.getAttribute(
				HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);

		Broadcaster b = lookupBroadcaster(topic, restOfTheUrl);

		String message = req.getReader().readLine();
		resource.resume();
		if (message != null && message.indexOf("message") != FORE_EVER) {
			b.broadcast(message.substring("message=".length()));
		}
	}

	/**
	 * Retrieve the {@link Broadcaster} based on the request's path info.
	 * 
	 * @param pathInfo
	 * @return the {@link Broadcaster} based on the request's path info.
	 */
	Broadcaster lookupBroadcaster(String topic, String restOfTheUrl) {
		String pathInfo = topic + SLASH + restOfTheUrl;
		String[] decodedPath = pathInfo.split(SLASH);
		return BroadcasterFactory.getDefault().lookup(decodedPath[decodedPath.length - 1], true);
	}
}
