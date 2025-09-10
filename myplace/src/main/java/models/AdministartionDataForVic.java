package models;

/**
 * Created by Ashish.Kumar on 20-10-2016.
 */
public class AdministartionDataForVic {
    String ContractId="", ItemName="", DateCompleted="", StageName="", Id="";

//    public AdministartionDataForVic(String ContractId, String ItemName, String DateCompleted, String StageName, String Id) {
//        this.ContractId = ContractId;
//        this.ItemName = ItemName;
//        this.DateCompleted = DateCompleted;
//        this.StageName = StageName;
//        this.Id = Id;
//    }
    public void setContractId(String ContractId) {
        this.ContractId=ContractId;
    }

    public void setItemName(String ItemName) {
       this.ItemName=ItemName;
    }

    public void setDateCompleted(String DateCompleted) {
        this.DateCompleted=DateCompleted;
    }

    public void setStageName(String StageName) {
         this.StageName=StageName;
    }

    public void setId(String Id) {
        this.Id=Id;
    }

    public String getContractId() {
        return ContractId;
    }

    public String getItemName() {
        return ItemName;
    }

    public String getDateCompleted() {
        return DateCompleted;
    }

    public String getStageName() {
        return StageName;
    }

    public String getId() {
        return Id;
    }

}
