package composer.rules;

import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTNonTerminal;

/**
 * allows transformations and other actions on introductions
 */
public interface IntroductionRule {

	/**
	 * called when the composer introduces FSTNodes.
	 * Either `left` or `right` is `null`, depending on the situation:  
	 * If `right` is `null`, then `left` is already part of the intermediary result
	 * of the composition; if `left` is `null`, then `right` is introduced by the
	 * feature that is currently processed by the composer.
	 *
	 * Note: The composer passes in the original instances for a and b.
	 * Implementing introduction rules must return a new instance that is then
	 * introduced by the composer. In the simple case getDeepClone may be used for that.
	 *
	 * @param a introduced node if not null
	 * @param b introduced node if not null
	 * @param compParent parent of introduced node
	 * @return node to insert
	 */
	public FSTNode introduce(FSTNode a, FSTNode b, FSTNonTerminal compParent);
}
