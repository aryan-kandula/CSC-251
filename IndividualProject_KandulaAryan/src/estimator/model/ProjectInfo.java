package estimator.model;

public class ProjectInfo {
    private String projectName;
    private String clientName;
    private String location;
    private String estimateDate;
    private String estimatorName;
    private String notes;

    public ProjectInfo(String projectName, String clientName, String location,
                       String estimateDate, String estimatorName, String notes) {
        this.projectName   = projectName;
        this.clientName    = clientName;
        this.location      = location;
        this.estimateDate  = estimateDate;
        this.estimatorName = estimatorName;
        this.notes         = notes;
    }

    public String getProjectName()   { return projectName; }
    public String getClientName()    { return clientName; }
    public String getLocation()      { return location; }
    public String getEstimateDate()  { return estimateDate; }
    public String getEstimatorName() { return estimatorName; }
    public String getNotes()         { return notes; }
}
