package com.kltn.anigan

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.AcknowledgePurchaseResponseListener
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ConsumeResponseListener
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.kltn.anigan.domain.BillingViewModel
import com.kltn.anigan.domain.DocsViewModel
import com.kltn.anigan.domain.enums.BillingSecurity
import com.kltn.anigan.ui.AniganNavHost
import com.kltn.anigan.ui.theme.AniGANTheme
import okio.IOException
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {
    private val cameraPermissionRequestCode = 100
    private val billingViewModel by mutableStateOf(BillingViewModel())

    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        installSplashScreen()

        super.onCreate(savedInstanceState)
        setContent {
            AniGANTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel = remember { DocsViewModel() }
                    billingViewModel.billingClient = BillingClient.newBuilder(this)
                        .setListener(purchasesUpdatedListener)
                        .enablePendingPurchases()
                        .build()
                    AniganNavHost(viewModel = viewModel, billingViewModel = billingViewModel)
                }
            }
        }

        // Check if camera permission is granted
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request camera permission if it is not granted
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                cameraPermissionRequestCode
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(billingViewModel.billingClient != null) {
            billingViewModel.billingClient!!.endConnection()
        }
    }

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            if(billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases!=null) {
                for(purchase in purchases) {
                    handlePurchase(billingViewModel, purchase)
                }
            }
            else if(billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                Toast.makeText(applicationContext, "Already Subscribed", Toast.LENGTH_LONG).show()
                billingViewModel.isSuccess = true
            }
            else if(billingResult.responseCode == BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED) {
                Toast.makeText(applicationContext, "Feature not supported",Toast.LENGTH_LONG).show()
            }
            else Toast.makeText(applicationContext, billingResult.debugMessage,Toast.LENGTH_LONG).show()
        }

    private fun handlePurchase(billingViewModel: BillingViewModel, purchase: Purchase) {
        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        val listener = ConsumeResponseListener {billingResult, s ->
            if(billingResult.responseCode == BillingClient.BillingResponseCode.OK) {

            }
        }
        billingViewModel.billingClient!!.consumeAsync(consumeParams, listener)
        if(purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if(!verifyValidSignature(purchase.originalJson, purchase.signature)) {
                Toast.makeText(applicationContext, "Error: Invalid purchase",Toast.LENGTH_LONG).show()
                return
            }
            if(!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams
                    .newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingViewModel.billingClient!!.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener)
                Toast.makeText(applicationContext, "Subscribe successful",Toast.LENGTH_LONG).show()
                billingViewModel.isSuccess = true
            } else {
                Toast.makeText(applicationContext, "Already subscribed",Toast.LENGTH_LONG).show()
            }
        }
        else if(purchase.purchaseState == Purchase.PurchaseState.PENDING) {
            Toast.makeText(applicationContext, "Subscription PENDING",Toast.LENGTH_LONG).show()
        }
        else if (purchase.purchaseState == Purchase.PurchaseState.UNSPECIFIED_STATE) {
            Toast.makeText(applicationContext, "Unspecified state",Toast.LENGTH_LONG).show()
        }

    }

    var acknowledgePurchaseResponseListener = AcknowledgePurchaseResponseListener{billingResult ->
        if(billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            Toast.makeText(applicationContext, "Subscribe successful",Toast.LENGTH_LONG).show()
            billingViewModel.isSuccess = true
        }
    }

    private  fun verifyValidSignature(signedData: String, signature: String): Boolean {
        return try {
            val base64Key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAujkzSRyrBM3opaVNx4MtxzBO3/zjKMqGW5rRpJ7Ms8mIGs1pmhN94rep6okSN/P5/NWyKHxoYK4dJHQfGGNcJPQdaMLFwPbtNirILaMX1OUg+L14u0fuxbHnxfC7pXVKkSoMjmiZSL4gk33NqUw86reB28YNz5U39uokucxwEYqiKX1JdSvyyMFF2zIu+pUIS3JN3+VbBKnVvLi88UHJd7qOLA5BCszU9UQZhuqRG+kKbM1pEaBC2Z3PI1yo2LRdh87dulGQcK5spqfh5m13WvW0yThcY7PHFBXCPDIbEQ4CY8ABDXToClRUcDdJy8QN2Bj5UK87AmYaye+3E/+RCQIDAQAB"
            val security = BillingSecurity()
            security.verifyPurchase(base64Key, signedData, signature)
        } catch (e: IOException) {
            false
        }
    }
}