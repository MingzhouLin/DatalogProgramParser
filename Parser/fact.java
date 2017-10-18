package Parser;

import java.util.ArrayList;
import java.util.LinkedList;

public class fact {
    private ArrayList<path> pathFact=new ArrayList<>();
    private String factName;

    public fact() {
    }

    public ArrayList<path> getPathFact() {

        return pathFact;
    }

    public void setPathFact(path pathFact) {
        this.pathFact.add(pathFact);
    }

    public String getFactName() {
        return factName;
    }

    public void setFactName(String factName) {
        this.factName = factName;
    }

    public fact(ArrayList<path> pathFact, String factName) {

        this.pathFact = pathFact;
        this.factName = factName;
    }
}
