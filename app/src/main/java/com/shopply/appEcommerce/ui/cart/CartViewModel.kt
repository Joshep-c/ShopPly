package com.shopply.appEcommerce.ui.cart

import androidx.lifecycle.ViewModel
import com.shopply.appEcommerce.data.repository.CartRepository
import com.shopply.appEcommerce.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val userRepository: UserRepository
) : ViewModel()