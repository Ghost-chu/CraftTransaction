/*
 *  This file is a part of project QuickShop, the name is WithdrawEconomyOperation.java
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

package com.ghostchu.crafttransaction.economy.operation;

import com.ghostchu.crafttransaction.economy.EconomyCore;
import com.ghostchu.crafttransaction.operation.Operation;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class WithdrawEconomyOperation implements Operation {
    private final UUID account;
    private final double amount;
    private final EconomyCore economyCore;
    private final World world;
    private final String currency;
    private boolean committed = false;
    private boolean rollback = false;

    public WithdrawEconomyOperation(@NotNull UUID account, double amount, @NotNull World world, @Nullable String currency, @NotNull EconomyCore economyCore) {
        this.account = account;
        this.amount = amount;
        this.world = world;
        this.currency = currency;
        this.economyCore = economyCore;
    }

    @Override
    public boolean commit() throws Exception {
        boolean result = economyCore.withdraw(account, amount, world, currency);
        if(result)
            committed = true;
        return result;
    }

    @Override
    public boolean rollback() throws Exception {
        boolean result = economyCore.deposit(account, amount, world, currency);
        if(result)
            rollback = true;
        return result;
    }

    @Override
    public boolean isCommitted() {
        return this.committed;
    }

    @Override
    public boolean isRollback() {
        return this.rollback;
    }
}
