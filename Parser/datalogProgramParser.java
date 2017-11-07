package Parser;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
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
        long start=System.currentTimeMillis();
        semiNaive();
        long end=System.currentTimeMillis();
        System.out.println((end-start)+"ms");
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
            String pattern2 = "([a-z]+\\([^\\(\\)]+\\)){1}(:-)((\\w+\\([^\\(\\)]+\\),)*(\\w+\\([^\\(\\)]+\\)\\.)$)";
            Pattern r1= Pattern.compile(pattern1);
            Pattern r2= Pattern.compile(pattern2);
            boolean finishFacts=false;
            while ((line = br.readLine()) != null) {
                Matcher m1 = r1.matcher(line);
                Matcher m2 = r2.matcher(line);
                //facts
                if (m1.find()) {
                    if (finishFacts==false) {
                            String content = m1.group(2);
                            String pattern3="(\\()((\\w+,)+(\\w))(\\))";
                            Pattern r3=Pattern.compile(pattern3);
                            Matcher m3=r3.matcher(content);
                            if (m3.find()) {
                                String[] constent=m3.group(2).split(",");
                                for (String s:constent){
                                    if (isUpperCase(s)){
                                        System.out.println("Wrong:variable in facts");
                                        break;
                                    }
                                }
                                fact newFact = new fact(m1.group(1), content.substring(1, 2), content.substring(3, 4));
                                facts.add(newFact);
                            }else {
                                System.out.println("Wrong:facts' format is wrong");
                            }
                    }else {
                        System.out.println("Wrong: sequence is error.");
                    }
                } else if (m2.find()){   //rules
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

    public static boolean isUpperCase(String word) {
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (!Character.isUpperCase(c)) {
                return false;
            }
        }
        return true;
    }

    public rule parser(String formula) {
        int index = formula.indexOf(":-");
        String cause = "", idb = "";
        cause = formula.substring(index + 2);
        idb = formula.substring(0, index);
        rule clause = new rule();
        String pattern = "(\\w+)(\\()([^\\(\\)]+)(\\))";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(cause);
        //Put body into rule.
        while (m.find()) {
            boolean afterEdb=false;
            if (m.group(1).equals("greater")||m.group(1).equals("less")||m.group(1).equals("equal")){
                function buildIn=new function();
                buildIn.setFunctionName(m.group(1));
                buildIn.setVariable(m.group(3).split(","));
                clause.setBuildIn(buildIn);
                afterEdb=true;
            }else {
                if (afterEdb){
                    System.out.println("error:The sequence of body is wrong!");
                }
                function edb = new function();
                edb.setFunctionName(m.group(1));
                edb.setVariable(m.group(3).split(","));
                clause.setEdb(edb);
            }
        }
        //Check if Variables in buildIn appear in edb.
        if (!balance(clause.getEdb(),clause.getBuildIn())){
            System.out.println("error:Not any Variable in buildIn appears in edb");
        }
        //Put head into rule.
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
        //Check if Variables in body appear in head.
        if (!balance(clause)) {
            System.out.println("warning:Some variable in IDB can't be found in EDB");
        }
        return clause;
    }

    private boolean balance(ArrayList<function> edb,ArrayList<function> buildIn){
        Pattern p=Pattern.compile("[A-Z]+");

        for (function f:buildIn){
            for (String s:f.getVariable()){
                Matcher m=p.matcher(s);
                if (m.find()) {
                    if (!findInEdb(edb, s)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean findInEdb(ArrayList<function> edb,String var){
         for (function f:edb){
             for (String s:f.getVariable()){
                 if (s.equals(var)){
                     return true;
                 }
             }
         }
         return false;
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

    // Naive engine.
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
    }

    //Semi-Naive engine.
    public void semiNaive(){
        boolean judge=true;
        ArrayList<fact> oldNewFacts=new ArrayList<>();
        do {
            ArrayList<fact> newFacts=new ArrayList<>();
            ArrayList<function> substitution=new ArrayList<>();
            int count=0;
            for (rule r:rules) {
                int i=0;
                semiInference(i, r, newFacts, substitution,oldNewFacts);
                if (newFacts.size()==0){
                    count++;
                }
            }
            for (int i = 0; i <count; i++) {
                rules.removeFirst();
            }
            oldNewFacts.clear();
            for (fact p:newFacts){
                oldNewFacts.add(p);
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
//        for (ArrayList<function> arr:memory){
//            for (function f:arr){
//                f.printFuction();
//            }
//            System.out.println();
//        }
    }

    public void semiInference(int i,rule r,ArrayList<fact> newPath,ArrayList<function> substitution,ArrayList<fact> oldNewFacts) {
        if (i>=r.getEdb().size()){
                fact judgeDeposit = inferenceStep(substitution, r);
                if (!judgeDeposit.getStart().equals("")) {
                    boolean repetition = false;
                    for (fact check : facts) {
                        if (check.getFunctionName().equals(judgeDeposit.getFunctionName()) && check.getStart().equals(judgeDeposit.getStart()) && check.getEnd().equals(judgeDeposit.getEnd())) {
                            repetition = true;
                            break;
                        }
                    }
                    for (fact check : newPath) {
                        if (check.getFunctionName().equals(judgeDeposit.getFunctionName()) && check.getStart().equals(judgeDeposit.getStart()) && check.getEnd().equals(judgeDeposit.getEnd())) {
                            repetition = true;
                            break;
                        }
                    }
                    if (!repetition) {
                        newPath.add(judgeDeposit);
                    }
                }
        }else {
            if (i==0) {
                for (fact f : facts) {
                    if (r.getEdb().get(i).getFunctionName().equals(f.getFunctionName())) {
                        function func = new function();
                        func.setVariable(r.getEdb().get(i).getVariable());
                        substitution.add(substitute(func, f));
                        semiInference(i + 1, r, newPath, substitution, oldNewFacts);
                    }
                }
            }else {
                boolean judge=false;
                for (fact f:oldNewFacts){
                    if (substitution.get(0).getFunctionName().equals(f.getFunctionName())){
                        if (substitution.get(0).getVariable()[0].equals(f.getStart())&&substitution.get(0).getVariable()[1].equals(f.getEnd())){
                            judge=true;
                            break;
                        }
                    }
                }
                if (judge){
                    for (fact f : facts) {
                        if (r.getEdb().get(i).getFunctionName().equals(f.getFunctionName())) {
                            function func = new function();
                            func.setVariable(r.getEdb().get(i).getVariable());
                            substitution.add(substitute(func, f));
                            semiInference(i + 1, r, newPath, substitution, oldNewFacts);
                        }
                    }
                }else {
                    for (fact f:oldNewFacts){
                        if (r.getEdb().get(i).getFunctionName().equals(f.getFunctionName())) {
                            function func = new function();
                            func.setVariable(r.getEdb().get(i).getVariable());
                            substitution.add(substitute(func, f));
                            semiInference(i + 1, r, newPath, substitution, oldNewFacts);
                        }
                    }
                }
            }
        }
        if (substitution.size()>0) {
            substitution.remove(substitution.size() - 1);
        }
    }

//    private boolean findInMemory(ArrayList<function> substitution,LinkedList<ArrayList<function>> memory){
//        boolean judge=false;
//        for (ArrayList<function> m:memory){
//            if (substitution.size()==m.size()) {
//                boolean allTure=true;
//                outer:
//                for (int i = 0; i < m.size(); i++) {
//                    for (int j = 0; j < m.get(i).getVariable().length; j++) {
//                        if (!substitution.get(i).getVariable()[j].equals(m.get(i).getVariable()[j])) {
//                            allTure=false;
//                            break outer;
//                        }
//                    }
//                }
//                if (allTure){
//                    judge=true;
//                    break;
//                }
//            }
//        }
//        return judge;
//    }

    private function substitute(function oneOfEdb, fact p) {
        String s[] = new String[2];
        oneOfEdb.setFunctionName(p.getFunctionName());
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
        if (newFact&&satisfyBuildIn(substitution,r.getBuildIn())) {
            newPath = new fact(r.getIdb().getFunctionName(),variable.get(0), variable.get(1));
        }
        return newPath;
    }

    private boolean satisfyBuildIn(ArrayList<function> substitution,ArrayList<function> buildIn){
        Pattern p1=Pattern.compile("[A-Z]+");
        Pattern p2=Pattern.compile("[1-9]+");
        for (function f:buildIn){
            function func=new function();
            func.setFunctionName(f.getFunctionName());
            func.setVariable(new String[3]);
            for (int i = 0; i <f.getVariable().length; i++){
                Matcher m1=p1.matcher(f.getVariable()[i]);
                Matcher m2=p2.matcher((f.getVariable()[i]));
                if (m1.find()){
                    func.setOneOfVariable(i,findVariableAndSubstitute(f.getVariable()[i],substitution));
                }else if (m2.find()){
                    func.setOneOfVariable(i,f.getVariable()[i]);
                }else {
                    System.out.println("error:BuildIn format is wrong!");
                }
            }
            if (!logicalJudge(func)){
                return false;
            }
        }
        return true;
    }

    private boolean logicalJudge(function f){
        HashMap<String,Integer> map=new HashMap<>();
        map.put("greater",1);
        map.put("less",2);
        map.put("equal",3);
        int i=map.get(f.getFunctionName());
        Pattern p1=Pattern.compile("[A-Z]+");
        Matcher m1=p1.matcher(f.getVariable()[0]);
        Matcher m2=p1.matcher(f.getVariable()[1]);
        boolean first=m1.find();
        boolean second=m2.find();
        if (!first&&!second){
            System.out.println("error:The Build-in makes no sense");
        }else {
            switch (i) {
                case 1: {
                    if (first&&second) {
                        if (Integer.parseInt(f.getVariable()[0].substring(1)) > Integer.parseInt(f.getVariable()[1].substring(1))) {
                            return true;
                        } else return false;
                    } else if (first && !second) {
                        if (Integer.parseInt(f.getVariable()[0].substring(1)) > Integer.parseInt(f.getVariable()[1])) {
                            return true;
                        } else return false;
                    } else if (!first && second) {
                        if (Integer.parseInt(f.getVariable()[0]) > Integer.parseInt(f.getVariable()[1].substring(1))) {
                            return true;
                        } else return false;
                    }
                }
                case 2: {
                    if (first && second) {
                        if (Integer.parseInt(f.getVariable()[0].substring(1)) < Integer.parseInt(f.getVariable()[1].substring(1))) {
                            return true;
                        } else return false;
                    } else if (first && !second) {
                        if (Integer.parseInt(f.getVariable()[0].substring(1)) < Integer.parseInt(f.getVariable()[1])) {
                            return true;
                        } else return false;
                    } else if (!first && second) {
                        if (Integer.parseInt(f.getVariable()[0]) < Integer.parseInt(f.getVariable()[1].substring(1))) {
                            return true;
                        } else return false;
                    }
                }
                case 3: {
                    if (first&& second) {
                        if (Integer.parseInt(f.getVariable()[0].substring(1)) == Integer.parseInt(f.getVariable()[1].substring(1))) {
                            return true;
                        } else return false;
                    } else if (first && !second) {
                        if (Integer.parseInt(f.getVariable()[0].substring(1)) == Integer.parseInt(f.getVariable()[1])) {
                            return true;
                        } else return false;
                    } else if (!first && second) {
                        if (Integer.parseInt(f.getVariable()[0]) == Integer.parseInt(f.getVariable()[1].substring(1))) {
                            return true;
                        } else return false;
                    }
                }
            }
        }
        return false;
    }

    private String findVariableAndSubstitute(String s,ArrayList<function> substitution){
        outer:
        for (function f:substitution){
            for (String str:f.getVariable()){
                if (s.equals(str.substring(0,1))){
                    return str;
                }
            }
        }
        return "";
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