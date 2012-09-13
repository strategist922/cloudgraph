package org.cloudgraph.common.key;

 
import org.plasma.sdo.PlasmaProperty;
import org.plasma.sdo.PlasmaType;


/**
 * Generates a column key based on the configured CloudGraph column key {@link org.cloudgraph.config.ColumnKeyModel
 * model} for a specific table {@link org.cloudgraph.config.Table configuration}. 
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
 * @see org.cloudgraph.config.ColumnKeyModel
 * @see org.cloudgraph.config.Table
 */
public interface GraphColumnKeyFactory {
	
	/**
	 * Generates and returns a column key based on the given metadata type and property
	 * as well as the configured CloudGraph column key {@link org.cloudgraph.config.ColumnKeyModel
     * model} for a specific table {@link org.cloudgraph.config.Table configuration}. 
	 * @param type the metadata type
	 * @param property the property
	 * @return the column key bytes
	 */
	public byte[] createColumnKey( 
	    PlasmaType type, PlasmaProperty property);

}