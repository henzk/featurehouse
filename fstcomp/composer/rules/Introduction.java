package composer.rules;

import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTNonTerminal;

/**
 * default introduction
 */
public class Introduction implements IntroductionRule {

	/**
	 * no transformation, no action
	 */
	@Override
	public FSTNode introduce(FSTNode a, FSTNode b, FSTNonTerminal compParent) {
		return (a != null) ? a : b;
	}

}
