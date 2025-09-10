package models.finance

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FinanceData(
    val contractPrice: Double,
    val financeClaims: List<FinanceAmount>,
    val financeReceipts: List<FinanceAmount>,
    val financeVariations: List<FinanceAmount>,
    val Id: Int
): Parcelable
@Parcelize
data class ResultDataData(
    val status: Boolean,
    val financeDatails: FinanceData,

): Parcelable