package Parser;

import java.util.ArrayList;
import java.util.LinkedList;

public class fact {
    private String functionName=new String();
    private String start=new String();
    private String end=new String();

    public fact(String functionName, String start, String end) {
        this.functionName = functionName;
        this.start = start;
        this.end = end;
    }

    public fact() {
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}
