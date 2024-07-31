package com.kltn.anigan.domain

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.PurchasesUpdatedListener
import com.kltn.anigan.domain.billing.Product

class BillingViewModel(
    var billingClient: BillingClient? = null
) : ViewModel() {

    var isSuccess = false
    var productList =  mutableStateListOf<Product>()
}