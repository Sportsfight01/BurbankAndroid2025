package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class NewHomeQuizModel (
    @field:SerializedName("status") var status:Boolean,
    @field:SerializedName("newhomesQuiz")
    var newhomesQuiz:ArrayList<NewHomeQuizListModel>

)