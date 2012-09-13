package org.cloudgraph.hbase.key;

import org.cloudgraph.config.DataGraphConfig;
import org.cloudgraph.config.TableConfig;
import org.plasma.sdo.PlasmaType;

/**
 * A key factory which helps implementations leverage 
 * table and data graph specific configuration information,
 * such as the hashing algorithm and field level row and column
 * model settings. 
 * <p>
 * The initial creation and subsequent reconstitution for query retrieval
 * purposes of both row and column keys in CloudGraph&#8482; is efficient, 
 * as it leverages byte array level API in both Java and the current 
 * underlying SDO 2.1 implementation, <a target="#" href="http://plasma-sdo.org">PlasmaSDO&#8482;</a>. Both composite row and 
 * column keys are composed in part of structural metadata, and the 
 * lightweight metadata API within <a target="#" href="http://plasma-sdo.org">PlasmaSDO&#8482;</a> contains byte-array level, 
 * cached lookup of all basic metadata elements including logical and 
 * physical type and property names.  
 * </p>
 * @see org.cloudgraph.config.CloudGraphConfig
 * @see org.cloudgraph.config.TableConfig
 * @see org.cloudgraph.config.DataGraphConfig
*/
public interface ConfigurableKeyFactory {
	public TableConfig getTable();
	public DataGraphConfig getGraph();
	public PlasmaType getRootType();
}