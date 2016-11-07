package edu.cornell.hackathon.workday.resources;

import java.util.List;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import edu.cornell.hackathon.workday.api.APIResponse;
import edu.cornell.hackathon.workday.distcache.DistCache;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class WorkdayResource {

	private final DistCache cache;

	@Inject
	public WorkdayResource(final DistCache cache) {
		this.cache = cache;
	}

	@GET
	@Path("/ping")
	public String pingPong() {
		return "pong";
	}

	@GET
	@Path("/{serviceName}")
	public APIResponse getReport(@PathParam("serviceName") final String name, @Context final UriInfo urlInfo) {

		final MultivaluedMap<String, String> queryParams = urlInfo.getQueryParameters();

		for (final Entry<String, List<String>> e : queryParams.entrySet()) {
			System.out.println(e.getKey() + " = " + e.getValue());
		}

		return null;
	}

}
