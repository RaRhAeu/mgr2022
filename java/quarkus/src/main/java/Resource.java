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
import java.time.LocalDateTime;
import java.time.ZoneId;

@Path("")
public class Resource {

    @Inject
    AgroalDataSource agroalDataSource;

    @Path("/s1")
    @GET()
    public String firstScenario() {
        return "Hello, World!";
    }

    @Path("/s2")
    @GET()
    @Produces(MediaType.APPLICATION_JSON)
    public Response secondScenario() {
        return Response.status(200).entity(new StatusDTO("ok", LocalDateTime.now(ZoneId.of("UTC")))).build();
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
    public Response fourthScenario() throws SQLException {
        this.simulateAction();
        return Response.ok().build();
    }

    private PasswordDTO createHashedPassword(PasswordDTO dto) {
        return new PasswordDTO(Password.hash(dto.password()).withBcrypt().getResult());
    }

    private void simulateAction() throws SQLException {
        try(Connection connection = this.agroalDataSource.getConnection()) {
            try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT pg_sleep(0.5)")) {
                try(ResultSet rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                    }
                }
            }
        }
    }

}
