package models;

import java.util.ArrayList;

/**
 * Created by Ashish.Kumar on 08-08-2016.
 */
public class FinanceDataSet {
   ArrayList<ContractFlowDataset > cont_recp_Dataset;
    String jobNumber;
    String contractNumber;
    String ContractAmount;

    public FinanceDataSet(ArrayList<ContractFlowDataset > cont_recp_Dataset, String ContractAmount) {
        this.cont_recp_Dataset = cont_recp_Dataset;

        this.ContractAmount= ContractAmount;
    }

    public ArrayList<ContractFlowDataset > getContract() {
        return cont_recp_Dataset;
    }

    public String getJobNumber() {
        return jobNumber;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public String getContractAmount() {
        return ContractAmount;
    }


}
