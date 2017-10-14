package example;
import Parser.rule;

import java.util.LinkedList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
public class trysomthing {
    public static void main(String[] args) {
        new trysomthing().run();
    }
    public void run(){
        String formula="path={(1,2) (2,3) (3,4) (4,5) (5,6) (6,7) (7,8) (8,9) (9,10)}";
        String pattern="([a-z]+)(\\=)(\\{)(\\(.*\\))";
        Pattern r=Pattern.compile(pattern);
        Matcher m=r.matcher(formula);
        while (m.find()){
            System.out.println(m.group(4));
        }
        example.rule test=new example.rule();
        test.setIdb("path(x,z)");
        test.setEdb("path(x,y)");
        test.setEdb("path(y,z)");
        System.out.println(balance(test));
    }
    private boolean balance(example.rule r){
        boolean judge=true;
        String t=r.getIdb().substring(r.getIdb().indexOf("(")+1,r.getIdb().indexOf(")"));
        String variable[]=t.split(",");
        for (String s:variable){
            boolean find=false;
            index:for (String v:r.getEdb()){
                t=v.substring(v.indexOf("(")+1,v.indexOf(")"));
                String variableEdb[]=t.split(",");
                for (String vEdb:variableEdb){
                    if (vEdb.equals(s)){
                        find=true;
                        break index;
                    }
                }
            }
            if (find==false){
                judge=false;
            }
        }
        return judge;
    }
}
