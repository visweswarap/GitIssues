package com.alienvault.resp;


import java.util.List;

public class GitIssuesResp {

    private List<Issue> issues;

    private TopPerDay top_day;

    public List<Issue> getIssues() {
        return issues;
    }

    public void setIssues(List<Issue> issues) {
        this.issues = issues;
    }

    public TopPerDay getTop_day() {
        return top_day;
    }

    public void setTop_day(TopPerDay top_day) {
        this.top_day = top_day;
    }
}
