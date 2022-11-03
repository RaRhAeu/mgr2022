import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.agroal.api.AgroalDataSource;
import org.graalvm.collections.Pair;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Path("")
public class Resource {

    @Inject
    AgroalDataSource agroalDataSource;

    @Inject
    ObjectMapper objectMapper;

    @Path("/s1")
    @GET()
    @Produces(MediaType.APPLICATION_JSON)
    public Response firstScenario() {
        return Response.status(200).entity("OK").build();
    }

    @Path("/s2")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response secondScenario() throws SQLException {
        return Response.status(200).entity(this.simulateAction()).build();
    }

    @Path("/s3")
    @POST()
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response thirdScenario(PasswordDTO dto) {
        return Response.status(200).entity(createHashedPassword(dto)).build();
    }

    private PasswordDTO createHashedPassword(PasswordDTO dto) {
        return new PasswordDTO(BCrypt.hashpw(dto.password(), "$2a$12$R9h/cIPz0gi.URNNX3kh2O"));
    }

    @Path("/s4")
    @POST()
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response fourthScenario(PasswordDTO dto) {
        //TODO not ready yet
        return Response.status(200).build();
    }

    @Path("/s5")
    @GET()
    @Produces(MediaType.APPLICATION_JSON)
    public Response fifthScenario() throws InterruptedException, JsonProcessingException {
        Map<String, Object> objectMap = new HashMap<>();
        ExecutorService service = Executors.newCachedThreadPool();
        List<Callable<Pair<String, Object>>> tasks = new ArrayList<>();
        tasks.add(() -> Pair.create("s1", "ok"));
        tasks.add(() -> Pair.create("s2", this.simulateAction()));
        tasks.add(() -> Pair.create("s3", this.createHashedPassword(new PasswordDTO("some-user-password"))));
        List<Future<Pair<String, Object>>> futures = service.invokeAll(tasks);
        futures.forEach(it -> {
            try {
                objectMap.put(it.get().getLeft(), it.get().getRight());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        return Response.status(200).entity(objectMapper.writeValueAsString(objectMap)).build();
    }

    private Object simulateAction() throws SQLException {
        Object data = null;
        try (ResultSet rs = this.agroalDataSource.getConnection().prepareStatement("SELECT pg_sleep(0.5)").executeQuery()) {
            while (rs.next()) {
                data = rs.getString(1);
            }
        }
        return data;
    }

}
