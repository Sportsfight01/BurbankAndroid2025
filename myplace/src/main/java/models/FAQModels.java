package models;

import android.content.Context;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import common.AppController;
import common.MyPlaceDataBase;

public class FAQModels {
    String regionId,question,answer;

    public FAQModels(String result) {
        try {

            JSONObject job = new JSONObject(result);
            regionId = job.isNull("stageId") ? "" : job.getString("stageId");
            question = job.isNull("Question") ? "" : job.getString("Question");
            answer = job.isNull("Answer") ? "" : job.getString("Answer");
        } catch (Exception ex) {
            ex.fillInStackTrace();
        }
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
