package composer;

import java.io.File;

/**
 * encapsulates configuration data for the composer:
 * 
 * -which composition ruleset is applied?
 * -output options
 * 
 * configuration data is considered to be READ ONLY!
 */
public class Configuration {

	public boolean verbose = false;

	public boolean isCount = false;

	public boolean isAheadEquationFile;

	public String equationFileName;

	public String equationBaseDirectoryName;

	public String outputDirectoryName = null;

	public String contract_style = "none";

	public boolean isBaseDirectoryName = false;

	public boolean showXML = false;

	public boolean showFST = false;

	public boolean showSum = false;

	public boolean showGui = false;

	public boolean fileOutput = false;

	public boolean lifting = false;

	public String lifting_language = "";
	
	public boolean featureAnnotation = false;

	public String getOutputDir() {
		String outputDir = equationBaseDirectoryName;
		if (outputDirectoryName != null)
			outputDir = outputDirectoryName;

		if (outputDir.endsWith(File.separator))
			outputDir = outputDir.substring(0, outputDir.length()-1);
		return outputDir;
	}
	
}