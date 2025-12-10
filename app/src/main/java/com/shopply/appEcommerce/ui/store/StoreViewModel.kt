package com.shopply.appEcommerce.ui.store

import androidx.lifecycle.ViewModel
import com.shopply.appEcommerce.data.repository.ProductRepository
import com.shopply.appEcommerce.data.repository.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val storeRepository: StoreRepository,
    private val productRepository: ProductRepository
) : ViewModel()