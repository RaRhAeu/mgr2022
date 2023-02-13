package com.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.password4j.Password;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.postgresql.api.PostgresqlConnection;
import jakarta.inject.Inject;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class Resource {

    @Inject
    PostgresqlConnectionFactory connectionFactory;

    @Inject
    ObjectMapper objectMapper;

    @Get("/s1")
    public Mono<HttpResponse<StatusDTO>> firstScenario() {
        return Mono.just(HttpResponse.ok(new StatusDTO("ok")));
    }

    @Get("/s2")
    public Mono<HttpResponse<StatusDTO>> secondScenario() {
        return this.simulateAction().map(HttpResponse::ok);
    }

    @Post("/s3")
    public Mono<HttpResponse<PasswordDTO>> thirdScenario(@Body PasswordDTO passwordDTO) {
        return this.createdHashedPassword(passwordDTO).map(HttpResponse::ok);
    }

    private Mono<PasswordDTO> createdHashedPassword(PasswordDTO passwordDTO) {
        return Mono.just(new PasswordDTO(Password.hash(passwordDTO.password()).withBcrypt().getResult()));
    }

    @Get("/s4")
    public Mono<HttpResponse<String>> fourthScenario() {
        Map<String, Object> objectMap = new ConcurrentHashMap<>();
        return Mono.zip(
                Mono.just(new StatusDTO("ok")),
                this.simulateAction(),
                createdHashedPassword(new PasswordDTO("some-user-password"))
                ).map(tuple ->
                {
                    objectMap.put("s1", tuple.getT1());
                    objectMap.put("s2", tuple.getT2());
                    objectMap.put("s3", tuple.getT3());
                    return objectMap;
                }
        ).mapNotNull(it -> {
            try {
                return HttpResponse.ok(objectMapper.writeValueAsString(objectMap));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    private Mono<StatusDTO> simulateAction() {
        return Mono.usingWhen(this.connectionFactory.create(),
                        c -> c.createStatement("SELECT pg_sleep(0.5)").execute().next(),
                        PostgresqlConnection::close)
                .flatMap(it -> Mono.just(new StatusDTO("ok")));
    }
}
