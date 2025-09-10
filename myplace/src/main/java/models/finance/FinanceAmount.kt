package models.finance

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FinanceAmount(
    val amount: Double,
    val description: String
): Parcelable
