package Parser;

public class function {
    private String functionName;
    private String variable[];

    public void setOneOfVariable(int index,String s){
        this.variable[index]=s;
    }

    public function(String functionName, String[] variable) {
        this.functionName = functionName;
        this.variable = variable;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void printFuction(){
            System.out.print(this.functionName + "(");
            for (String s : variable) {
                if (variable[variable.length-1] != s) {
                    System.out.print(s + ",");
                } else {
                    System.out.print(s + ") ");
                }
            }
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String[] getVariable() {
        return variable;
    }

    public void setVariable(String[] variable) {
        this.variable = variable;
    }

    public function() {

    }
}
