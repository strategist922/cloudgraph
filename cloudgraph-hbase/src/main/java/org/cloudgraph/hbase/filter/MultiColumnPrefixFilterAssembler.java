package org.cloudgraph.hbase.filter;


import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.filter.BinaryPrefixComparator;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.QualifierFilter;
import org.cloudgraph.common.key.GraphColumnKeyFactory;
import org.cloudgraph.hbase.key.CompositeColumnKeyFactory;
import org.plasma.sdo.PlasmaProperty;
import org.plasma.sdo.PlasmaType;

/**
 * Creates a column filter list using <a target="#" href="http://hbase.apache.org/apidocs/org/apache/hadoop/hbase/filter/QualifierFilter.html">QualifierFilter</a> 
 * and <a target="#" href="http://hbase.apache.org/apidocs/org/apache/hadoop/hbase/filter/BinaryPrefixComparator.html">BinaryPrefixComparator</a> and
 * recreating composite column qualifier prefixes for comparison using {@link CompositeColumnKeyFactory}. 
 * <p>
 * HBase filters may be collected into 
 * lists using <a href="http://hbase.apache.org/apidocs/org/apache/hadoop/hbase/filter/FilterList.html" target="#">FilterList</a>
 * each with a 
 * <a href="http://hbase.apache.org/apidocs/org/apache/hadoop/hbase/filter/FilterList.Operator.html#MUST_PASS_ALL" target="#">MUST_PASS_ALL</a> or <a href="http://hbase.apache.org/apidocs/org/apache/hadoop/hbase/filter/FilterList.Operator.html#MUST_PASS_ONE" target="#">MUST_PASS_ONE</a>
 *  (logical) operator. Lists may then be assembled into hierarchies 
 * used to represent complex expression trees filtering either rows
 * or columns in HBase.
 * </p> 
 * @see org.cloudgraph.common.key.GraphColumnKeyFactory
 * @see org.cloudgraph.hbase.key.CompositeColumnKeyFactory
 */
public class MultiColumnPrefixFilterAssembler extends FilterListAssembler
{
    private static Log log = LogFactory.getLog(MultiColumnPrefixFilterAssembler.class);
	private GraphColumnKeyFactory columnKeyFac;

	public MultiColumnPrefixFilterAssembler( 
			List<String> propertyNames,
			PlasmaType contextType,
			PlasmaType rootType) 
	{
		super(rootType);
		this.contextType = contextType;
    	this.columnKeyFac = new CompositeColumnKeyFactory(rootType);
		
    	this.rootFilter = new FilterList(
    		FilterList.Operator.MUST_PASS_ONE);
        
    	// Note: using many binary prefix qualifier filters
    	// rather than a single MultipleColumnPrefixFilter under the
    	// assumption that the binary compare is more
    	// efficient than the string conversion
    	// required by the MultipleColumnPrefixFilter (?)
    	for (String name : propertyNames) {
    		PlasmaProperty prop = (PlasmaProperty)this.contextType.getProperty(name);
    	    this.contextProperty = prop;
    		byte[] key = this.columnKeyFac.createColumnKey(this.contextType, prop);
            QualifierFilter qualFilter = new QualifierFilter(
                CompareFilter.CompareOp.EQUAL,
                new BinaryPrefixComparator(key)); 
            this.rootFilter.addFilter(qualFilter);
    	}
	}	
}
