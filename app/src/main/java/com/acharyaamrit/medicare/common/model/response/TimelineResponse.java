package com.acharyaamrit.medicare.common.model.response;

import com.acharyaamrit.medicare.common.model.TimelineItem;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TimelineResponse {

    @SerializedName("Timeline")
    List<TimelineItem> timelineItemsList;

    public TimelineResponse() {
    }

    public TimelineResponse(List<TimelineItem> timelineItemsList) {
        this.timelineItemsList = timelineItemsList;
    }

    public List<TimelineItem> getTimelineItemsList() {
        return timelineItemsList;
    }

    public void setTimelineItemsList(List<TimelineItem> timelineItemsList) {
        this.timelineItemsList = timelineItemsList;
    }
}
