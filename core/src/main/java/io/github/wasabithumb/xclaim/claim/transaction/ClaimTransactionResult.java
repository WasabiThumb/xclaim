package io.github.wasabithumb.xclaim.claim.transaction;

import io.github.wasabithumb.xclaim.claim.Claim;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public interface ClaimTransactionResult {

    static @NotNull ClaimTransactionResult success(@NotNull Claim claim) {
        return new Success(claim);
    }

    static @NotNull ClaimTransactionResult error() {
        return Error.INSTANCE;
    }

    //

    boolean isSuccess();

    @UnknownNullability Claim claim();

    void unwrap();

    //

    record Success(@NotNull Claim claim) implements ClaimTransactionResult {

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public void unwrap() { }

    }

    final class Error implements ClaimTransactionResult {

        public static final Error INSTANCE = new Error();

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        @Contract("-> null")
        public Claim claim() {
            return null;
        }

        @Override
        public void unwrap() {
            throw new AssertionError("Claim transaction failed");
        }

    }

}
