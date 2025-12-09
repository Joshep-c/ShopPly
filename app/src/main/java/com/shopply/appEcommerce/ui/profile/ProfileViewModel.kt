package com.shopply.appEcommerce.ui.profile

import androidx.lifecycle.ViewModel
import com.shopply.appEcommerce.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    val userRepository: UserRepository
) : ViewModel()