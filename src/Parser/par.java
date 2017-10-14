package Parser;

public class par {
    private String parent;
    private String nextG;

    public par() {
    }

    public par(String nextG, String parent) {
        this.parent = parent;
        this.nextG = nextG;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getNextG() {
        return nextG;
    }

    public void setNextG(String nextG) {
        this.nextG = nextG;
    }
}
