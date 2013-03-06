import java.io.PrintStream;

public class For {
    private final static IFunctions func = new Functions();
			/**
			 * @param args
			 */
	public static void main(String[] args) {
        String consoleEncoding = System.getProperty("consoleEncoding");
        if (consoleEncoding != null) {
            try {
                System.setOut(new PrintStream(System.out, true, consoleEncoding));
            } catch (java.io.UnsupportedEncodingException ex) {
                System.err.println("Unsupported encoding set for console: "+consoleEncoding);
            }
        }

		for (int i = 0; i < args.length; i++) {
            String prm = args[i];
            if(prm.equals("-run")){}
            switch (prm) {
			case "-run":
				System.out.println(func.run(args[i + 1]));
                break;
            case "-gui":
                MainView view = new MainView();
                break;
			case "-help":
				System.out.println("use -run <FILE> to execute solution\n-info to overview project\n-about to see information about programm ");
				break;
            case "":
                System.out.println("use -run <FILE> to execute solution\n-info to overview project\n-about to see information about programm ");
                break;
			case "-info":
				System.out.println("Statement FOR of C++ language");
				break;
			case "-about":
				System.out
						.println("The System Software project\n Statement FOR of C++ language\nDesigned and created by Derevyanko Denis\nAVT-914\n2012");
				break;
			}
		}
	}

}
