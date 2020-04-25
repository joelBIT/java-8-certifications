package joelbits.parser;

import java.util.Objects;

@FunctionalInterface
public interface VoidCommand {
    void execute() throws Exception;

    default VoidCommand andThen(VoidCommand var1) {
        Objects.requireNonNull(var1);
        return () -> {
            this.execute();
            var1.execute();
        };
    }
}
