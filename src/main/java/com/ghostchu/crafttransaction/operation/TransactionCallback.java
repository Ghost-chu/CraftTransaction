package com.ghostchu.crafttransaction.operation;

import org.jetbrains.annotations.NotNull;

public interface TransactionCallback {
    /**
     * Calling while Transaction commit
     *
     * @param transaction Transaction
     * @return Does commit event has been cancelled
     */
    default boolean onCommit(@NotNull Transaction transaction) {
        return true;
    }

    /**
     * Calling while Transaction commit successfully
     *
     * @param transaction Transaction
     */
    default void onSuccess(@NotNull Transaction transaction) {
    }

    /**
     * Calling while Transaction commit failed
     * Use Transaction#getLastError() to getting reason
     * Use Transaction#getSteps() to getting the fail step
     *
     * @param transaction Transaction
     */
    default void onFailed(@NotNull Transaction transaction) {
    }
}
