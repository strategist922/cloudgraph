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
package org.cloudgraph.state;

import org.cloudgraph.config.TableConfig;

/**
 * Encapsulates the configuration and state related
 * context information for a specific table. 
 * @see org.cloudgraph.config.TableConfig
 * @author Scott Cinnamond
 * @since 0.5.1
 */
public abstract class GraphTable implements TableState {
    protected TableConfig table;

    @SuppressWarnings("unused")
	private GraphTable() {}
    public GraphTable(TableConfig table) 
    {
    	if (table == null)
    		throw new IllegalArgumentException("unexpected null value for 'table'");
    	this.table = table;
    }
    
	@Override
	public TableConfig getTable() {
		return this.table;
	}
	
}
