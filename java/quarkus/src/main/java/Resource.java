import com.fasterxml.jackson.databind.ObjectMapper;
import com.password4j.Password;
import io.agroal.api.AgroalDataSource;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Path("")
public class Resource {

    @Inject
    AgroalDataSource agroalDataSource;

    @Inject
    ObjectMapper objectMapper;

    @Path("/s1")
    @GET()
    @Produces(MediaType.APPLICATION_JSON)
    public String firstScenario() {
        return "hello world";
    }

    @Path("/s2")
    @GET()
    @Produces(MediaType.APPLICATION_JSON)
    public Response secondScenario() {
        return Response.status(200).entity(new StatusDTO("ok")).build();
    }

    @Path("/s3")
    @POST()
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response thirdScenario(PasswordDTO dto) {
        return Response.status(200).entity(createHashedPassword(dto)).build();
    }

    @Path("/s4")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response fourthScenario() throws SQLException {
        return Response.status(200).entity(this.simulateAction()).build();
    }

    private PasswordDTO createHashedPassword(PasswordDTO dto) {
        return new PasswordDTO(Password.hash(dto.password()).withBcrypt().getResult());
    }

    private Object simulateAction() throws SQLException {
        try(Connection connection = this.agroalDataSource.getConnection()) {
            try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT pg_sleep(0.5)")) {
                try(ResultSet rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                    }
                }
            }
        }
        return new StatusDTO("ok");
    }

}
