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
package org.cloudgraph.hbase.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cloudgraph.config.CloudGraphConfig;
import org.cloudgraph.config.TableConfig;
import org.cloudgraph.state.StateMarshallingContext;
import org.plasma.sdo.PlasmaType;

import commonj.sdo.DataObject;
import commonj.sdo.Type;


/**
 * Encapsulates one or more
 * graph table reader components for federation across
 * multiple physical tables and/or physical table rows. 
 * Maps physical configured
 * table names to respective table readers. In most usage
 * scenarios, a "root" table reader is typically added 
 * initially, then other reader are incrementally added
 * as association target types are detected and found configured as
 * graph roots within another distinct table context. 
 * <p>
 * Acts as a container for one or more {@link TableReader} elements
 * encapsulating a set of component table read operations 
 * for federated assembly across multiple tables, or a single table
 * in the most simple (degenerate) case.  
 * </p>
 * @see org.cloudgraph.hbase.io.GraphTableReader
 * @see org.cloudgraph.state.GraphTable
 * @author Scott Cinnamond
 * @since 0.5.1
 */
public class FederatedGraphReader implements FederatedReader {

    private static Log log = LogFactory.getLog(FederatedGraphReader.class);
	
    private TableReader rootReader;
	/** maps table names to table readers */
	private Map<String, TableReader> tableReaderMap = new HashMap<String, TableReader>();
	/** maps qualified graph-root type names to table readers */
	private Map<QName, TableReader> typeTableReaderMap = new HashMap<QName, TableReader>();
	/** maps table readers to graph-root types */
	private Map<TableReader, List<Type>> tableReaderTypeMap = new HashMap<TableReader, List<Type>>();
	
	// maps data objects to row readers
	private Map<DataObject, RowReader> rowReaderMap = new HashMap<DataObject, RowReader>();
	
	private StateMarshallingContext marshallingContext;
	
	@SuppressWarnings("unused")
	private FederatedGraphReader() {}
	
	public FederatedGraphReader(Type rootType, List<Type> types,
			StateMarshallingContext marshallingContext) {
	    this.marshallingContext = marshallingContext;
		PlasmaType root = (PlasmaType)rootType;
	    
	    TableConfig rootTable = CloudGraphConfig.getInstance().getTable(
	    		root.getQualifiedName());
	    
	    TableReader tableReader = new GraphTableReader(rootTable,
	    		this);
	    this.tableReaderMap.put(tableReader.getTable().getName(), 
	    	tableReader);
	    
		this.rootReader = tableReader;
	    this.typeTableReaderMap.put(((PlasmaType)rootType).getQualifiedName(), 
	    		this.rootReader);
	    List<Type> list = new ArrayList<Type>();
	    this.tableReaderTypeMap.put(this.rootReader, list);
	    list.add(rootType);
		
		for (Type t : types) {
		    PlasmaType type = (PlasmaType)t;
		    TableConfig table = CloudGraphConfig.getInstance().findTable(
				type.getQualifiedName());
		    if (table == null)
		    	continue; // not a graph root
		    
		    tableReader = this.tableReaderMap.get(table.getName());
		    if (tableReader == null) {
		        // create a new table reader if not added already, e.g.
		    	// as root above or from a graph root type
		    	// mapped to a table we have seen here
		        tableReader = new GraphTableReader(table, this);
		        this.tableReaderMap.put(tableReader.getTable().getName(), 
		    	    tableReader);
		    }
		    
		    // always map root types 
		    this.typeTableReaderMap.put(type.getQualifiedName(), tableReader);
		    
		    list = this.tableReaderTypeMap.get((TableOperation)tableReader);
		    if (list == null) {
		    	list = new ArrayList<Type>();
		    	this.tableReaderTypeMap.put(tableReader, list);
		    }
		    if (!list.contains(type))
		        list.add(type);
		}
	}	
	
	/**
	 * Returns the table reader for the given 
	 * configured table name, or null of not exists.
	 * @param tableName the name of the configured table. 
	 * @return the table reader for the given 
	 * configured table name, or null of not exists.
	 */
	@Override
	public TableReader getTableReader(String tableName) {
		return this.tableReaderMap.get(tableName);
	}

	{
		
	}
	
	/**
	 * Returns the table reader for the given 
	 * qualified type name, or null if not exists.
	 * @param qualifiedTypeName the qualified type name. 
	 * @return the table reader for the given 
	 * qualified type name, or null if not exists.
	 */
    public TableReader getTableReader(QName qualifiedTypeName) 
    {
    	return this.typeTableReaderMap.get(qualifiedTypeName);
    }
    
    /**
	 * Adds the given table reader to the container
	 * @param reader the table reader. 
	 */
	@Override
	public void addTableReader(TableReader reader) {
		String name = reader.getTable().getName();
		if (this.tableReaderMap.get(name) != null)
			throw new OperationException("table reader for '" + 
				name + "' already exists");
		this.tableReaderMap.put(name, reader);
	}

	/**
	 * Returns the count of table readers for this 
	 * container.
	 * @return the count of table readers for this 
	 * container
	 */
	@Override
	public int getTableReaderCount() {
		return this.tableReaderMap.size();
	}

    /**
     * Returns all table readers for the this container
     * @return all table readers for the this container
     */
    public List<TableReader> getTableReaders() {
    	List<TableReader> result = new ArrayList<TableReader>();
    	result.addAll(this.tableReaderMap.values());
    	return result;
    }
    
    /**
     * Returns the table reader associated with the
     * data graph root. 
     * @return the table reader associated with the
     * data graph root.
     */
    public TableReader getRootTableReader() {
    	return this.rootReader;
    }

    /**
     * Sets the table reader associated with the
     * data graph root. 
     * @param reader the table reader
     */
    public void setRootTableReader(TableReader reader) {
    	this.rootReader = reader;
    	this.tableReaderMap.put(rootReader.getTable().getName(), rootReader);
    }
    
    
    /**
     * Returns the row reader associated with the given data object
     * @param dataObject the data object
     * @return the row reader associated with the given data object
     * @throws IllegalArgumentException if the given data object
     * is not associated with any row reader.
     */
    public RowReader getRowReader(DataObject dataObject) {
    	RowReader result = rowReaderMap.get(dataObject);
    	if (result == null)
    		throw new IllegalArgumentException("the given data object of type "
    		   + dataObject.getType().getURI() + "#" + dataObject.getType().getName()		
    	       + " is not associated with any row reader");
    	return result;
    }

    public void mapRowReader(DataObject dataObject, 
    		RowReader rowReader) {
    	RowReader existing = this.rowReaderMap.get(dataObject);
    	if (existing != null) {
    		//throw new IllegalArgumentException
    		log.warn("the given data object of type "
    		   + dataObject.getType().getURI() + "#" + dataObject.getType().getName()		
    	       + " is already associated with a row reader");
    		return;
    	}
    	 
    	rowReaderMap.put(dataObject, rowReader);
    }
    
    /**
     * Returns a list of types associated
     * with the given table reader. 
     * @param reader the table reader
     * @return a list of types associated
     * with the given table reader. 
     */
    @Override
	public List<Type> getTypes(TableReader operation) {
		return this.tableReaderTypeMap.get(operation);
	}
	
	/**
	 * Returns true if only one table operation exists
	 * with only one associated (root) type for this
	 * operation. 
	 * @return true if only one table operation exists
	 * with only one associated (root) type for this
	 * operation. 
	 */
    public boolean hasSingleRootType() {
        if (this.getTableReaderCount() == 1 &&
        	this.getTypes(this.rootReader).size() == 1) {
        	return true;
        }
        else
        	return false;
    }
    
    /**
     * Frees resources associated with this reader and any
     * component readers. 
     */
    public void clear() {
    	this.rowReaderMap.clear();
    	// table readers are created based entirely on metadata
    	// i.e. selected types for a query
    	for (TableReader tableReader : tableReaderMap.values())
    		tableReader.clear();    	
    }

	@Override
	public StateMarshallingContext getMarshallingContext() {
		return this.marshallingContext;
	}
}