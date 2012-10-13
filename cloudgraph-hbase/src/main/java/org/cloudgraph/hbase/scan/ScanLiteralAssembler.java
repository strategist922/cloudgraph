package org.cloudgraph.hbase.scan;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cloudgraph.common.service.GraphServiceException;
import org.cloudgraph.config.CloudGraphConfig;
import org.cloudgraph.config.DataGraphConfig;
import org.cloudgraph.config.TableConfig;
import org.cloudgraph.config.UserDefinedFieldConfig;
import org.plasma.query.model.AbstractPathElement;
import org.plasma.query.model.GroupOperator;
import org.plasma.query.model.Literal;
import org.plasma.query.model.LogicalOperator;
import org.plasma.query.model.NullLiteral;
import org.plasma.query.model.Path;
import org.plasma.query.model.PathElement;
import org.plasma.query.model.Property;
import org.plasma.query.model.RelationalOperator;
import org.plasma.query.model.Where;
import org.plasma.query.model.WildcardOperator;
import org.plasma.query.model.WildcardPathElement;
import org.plasma.query.visitor.DefaultQueryVisitor;
import org.plasma.sdo.PlasmaProperty;
import org.plasma.sdo.PlasmaType;


/**
 * Assembles the set of data "flavor" and data type specific
 * scan literals used to construct composite partial row 
 * (start/stop) key pair.  
 * 
 * @see org.cloudgraph.config.DataGraphConfig
 * @see org.cloudgraph.config.TableConfig
 */
public class ScanLiteralAssembler extends DefaultQueryVisitor 
{
    private static Log log = LogFactory.getLog(ScanLiteralAssembler.class);
	protected PlasmaType rootType;
	protected PlasmaType contextType;
	protected PlasmaProperty contextProperty;
	protected String contextPropertyPath;
	protected RelationalOperator contextRelationalOperator;
	protected LogicalOperator contextLogicalOperator;
	protected DataGraphConfig graph;
	protected TableConfig table;
	protected List<ScanLiteral> literalList = new ArrayList<ScanLiteral>();
	protected ScanLiteralFactory scanLiteralFactory = new ScanLiteralFactory();
	
	@SuppressWarnings("unused")
	private ScanLiteralAssembler() {}
	
	public ScanLiteralAssembler(PlasmaType rootType)
	{
    	this.rootType = rootType;
		this.contextType = this.rootType;
		QName rootTypeQname = this.rootType.getQualifiedName();
		this.graph = CloudGraphConfig.getInstance().getDataGraph(
				rootTypeQname);
		this.table = CloudGraphConfig.getInstance().getTable(rootTypeQname);
	}
	
    public List<ScanLiteral> getLiteralList() {
		return literalList;
	}

	/**
     * Assemble the set of data "flavor" and data type specific
     * scan literals used to construct composite partial row 
     * (start/stop) key pair. 
     * @param where the row predicate hierarchy
     * @param contextType the context type which may be the root type or another
     * type linked by one or more relations to the root
     */
	public void assemble(Where where, PlasmaType contextType) {
    	
		this.contextType = contextType;
		
		if (log.isDebugEnabled())
    		log.debug("begin traverse");
    	
    	where.accept(this); // traverse
    	
    	if (log.isDebugEnabled())
    		log.debug("end traverse");      	
    }
	
	/**
	 * Process the traversal start event for a query {@link org.plasma.query.model.Property property}
     * within an {@link org.plasma.query.model.Expression expression} just
     * traversing the property path if exists and capturing context information
     * for the current {@link org.plasma.query.model.Expression expression}.  
	 * @see org.plasma.query.visitor.DefaultQueryVisitor#start(org.plasma.query.model.Property)
	 */
	@Override
    public void start(Property property)
    {                
        org.plasma.query.model.FunctionValues function = property.getFunction();
        if (function != null)
            throw new GraphServiceException("aggregate functions only supported in subqueries not primary queries");
          
        Path path = property.getPath();
        PlasmaType targetType = (PlasmaType)this.rootType;                
        if (path != null)
        {
            for (int i = 0 ; i < path.getPathNodes().size(); i++)
            {    
            	AbstractPathElement pathElem = path.getPathNodes().get(i).getPathElement();
                if (pathElem instanceof WildcardPathElement)
                    throw new GraphServiceException("wildcard path elements applicable for 'Select' clause paths only, not 'Where' clause paths");
                String elem = ((PathElement)pathElem).getValue();
                PlasmaProperty prop = (PlasmaProperty)targetType.getProperty(elem);                
                targetType = (PlasmaType)prop.getType(); // traverse
            }
        }
        PlasmaProperty endpointProp = (PlasmaProperty)targetType.getProperty(property.getName());
        this.contextProperty = endpointProp;
        this.contextType = targetType;
        this.contextPropertyPath = property.asPathString();
        
        super.start(property);
    }     

	public void start(WildcardOperator operator) {
		switch (operator.getValue()) {
		default:
			throw new GraphServiceException("unsupported operator '"
					+ operator.getValue().toString() + "'");
		}
	}	
	
    /**
     * Process the traversal start event for a query {@link org.plasma.query.model.Literal literal}
     * within an {@link org.plasma.query.model.Expression expression}.
     * @param literal the expression literal
     * @throws GraphServiceException if no user defined row-key token
     * is configured for the current literal context.
     */
	@Override
	public void start(Literal literal) {
		String content = literal.getValue();
		if (this.contextProperty == null)
			throw new IllegalStateException("expected context property for literal");
		if (this.contextType == null)
			throw new IllegalStateException("expected context type for literal");
		if (this.rootType == null)
			throw new IllegalStateException("expected context type for literal");

		// Match the current property to a user defined 
		// row key token, if found we can process
		UserDefinedFieldConfig fieldConfig = this.graph.getUserDefinedRowKeyField(this.contextPropertyPath);
		if (fieldConfig != null) 
		{
			PlasmaProperty property = (PlasmaProperty)fieldConfig.getEndpointProperty();
			ScanLiteral context = this.scanLiteralFactory.createLiteral(
					content, property, 
					this.rootType, 
					this.contextRelationalOperator, 
					this.contextLogicalOperator, 
					fieldConfig);
			literalList.add(context);
		}
		else
	        throw new GraphServiceException("no user defined row-key field for query path '"
			    	+ this.contextPropertyPath + "'");
		
		super.start(literal);
	}

	/**
	 * (non-Javadoc)
	 * @see org.plasma.query.visitor.DefaultQueryVisitor#start(org.plasma.query.model.NullLiteral)
	 */
	@Override
	public void start(NullLiteral nullLiteral) {
        throw new GraphServiceException("null literals for row scans not yet supported");
	}
	
	/**
	 * Process a {@link org.plasma.query.model.LogicalOperator logical operator} query traversal
	 * start event. 
	 */
	public void start(LogicalOperator operator) {
		
		switch (operator.getValue()) {
		case AND:
		case OR:	
			this.contextLogicalOperator = operator;
		}
		super.start(operator);
	}
    
	public void start(RelationalOperator operator) {
		switch (operator.getValue()) {
		case EQUALS:
		case NOT_EQUALS:
		case GREATER_THAN:
		case GREATER_THAN_EQUALS:
		case LESS_THAN:
		case LESS_THAN_EQUALS:
			this.contextRelationalOperator = operator;
			break;
		default:
			throw new GraphServiceException("unknown relational operator '"
					+ operator.getValue().toString() + "'");
		}
		super.start(operator);
	}
	
	public void start(GroupOperator operator) {
		switch (operator.getValue()) {
		case RP_1:  		
	        break;
		case RP_2:  		
		    break;
		case RP_3:  			
			break;
		case LP_1:  
			break;
		case LP_2:  			
			break;
		case LP_3:  
			break;
		default:
			throw new GraphServiceException("unsupported group operator, "
						+ operator.getValue().name());
		}
		super.start(operator);
	}

}