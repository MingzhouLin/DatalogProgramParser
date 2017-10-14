package example;

import java.util.LinkedList;

public class rule {
    private String idb;
    private LinkedList<String> edb=new LinkedList<>();

    public String getIdb() {
        return idb;
    }

    public void setIdb(String idb) {
        this.idb = idb;
    }

    public LinkedList<String> getEdb() {
        return edb;
    }
    public void printEdb(){
        for (String s:edb){
            System.out.print(s+" ");
        }
        System.out.println();
    }
    public void setEdb(String function) {
        edb.add(function);
    }

    public rule() {

    }
}
