/*
 *  This file is a part of project QuickShop, the name is AddItemOperation.java
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

package com.ghostchu.crafttransaction.inventory.operation;

import com.ghostchu.crafttransaction.operation.Operation;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class AddItemOperation implements Operation {
    private boolean committed;
    private boolean rollback;
    private final ItemStack item;
    private final int amount;
    private final Inventory inv;
    private int remains = 0;
    private int rollbackRemains = 0;
    private final int itemMaxStackSize;

    public AddItemOperation(@NotNull ItemStack item, int amount, @NotNull Inventory inv) {
        this.item = item.clone();
        this.amount = amount;
        this.inv = inv;
        this.itemMaxStackSize = item.getMaxStackSize();
    }

    public int getRemains() {
        return remains;
    }

    public int getRollbackRemains() {
        return rollbackRemains;
    }

    @Override
    public boolean commit() throws Exception {
        committed = true;
        remains = this.amount;
        while (remains > 0) {
            int stackSize = Math.min(remains, itemMaxStackSize);
            item.setAmount(stackSize);
            Map<Integer, ItemStack> notSaved = inv.addItem(item);
            if(notSaved.isEmpty()) {
                remains -= stackSize;
            }else{
                rollbackRemains -= stackSize - notSaved.entrySet().iterator().next().getValue().getAmount();
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean rollback() throws Exception {
        rollback = true;
        rollbackRemains = remains;
        while (rollbackRemains > 0) {
            int stackSize = Math.min(rollbackRemains, itemMaxStackSize);
            item.setAmount(stackSize);
            Map<Integer, ItemStack> notFit = inv.removeItem(item.clone());
            if(notFit.isEmpty()){
                rollbackRemains -= stackSize;
            }else{
                remains -= stackSize - notFit.entrySet().iterator().next().getValue().getAmount();
                return false;
            }
        }
        return true;
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
