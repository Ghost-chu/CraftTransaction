/*
 *  This file is a part of project QuickShop, the name is EconomyTransaction.java
 *  Copyright (C) Ghost_chu and contributors
 *
 *  This program is free software: you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License as published by the
 *  Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT
 *  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 *  for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.ghostchu.crafttransaction.economy;

import com.ghostchu.crafttransaction.economy.operation.DepositEconomyOperation;
import com.ghostchu.crafttransaction.economy.operation.WithdrawEconomyOperation;
import com.ghostchu.crafttransaction.operation.Transaction;
import com.ghostchu.crafttransaction.operation.TransactionCallback;
import lombok.Builder;
import lombok.Getter;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Getter
public class EconomyTransaction extends Transaction {
    @Nullable
    private final UUID from;
    @Nullable
    private final UUID to;
    private final double amount;
    @NotNull
    private final EconomyCore core;
    private final boolean allowLoan;
    @Getter
    private final World world;
    @Getter
    @Nullable
    private final String currency;


    /**
     * Create a transaction
     *
     * @param from        The account that money from, but null will be ignored.
     * @param to          The account that money to, but null will be ignored.
     * @param core        economy core
     * @param allowLoan   allow loan?
     * @param amount      the amount of money
     */

    @Builder
    public EconomyTransaction(@Nullable UUID from, @Nullable UUID to, double amount,  @NotNull EconomyCore core, boolean allowLoan, @NotNull World world, @Nullable String currency) {
        this.from = from;
        this.to = to;
        this.core = core;
        this.amount = amount;
        this.allowLoan = allowLoan;
        this.world = world;
        this.currency = currency;

        if (from == null && to == null) {
            this.setLastError("From and To cannot be null in same time.");
            throw new IllegalArgumentException("From and To cannot be null in same time.");
        }
    }

    /**
     * Commit the transaction with callback
     *
     * @param callback The result callback
     * @return The transaction success.
     */
    public boolean commit(@NotNull TransactionCallback callback) {
        if (!callback.onCommit(this)) {
            this.setLastError( "Plugin cancelled this transaction.");
            return false;
        }
        if (!checkBalance()) {
            this.setLastError("From hadn't enough money");
            callback.onFailed(this);
            return false;
        }
        if (from != null && !this.executeOperation(new WithdrawEconomyOperation(from, amount, world, currency, core))) {
            this.setLastError( "Failed to withdraw " + amount + " from player " + from + " account. LastError: " + core.getLastError());
            callback.onFailed(this);
            return false;
        }
        if (to != null && !this.executeOperation(new DepositEconomyOperation(to, amount, world, currency,core))) {
            this.setLastError( "Failed to deposit " + amount + " to player " + to + " account. LastError: " + core.getLastError());
            callback.onFailed(this);
            return false;
        }
        callback.onSuccess(this);
        return true;
    }

    /**
     * Checks this transaction can be finished
     * @return The transaction can be finished (had enough money)
     */
    public boolean checkBalance(){
        return from == null || !(core.getBalance(from, world, currency) < amount) || allowLoan;
    }
}
