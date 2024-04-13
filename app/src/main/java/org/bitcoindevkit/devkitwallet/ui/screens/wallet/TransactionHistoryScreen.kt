/*
 * Copyright 2020-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.ui.screens.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.bitcoindevkit.devkitwallet.domain.Wallet
import org.bitcoindevkit.devkitwallet.ui.Screen
import org.bitcoindevkit.devkitwallet.ui.components.ConfirmedTransactionCard
import org.bitcoindevkit.devkitwallet.ui.components.PendingTransactionCard
import org.bitcoindevkit.devkitwallet.ui.components.SecondaryScreensAppBar
import org.bitcoindevkit.devkitwallet.ui.theme.DevkitWalletColors

private const val TAG = "TransactionHistoryScreen"

@Composable
internal fun TransactionHistoryScreen(navController: NavController) {
    val (pendingTransactions, confirmedTransactions) = Wallet.getAllTxDetails().partition { it.pending }

    Scaffold(
        topBar = {
            SecondaryScreensAppBar(
                title = "Transaction History",
                navigation = { navController.navigate(Screen.HomeScreen.route) }
            )
        }
    ) { paddingValues ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(DevkitWalletColors.primary)
                .padding(top = 6.dp)
                .verticalScroll(state = scrollState)
        ) {
            if (pendingTransactions.isNotEmpty()) {
                pendingTransactions.forEach {
                    PendingTransactionCard(details = it, navController = navController)
                }
            }
            if (confirmedTransactions.isNotEmpty()) {
                confirmedTransactions.sortedBy { it.confirmationBlock?.height }.forEach {
                    ConfirmedTransactionCard(it, navController)
                }
            }
        }
    }
}

fun viewTransaction(navController: NavController, txid: String) {
    navController.navigate("${Screen.TransactionScreen.route}/txid=$txid")
}

// @Preview(device = Devices.PIXEL_4, showBackground = true)
// @Composable
// internal fun PreviewTransactionsScreen() {
//     TransactionsScreen(rememberNavController())
// }