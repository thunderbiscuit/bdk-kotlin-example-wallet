/*
 * Copyright 2021-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.bitcoindevkit.devkitwallet.data.NewWalletConfig
import org.bitcoindevkit.devkitwallet.data.RecoverWalletConfig
import org.bitcoindevkit.devkitwallet.data.SingleWallet
import org.bitcoindevkit.devkitwallet.data.UserPreferences
import org.bitcoindevkit.devkitwallet.data.UserPreferencesSerializer
import org.bitcoindevkit.devkitwallet.domain.UserPreferencesRepository
import org.bitcoindevkit.devkitwallet.domain.Wallet
import org.bitcoindevkit.devkitwallet.presentation.navigation.HomeNavigation
import org.bitcoindevkit.devkitwallet.presentation.navigation.CreateWalletNavigation
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.intro.OnboardingScreen
import org.bitcoindevkit.devkitwallet.presentation.theme.DevkitTheme

private const val TAG = "DevkitWalletActivity"
private val Context.userPreferencesStore: DataStore<UserPreferences> by dataStore(
    fileName = "user_preferences.pb",
    serializer = UserPreferencesSerializer
)

class DevkitWalletActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userPreferencesRepository = UserPreferencesRepository(userPreferencesStore)
        val onBuildWalletButtonClicked: (WalletCreateType) -> Unit = { walletCreateType ->
            try {
                val activeWallet = when (walletCreateType) {
                    is WalletCreateType.FROMSCRATCH -> Wallet.createWallet(
                        newWalletConfig = walletCreateType.newWalletConfig,
                        internalAppFilesPath = filesDir.absolutePath,
                        userPreferencesRepository = userPreferencesRepository,
                    )
                    is WalletCreateType.LOADEXISTING -> Wallet.loadActiveWallet(
                        activeWallet = walletCreateType.activeWallet,
                        internalAppFilesPath = filesDir.absolutePath,
                    )
                    is WalletCreateType.RECOVER -> Wallet.recoverWallet(
                        recoverWalletConfig = walletCreateType.recoverWalletConfig,
                        internalAppFilesPath = filesDir.absolutePath,
                        userPreferencesRepository = userPreferencesRepository,
                    )
                }
                activeWallet.setStoragePath(filesDir.absolutePath)
                setContent {
                    DevkitTheme {
                        HomeNavigation(activeWallet)
                    }
                }
            } catch (e: Throwable) {
                Log.i(TAG, "Could not build wallet: $e")
            }
        }

        lifecycleScope.launch {
            val activeWallets = async {
                userPreferencesRepository.fetchActiveWallets()
            }.await()

            val onboardingDone = async {
                userPreferencesRepository.fetchIntroDone()
            }.await()

            val onFinishOnboarding: () -> Unit = {
                lifecycleScope.launch { userPreferencesRepository.setIntroDone() }
                setContent {
                    DevkitTheme {
                        CreateWalletNavigation(onBuildWalletButtonClicked, activeWallets)
                    }
                }
            }

            setContent {
                if (!onboardingDone) {
                    OnboardingScreen(onFinishOnboarding)
                } else {
                    DevkitTheme {
                        CreateWalletNavigation(onBuildWalletButtonClicked, activeWallets)
                    }
                }
            }
        }
    }
}

sealed class WalletCreateType {
    data class FROMSCRATCH(val newWalletConfig: NewWalletConfig) : WalletCreateType()
    data class LOADEXISTING(val activeWallet: SingleWallet) : WalletCreateType()
    data class RECOVER(val recoverWalletConfig: RecoverWalletConfig) : WalletCreateType()
}
