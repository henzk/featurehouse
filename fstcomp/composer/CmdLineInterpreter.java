package composer;

import java.io.File;

import de.ovgu.cide.fstgen.ast.CommandLineParameterHelper;

public class CmdLineInterpreter {

	public static final String INPUT_OPTION_EQUATIONFILE = "--expression";

	public static final String INPUT_OPTION_AHEAD_EQUATION_FILE = "--ahead";

	public static final String INPUT_OPTION_BASE_DIRECTORY = "--base-directory";

	public static final String INPUT_OPTION_SHOW_GUI = "--gui";

	public static final String INPUT_OPTION_SHOW_XML = "--xml";

	public static final String INPUT_OPTION_SHOW_SUM = "--sum";

	public static final String INPUT_OPTION_SHOW_FST = "--fst";

	public static final String INPUT_OPTION_FILE_OUTPUT = "--write";

	public static final String INPUT_OPTION_OUTPUT_DIRECTORY = "--output-directory";

	public static final String INPUT_OPTION_CONTRACT_STYLE = "--contract-style";

	public static final String INPUT_OPTION_RESOLVE_REFERENCES = "--resolve-references";

	public static final String INPUT_OPTION_HELP = "--help";

	public static final String INPUT_OPTION_COUNT = "--count";

	public static final String INPUT_OPTION_LIFTING = "--lift";
	
	public static final String INPUT_OPTION_ANNOTATION = "--featureAnnotationJava";

	public static Configuration parseCmdLineArguments(String[] args) {
		Configuration conf = new Configuration();
		boolean jml = false;
		boolean errorOccured = false;
		if (args != null && args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals(INPUT_OPTION_EQUATIONFILE)) {
					i++;
					if (i < args.length) {
						conf.equationFileName = args[i];
						if (!conf.isBaseDirectoryName)
							conf.equationBaseDirectoryName = getDirectoryName(new File(
									conf.equationFileName)) + File.separator;
						conf.equationFileName = conf.equationFileName.replace("\\",
								File.separator);
						conf.equationFileName = conf.equationFileName.replace("/",
								File.separator);
					} else {
						System.out.println("Error occured option: "
								+ INPUT_OPTION_EQUATIONFILE);
						errorOccured = true;
					}
				} else if (args[i].equals(INPUT_OPTION_BASE_DIRECTORY)) {
					i++;
					if (i < args.length) {
						conf.equationBaseDirectoryName = args[i];
						conf.equationBaseDirectoryName = conf.equationBaseDirectoryName
								.replace("\\", File.separator);
						conf.equationBaseDirectoryName = conf.equationBaseDirectoryName
								.replace("/", File.separator);
						conf.isBaseDirectoryName = true;
					} else {
						System.out.println("Error occured option: "
								+ INPUT_OPTION_BASE_DIRECTORY);
						errorOccured = true;
					}
				} else if (args[i].equals(INPUT_OPTION_OUTPUT_DIRECTORY)) {
					i++;
					if (i < args.length) {
						conf.outputDirectoryName = args[i];
					} else {
						System.out.println("Error occured option: "
								+ INPUT_OPTION_OUTPUT_DIRECTORY);
						errorOccured = true;
					}
				} else if (args[i].equals(INPUT_OPTION_COUNT)) {
					conf.isCount = true;
				} else if (args[i].equals(INPUT_OPTION_FILE_OUTPUT)) {
					conf.fileOutput = true;
				} else if (args[i].equals(INPUT_OPTION_SHOW_GUI)) {
					conf.showGui = true;
				} else if (args[i].equals(INPUT_OPTION_SHOW_XML)) {
					conf.showXML = true;
				} else if (args[i].equals(INPUT_OPTION_SHOW_FST)) {
					conf.showFST = true;
				} else if (args[i].equals(INPUT_OPTION_SHOW_SUM)) {
					conf.showSum = true;
				} else if (args[i].equals(INPUT_OPTION_AHEAD_EQUATION_FILE)) {
					conf.isAheadEquationFile = true;
				} else if (args[i].equals(INPUT_OPTION_ANNOTATION)) {
					conf.featureAnnotation = true;
				} else if (args[i].startsWith(INPUT_OPTION_LIFTING)) {
					conf.lifting = true;
					conf.lifting_language = args[i]
							.substring(INPUT_OPTION_LIFTING.length()).trim()
							.toLowerCase();
					if (!(conf.lifting_language.equals("java") || conf.lifting_language
							.equals("c"))) {
						throw new IllegalArgumentException(
								"Lifting requires a language as parameter (e.g. --liftC or --liftJava)");
					}
				} else if (args[i].equals(INPUT_OPTION_RESOLVE_REFERENCES)) {
					System.out.println("The option '"
							+ INPUT_OPTION_RESOLVE_REFERENCES
							+ "' is obsolete.");
				} else if (args[i].equals(INPUT_OPTION_HELP)) {
					printHelp(false);
				} else if (args[i].equals(INPUT_OPTION_CONTRACT_STYLE)) {
					i++;
					conf.contract_style=args[i];
					if (!conf.contract_style.equals("none"))
						jml = true;
					else if (!(conf.contract_style.equals("plain_contracting")
							|| conf.contract_style.equals("explicit_contracting")
							|| conf.contract_style.equals("contract_overriding")
							|| conf.contract_style.equals("consecutive_contracting") || conf.contract_style
								.equals("none"))) {
						throw new IllegalArgumentException(
								"Unknown contract style. Please choose from: plain_contracting, explicit_contracting, consecutive_contracting");
					}
				}

				else {
					errorOccured = true;
				}
			}
		} else {
			errorOccured = true;
		}

		CommandLineParameterHelper.setJML(jml);//FIXME

		if (errorOccured) {
			printHelp(errorOccured);
		}
		return conf;
	}

	private static void printHelp(boolean errorOccured) {
		if (errorOccured) {
			System.out.println("Incorrect command line parameters!");
		}
		System.out.println("Use `java -jar FeatureHouse.jar "
				+ INPUT_OPTION_EQUATIONFILE + " <file name> ["
				+ INPUT_OPTION_BASE_DIRECTORY + " <directory name>]'");
		System.out
				.println("The option `"
						+ INPUT_OPTION_EQUATIONFILE
						+ "' defines the name of the file that lists the input features/components.");
		System.out
				.println("The option `"
						+ INPUT_OPTION_BASE_DIRECTORY
						+ "' defines the working directory, which is the search path for the input features/components.");
		System.out.println("The option `" + INPUT_OPTION_LIFTING
				+ "' can currently only be used with C Code. It composes the "
				+ "sources in a way that allows feature selection at runtime");

	}

	private static String getDirectoryName(File file) {
		String result = "";
		if (file.isDirectory()) {
			result = file.getPath();
		} else {
			result = file.getAbsoluteFile().getParentFile().getPath();
		}
		return result;
	}
}
