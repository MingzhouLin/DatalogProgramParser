package Parser;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class datalogProgramParser {
    private ArrayList<fact> facts = new ArrayList<>();
    private LinkedList<rule> rules = new LinkedList<>();

    public static void main(String[] args) {
        new datalogProgramParser().run();
    }

    public void run() {
        fileReaderAndParse();
        for (fact f:facts) {
            System.out.print(f.getFunctionName() + "(" + f.getStart() + "," + f.getEnd()+ ") ");
        }
        System.out.println();
        for (rule r : rules) {
            r.getIdb().printFuction();
            System.out.println();
            for (function f : r.getEdb()) {
                f.printFuction();
            }
            System.out.println();
        }
        Naive();
        for (fact f:facts) {
            System.out.print(f.getFunctionName() + "(" + f.getStart() + "," + f.getEnd()+ ") ");
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
            String pattern1 = "(^[a-z]+)(\\([^\\(\\)]+\\)\\.$)";
            String pattern2 = "([a-z]+\\([^\\(\\)]+\\)){1}(:-)(([a-z]+\\([^\\(\\)]+\\),)*([a-z]+\\([^\\(\\)]+\\)\\.)$)";
            Pattern r1= Pattern.compile(pattern1);
            Pattern r2= Pattern.compile(pattern2);
            boolean finishFacts=false;
            while ((line = br.readLine()) != null) {
                Matcher m1 = r1.matcher(line);
                Matcher m2 = r2.matcher(line);
                if (m1.find()) {
                    if (finishFacts==false) {
                            String content = m1.group(2);
                            fact newFact = new fact(m1.group(1), content.substring(1, 2), content.substring(3, 4));
                            facts.add(newFact);
                    }else {
                        System.out.println("Wrong: sequence is error.");
                    }
                } else if (m2.find()){
                    finishFacts=true;
                    rule newRule = parser(line);
                    rules.add(newRule);
                }else {
                    System.out.println("Wrong: format is error.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public rule parser(String formula) {
        int index = formula.indexOf(":-");
        String cause = "", idb = "";
        cause = formula.substring(index + 2);
        idb = formula.substring(0, index);
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

    public void Naive(){
        boolean judge=true;
        do {
            ArrayList<fact> newFacts=new ArrayList<>();
            ArrayList<function> substitution=new ArrayList<>();
            for (rule r:rules) {
                int i=0;
                inference(i, r, newFacts, substitution);
            }
            for (fact p:newFacts){
                System.out.print(p.getFunctionName()+"("+p.getStart()+","+p.getEnd()+") ");
            }
            System.out.println();
            if (newFacts.size()==0){
                judge=false;
            }
            for(fact p:newFacts){
                facts.add(p);
            }
        }while (judge);
    }

    public void inference(int i,rule r,ArrayList<fact> newPath,ArrayList<function> substitution) {

//        for (rule r : rules) {
//            for (int k = 0; k <facts.size(); k++) {
//                fact p=facts.get(k);
//                for (int i = 0; i < facts.size() - r.getEdb().size() + 2; i++) {
//                    ArrayList<function> substitution = new ArrayList<>();
//                    for (int j = 0; j < r.getEdb().size(); j++) {
//                        function f = new function();
//                        f.setVariable(r.getEdb().get(j).getVariable());
//                        if (r.getEdb().get(j).getFunctionName().equals(facts.get())) {
//                            if (j==0){
//                                substitution.add(substitute(f,p));
//                            }else {
//                                substitution.add(substitute(f, facts.get(i + j-1)));
//                            }
//                        }
//        }
        if (i>=r.getEdb().size()){
            fact judgeDeposit=inferenceStep(substitution,r);
                    if (!judgeDeposit.getStart().equals("")) {
                        boolean repetition=false;
                        for (fact check:facts){
                            if (check.getFunctionName().equals(judgeDeposit.getFunctionName())&&check.getStart().equals(judgeDeposit.getStart())&&check.getEnd().equals(judgeDeposit.getEnd())){
                                repetition=true;
                                break;
                            }
                        }
                        for (fact check:newPath){
                            if (check.getFunctionName().equals(judgeDeposit.getFunctionName())&&check.getStart().equals(judgeDeposit.getStart())&&check.getEnd().equals(judgeDeposit.getEnd())){
                                repetition=true;
                                break;
                            }
                        }
                        if (!repetition){
                            newPath.add(judgeDeposit);
                        }
                    }
        }else {
            for (fact f:facts){
                if (r.getEdb().get(i).getFunctionName().equals(f.getFunctionName())) {
                    function func = new function();
                    func.setVariable(r.getEdb().get(i).getVariable());
                    substitution.add(substitute(func, f));
                    inference(i + 1, r, newPath, substitution);
                }
            }
        }
        if (substitution.size()>0) {
            substitution.remove(substitution.size() - 1);
        }
//                }
//            }
//        }
    }

    private function substitute(function oneOfEdb, fact p) {
        String s[] = new String[2];
        s[0] = oneOfEdb.getVariable()[0] + p.getStart();
        s[1] = oneOfEdb.getVariable()[1] + p.getEnd();
        oneOfEdb.setVariable(s);
        return oneOfEdb;
    }


    private fact inferenceStep(ArrayList<function> substitution, rule r) {
        boolean newFact =true;
        fact newPath=new fact();
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
            newPath = new fact(r.getIdb().getFunctionName(),variable.get(0), variable.get(1));
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