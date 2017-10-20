package Parser;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class datalogProgramParser {
    private fact factPath = new fact();
    private LinkedList<rule> rules = new LinkedList<>();

    public static void main(String[] args) {
        new datalogProgramParser().run();
    }

    public void run() {
        fileReaderAndParse();
        for (path a : factPath.getPathFact()) {
            System.out.print(a.getFunctionName() + "(" + a.getPoint1() + "," + a.getPoint2() + ") ");
        }
        System.out.println();
        for (rule r : rules) {
            r.getIdb().printFuction();
            System.out.println();
            for (function f : r.getEdb()) {
                f.printFuction();
            }
        }
        semiNaive(rules,factPath);
        for (path a : factPath.getPathFact()) {
            System.out.print(a.getFunctionName() + "(" + a.getPoint1() + "," + a.getPoint2() + ") ");
        }
    }

    public datalogProgramParser() {
    }

    public String createSample() {
        String formula = "path(x,z):-path(x,y),path(y,z)";
        return formula;
    }

    private void fileReaderAndParse() {
        try {
            String path = "/javaCodeDesign/Comp6591Project/src/Path.txt";
            File filename = new File(path);
            InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
            BufferedReader br = new BufferedReader(reader);
            String line = "";
            String pattern = "([a-z]+)(\\=)(\\{)([a-z]+\\(.*\\))";
            Pattern r = Pattern.compile(pattern);
            while ((line = br.readLine()) != null) {
                Matcher m = r.matcher(line);
                if (m.find()) {
                    if (m.group(1).equals("path")) {
                        String content = m.group(4);
                        String fact[] = content.split("\\.");
                        factPath.setFactName("p");
                        for (String s : fact) {
                            path newFact = new path(s.substring(0, 1), s.substring(2, 3), s.substring(4, 5));
                            factPath.setPathFact(newFact);
                        }
                    }
                } else {
                    rule newRule = parser(line);
                    rules.add(newRule);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public rule parser(String formula) {
        int index = formula.indexOf(":-");
        String cause = "", idb = "";
        if (index == -1) {
            System.out.println("err:lack :-");
        } else {
            cause = formula.substring(index + 2);
            idb = formula.substring(0, index);
        }
        rule clause = new rule();
        String pattern = "([a-z]+)(\\()([^\\(\\)]+)(\\))";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(cause);
        while (m.find()) {
            function edb = new function();
            edb.setFunctionName(m.group(1));
            edb.setVariable(m.group(3).split(","));
            clause.setEdb(edb);
        }
        m = r.matcher(idb);
        int count = 0;
        while (m.find()) {
            count++;
            if (count > 1) {
                System.out.println("warning:It's contigent");
            }
            function result = new function();
            result.setFunctionName(m.group(1));
            result.setVariable(m.group(3).split(","));
            clause.setIdb(result);
        }
        if (!balance(clause)) {
            System.out.println("warning:Some variable in IDB can't be found in EDB");
        }
        return clause;
    }

    private boolean balance(rule r) {
        boolean judge = true;
        for (String s : r.getIdb().getVariable()) {
            boolean find = false;
            outer:
            for (function f : r.getEdb()) {
                for (String s1 : f.getVariable()) {
                    if (s1.equals(s)) {
                        find = true;
                        break outer;
                    }
                }
            }
            if (find == false) {
                judge = false;
            }
        }
        return judge;
    }

    public void semiNaive(LinkedList<rule> rules,fact facts){
        boolean judge=true;
        do {
            ArrayList<path> newFacts;
            newFacts = inference(rules, facts);
            for (path p:newFacts){
                System.out.print(p.getFunctionName()+"("+p.getPoint1()+","+p.getPoint2()+") ");
            }
            System.out.println();
            if (newFacts.size()==0){
                judge=false;
            }
            for(path p:newFacts){
                facts.setPathFact(p);
            }
        }while (judge);
    }

    public ArrayList<path> inference(LinkedList<rule> rules, fact facts) {
        ArrayList<path> newPath=new ArrayList<>();
        for (rule r : rules) {
            for (int k = 0; k <facts.getPathFact().size(); k++) {
                path p=facts.getPathFact().get(k);
                for (int i = 0; i < facts.getPathFact().size() - r.getEdb().size() + 2; i++) {
                    ArrayList<function> substitution = new ArrayList<>();
                    for (int j = 0; j < r.getEdb().size(); j++) {
                        function f = new function();
                        f.setVariable(r.getEdb().get(j).getVariable());
                        if (r.getEdb().get(j).getFunctionName().equals(facts.getFactName())) {
                            if (j==0){
                                substitution.add(substitute(f,p));
                            }else {
                                substitution.add(substitute(f, facts.getPathFact().get(i + j-1)));
                            }
                        }
                    }
                    path judgeDeposit=inferenceStep(substitution,r);
                    if (judgeDeposit.getPoint1()!=null) {
                        boolean repetition=false;
                        for (path check:facts.getPathFact()){
                            if (check.getPoint1().equals(judgeDeposit.getPoint1())&&check.getPoint2().equals(judgeDeposit.getPoint2())){
                                repetition=true;
                                break;
                            }
                        }
                        for (path check:newPath){
                            if (check.getPoint1().equals(judgeDeposit.getPoint1())&&check.getPoint2().equals(judgeDeposit.getPoint2())){
                                repetition=true;
                                break;
                            }
                        }
                        if (!repetition){
                            newPath.add(judgeDeposit);
                        }
                    }
                }
            }
        }
        return newPath;
    }

    private function substitute(function oneOfEdb, path p) {
        String s[] = new String[2];
        s[0] = oneOfEdb.getVariable()[0] + p.getPoint1();
        s[1] = oneOfEdb.getVariable()[1] + p.getPoint2();
        oneOfEdb.setVariable(s);
        return oneOfEdb;
    }


    private path inferenceStep(ArrayList<function> substitution, rule r) {
        boolean newFact =true;
        path newPath=new path();
        ArrayList<String> variable=new ArrayList<>();
        outer:
        for (int i = 0; i < substitution.size(); i++) {
            ArrayList<String> remember = new ArrayList<>();
            for (String v : substitution.get(i).getVariable()) {
                String firstLetter = v.substring(0, 1);
                if (find(remember, firstLetter)) {
                    continue;
                } else {
                    for (int j = 0; j < r.getIdb().getVariable().length; j++) {
                        String s = r.getIdb().getVariable()[j];
                        if (s.equals(firstLetter)) {
                            variable.add(v.substring(1));
                            break;
                        }
                    }
                    for (int j = i; j < substitution.size(); j++) {
                        for (String s : substitution.get(j).getVariable()) {
                            if (firstLetter.equals(s.substring(0, 1))) {
                                if (!v.equals(s)) {
                                    newFact = false;
                                    break outer;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (newFact) {
            newPath = new path(r.getIdb().getFunctionName(),variable.get(0), variable.get(1));
        }
        return newPath;
    }

    private boolean find(ArrayList<String> remember, String v) {
        boolean judge = false;
        for (String s : remember) {
            if (s.equals(v)) {
                judge = true;
            }
        }
        return judge;
    }
}