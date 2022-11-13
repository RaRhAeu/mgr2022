import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
record StatusDTO(String status) {
}
