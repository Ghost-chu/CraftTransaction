package com.ghostchu.crafttransaction.operation;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public abstract class Transaction {
    private final Stack<Operation> processingStack = new Stack<>();
    @Getter
    @Setter
    private String lastError;

    /**
     * Commit the transaction by the Fail-Safe way
     * Automatic rollback when commit failed
     *
     * @return The transaction success.
     */
    public boolean failSafeCommit() {
        boolean result = commit(new TransactionCallback() {});
        if (!result) {
            rollback(true);
        }
        return result;
    }

    public abstract boolean commit(@NotNull TransactionCallback callback);

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected boolean executeOperation(@NotNull Operation operation) {
        if (operation.isCommitted()) {
            throw new IllegalStateException("Operation already committed");
        }
        if (operation.isRollback()) {
            throw new IllegalStateException("Operation already rolled back, you must create another new operation.");
        }
        try {
            if(operation.commit()) {
                processingStack.push(operation);
                return true;
            }
            return false;
        } catch (Exception exception) {
            this.lastError = "Failed to execute operation: " + operation;
            return false;
        }
    }

    /**
     * Rolling back the transaction
     *
     * @param continueWhenFailed Continue when some parts of the rollback fails.
     * @return A list contains all steps executed. If "continueWhenFailed" is false, it only contains all success steps before hit the error. Else all.
     */
    @SuppressWarnings("UnusedReturnValue")
    @NotNull
    public List<Operation> rollback(boolean continueWhenFailed) {
        List<Operation> operations = new ArrayList<>();
        while (!processingStack.isEmpty()) {
            Operation operation = processingStack.pop();
            if (!operation.isCommitted()) {
                continue;
            }
            if (operation.isRollback()) {
                continue;
            }
            try {
                boolean result = operation.rollback();
                if (!result) {
                    if (continueWhenFailed) {
                        operations.add(operation);
                        continue;
                    } else {
                        break;
                    }
                }
                operations.add(operation);
            } catch (Exception exception) {
                if (continueWhenFailed) {
                    operations.add(operation);
                } else {
                    break;
                }
            }
        }
        return operations;
    }
}
