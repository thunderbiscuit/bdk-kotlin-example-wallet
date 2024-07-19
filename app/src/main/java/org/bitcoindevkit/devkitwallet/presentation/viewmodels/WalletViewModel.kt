/*
 * Copyright 2021-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.bitcoindevkit.devkitwallet.domain.Wallet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.bitcoindevkit.NodeMessageHandler
import org.bitcoindevkit.NodeState
import org.bitcoindevkit.Peer
import org.bitcoindevkit.devkitwallet.domain.CurrencyUnit
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.WalletScreenAction
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.WalletScreenState
import org.bitcoindevkit.runNode

private const val TAG = "WalletViewModel"

internal class WalletViewModel(
    private val wallet: Wallet
) : ViewModel() {

    var state: WalletScreenState by mutableStateOf(WalletScreenState())
        private set

    fun onAction(action: WalletScreenAction) {
        when (action) {
            WalletScreenAction.UpdateBalance  -> updateBalance()
            WalletScreenAction.SwitchUnit     -> switchUnit()
            WalletScreenAction.StartKyotoNode -> startKyotoNode()
        }
    }

    private fun switchUnit() {
        state = when (state.unit) {
            CurrencyUnit.Bitcoin -> state.copy(unit = CurrencyUnit.Satoshi)
            CurrencyUnit.Satoshi -> state.copy(unit = CurrencyUnit.Bitcoin)
        }
    }

    private fun updateBalance() {
        state = state.copy(syncing = true)

        viewModelScope.launch(Dispatchers.IO) {
            val syncHadUpdate: Boolean = wallet.kyotoSync()
            Log.i(TAG, "Synced with update value: $syncHadUpdate")
            if (syncHadUpdate) {
                Log.i(TAG, "Sync had update")
                val balance = wallet.getBalance()
                Log.i(TAG, "New balance: $balance")
                withContext(Dispatchers.Main) {
                    state = state.copy(balance = balance, syncing = false)
                }
            } else {
                withContext(Dispatchers.Main) {
                    state = state.copy(syncing = false)
                }
            }
        }
    }

    private fun startKyotoNode() {
        viewModelScope.launch(Dispatchers.IO) {
            wallet.startKyotoNode()
        }
    }
}
