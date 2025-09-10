package models.contacts

data class MyContactsData(
    val cro: String,
    val croEmail: String,
    val croPhone: String,
    val electricalConsultantEmail: String,
    val electricalConsultantPhone: String,
    val interiorDesignerEmail: String,
    val interiorDesignerPhone: String,
    val newHomeConsultantEmail: String,
    val newHomeConsultantPhone: String,
    val siteSupervisorEmail: String,
    val siteSupervisorPhone: String,
    val buyerType: String,
    val callback: Any,
    val colorDate: String,
    val contractName: List<String>,
    val electricalConsultant: String,
    val email: String,
    val homeType: String,
    val interiorDesigner: String,
    val jobRegion: String,
    val methodOfContact: String,
    val mobileNumber: String,
    val newHomeConsultant: String,
    val phoneNumber: String,
    val propertyAddress: String,
    val relocatingSuburb: String,
    val siteSupervisor: String,
    val staffManager: Any
)

data class AppVersionModel (
    val Id: String,
    val AppType: String,
    val AppVersion: String
)