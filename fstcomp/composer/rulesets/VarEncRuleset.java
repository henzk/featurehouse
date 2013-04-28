package composer.rulesets;

import java.io.File;

import composer.Configuration;

/**
 *
 * implements language agnostic part of variability encoding:
 *
 * - records necessary metadata about features and roles
 * - saves role metadata to `roles.meta` in JSON format(e.g. for error path analysis with CPAChecker)
 *
 * To add language support for variability encoding:
 *
 * - subclass VarEncRuleset
 * - implement language specific CompostionRule that supports variability encoding.
 *   Implementations for C and Java both provide rules for Replacement and FuntionRefinement.
 *   Use these as a guide.
 * - implement generateSimulator: use implementations for C and JAVA as a guide.
 */
public abstract class VarEncRuleset extends DefaultRuleset {

	protected File cnfFile;

	/**
	 * -set path where role metadata is written
	 * -set path to feature model in CNF
	 *
	 * subclasses need to setup specific composition rules here!
	 */
	@Override
	public void configure(Configuration conf) {
		super.configure(conf);
		cnfFile = new File(conf.equationBaseDirectoryName, "model.cnf");
	}

	/**
	 * -triggers generation of the program simulator
	 */
	@Override
	public void finalizeComposition() {
		super.finalizeComposition();
		//trigger language specific generation of the program simulator
		generateSimulator();
	}

	/**
	 * to be implemented by subclass
	 *
	 * needs to generate the language specific program simulator to
	 * allow family based analysis.
	 */
	abstract protected void generateSimulator();
}