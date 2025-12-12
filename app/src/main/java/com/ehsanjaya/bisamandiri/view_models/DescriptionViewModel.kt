package com.ehsanjaya.bisamandiri.view_models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class DescriptionViewModel : ViewModel() {
    var type: MutableState<String?> = mutableStateOf(null)
    var visibleButtonStart: MutableState<Boolean> = mutableStateOf(false)
}