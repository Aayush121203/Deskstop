package org.example.dao;

public class RcaDetails {
    private String rootCause;
    private String permanentFix;
    private String preventiveMeasures;

    public String getRootCause() { return rootCause; }
    public void setRootCause(String rootCause) { this.rootCause = rootCause; }

    public String getPermanentFix() { return permanentFix; }
    public void setPermanentFix(String permanentFix) { this.permanentFix = permanentFix; }

    public String getPreventiveMeasures() { return preventiveMeasures; }
    public void setPreventiveMeasures(String preventiveMeasures) { this.preventiveMeasures = preventiveMeasures; }
}
