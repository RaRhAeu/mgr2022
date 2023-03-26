import com.fasterxml.jackson.annotation.JsonFormat;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.LocalDateTime;

@RegisterForReflection
record StatusDTO(String status,
                 @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
                 LocalDateTime now) {
}
