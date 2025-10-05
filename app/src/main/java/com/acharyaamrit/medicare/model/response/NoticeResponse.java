package com.acharyaamrit.medicare.model.response;

import com.acharyaamrit.medicare.model.Notice;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NoticeResponse {

    @SerializedName("notice")
    private List<Notice> notice;
    public NoticeResponse(List<Notice> notice) {
        this.notice = notice;
    }

    public NoticeResponse() {
    }

    public List<Notice> getNotice() {
        return notice;
    }

    public void setNotice(List<Notice> notice) {
        this.notice = notice;
    }
}
