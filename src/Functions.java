import java.io.File;
import java.util.List;

public class Functions implements IFunctions {
	private final ISubFunctions sf = new SubFunctions();
	private final IParseFunctions pf = new ParseFunctions();

	@Override
	public String run(File filename) {
		return execute(sf.readFile(filename));
	}

	@Override
	public String run(String filename) {
		return execute(sf.readFile(filename));
	}

	public String execute(List<String> lines) {
		StringBuffer sb = new StringBuffer("");
		for (int k = 0; k < lines.size(); k++) {
			if (0 == lines.get(k).length())
				continue;
			if (pf.make(lines.get(k), sb, k + 1) == 0) {
				//System.out.println(/* lines.get(k)+"\n"+ */sb);
				//sb = new StringBuffer("");
			}
		}

		return sb.toString();
	}

}
