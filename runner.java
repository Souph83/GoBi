import java.io.IOException;

public class runner {

	public static void main(String[] args) {
		try {
			Annotation anno = new Annotation(args);
		} catch (IOException e) {
			System.out.println("exeption in runner \n" + e);
		}

	}

}
