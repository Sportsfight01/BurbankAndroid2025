package models.faq

import com.google.gson.annotations.SerializedName

data class FAQData(
    @SerializedName("lstFaq")
    val faqList : ArrayList<FAQDataItem>
)