/**
 *        CloudGraph Community Edition (CE) License
 * 
 * This is a community release of CloudGraph, a dual-license suite of
 * Service Data Object (SDO) 2.1 services designed for relational and 
 * big-table style "cloud" databases, such as HBase and others. 
 * This particular copy of the software is released under the 
 * version 2 of the GNU General Public License. CloudGraph was developed by 
 * TerraMeta Software, Inc.
 * 
 * Copyright (c) 2013, TerraMeta Software, Inc. All rights reserved.
 * 
 * General License information can be found below.
 * 
 * This distribution may include materials developed by third
 * parties. For license and attribution notices for these
 * materials, please refer to the documentation that accompanies
 * this distribution (see the "Licenses for Third-Party Components"
 * appendix) or view the online documentation at 
 * <http://cloudgraph.org/licenses/>. 
 */
package org.cloudgraph.hbase.query;

import org.plasma.query.model.Literal;
import org.plasma.query.model.Property;
import org.plasma.query.model.WildcardOperator;

/**
 * Contains default functionality for 
 * <a href="http://docs.plasma-sdo.org/api/org/plasma/query/model/WildcardOperator.html">wildcard</a> 
 * binary query expressions.   
 * @author Scott Cinnamond
 * @since 0.5.2
 * @see Expr
 * @see ExprVisitor
 * @see EvaluationContext
 */
public class DefaultWildcardBinaryExpr extends DefaultBinaryExpr 
    implements WildcardBinaryExpr {
    protected Property property;
    protected Literal literal;
    protected WildcardOperator operator;
	/**
	 * Constructs a composite expression based on the given
	 * terms and <a href="http://docs.plasma-sdo.org/api/org/plasma/query/model/WildcardOperator.html">wildcard</a>
	 * operator.
	 * @param left the "left" expression term
	 * @param right the "right" expression term
	 * @param oper the logical operator
	 */
	public DefaultWildcardBinaryExpr(Property property,
			Literal literal, 
			WildcardOperator operator) {
		super(property, literal);
		if (property == null)
			throw new IllegalArgumentException("expected arg 'property'");
		if (literal == null)
			throw new IllegalArgumentException("expected arg 'literal'");
		this.property = property;
		this.literal = literal;
		this.operator = operator;
	}
	
	/**
	 * Returns a "truth" value for the expression based
	 * on an evaluation of the <a href="http://docs.plasma-sdo.org/api/org/plasma/query/model/WildcardOperator.html">wildcard</a>
	 * operator within the given context. 
	 * @param context the context
	 * @return "truth" value for the expression based
	 * on an evaluation of the <a href="http://docs.plasma-sdo.org/api/org/plasma/query/model/WildcardOperator.html">wildcard</a>
	 * operator within the given context. 
	 */
	@Override
	public boolean evaluate(EvaluationContext context) {
		return true;
	}
	
	/**
	 * Returns the operator for the expression.
	 * @return the operator for the expression.
	 */
	@Override
	public WildcardOperator getOperator() {
		return this.operator;
	}
    
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(this.getClass().getSimpleName());
		buf.append(" [");
		buf.append(property.getName());
		buf.append(" ");
		buf.append(this.operator.getValue().name());
		buf.append(" ");
		buf.append(this.literal.getValue());
		buf.append("]");
		return buf.toString();
	}
}
