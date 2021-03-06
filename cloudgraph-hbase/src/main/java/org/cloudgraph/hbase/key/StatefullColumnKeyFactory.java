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
package org.cloudgraph.hbase.key;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.util.Bytes;
import org.cloudgraph.common.key.GraphStatefullColumnKeyFactory;
import org.cloudgraph.state.GraphState;
import org.plasma.sdo.PlasmaDataObject;
import org.plasma.sdo.PlasmaProperty;
import org.plasma.sdo.PlasmaType;


/**
 * Generates an HBase column key based on the configured CloudGraph column key {@link org.cloudgraph.config.ColumnKeyModel
 * model} for a specific HTable {@link org.cloudgraph.config.Table configuration}. 
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
 * @see org.cloudgraph.state.GraphState
 * @author Scott Cinnamond
 * @since 0.5
 */
public class StatefullColumnKeyFactory extends CompositeColumnKeyFactory 
    implements GraphStatefullColumnKeyFactory 
{
	private static final Log log = LogFactory.getLog(StatefullColumnKeyFactory.class);
	private GraphState graphState;
		
	public StatefullColumnKeyFactory(PlasmaType rootType,
			GraphState graphState) {
		super(rootType);
	    this.graphState = graphState;
	}
	
	@Override
	public byte[] createColumnKey( 
			PlasmaDataObject dataObject, PlasmaProperty property)	
	{
 	    PlasmaType type = (PlasmaType)dataObject.getType();

		Integer seqNum = this.graphState.findSequence(dataObject);
		if (seqNum == null)
			seqNum = this.graphState.addSequence(dataObject);
		return getKey(type, seqNum, property);
	}

	@Override
	public byte[] createColumnKey(PlasmaType type, 
			Integer dataObjectSeqNum, PlasmaProperty property)	
	{
		return getKey(type, dataObjectSeqNum, property);
	}
	
	private byte[] getKey(PlasmaType type, 
			Integer dataObjectSeqNum, PlasmaProperty property) 
	{
		// Use the bytes of the sequence number 
		// String representation for column names so we can read
		// the column names in third party tools. 
		byte[] seqNumBytes = Bytes.toBytes(String.valueOf(dataObjectSeqNum));
		byte[] sectionDelim = graph.getColumnKeySectionDelimiterBytes();	    	    
		byte[] prefix = super.createColumnKey(type, property);
		
		int len = prefix.length + sectionDelim.length + seqNumBytes.length;
		byte[] result = new byte[len];
		
		int destPos = 0;
		System.arraycopy(prefix, 0, result, destPos, prefix.length);
		
		destPos += prefix.length;
		System.arraycopy(sectionDelim, 0, result, destPos, sectionDelim.length);
		
		destPos += sectionDelim.length;
		System.arraycopy(seqNumBytes, 0, result, destPos, seqNumBytes.length);
 		
		if (log.isDebugEnabled())
			log.debug("key: " + Bytes.toString(result));
		
		return result;
		
	}
}
