import java.io.File;
import java.util.List;

public interface ISubFunctions {
	List<String> readFile(File filename);

	List<String> readFile(String filename);

    String getText(File filename);

	void writeFile(File filename, String text);

}
