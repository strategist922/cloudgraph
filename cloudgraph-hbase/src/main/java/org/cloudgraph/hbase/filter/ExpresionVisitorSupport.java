package org.cloudgraph.hbase.filter;

import java.util.ArrayList;
import java.util.List;

import org.plasma.query.model.Expression;
import org.plasma.query.model.Literal;
import org.plasma.query.model.LogicalOperatorValues;
import org.plasma.query.model.QueryConstants;
import org.plasma.query.model.Term;
import org.plasma.query.visitor.DefaultQueryVisitor;

/**
 * Contains logic related to query expression processing. 
 * @author Scott Cinnamond
 * @since 0.5.2
 */
public abstract class ExpresionVisitorSupport extends DefaultQueryVisitor {
	
   	private static Expression[] NOOP_EXPR_ARRAY = new Expression[0];
    
	/**
	 * Returns true if the given expression has any immediate
	 * child expressions. 
	 * @param expression the expression
	 * @return true if the given expression has any immediate
	 * child expressions.
	 */
    protected boolean hasChildExpressions(Expression expression) {
		for (Term term : expression.getTerms())
			if (term.getExpression() != null)
				return true;
		return false;
	}

    /**
     * Returns a count of the number of child expressions 
     * for the given expression. 
     * @param expression the expression
     * @return a count of the number of child expressions 
     * for the given expression.
     */
    protected int getChildExpressionCount(Expression expression) {
		int result = 0;
    	for (Term term : expression.getTerms())
			if (term.getExpression() != null)
				result++;
    	
		return result;
	}
    
    protected Expression[] getChildExpressions(Expression expression) {
		List<Expression> list = new ArrayList<Expression>();
    	for (Term term : expression.getTerms())
			if (term.getExpression() != null)
				list.add(term.getExpression());
    	Expression[] result = new Expression[list.size()];
    	list.toArray(result);
		return result;
	}
    
    /**
     * Returns a count of logical operators which match the
     * given logical operator
     * found within the child expressions for the given 
     * expression. 
     * @param expression the expression
     * @return a count of logical operators which match the
     * given logical operator
     * found within the child expressions for the given 
     * expression. 
     */
    protected int getLogicalOperatorCount(Expression expression, LogicalOperatorValues operator) {
    	int result = 0;
    	for (Term term : expression.getTerms())
			if (term.getLogicalOperator() != null) {
				if (term.getLogicalOperator().getValue().ordinal() ==  operator.ordinal())
					result++;
			}
    	
    	return result;
	}
    
    /**
     * Returns true if the given expression contains a
     * literal wildcard. 
     * @param expression the expression
     * @return true if the given expression contains a
     * literal wildcard.
     */
	protected boolean hasWildcard(Expression expression) {
		for (int i = 0; i < expression.getTerms().size(); i++) {
			if (expression.getTerms().get(i).getWildcardOperator() != null)
			{
			    Literal literal = expression.getTerms().get(i + 1).getLiteral();
			    if (literal.getValue().indexOf(QueryConstants.WILDCARD) >= 0) // otherwise we can treat the expr like any other
				    return true;
		    }
		}
		return false;
	}
}