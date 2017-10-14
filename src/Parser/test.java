package Parser;

public class test {
    public static void main(String[] args) {
        new test().run();
    }
    public void run(){
        datalogProgramParser parserTest=new datalogProgramParser();
        String formular=parserTest.createSample();
        System.out.println(parserTest.parser(formular).getIdb());
        parserTest.parser(formular).printEdb();
    }
}
