package com.dmss.burbankapp.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class HouseDetailsByHouseType(
    @SerializedName("HouseId_LandBank")
    var HouseId_LandBank: Int,
    @SerializedName("HouseName")
    var HouseName: String,
    @SerializedName("HouseSize")
    var HouseSize: Int,
    @SerializedName("Facade")
    var Facade: String,
    @SerializedName("Storey")
    var Storey: Int,
    @SerializedName("CarSpace")
    var CarSpace: Int = 0,
    @SerializedName("BedRooms")
    var BedRooms: Int = 0,
    @SerializedName("BathRooms")
    var BathRooms: Int = 0,

    @SerializedName("FacadePermanentUrl")
    var FacadePermanentUrl: String?,
    @SerializedName("OpenTimes")
    var openTimes: String,
    @SerializedName("DisplayEstateName")
    var displayEstateName: String,
    @SerializedName("Suburb")
    var suburb: String,
    @SerializedName("Street")
    var street: String,
    @SerializedName("IsFav")
    var isFavorite: Boolean = false,
    @SerializedName("Price")
    var price: Double,
    @SerializedName("Id")
    var id: Int,
    @SerializedName("Latitude")
    var Latitude: String,
    @SerializedName("Longitude")
    var Longitude: String,
    @SerializedName("DisplayId")
    var displayId: Int,
    @SerializedName("MinLotWidth")
    var lotWidth: Double


    /*
    * "HouseId_LandBank": 836,
            "NodeGuid": "00000000-0000-0000-0000-000000000000",
            "StateId": 11,
            "HouseName": "Fitzgerald",
            "HouseSize": 316,
            "Facade": "Metropolitan",
            "StandardFacades": "Condor|Matisse|Monet",
            "FacadeImage": null,
            "FacadeImageGuid": "00000000-0000-0000-0000-000000000000",
            "FacadeImageName": null,
            "Brand": "Burbank Range",
            "Description": "The Fitzgerald is a home that packs it all into its plan. Spread across two storeys, the design places the master suite on the second floor at the front of the home, with all secondary bedrooms also placed on this level, reserving the ground floor for entertaining. With multiple living areas and an alfresco, the Fitzgerald is the affordable double storey home you’ll love. ",
            "Storey": 2,
            "CarSpace": 2,
            "Price": 306200.00,
            "Study": true,
            "Ensuite": true,
            "BedRooms": 4,
            "MinLotWidth": 14.0,
            "HouseLength": 16.91,
            "HouseWidth": 11.9900,
            "MinLotLength": 25.0,
            "BathRooms": 3,
            "LivingAreasq": 0.0000,
            "LivingAreasqm": 0.0000,
            "TotalSizesq": 34.0000,
            "TotalSizesqm": 315.9000,
            "Alfrescosq": 1.2400,
            "Alfrescosqm": 11.5000,
            "FirstFloorsq": 14.7000,
            "FirstFloorsqm": 136.6000,
            "Garagesq": 3.9300,
            "Garagesqm": 36.5000,
            "GroundFloorsq": 13.6700,
            "GroundFloorsqm": 127.0000,
            "Porchsq": 0.4600,
            "Porchsqm": 4.3000,
            "IsShowOnWeb": true,
            "DateAvailable": "2019-09-12T00:00:00",
            "CollectionUrl": null,
            "FacadePermanentUrl": "~/getmedia/c0b518af-ecf7-4082-96ac-ea72ca5c6529/Fitzgerald_Metropolitan.jpg",
            "HouseDimensions_HouseName": [],
            "HomePlan": null,
            "FacadeMediumImageUrls": [],
            "EdgeImages": [],
            "SliderImages": [],
            "DisplaysList": [],
            "Rooms": "Garage,Garage|Porch,Porch|bed 1,Bedroom 1|bed 2,Bedroom 2|bed 3,Bedroom 3|bed 4,Bedroom 4|Entry,Entry|Family,Family|Landing,Landing|Living,Living|Meals,Meals|Void,Staircase|w.i.p,Walk in Pantry|w.i.r 1,Walk in Robe 1|wc,WC|Kitchen,Kitchen|Ldry,Laundry|Bath,Bathroom|ENS,Ensuite|Pdr,Powder|alfresco,Alfresco|w.i.r 2,Walk in Robe 2|w.i.r 4,Walk in Robe 4|w.i.r 3,Walk in Robe 3|Study,Study|passage 1,Passage 1|passage 2,Passage 2",
            "VideoURL": "",
            "Visualisation": true,
            "IsCompare": false,
            "IsFav": false,
            "Livingsqm": 15.5000,
            "Mealssqm": 18.5000,
            "Familysqm": 36.7000,
            "Bed1sqm": 21.8000,
            "Bed2sqm": 13.3000,
            "Bed3sqm": 11.2000,
            "Bed4sqm": 11.2000,
            "InclusionFileName": "Inclusions_Burbank Range_20200302.pdf",
            "ValidFacades": "Aqua|Condor|Hester|Matisse|Metropolitan|Monet|Orana|Rainforest",
            "VisualiseFacade": "Condor",
            "Palettes": "Contemporary|Coastal|Heritage|Scandi|Hamptons|Modern|Lux|Rustic|Bohemian|Classic",
            "DefaultPalette": "Contemporary",
            "SalesCount": 0,
            "IsDisplay": false,
            "DisplayEstateName": null,
            "Id": 1443*/
) : Parcelable