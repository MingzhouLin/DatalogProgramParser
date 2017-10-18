package Parser;

public class path {
    private String functionName;
    private String point1;
    private String point2;

    public path(String functionName, String point1, String point2) {
        this.functionName = functionName;
        this.point1 = point1;
        this.point2 = point2;
    }

    public path(String point1, String point2) {
        this.point1 = point1;
        this.point2 = point2;
    }

    public String getFunctionName() {

        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getPoint1() {
        return point1;
    }

    public void setPoint1(String point1) {
        this.point1 = point1;
    }

    public String getPoint2() {
        return point2;
    }

    public void setPoint2(String point2) {
        this.point2 = point2;
    }

    public path() {

    }

}
