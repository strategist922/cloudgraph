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
package org.cloudgraph.examples.bioxsd.sequence;




import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cloudgraph.examples.bioxsd.AminoacidSequenceRecord;
import org.cloudgraph.examples.bioxsd.GeneralSequenceSegment;
import org.cloudgraph.examples.bioxsd.NucleotideSequenceRecord;
import org.cloudgraph.examples.bioxsd.RecommendedDatabaseName;
import org.cloudgraph.examples.bioxsd.SequencePosition;
import org.cloudgraph.examples.bioxsd.SequenceReference;
import org.cloudgraph.examples.bioxsd.Species;
import org.cloudgraph.examples.cml.Atom;
import org.cloudgraph.examples.cml.AtomArray;
import org.cloudgraph.examples.cml.Bond;
import org.cloudgraph.examples.cml.BondArray;
import org.cloudgraph.examples.cml.ElementTypeType;
import org.cloudgraph.examples.cml.Molecule;
import org.cloudgraph.examples.cml.query.QAtom;
import org.cloudgraph.examples.cml.query.QMolecule;
import org.cloudgraph.test.BioXSDTest;
import org.plasma.sdo.helper.PlasmaDataFactory;
import org.plasma.sdo.helper.PlasmaTypeHelper;

import commonj.sdo.DataGraph;
import commonj.sdo.Type;

/**
 * BioXSD 1.1 - Nucleotide Sequence Tests
 */
public class NucleotideSequenceExample extends BioXSDTest {
    private static Log log = LogFactory.getLog(NucleotideSequenceExample.class);

    public void setUp() throws Exception {
        super.setUp();
    }        
   
    public void testCreate() throws IOException {
    	    	
        DataGraph dataGraph = PlasmaDataFactory.INSTANCE.createDataGraph();
        dataGraph.getChangeSummary().beginLogging(); // log changes from this point
    	Type rootType = PlasmaTypeHelper.INSTANCE.getType(NucleotideSequenceRecord.class);
    	
    	NucleotideSequenceRecord nucleotide = (NucleotideSequenceRecord)dataGraph.createRootObject(rootType);
    	nucleotide.setSequence_("aagacgctcaagtcgggctacacggagaagcagcgccgggacttcctgagcgaagcctccatcatgggccagttcgaccatcccaacgtcatccacctggagggtgtcgtgaccaagagcacacctgtgatgatcatcaccgagttcatggagaatggctccctggactcctttctccgggtaggg");
    	nucleotide.setNote("Human erk gene for elk-related kinase, exon D2/D3, partial : Location:");
		nucleotide.setName("exon D2/D3, partial " 
    	    + String.valueOf(System.currentTimeMillis()));
    	
    	SequenceReference reference = nucleotide.createReference();
    	reference.setDbName(RecommendedDatabaseName.EMBL.getInstanceName());
    	reference.setDbUri("http://www.ebi.ac.uk/embl");
    	reference.setAccession("X59292");
    	reference.setEntryUri("http://www.ebi.ac.uk/cgi-bin/emblfetch?id=X59292"); 
    	
    	SequencePosition sequencePosition = reference.createSubsequencePosition();
    	GeneralSequenceSegment segment = sequencePosition.createSegment();
    	segment.setMin(4);
    	segment.setMax(189);
    	
    	String xml = this.serializeGraph(nucleotide.getDataGraph());
    	log.info(xml);
    	
    	this.service.commit(nucleotide.getDataGraph(), 
    			USERNAME);
    	
    }
    
    
}