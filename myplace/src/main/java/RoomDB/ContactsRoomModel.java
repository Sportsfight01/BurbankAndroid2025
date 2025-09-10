package RoomDB;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "ContactsTable")
public class ContactsRoomModel {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "jobNUmber")
    private String jobNumber;

    @Nullable
    @ColumnInfo(name = "buyerType")
    private String buyerType;

    @Nullable
    @ColumnInfo(name = "homeType")
    private String homeType;

    @Nullable
    @ColumnInfo(name = "propertyAddress")
    private String propertyAddress;

    @Nullable
    @ColumnInfo(name = "relocatingSuburb")
    private String relocatingSuburb;

    @Nullable
    @ColumnInfo(name = "methodOfContact")
    private String methodOfContact;

    @Nullable
    @ColumnInfo(name = "siteSupervisor")
    private String siteSupervisor;

    @Nullable
    @ColumnInfo(name = "CRO")
    private String CRO;

    @Nullable
    @ColumnInfo(name = "interiorDesigner")
    private String interiorDesigner;

    @Nullable
    @ColumnInfo(name = "electricalConsultant")
    private String electricalConsultantelectricalConsultant;

    @Nullable
    @ColumnInfo(name = "newHomeConsultant")
    private String newHomeConsultant;

    @Nullable
    @ColumnInfo(name = "staffManager")
    private String staffManager;

    @Nullable
    @ColumnInfo(name = "mobileNumber")
    private String mobileNumber;

    @Nullable
    @ColumnInfo(name = "phoneNumber")
    private String phoneNumber;

    @Nullable
    @ColumnInfo(name = "email")
    private String email;

    @Nullable
    @ColumnInfo(name = "colorDate")
    private String colorDate;

    @Nullable
    @ColumnInfo(name = "jobRegion")
    private String jobRegion;

    @Nullable
    @ColumnInfo(name = "callback")
    private String callback;

    @Nullable
    @ColumnInfo(name = "NewHomeConsultantEmail")
    private String NewHomeConsultantEmail;

    @Nullable
    @ColumnInfo(name = "InteriorDesignerEmail")
    private String InteriorDesignerEmail;

    @Nullable
    @ColumnInfo(name = "ElectricalConsultantEmail")
    private String ElectricalConsultantEmail;

    @Nullable
    @ColumnInfo(name = "SiteSupervisorEmail")
    private String SiteSupervisorEmail;

    @Nullable
    @ColumnInfo(name = "CROEmail")
    private String CROEmail;

    @Nullable
    @ColumnInfo(name = "NewHomeConsultantPhone")
    private String NewHomeConsultantPhone;

    @Nullable
    @ColumnInfo(name = "ElectricalConsultantPhone")
    private String ElectricalConsultantPhone;

    @Nullable
    @ColumnInfo(name = "InteriorDesignerPhone")
    private String InteriorDesignerPhone;

    @Nullable
    @ColumnInfo(name = "CROPhone")
    private String CROPhone;

    @Nullable
    @ColumnInfo(name = "SiteSupervisorPhone")
    private String SiteSupervisorPhone;

    public ContactsRoomModel(@NonNull String jobNumber, @Nullable String buyerType, @Nullable String homeType, @Nullable String propertyAddress, @Nullable String relocatingSuburb, @Nullable String methodOfContact, @Nullable String siteSupervisor, @Nullable String CRO, @Nullable String interiorDesigner, @Nullable String electricalConsultantelectricalConsultant, @Nullable String newHomeConsultant, @Nullable String staffManager, @Nullable String mobileNumber, @Nullable String phoneNumber, @Nullable String email, @Nullable String colorDate, @Nullable String jobRegion, @Nullable String callback, @Nullable String newHomeConsultantEmail, @Nullable String interiorDesignerEmail, @Nullable String electricalConsultantEmail, @Nullable String siteSupervisorEmail, @Nullable String CROEmail, @Nullable String newHomeConsultantPhone, @Nullable String electricalConsultantPhone, @Nullable String interiorDesignerPhone, @Nullable String CROPhone, @Nullable String siteSupervisorPhone) {
        this.jobNumber = jobNumber;
        this.buyerType = buyerType;
        this.homeType = homeType;
        this.propertyAddress = propertyAddress;
        this.relocatingSuburb = relocatingSuburb;
        this.methodOfContact = methodOfContact;
        this.siteSupervisor = siteSupervisor;
        this.CRO = CRO;
        this.interiorDesigner = interiorDesigner;
        this.electricalConsultantelectricalConsultant = electricalConsultantelectricalConsultant;
        this.newHomeConsultant = newHomeConsultant;
        this.staffManager = staffManager;
        this.mobileNumber = mobileNumber;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.colorDate = colorDate;
        this.jobRegion = jobRegion;
        this.callback = callback;
        NewHomeConsultantEmail = newHomeConsultantEmail;
        InteriorDesignerEmail = interiorDesignerEmail;
        ElectricalConsultantEmail = electricalConsultantEmail;
        SiteSupervisorEmail = siteSupervisorEmail;
        this.CROEmail = CROEmail;
        NewHomeConsultantPhone = newHomeConsultantPhone;
        ElectricalConsultantPhone = electricalConsultantPhone;
        InteriorDesignerPhone = interiorDesignerPhone;
        this.CROPhone = CROPhone;
        SiteSupervisorPhone = siteSupervisorPhone;
    }

    @NonNull
    public String getJobNumber() {
        return jobNumber;
    }

    @Nullable
    public String getBuyerType() {
        return buyerType;
    }

    @Nullable
    public String getHomeType() {
        return homeType;
    }

    @Nullable
    public String getPropertyAddress() {
        return propertyAddress;
    }

    @Nullable
    public String getRelocatingSuburb() {
        return relocatingSuburb;
    }

    @Nullable
    public String getMethodOfContact() {
        return methodOfContact;
    }

    @Nullable
    public String getSiteSupervisor() {
        return siteSupervisor;
    }

    @Nullable
    public String getCRO() {
        return CRO;
    }

    @Nullable
    public String getInteriorDesigner() {
        return interiorDesigner;
    }

    @Nullable
    public String getElectricalConsultantelectricalConsultant() {
        return electricalConsultantelectricalConsultant;
    }

    @Nullable
    public String getNewHomeConsultant() {
        return newHomeConsultant;
    }

    @Nullable
    public String getStaffManager() {
        return staffManager;
    }

    @Nullable
    public String getMobileNumber() {
        return mobileNumber;
    }

    @Nullable
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Nullable
    public String getEmail() {
        return email;
    }

    @Nullable
    public String getColorDate() {
        return colorDate;
    }

    @Nullable
    public String getJobRegion() {
        return jobRegion;
    }

    @Nullable
    public String getCallback() {
        return callback;
    }

    @Nullable
    public String getNewHomeConsultantEmail() {
        return NewHomeConsultantEmail;
    }

    @Nullable
    public String getInteriorDesignerEmail() {
        return InteriorDesignerEmail;
    }

    @Nullable
    public String getElectricalConsultantEmail() {
        return ElectricalConsultantEmail;
    }

    @Nullable
    public String getSiteSupervisorEmail() {
        return SiteSupervisorEmail;
    }

    @Nullable
    public String getCROEmail() {
        return CROEmail;
    }

    @Nullable
    public String getNewHomeConsultantPhone() {
        return NewHomeConsultantPhone;
    }

    @Nullable
    public String getElectricalConsultantPhone() {
        return ElectricalConsultantPhone;
    }

    @Nullable
    public String getInteriorDesignerPhone() {
        return InteriorDesignerPhone;
    }

    @Nullable
    public String getCROPhone() {
        return CROPhone;
    }

    @Nullable
    public String getSiteSupervisorPhone() {
        return SiteSupervisorPhone;
    }
}
