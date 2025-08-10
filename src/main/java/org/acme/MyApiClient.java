package org.acme;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/posts")
@RegisterRestClient(baseUri = "https://jsonplaceholder.typicode.com")
public interface MyApiClient {

	@GET
	@Path("/{id}")
	String getPost(@PathParam("id") int id);
}
