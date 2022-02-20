/*
 *  This file is a part of project QuickShop, the name is InventoryTransaction.java
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

package com.ghostchu.crafttransaction.inventory;

import com.ghostchu.crafttransaction.inventory.operation.AddItemOperation;
import com.ghostchu.crafttransaction.inventory.operation.RemoveItemOperation;
import com.ghostchu.crafttransaction.operation.Transaction;
import com.ghostchu.crafttransaction.operation.TransactionCallback;
import lombok.Builder;
import lombok.Getter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InventoryTransaction extends Transaction {
    @Getter
    private final Inventory from;
    @Getter
    private final Inventory to;
    @Getter
    private final ItemStack item;
    @Getter
    private final int amount;


    @Builder
    public InventoryTransaction(@Nullable Inventory from, @Nullable Inventory to, @NotNull ItemStack item, int amount) {
        if (from == null && to == null)
            throw new IllegalArgumentException("Both from and to are null");
        this.from = from;
        this.to = to;
        this.item = item.clone();
        this.amount = amount;
    }

    /**
     * Commit the transaction with callback
     *
     * @param callback The result callback
     * @return The transaction success.
     */
    public boolean commit(@NotNull TransactionCallback callback) {
        if (!callback.onCommit(this)) {
            this.setLastError("Plugin cancelled this transaction.");
            return false;
        }
        if (from != null && !this.executeOperation(new RemoveItemOperation(item, amount, from))) {
            this.setLastError("Failed to remove " + amount + "x " + item + " from " + from);
            callback.onFailed(this);
            return false;
        }
        if (to != null && !this.executeOperation(new AddItemOperation(item, amount, to))) {
            this.setLastError("Failed to add " + amount + "x " + item + " to " + to);
            callback.onFailed(this);
            return false;
        }
        callback.onSuccess(this);
        return true;
    }

}
