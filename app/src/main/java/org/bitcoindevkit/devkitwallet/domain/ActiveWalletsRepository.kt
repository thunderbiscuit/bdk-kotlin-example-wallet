/*
 * Copyright 2021-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.domain

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.first
import org.bitcoindevkit.devkitwallet.data.SingleWallet
import org.bitcoindevkit.devkitwallet.data.ActiveWallets
import org.bitcoindevkit.devkitwallet.data.IntroDone

class ActiveWalletsRepository(
    private val walletsPreferencesStore: DataStore<ActiveWallets>,
    private val introDonePreferencesStore: DataStore<IntroDone>
) {
    suspend fun fetchIntroDone(): IntroDone {
        return introDonePreferencesStore.data.first()
    }

    suspend fun setIntroDone() {
        introDonePreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder().setIntroDone(true).build()
        }
    }

    suspend fun fetchActiveWallets(): ActiveWallets {
        return walletsPreferencesStore.data.first()
    }

    suspend fun updateActiveWallets(singleWallet: SingleWallet) {
        walletsPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder().addWallets(singleWallet).build()
        }
    }
}
