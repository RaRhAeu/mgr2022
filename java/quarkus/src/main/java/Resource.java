import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.password4j.Password;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Path("")
public class Resource {

    @Inject
    PgPool client;

    @Inject
    ObjectMapper objectMapper;

    @Path("/s1")
    @GET()
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> firstScenario() {
        return Uni.createFrom().item(Response.status(200).entity(new StatusDTO("ok")).build());
    }

    @Path("/s2")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> secondScenario() {
        return this.simulateAction().map(it -> Response.status(200).entity(it).build());
    }

    @Path("/s3")
    @POST()
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> thirdScenario(PasswordDTO dto) {
        return Uni.createFrom().item(Response.status(200).entity(createHashedPassword(dto)).build());
    }

    private PasswordDTO createHashedPassword(PasswordDTO dto) {
        return new PasswordDTO(Password.hash(dto.password()).withBcrypt().getResult());
    }

    @Path("/s4")
    @GET()
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> fourthScenario() {
        Map<String, Object> objectMap = new ConcurrentHashMap<>();
        return Uni.combine().all().unis(
                Uni.createFrom().item(new StatusDTO("ok")),
                this.simulateAction(),
                Uni.createFrom().item(this.createHashedPassword(new PasswordDTO("some-user-password")))
        ).asTuple().map(it ->
                {
                    objectMap.put("s1", it.getItem1());
                    objectMap.put("s2", it.getItem2());
                    objectMap.put("s3", it.getItem3());
                    return objectMap;
                }
                ).map(it -> {
                    try {
                        return Response.ok().entity(this.objectMapper.writeValueAsString(it)).build();
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
        );
    }

    private Uni<StatusDTO> simulateAction() {
        return this.client.query("SELECT pg_sleep(0.5)").execute()
                .map(it -> new StatusDTO("ok"));
    }

}
