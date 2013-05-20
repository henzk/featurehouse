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

	public String outputDirectoryName;

	public String contract_style = "none";

	public boolean showXML = false;

	public boolean showFST = false;

	public boolean showSum = false;

	public boolean showGui = false;

	public boolean fileOutput = false;

	public boolean lifting = false;

	public String lifting_language = "";
	
	public boolean featureAnnotation = false;

	public boolean compose = true;

	public String toString() {
		String res = "";
		res += "CONFIGURATION:\n\n";
		res += "equationFileName    : " + equationFileName + "\n";
		res += "isAheadEquationFile : " + isAheadEquationFile + "\n";
		res += "baseDirectoryName   : " + baseDirectoryName + "\n";
		res += "outputDirectoryName : " + outputDirectoryName + "\n";
		res += "verbose             : " + verbose + "\n";
		res += "isCount             : " + isCount + "\n";
		res += "contract_style      : " + contract_style + "\n";
		res += "showXML             : " + showXML + "\n";
		res += "showFST             : " + showFST + "\n";
		res += "showSum             : " + showSum + "\n";
		res += "showGui             : " + showGui + "\n";
		res += "fileOutput          : " + fileOutput + "\n";
		res += "lifting             : " + lifting + "\n";
		res += "lifting_language    : " + lifting_language + "\n";
		res += "featureAnnotation   : " + featureAnnotation + "\n";
		res += "compose             : " + compose + "\n\n";
		return res;
	}
	
}