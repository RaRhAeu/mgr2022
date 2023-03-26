package com.example;

import com.password4j.Password;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import jakarta.inject.Inject;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;

@ExecuteOn(TaskExecutors.IO)
@Controller
public class Resource {

    @Inject
    DataSource dataSource;

    @Get("/s1")
    public String firstScenario() {
        return "Hello, World!";
    }

    @Get("/s2")
    public HttpResponse<StatusDTO> secondScenario() {
        return HttpResponse.ok(new StatusDTO("ok", LocalDateTime.now(ZoneId.of("UTC"))));
    }

    @Post("/s3")
    public HttpResponse<PasswordDTO> thirdScenario(@Body PasswordDTO passwordDTO) {
        return HttpResponse.ok(createdHashedPassword(passwordDTO));
    }

    @Get("/s4")
    public HttpResponse<?> fourth() throws SQLException {
        this.simulateAction();
        return HttpResponse.ok();
    }

    private PasswordDTO createdHashedPassword(PasswordDTO passwordDTO) {
        return new PasswordDTO(Password.hash(passwordDTO.password()).withBcrypt().getResult());
    }

    private void simulateAction() throws SQLException {
        try (Connection connection = this.dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT pg_sleep(0.5)")) {
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                    }
                }
            }
        }
    }
}
