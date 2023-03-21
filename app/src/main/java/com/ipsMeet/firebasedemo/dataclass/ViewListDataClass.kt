package com.ipsMeet.firebasedemo.dataclass

data class ViewListDataClass(
    var key: String = "",
    val name: String = "",
    val organization: String = "",
    val address: String = "",
    val email: String = "",
    val phone: String = "",
    val totalPurchase: Int? = 0
)