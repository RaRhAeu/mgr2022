package com.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@ExecuteOn(TaskExecutors.IO)
@Controller
public class Resource {

    @Inject
    DataSource dataSource;

    @Inject
    ObjectMapper objectMapper;

    @Get("/s1")
    public HttpResponse<StatusDTO> firstScenario() {
        return HttpResponse.ok(new StatusDTO("ok"));
    }

    @Get("/s2")
    public HttpResponse<StatusDTO> secondScenario() throws SQLException {
        return HttpResponse.ok(simulateAction());
    }

    @Post("/s3")
    public HttpResponse<PasswordDTO> thirdScenario(@Body PasswordDTO passwordDTO) {
        return HttpResponse.ok(createdHashedPassword(passwordDTO));
    }

    private PasswordDTO createdHashedPassword(PasswordDTO passwordDTO) {
        return new PasswordDTO(Password.hash(passwordDTO.password()).withBcrypt().getResult());
    }

    @Get("/s4")
    public HttpResponse<String> fourthScenario() throws InterruptedException, JsonProcessingException {
        Map<String, Object> objectMap = new HashMap<>();
        ExecutorService service = Executors.newCachedThreadPool();
        List<Callable<Pair<String, Object>>> tasks = new ArrayList<>();
        tasks.add(() -> Pair.create("s1", new StatusDTO("ok")));
        tasks.add(() -> Pair.create("s2", this.simulateAction()));
        tasks.add(() -> Pair.create("s3", createdHashedPassword(new PasswordDTO("some-user-password"))));
        List<Future<Pair<String, Object>>> futures = service.invokeAll(tasks);
        futures.forEach(it -> {
            try {
                objectMap.put(it.get().getLeft(), it.get().getRight());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        return HttpResponse.ok(objectMapper.writeValueAsString(objectMap));
    }

    private StatusDTO simulateAction() throws SQLException {
        try (Connection connection = this.dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT pg_sleep(0.5)")) {
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                    }
                }
            }
        }
        return new StatusDTO("ok");
    }
}
