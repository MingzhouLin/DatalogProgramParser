package Parser;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class datalogProgramParser {
    private LinkedList<path> factPath =new LinkedList<>();
    private LinkedList<rule> rules=new LinkedList<>();

    public static void main(String[] args) {
        new datalogProgramParser().run();
    }
    public void run(){
        fileReaderAndParse();
        for (path a:factPath){
            System.out.print("("+a.getPoint1()+","+a.getPoint2()+") ");
        }
        System.out.println();
        for (rule r:rules){
            System.out.println("idb:"+r.getIdb());
            r.printEdb();
        }
    }
    public datalogProgramParser() {
    }

    public String createSample(){
        String formula="path(x,z):-path(x,y),path(y,z)";
        return formula;
    }
    
    private void fileReaderAndParse(){
        try {
            String path="/javaCodeDesign/Comp6591Project/src/Path.txt";
            File filename=new File(path);
            InputStreamReader reader=new InputStreamReader(new FileInputStream(filename));
            BufferedReader br=new BufferedReader(reader);
            String line="";
            String pattern="([a-z]+)(\\=)(\\{)(\\(.*\\))";
            Pattern r=Pattern.compile(pattern);
            while((line=br.readLine())!=null){
                Matcher m=r.matcher(line);
                if (m.find()){
                    if (m.group(1).equals("path")){
                        String content=m.group(4);
                        String fact[]=content.split(" ");
                        for (String s:fact){
                            path newFact=new path(s.substring(1,2),s.substring(3,4));
                            factPath.add(newFact);
                        }
                    }
                }else {
                    rule newRule=parser(line);
                    rules.add(newRule);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public rule parser(String formula){
        int index=formula.indexOf(":-");
        String cause="",idb="";
        if (index==-1){
            System.out.println("err:lack :-");
        }else {
            cause=formula.substring(index+2);
            idb=formula.substring(0,index);
        }
        rule clause=new rule();
        String pattern="([a-z]+\\([^\\(\\)]+\\))";
        Pattern r=Pattern.compile(pattern);
        Matcher m=r.matcher(cause);
        while (m.find()){
            clause.setEdb(m.group());
        }
        m=r.matcher(idb);
        int count=0;
        while (m.find()){
            count++;
            if (count>1){
                System.out.println("warning:It's contigent");
            }
            clause.setIdb(m.group());
        }
        if (!balance(clause)){
            System.out.println("warning:Some variable in IDB can't be found in EDB");
        }
        return clause;
    }

    private boolean balance(rule r){
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

    public boolean inference(LinkedList<rule> rules,LinkedList<path> facts ){
        boolean newFacts=false;
        ArrayList<path> cause=new ArrayList<>();
        String pattern="(\\()([a-z]+)(,)([a-z]+)(\\))";
        Pattern p=Pattern.compile(pattern);
        for (rule r:rules){
            for (String s:r.getEdb()){
                String classs=s.substring(0,s.indexOf("("));
                //build rulePath
                if (classs.equals("path")){
                    Matcher m=p.matcher(s);
                    if (m.find()) {
                        path newCause = new path(m.group(2),m.group(4));
                        cause.add(newCause);
                    }
                }
            }
            //inference
            for (int i = 0; i <cause.size(); i++) {

            }
        }
    }
}

