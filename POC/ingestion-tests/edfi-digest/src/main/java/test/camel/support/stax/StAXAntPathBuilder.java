package test.camel.support.stax;

import org.apache.camel.Expression;

public final class StAXAntPathBuilder {

	private StAXAntPathBuilder() {
	}

	public static Expression stax(String antPath, int group) {
		return new StAXAntPathIteratorExpression(antPath, group);
	}
}
