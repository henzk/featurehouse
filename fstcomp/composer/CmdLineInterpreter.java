package composer;

import java.io.File;
import java.io.IOException;

import de.ovgu.cide.fstgen.ast.CommandLineParameterHelper;

public class CmdLineInterpreter {

	public static class CmdLineException extends Exception {
		private static final long serialVersionUID = 1;
		public CmdLineException() {
			super();
		}
		public CmdLineException(Exception e) {
			super(e);
		}
		public CmdLineException(String message) {
			super(message);
		}
	}

	public static final String INPUT_OPTION_EQUATIONFILE = "--expression";

	public static final String INPUT_OPTION_AHEAD_EQUATION_FILE = "--ahead";

	public static final String INPUT_OPTION_BASE_DIRECTORY = "--base-directory";

	public static final String INPUT_OPTION_SHOW_GUI = "--gui";

	public static final String INPUT_OPTION_SHOW_XML = "--xml";

	public static final String INPUT_OPTION_SHOW_SUM = "--sum";

	public static final String INPUT_OPTION_SHOW_FST = "--fst";

	public static final String INPUT_OPTION_NO_COMPOSE = "--no-compose";

	public static final String INPUT_OPTION_FILE_OUTPUT = "--write";

	public static final String INPUT_OPTION_OUTPUT_DIRECTORY = "--output-directory";

	public static final String INPUT_OPTION_CONTRACT_STYLE = "--contract-style";

	public static final String INPUT_OPTION_RESOLVE_REFERENCES = "--resolve-references";

	public static final String INPUT_OPTION_HELP = "--help";

	public static final String INPUT_OPTION_COUNT = "--count";

	public static final String INPUT_OPTION_LIFTING = "--lift";
	
	public static final String INPUT_OPTION_ANNOTATION = "--featureAnnotationJava";

	public static final String INPUT_OPTION_VERBOSE = "--verbose";

	public static Configuration parseCmdLineArguments(String[] args) throws CmdLineException {
		Configuration conf = new Configuration();
		boolean jml = false;
		if (args != null && args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals(INPUT_OPTION_EQUATIONFILE)) {
					i++;
					if (i < args.length) {
						try {
							conf.equationFileName = new File(args[i]).getCanonicalPath();
						} catch (IOException e) {
							throw new CmdLineException(e);
						}
					} else {
						throw new CmdLineException("Missing value for " + INPUT_OPTION_EQUATIONFILE + " !");
					}
				} else if (args[i].equals(INPUT_OPTION_BASE_DIRECTORY)) {
					i++;
					if (i < args.length) {
						try {
							conf.baseDirectoryName = new File(args[i]).getCanonicalPath();
						} catch (IOException e) {
							throw new CmdLineException(e);
						}
					} else {
						throw new CmdLineException("Missing value for " + INPUT_OPTION_BASE_DIRECTORY + " !");
					}
				} else if (args[i].equals(INPUT_OPTION_OUTPUT_DIRECTORY)) {
					i++;
					if (i < args.length) {
						try {
							conf.outputDirectoryName = new File(args[i]).getCanonicalPath();
						} catch (IOException e) {
							throw new CmdLineException(e);
						}
					} else {
						throw new CmdLineException("Missing value for " + INPUT_OPTION_OUTPUT_DIRECTORY + " !");
					}
				} else if (args[i].equals(INPUT_OPTION_COUNT)) {
					conf.isCount = true;
				} else if (args[i].equals(INPUT_OPTION_NO_COMPOSE)) {
					conf.compose = false;
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
				} else if (args[i].equals(INPUT_OPTION_VERBOSE)) {
					conf.verbose = true;
				} else if (args[i].startsWith(INPUT_OPTION_LIFTING)) {
					conf.lifting = true;
					conf.lifting_language = args[i]
							.substring(INPUT_OPTION_LIFTING.length()).trim()
							.toLowerCase();
					if (!(conf.lifting_language.equals("java") || conf.lifting_language
							.equals("c"))) {
						throw new CmdLineException(
								"Lifting requires a language as parameter (e.g. --liftC or --liftJava)");
					}
				} else if (args[i].equals(INPUT_OPTION_RESOLVE_REFERENCES)) {
					System.out.println("The option '"
							+ INPUT_OPTION_RESOLVE_REFERENCES
							+ "' is obsolete.");
				} else if (args[i].equals(INPUT_OPTION_HELP)) {
					printHelp(null);
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
						throw new CmdLineException(
								"Unknown contract style. Please choose from: plain_contracting, explicit_contracting, consecutive_contracting");
					}
				} else {
					throw new CmdLineException("Unrecognized argument: " + args[i]);
				}
			}
		}

		CommandLineParameterHelper.setJML(jml);

		//equation file is mandatory
		if (conf.equationFileName == null) {
			throw new CmdLineException("An equation file needs to be specified using the " + INPUT_OPTION_EQUATIONFILE + " option!");
		}

		//base directory defaults to the directory of the equation file
		if (conf.baseDirectoryName == null) {
			conf.baseDirectoryName = getDirectoryName(new File(
					conf.equationFileName)) + File.separator;
		}

		//output directory defaults to base directory
		if (conf.outputDirectoryName == null) {
			conf.outputDirectoryName = conf.baseDirectoryName;
		}

		return conf;
	}

	public static void printHelp(CmdLineException e) {
		if (e != null) {
			System.out.println("Incorrect command line parameters:");
			System.out.println(e.getMessage());
		}
		System.out.println();
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
