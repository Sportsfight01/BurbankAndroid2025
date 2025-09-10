package models;

import java.util.ArrayList;

/**
 * Created by Ashish.Kumar on 08-08-2016.
 */
public class ContractFlowDataset {

        ArrayList<DetailsData> details;
        String app_Variation;
        String Ad_Variation;
        String totalAmountClaimed;
        String totalAmountReceived;
        String headerName;
        public ArrayList<DetailsData> getContractFlow() {
            return details;
        }

        public void setContractFlow(ArrayList<DetailsData> details) {
            this.details = details;
        }

        public String getApp_Variation() {
            return app_Variation;
        }

        public void setApp_Variation(String app_Variation) {
            this.app_Variation=app_Variation;
        }

        public String getAd_Variation() {
            return Ad_Variation;
        }

        public void setAd_Variation(String Ad_Variation) {
            this.Ad_Variation=Ad_Variation;
        }

        public String getTotalAmountClaimed() {
            return totalAmountClaimed;
        }

        public void setTotalAmountClaimed(String totalAmountClaimed) {
            this.totalAmountClaimed=totalAmountClaimed;
        }

        public String getTotalAmountReceived() {
            return totalAmountReceived;
        }

        public void setTotalAmountReceived(String totalAmountReceived) {
            this.totalAmountReceived=totalAmountReceived;
        }

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName=headerName;
    }
    }




