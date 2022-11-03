import io.agroal.api.AgroalDataSource;

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

@Path("")
public class Resource {

    @Inject
    AgroalDataSource agroalDataSource;

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
        return Response.status(200).entity(new PasswordDTO(BCrypt.hashpw(dto.password(), "$2a$12$R9h/cIPz0gi.URNNX3kh2O"))).build();
    }

    @Path("/s4")
    @POST()
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response fourthScenario(PasswordDTO dto) {
        return Response.status(200).entity("").build();
    }


    private Object simulateAction() throws SQLException {
        Object data = null;
        try (ResultSet rs = this.agroalDataSource.getConnection().prepareStatement("SELECT pg_sleep(0.5):").executeQuery()) {
            while (rs.next()) {
                data = rs.getString(1);
            }
        }
        return data;
    }

}
