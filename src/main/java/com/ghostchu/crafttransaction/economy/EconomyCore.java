/*
 *  This file is a part of project QuickShop, the name is EconomyCore.java
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

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @author netherfoam Represents an economy.
 */
public interface EconomyCore {
    /**
     * Deposits a given amount of money from thin air to the given username.
     *
     * @param name     The exact (case insensitive) username to give money to
     * @param amount   The amount to give them
     * @param currency The currency name
     * @param world    The transaction world
     * @return True if success (Should be almost always)
     */
    boolean deposit(@NotNull UUID name, double amount, @NotNull World world, @Nullable String currency);

    /**
     * Formats the given number... E.g. 50.5 becomes $50.5 Dollars, or 50 Dollars 5 Cents
     *
     * @param balance  The given number
     * @param currency The currency name
     * @param world    The transaction world
     * @return The balance in human readable text.
     */
    String format(double balance, @NotNull World world, @Nullable String currency);

    /**
     * Fetches the balance of the given account name
     *
     * @param name     The name of the account
     * @param currency The currency name
     * @param world    The transaction world
     * @return Their current balance.
     */
    double getBalance(@NotNull UUID name, @NotNull World world, @Nullable String currency);

    /**
     * Withdraws a given amount of money from the given username and turns it to thin air.
     *
     * @param name     The exact (case insensitive) username to take money from
     * @param amount   The amount to take from them
     * @param currency The currency name
     * @param world    The transaction world
     * @return True if success, false if they didn't have enough cash
     */
    boolean withdraw(@NotNull UUID name, double amount, @NotNull World world, @Nullable String currency);

    /**
     * Gets the economy processor last error message
     *
     * @return Error message or null if never happens
     */
    @Nullable
    String getLastError();

    /**
     * Getting Economy impl name
     *
     * @return Impl name
     */
    @NotNull String getName();
}
