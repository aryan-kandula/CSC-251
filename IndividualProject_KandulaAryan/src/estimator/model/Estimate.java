package estimator.model;

public class Estimate {
    private static int counter = 1000;

    private int         estimateId;
    private ProjectInfo projectInfo;
    private ConcretePad concretePad;
    private ChainLinkFence chainLinkFence; // null if not included

    public Estimate(ProjectInfo projectInfo, ConcretePad concretePad, ChainLinkFence chainLinkFence) {
        this.estimateId    = ++counter;
        this.projectInfo   = projectInfo;
        this.concretePad   = concretePad;
        this.chainLinkFence = chainLinkFence;
    }

    public int           getEstimateId()     { return estimateId; }
    public ProjectInfo   getProjectInfo()    { return projectInfo; }
    public ConcretePad   getConcretePad()    { return concretePad; }
    public ChainLinkFence getChainLinkFence(){ return chainLinkFence; }
    public boolean hasFencing()             { return chainLinkFence != null; }

    public double getProjectGrandTotal() {
        double total = concretePad.getPadGrandTotal();
        if (hasFencing()) total += chainLinkFence.getFenceGrandTotal();
        return total;
    }
}
