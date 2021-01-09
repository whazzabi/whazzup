package io.github.whazzabi.whazzup.business.fabric;

/**
 * Checked exception used in {@link FabricCheckExecutor}
 */
public class FabricExecutionException extends Exception {
    public FabricExecutionException(String message) {
        super(message);
    }
}
