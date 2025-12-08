package org.example.model;

public class Incident {
    private int id;
    private int applicationId;
    private String applicationName;
    private String issueStartTime;
    private String issueEndTime;
    private String problemStatement;
    private String businessImpact;
    private String temporarySolution;
    private String permanentSolution;
    private String status; // OPEN, ASSIGNED, CLOSED
    private int createdBy;
    private String createdByName;
    private Integer assignedTo;
    private String assignedToName;
    private boolean rcaProvided;

    // Constructors
    public Incident() {}

    public Incident(int applicationId, String applicationName, String issueStartTime,
                    String problemStatement, String businessImpact, String temporarySolution,
                    int createdBy) {
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.issueStartTime = issueStartTime;
        this.problemStatement = problemStatement;
        this.businessImpact = businessImpact;
        this.temporarySolution = temporarySolution;
        this.createdBy = createdBy;
        this.status = "OPEN";
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getApplicationId() { return applicationId; }
    public void setApplicationId(int applicationId) { this.applicationId = applicationId; }

    public String getApplicationName() { return applicationName; }
    public void setApplicationName(String applicationName) { this.applicationName = applicationName; }

    public String getIssueStartTime() { return issueStartTime; }
    public void setIssueStartTime(String issueStartTime) { this.issueStartTime = issueStartTime; }

    public String getIssueEndTime() { return issueEndTime; }
    public void setIssueEndTime(String issueEndTime) { this.issueEndTime = issueEndTime; }

    public String getProblemStatement() { return problemStatement; }
    public void setProblemStatement(String problemStatement) { this.problemStatement = problemStatement; }

    public String getBusinessImpact() { return businessImpact; }
    public void setBusinessImpact(String businessImpact) { this.businessImpact = businessImpact; }

    public String getTemporarySolution() { return temporarySolution; }
    public void setTemporarySolution(String temporarySolution) { this.temporarySolution = temporarySolution; }

    public String getPermanentSolution() { return permanentSolution; }
    public void setPermanentSolution(String permanentSolution) { this.permanentSolution = permanentSolution; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }

    public Integer getAssignedTo() { return assignedTo; }
    public void setAssignedTo(Integer assignedTo) { this.assignedTo = assignedTo; }

    public String getAssignedToName() { return assignedToName; }
    public void setAssignedToName(String assignedToName) { this.assignedToName = assignedToName; }

    public boolean isRcaProvided() { return rcaProvided; }
    public void setRcaProvided(boolean rcaProvided) { this.rcaProvided = rcaProvided; }
}
