package org.cloudgraph.hbase.filter;


import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cloudgraph.common.service.GraphState;
import org.cloudgraph.hbase.key.CompositeColumnKeyFactory;
import org.plasma.common.bind.DefaultValidationEventHandler;
import org.plasma.query.bind.PlasmaQueryDataBinding;
import org.plasma.query.model.Where;
import org.plasma.sdo.PlasmaType;
import org.xml.sax.SAXException;

/**
 * Creates a column filter hierarchy to return HBase scan results 
 * representing part of a graph "slice" from the set of columns
 * making up a collection or collection property within the graph. 
 * Uses <a target="#" href="http://hbase.apache.org/apidocs/org/apache/hadoop/hbase/filter/QualifierFilter.html">QualifierFilter</a> 
 *  /<a target="#" href="http://hbase.apache.org/apidocs/org/apache/hadoop/hbase/filter/ValueFilter.html">ValueFilter</a> pairs
 * recreating composite column qualifier prefixes using {@link CompositeColumnKeyFactory}. 
 * Processes visitor events for query model elements specific to assembly of HBase column
 * filters, such as properties, wildcards, literals, logical operators, relational 
 * operators, within the context of HBase filter hierarchy assembly.
 * Maintains various context information useful to subclasses. 
 * <p>
 * HBase filters may be collected into 
 * lists using <a href="http://hbase.apache.org/apidocs/org/apache/hadoop/hbase/filter/FilterList.html" target="#">FilterList</a>
 * each with a 
 * <a href="http://hbase.apache.org/apidocs/org/apache/hadoop/hbase/filter/FilterList.Operator.html#MUST_PASS_ALL" target="#">MUST_PASS_ALL</a> or <a href="http://hbase.apache.org/apidocs/org/apache/hadoop/hbase/filter/FilterList.Operator.html#MUST_PASS_ONE" target="#">MUST_PASS_ONE</a>
 *  (logical) operator. Lists may then be assembled into hierarchies 
 * used to represent complex expression trees filtering either rows
 * or columns in HBase.
 * </p> 
 * @see org.cloudgraph.common.key.CompositeColumnKeyFactory
 */
public class GraphSliceColumnFilterAssembler extends ColumnPredicateVisitor
{
    private static Log log = LogFactory.getLog(GraphSliceColumnFilterAssembler.class);

    /**
	 * Constructor which takes a {@link org.plasma.query.model.Query query} where
	 * clause containing any number of predicates and traverses
	 * these as a {org.plasma.query.visitor.QueryVisitor visitor} only
	 * processing various traversal events as needed against the given
	 * root type. 
     * @param where where the where clause
     * @param graphState the data graph state information
     * @param contextType the current SDO type
     * @param rootType the graph root type
	 * @see org.plasma.query.visitor.QueryVisitor
	 * @see org.plasma.query.model.Query
     */
	public GraphSliceColumnFilterAssembler(Where where,
			GraphState graphState,
			PlasmaType contextType,
			PlasmaType rootType) 
	{
		super(rootType);
		this.contextType = contextType;
    	
        this.columnKeyFac = new CompositeColumnKeyFactory(rootType);
        
    	for (int i = 0; i < where.getParameters().size(); i++)
    		params.add(where.getParameters().get(i).getValue());
    	
    	if (log.isDebugEnabled())
    		this.log(where);
    	
    	if (log.isDebugEnabled())
    		log.debug("begin traverse");
    	
    	where.accept(this); // traverse
    	
    	if (log.isDebugEnabled())
    		log.debug("end traverse");    	
	}	
	
    protected void log(Where root)
    {
    	String xml = "";
        PlasmaQueryDataBinding binding;
		try {
			binding = new PlasmaQueryDataBinding(
			    new DefaultValidationEventHandler());
	        xml = binding.marshal(root);
		} catch (JAXBException e) {
			log.debug(e);
		} catch (SAXException e) {
			log.debug(e);
		}
        log.debug("query: " + xml);
    }	

}
