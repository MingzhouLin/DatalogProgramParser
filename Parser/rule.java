package Parser;

import java.util.ArrayList;
import java.util.LinkedList;

public class rule {
    private function idb;
    private ArrayList<function> edbs=new ArrayList<>();
    private ArrayList<function> buildIn=new ArrayList<>();

    public ArrayList<function> getBuildIn() {
        return buildIn;
    }

    public void setBuildIn(function func) {
        this.buildIn.add(func);
    }

    public function getIdb() {
        return idb;
    }

    public ArrayList<function> getEdb() {
        return edbs;
    }

    public void setEdb(function edb) {
        this.edbs.add(edb);
    }

    public void setIdb(function idb) {
        this.idb = idb;
    }

    public rule() {

    }
}
