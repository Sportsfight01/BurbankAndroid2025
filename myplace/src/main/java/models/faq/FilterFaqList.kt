package models.faq

data class FilterFaqList(
    val heading: String,
    val subList : List<FAQDataItem>,
    var isExpanded: Boolean = false
)
