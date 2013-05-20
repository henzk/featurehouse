package composer;

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

	public String baseDirectoryName;

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

	public boolean compose = true;
	
}