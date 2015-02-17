package dummy;
import java.io.*;
public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
        InputStream stream = Test.class.getResourceAsStream("./newTest.txt");
        System.out.println(stream != null);
        stream = Test.class.getClassLoader()
            .getResourceAsStream("/newTest1.txt");
        System.out.println(stream != null);
	}

}
