package models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import common.AppController;

/**
 * Created by jaya.krishna on 05-06-2017.
 */

public class ContactsModel {

    String buyerType = "--", homeType = "--", propertyAddress = "--", relocatingSuburb = "--", methodOfContact = "--",
            siteSupervisor = "--", clientRelationsOfficer = "--", colorConsultant = "--", electricalConsultant = "--",
            salesConsultant = "--", staffManager = "--", mobileNumber = "--", phoneNumber = "--", email = "--", colorDate = "", jobRegion = "--",
            callback = "--", salesEmail = "--", colourEmail = "--", electricalEmail = "--", supervisorEmail = "--", cROEmail = "--",
            newHomeConsultantPhone = "--", electricalConsultantPhone = "--", interiorDesignerPhone = "--", cROPhone = "--", siteSupervisorPhone = "--";
    ArrayList<String> contractNames = new ArrayList<String>();
    boolean exceptionRaised = false;

    String contactName, contactNumber;

    public ContactsModel(String result) {
        try {

            Log.v("Support Test","Step 8 "+result);
            JSONObject job = new JSONObject(result);
            buyerType = job.isNull("buyerType") ? "--" : job.getString("buyerType");
            homeType = job.isNull("homeType") ? "--" : job.getString("homeType");
            propertyAddress = job.isNull("propertyAddress") ? "--" : job.getString("propertyAddress");
            relocatingSuburb = job.isNull("relocatingSuburb") ? "--" : job.getString("relocatingSuburb");
            methodOfContact = job.isNull("methodOfContact") ? "--" : job.getString("methodOfContact");
            siteSupervisor = job.isNull("siteSupervisor") ? "--" : job.getString("siteSupervisor");
            clientRelationsOfficer = job.isNull("CRO") ? "--" : job.getString("CRO");
            colorConsultant = job.isNull("interiorDesigner") ? "--" : job.getString("interiorDesigner");
            electricalConsultant = job.isNull("electricalConsultant") ? "--" : job.getString("electricalConsultant");
            salesConsultant = job.isNull("newHomeConsultant") ? "--" : job.getString("newHomeConsultant");
            staffManager = job.isNull("staffManager") ? "--" : job.getString("staffManager");
            mobileNumber = job.isNull("mobileNumber") ? "--" : job.getString("mobileNumber");
            phoneNumber = job.isNull("phoneNumber") ? "--" : job.getString("phoneNumber");
            email = job.isNull("email") ? "--" : job.getString("email");
            colorDate = job.isNull("colorDate") ? "" : job.getString("colorDate");
            jobRegion = job.isNull("jobRegion") ? "--" : job.getString("jobRegion");
            callback = job.isNull("callback") ? "--" : job.getString("callback");
            salesEmail = job.isNull("NewHomeConsultantEmail") ? "--" : job.getString("NewHomeConsultantEmail");
            colourEmail = job.isNull("InteriorDesignerEmail") ? "--" : job.getString("InteriorDesignerEmail");
            electricalEmail = job.isNull("ElectricalConsultantEmail") ? "--" : job.getString("ElectricalConsultantEmail");
            supervisorEmail = job.isNull("SiteSupervisorEmail") ? "--" : job.getString("SiteSupervisorEmail");
            cROEmail = job.isNull("CROEmail") ? "--" : job.getString("CROEmail");
            newHomeConsultantPhone = job.isNull("NewHomeConsultantPhone") ? "--" : job.getString("NewHomeConsultantPhone");
            electricalConsultantPhone = job.isNull("ElectricalConsultantPhone") ? "--" : job.getString("ElectricalConsultantPhone");
            interiorDesignerPhone = job.isNull("InteriorDesignerPhone") ? "--" : job.getString("InteriorDesignerPhone");
            cROPhone = job.isNull("CROPhone") ? "--" : job.getString("CROPhone");
            siteSupervisorPhone = job.isNull("SiteSupervisorPhone") ? "--" : job.getString("SiteSupervisorPhone");

            if (!job.isNull("contractNames")) {
                JSONArray photoJsonArray = job.getJSONArray("contractNames");
                for (int i = 0; i < photoJsonArray.length(); i++) {
                    contractNames.add(photoJsonArray.get(i).toString());
                }
            }

            buyerType = buyerType.trim();
            homeType = homeType.trim();
            propertyAddress = propertyAddress.trim();
            relocatingSuburb = relocatingSuburb.trim();
            methodOfContact = methodOfContact.trim();

            siteSupervisor = siteSupervisor.trim();
            clientRelationsOfficer = clientRelationsOfficer.trim();
            colorConsultant = colorConsultant.trim();
            electricalConsultant = electricalConsultant.trim();

            salesConsultant = salesConsultant.trim();
            staffManager = staffManager.trim();
            mobileNumber = mobileNumber.trim();
            phoneNumber = phoneNumber.trim();
            email = email.trim();
            colorDate = colorDate.trim();
            jobRegion = jobRegion.trim();

            callback = callback.trim();
            salesEmail = salesEmail.trim();
            colourEmail = colourEmail.trim();
            electricalEmail = electricalEmail.trim();
            supervisorEmail = supervisorEmail.trim();
            cROEmail = cROEmail.trim();

            newHomeConsultantPhone = newHomeConsultantPhone.trim();
            electricalConsultantPhone = electricalConsultantPhone.trim();
            interiorDesignerPhone = interiorDesignerPhone.trim();
            cROPhone = cROPhone.trim();
            siteSupervisorPhone = siteSupervisorPhone.trim();

        } catch (Exception ex) {
            exceptionRaised = true;
            Log.v("Support Test","Step 8 "+ex.getMessage());
            ex.fillInStackTrace();
        }
    }

    public ContactsModel() {
    }

    public ContactsModel(String contactName, String contactNumber) {
        this.contactNumber = contactNumber;
        this.contactName = contactName;
    }

    public boolean isExceptionRaised() {
        return exceptionRaised;
    }

    public void setExceptionRaised(boolean exceptionRaised) {
        this.exceptionRaised = exceptionRaised;
    }

    public String getNewHomeConsultantPhone() {
        return newHomeConsultantPhone;
    }

    public void setNewHomeConsultantPhone(String newHomeConsultantPhone) {
        this.newHomeConsultantPhone = newHomeConsultantPhone;
    }

    public String getElectricalConsultantPhone() {
        return electricalConsultantPhone;
    }

    public void setElectricalConsultantPhone(String electricalConsultantPhone) {
        this.electricalConsultantPhone = electricalConsultantPhone;
    }

    public String getInteriorDesignerPhone() {
        return interiorDesignerPhone;
    }

    public void setInteriorDesignerPhone(String interiorDesignerPhone) {
        this.interiorDesignerPhone = interiorDesignerPhone;
    }

    public String getcROPhone() {
        return cROPhone;
    }

    public void setcROPhone(String cROPhone) {
        this.cROPhone = cROPhone;
    }

    public String getSiteSupervisorPhone() {
        return siteSupervisorPhone;
    }

    public void setSiteSupervisorPhone(String siteSupervisorPhone) {
        this.siteSupervisorPhone = siteSupervisorPhone;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getBuyerType() {
        return buyerType;
    }

    public void setBuyerType(String buyerType) {
        this.buyerType = buyerType;
    }

    public String getHomeType() {
        return homeType;
    }

    public void setHomeType(String homeType) {
        this.homeType = homeType;
    }

    public String getPropertyAddress() {
        return propertyAddress;
    }

    public void setPropertyAddress(String propertyAddress) {
        this.propertyAddress = propertyAddress;
    }

    public String getRelocatingSuburb() {
        return relocatingSuburb;
    }

    public void setRelocatingSuburb(String relocatingSuburb) {
        this.relocatingSuburb = relocatingSuburb;
    }

    public String getMethodOfContact() {
        return methodOfContact;
    }

    public void setMethodOfContact(String methodOfContact) {
        this.methodOfContact = methodOfContact;
    }

    public String getSiteSupervisor() {
        return siteSupervisor;
    }

    public void setSiteSupervisor(String siteSupervisor) {
        this.siteSupervisor = siteSupervisor;
    }

    public String getClientRelationsOfficer() {
        return clientRelationsOfficer;
    }

    public void setClientRelationsOfficer(String clientRelationsOfficer) {
        this.clientRelationsOfficer = clientRelationsOfficer;
    }

    public String getColorConsultant() {
        return colorConsultant;
    }

    public void setColorConsultant(String colorConsultant) {
        this.colorConsultant = colorConsultant;
    }

    public String getElectricalConsultant() {
        return electricalConsultant;
    }

    public void setElectricalConsultant(String electricalConsultant) {
        this.electricalConsultant = electricalConsultant;
    }

    public String getSalesConsultant() {
        return salesConsultant;
    }

    public void setSalesConsultant(String salesConsultant) {
        this.salesConsultant = salesConsultant;
    }

    public String getStaffManager() {
        return staffManager;
    }

    public void setStaffManager(String staffManager) {
        this.staffManager = staffManager;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getColorDate() {
        return colorDate;
    }

    public void setColorDate(String colorDate) {
        this.colorDate = colorDate;
    }

    public String getJobRegion() {
        return jobRegion;
    }

    public void setJobRegion(String jobRegion) {
        this.jobRegion = jobRegion;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public String getSalesEmail() {
        return salesEmail;
    }

    public void setSalesEmail(String salesEmail) {
        this.salesEmail = salesEmail;
    }

    public String getColourEmail() {
        return colourEmail;
    }

    public void setColourEmail(String colourEmail) {
        this.colourEmail = colourEmail;
    }

    public String getElectricalEmail() {
        return electricalEmail;
    }

    public void setElectricalEmail(String electricalEmail) {
        this.electricalEmail = electricalEmail;
    }

    public String getSupervisorEmail() {
        return supervisorEmail;
    }

    public void setSupervisorEmail(String supervisorEmail) {
        this.supervisorEmail = supervisorEmail;
    }

    public String getcROEmail() {
        return cROEmail;
    }

    public void setcROEmail(String cROEmail) {
        this.cROEmail = cROEmail;
    }

    public ArrayList<String> getContractNames() {
        return contractNames;
    }

    public void setContractNames(ArrayList<String> contractNames) {
        this.contractNames = contractNames;
    }
}
