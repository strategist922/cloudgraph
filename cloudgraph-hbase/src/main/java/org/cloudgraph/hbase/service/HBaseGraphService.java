package org.cloudgraph.hbase.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.cloudgraph.config.CloudGraphConfig;
import org.cloudgraph.hbase.connect.HBaseConnectionManager;
import org.plasma.common.bind.DefaultValidationEventHandler;
import org.plasma.query.bind.PlasmaQueryDataBinding;
import org.plasma.query.model.From;
import org.plasma.query.model.Query;
import org.plasma.query.model.QueryValidator;
import org.plasma.sdo.PlasmaType;
import org.plasma.sdo.access.DataAccessException;
import org.plasma.sdo.access.DataGraphDispatcher;
import org.plasma.sdo.access.PlasmaDataAccessService;
import org.plasma.sdo.core.SnapshotMap;
import org.plasma.sdo.helper.PlasmaTypeHelper;
import org.xml.sax.SAXException;

import commonj.sdo.DataGraph;
import commonj.sdo.Type;

/**
 * Top level provider service implementing the
 * {@link org.plasma.sdo.access.DataAccessService DataAccessService } 
 * interface and delegating to {@link HBaseGraphQuery} for serving
 * data from HBase back to the client, and {@link HBaseGraphDispatcher} for
 * propagating changes to one or more data graphs back to HBase. 
 * <p>
 * HGraph is based on the Service Data Objects (SDO) 2.1 specification 
 * and is designed as an SDO Data Access Service (DAS) under the 
 * PlasmaSDO\u2122 Service Data Objects 2.1 implementation. 
 * </p> 
 * <p>
 * Typical CRUD operations are provided across any full or partial 
 * data graph, and any "slice" of a graph or set of
 * sub-graph slices can be returned using 
 * common SDO query mechanisms including XPath and others.     
 * </p>
 * @see org.plasma.sdo.access.DataAccessService
 * @see HBaseGraphQuery
 * @see HBaseGraphDispatcher
 */
public class HBaseGraphService implements PlasmaDataAccessService {
    
    private static Log log = LogFactory.getLog(HBaseGraphService.class);

    public HBaseGraphService() {
    }
 
    public void initialize() {}
    public void close() {}
    
    public int count(Query query) {
        if (query == null)
            throw new IllegalArgumentException("expected non-null 'query' argument");
        validate(query);
        if (log.isDebugEnabled()) {
            log(query);
        }
        HBaseGraphQuery dispatcher = new HBaseGraphQuery();
        return dispatcher.count(query);
    }
    
    public int[] count(Query[] queries) {
        if (queries == null)
            throw new IllegalArgumentException("expected non-null 'queries' argument");
        int[] counts = new int[queries.length];
        for (int i = 0; i < queries.length; i++)
            counts[i] = count(queries[i]);
        return counts;
    }

    public DataGraph[] find(Query query) {
        if (query == null)
            throw new IllegalArgumentException("expected non-null 'query' argument");
        //validate(query);
        if (log.isDebugEnabled()) {
            log(query);
        }
        HBaseGraphQuery dispatcher = new HBaseGraphQuery();
        Timestamp snapshotDate = new Timestamp((new Date()).getTime());
        return dispatcher.find(query, snapshotDate);
    }

    public DataGraph[] find(Query query, int maxResults) {
        if (query == null)
            throw new IllegalArgumentException("expected non-null 'query' argument");
        validate(query);
        if (log.isDebugEnabled()) {
            log(query);
        }
        HBaseGraphQuery dispatcher = new HBaseGraphQuery();
        DataGraph[] results = dispatcher.find(query, -1, new Timestamp((new Date()).getTime()));
        return results;
    }

    public List<DataGraph[]> find(Query[] queries) {
        if (queries == null)
            throw new IllegalArgumentException("expected non-null 'queries' argument");
        HBaseGraphQuery dispatcher = new HBaseGraphQuery();
        List<DataGraph[]> list = new ArrayList<DataGraph[]>();
        Timestamp snapshotDate = new Timestamp((new Date()).getTime());
        for (int i = 0; i < queries.length; i++)
        {
            validate(queries[i]);
            if (log.isDebugEnabled()) {
                log(queries[i]);
            }
            DataGraph[] results = dispatcher.find(queries[i], snapshotDate);
            list.add(results);
        }
        return list;
    }

    public SnapshotMap commit(DataGraph dataGraph, String username) {
        if (dataGraph == null)
            throw new IllegalArgumentException("expected non-null 'dataGraph' argument");
        if (username == null)
            throw new IllegalArgumentException("expected non-null 'username' argument");
        if (username.trim().length() == 0)
            throw new IllegalArgumentException("unexpected zero length 'username' argument");
        SnapshotMap snapshotMap = new SnapshotMap(new Timestamp((new Date()).getTime()));
        
        PlasmaType type = (PlasmaType)dataGraph.getRootObject().getType();
        String htable = CloudGraphConfig.getInstance().getHTableName(
        		type.getQualifiedName());
        HTableInterface con = null;
        con = HBaseConnectionManager.instance().getConnection(htable);
        //con.setAutoCommit(false);
        DataGraphDispatcher dispatcher = new HBaseGraphDispatcher(snapshotMap, 
                username, con);
        try {
            dispatcher.commit(dataGraph);
            //con.commit();
            return snapshotMap;
        }
        catch (DataAccessException e) {
            if (log.isDebugEnabled())
                log.debug(e.getMessage(), e);
			//con.rollback();
            throw e;
        }
        catch (Throwable t) {
            if (log.isDebugEnabled())
                log.debug(t.getMessage(), t);
			//con.rollback();
            throw new DataAccessException(t);
        }
        finally {
            dispatcher.close();
        }
    }

    public SnapshotMap commit(DataGraph[] dataGraphs, String username) {
        if (dataGraphs == null)
            throw new IllegalArgumentException("expected non-null 'dataGraphs' argument");
        if (username == null)
            throw new IllegalArgumentException("expected non-null 'username' argument");
        if (username.trim().length() == 0)
            throw new IllegalArgumentException("unexpected zero length 'username' argument");
        
        SnapshotMap snapshotMap = new SnapshotMap(new Timestamp((new Date()).getTime()));
        
        try {
            for (int i = 0; i < dataGraphs.length; i++) { 
                if (log.isDebugEnabled())
                    log.debug("commiting: " + dataGraphs[i].getChangeSummary().toString());
                PlasmaType type = (PlasmaType)dataGraphs[i].getRootObject().getType();

                String htable = CloudGraphConfig.getInstance().getHTableName(
                		type.getQualifiedName());
                HTableInterface con = null;
        		con = HBaseConnectionManager.instance().getConnection(htable);
                //con.setAutoCommit(false);
                
                DataGraphDispatcher dispatcher = new HBaseGraphDispatcher(snapshotMap,
                        username, con);
                try {
                    dispatcher.commit(dataGraphs[i]);        
                }
                finally {
                    dispatcher.close();
                    con.close();
                }
            }  
            //con.commit();
            return snapshotMap;
        }
        catch (DataAccessException e) {
			//con.rollback();
            throw e;
        }
        catch (Throwable t) {
			//con.rollback();
            throw new DataAccessException(t);
        }
        finally {
        }
    }

    private void validate(Query query)
    {
        From from = (From)query.getFromClause();
        Type type = PlasmaTypeHelper.INSTANCE.getType(
        		from.getEntity().getNamespaceURI(), 
        		from.getEntity().getName());
        log.debug("validating query");
        new QueryValidator((Query)query, type);
    }
 
    private void log(Query query)
    {
    	String xml = "";
        PlasmaQueryDataBinding binding;
		try {
			binding = new PlasmaQueryDataBinding(
			        new DefaultValidationEventHandler());
	        xml = binding.marshal(query);
		} catch (JAXBException e) {
		} catch (SAXException e) {
		}
        log.debug("query: " + xml);
    }
}