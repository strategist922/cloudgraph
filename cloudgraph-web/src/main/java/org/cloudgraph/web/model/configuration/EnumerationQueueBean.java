package org.cloudgraph.web.model.configuration;

// java imports
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.ajax4jsf.model.DataVisitor;
import org.ajax4jsf.model.Range;
import org.ajax4jsf.model.SequenceRange;
import org.ajax4jsf.model.SerializableDataModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cloudgraph.web.model.search.SearchBean;
import org.cloudgraph.web.query.EnumerationViewQuery;
import org.cloudgraph.web.sdo.adapter.EnumerationViewAdapter;
import org.cloudgraph.web.util.BeanFinder;
import org.plasma.query.Query;
import org.plasma.sdo.access.client.SDODataAccessClient;

import org.cloudgraph.web.sdo.core.EnumerationView;

import commonj.sdo.DataGraph;


/**
 */
public class EnumerationQueueBean extends SerializableDataModel {
    private static Log log =LogFactory.getLog(EnumerationQueueBean.class);

    private static final long serialVersionUID = 1L;
    private int scrollerPage = 1;
    private Integer currentPk = null;
    private Map<Integer, EnumerationViewAdapter> wrappedData = new HashMap<Integer, EnumerationViewAdapter>();
    private int maxRows = 15;
    private List<EnumerationViewAdapter> data = new ArrayList<EnumerationViewAdapter>();
    
    private BeanFinder beanFinder = new BeanFinder();
    

    public EnumerationQueueBean() {
    }
    
    public void clear() {
    	data.clear();
    }
    
    public String refresh() {   
    	this.clear();
    	return null; // AJAX action method
    }

    
    public void refresh(javax.faces.event.ActionEvent event) {
    	this.clear();
    }

    public int getMaxRows() {
		return maxRows;
	}

	public Query getQuery() {
    	SearchBean searchBean = this.beanFinder.findSearchBean();
        Query query = EnumerationViewQuery.createQuery();
    	
         return query; 
    }
	
    public List<EnumerationViewAdapter> getData() {
		this.data = new ArrayList<EnumerationViewAdapter>();
	    try {
	    	Query qry = getQuery();
	    			    	
	    	SDODataAccessClient service = new SDODataAccessClient();
	    	DataGraph[] results = service.find(qry);
	    	
	        for (int i = 0; i < results.length; i++) {
	        	EnumerationViewAdapter adapter = new EnumerationViewAdapter(
	        			(EnumerationView)results[i].getRootObject());
	        	data.add(adapter);
	        	wrappedData.put(new Integer(i), adapter); // assumes flat results set
	        }
	    }   
	    catch (Throwable t) {
	    	log.error(t.getMessage(), t);
	    }
    	return this.data;
    }

	public int getScrollerPage() {return scrollerPage;}
	public void setScrollerPage(int scrollerPage) {this.scrollerPage = scrollerPage;}

    /**
     * This method never called from framework.
     * (non-Javadoc)
     * @see org.ajax4jsf.model.ExtendedDataModel#getRowKey()
     */
    public Object getRowKey() {
    	if (currentPk != null) log.debug("getRowKey: " + currentPk.toString());
    	if (currentPk == null) log.debug("getRowKey: null");
        return currentPk;
    }
    
    
    /**
     * This method normally called by Visitor before request Data Row.
     */
    public void setRowKey(Object key) {
        this.currentPk = (Integer) key;
		if (currentPk != null) log.debug("setRowKey: " + currentPk.toString());
		if (currentPk == null) log.debug("setRowKey: null");
    }
	
	
    /**
     * This is main part of Visitor pattern. Method called by framework many times
     * during request processing. 
     */
    public void walk(FacesContext context, DataVisitor visitor, Range range, Object argument)
       throws IOException {
        int firstRow = ((SequenceRange)range).getFirstRow();
        int numberOfRows = ((SequenceRange)range).getRows();
		int lastRow = Math.min(firstRow + numberOfRows, getRowCount());
		log.debug("walk from: " + firstRow + " to " + lastRow);
		
		boolean alreadyRead = true;
		for (int i = firstRow; i < lastRow; i++)
		{
			if (wrappedData.get(new Integer(i)) == null)
			{
				alreadyRead = false;
				break;
			}
		}
		
		if (alreadyRead)
		{
			log.debug("Rows " + firstRow + " Thru " + lastRow + " Found In Cache");
		}
		else
//		if (!alreadyRead)
		{
			log.debug("Read DB For Rows " + firstRow + " Thru " + lastRow);
		    try {
		    	Query qry = getQuery();
		    	
		    	qry.setStartRange(firstRow);
		    	qry.setEndRange(firstRow + numberOfRows);
		    	
		    	SDODataAccessClient service = new SDODataAccessClient();
		    	DataGraph[] results = service.find(qry);
		    	
		        for (int i = 0; i < results.length; i++) {
		        	EnumerationViewAdapter adapter = new EnumerationViewAdapter(
		        			(EnumerationView)results[i].getRootObject());
		        	data.add(adapter);
		        	wrappedData.put(new Integer(i+firstRow), adapter); // assumes flat results set
		        }
		    }   
		    catch (Throwable t) {
		    	log.error(t.getMessage(), t);
		    }
		}
		
		for (int i = firstRow; i < lastRow; i++)
			visitor.process(context, new Integer(i), argument);
    }
    
    
	
	
    /**
     * This method must return actual data rows count from the Data Provider. It is used by
     * pagination control to determine total number of data items.
     */
    private Integer rowCount; // better to buffer row count locally
    public int getRowCount() {
        if (rowCount==null) {
    		Query qry = getQuery();
	    	SDODataAccessClient service = new SDODataAccessClient();
	    	rowCount = new Integer(service.count(qry));
        	log.debug("getRowCount DB Read: " + rowCount.toString());
            return rowCount.intValue();
        } else {
        	log.debug("getRowCount Cached: " + rowCount.toString());
            return rowCount.intValue();
        }
    }
	
	
    /**
     * This is main way to obtain data row. It is intensively used by framework. 
     * We strongly recommend use of local cache in that method. 
     */
    public EnumerationViewAdapter getRowData() {
        if (currentPk==null) {
        	log.debug("getRowData - currentPk: null");
            return null;
        } else {
        	EnumerationViewAdapter ret = wrappedData.get(currentPk);
        	log.debug("getRowData - currentPk: " + currentPk.toString());
        	if (ret == null)
	        	throw new IllegalStateException("cannot fetch wrapped data with pk of " + currentPk.toString());
            return ret;
        }
    }

	
    /**
     * Unused rudiment from old JSF staff.
     */
    public int getRowIndex() {
        throw new UnsupportedOperationException();
    }

    
    /**
     * Unused rudiment from old JSF staff.
     */
    public Object getWrappedData() {
        throw new UnsupportedOperationException();
    }

    
    /**
     * Never called by framework.
     */
    public boolean isRowAvailable() {
        if (currentPk==null) {
        	log.debug("isRowAvailable: false (currentPk is null)");
            return false;
        } else {
        	boolean isAvail = wrappedData.get(currentPk) != null;
            log.debug("isRowAvailable: " + isAvail + " (currentPk is " + currentPk.toString() + ")");
        	return isAvail;
        }
    }

    
    /**
     * Unused rudiment from old JSF staff.
     */
    public void setRowIndex(int rowIndex) {
        throw new UnsupportedOperationException();
    }

    
    /**
     * Unused rudiment from old JSF staff.
     */
    public void setWrappedData(Object data) {
        throw new UnsupportedOperationException();
    }

	
    /**
     * This method suppose to produce SerializableDataModel that will be serialized into View State
     * and used on a post-back. In current implementation we just mark current model as serialized.
     * In more complicated cases we may need to transform data to actually serialized form.
     */
    public  SerializableDataModel getSerializableModel(Range range) {
        if (!wrappedData.isEmpty()) {
            int firstRow = ((SequenceRange)range).getFirstRow();
            int numberOfRows = ((SequenceRange)range).getRows();
        	log.debug("getSerializableModel - range: " + firstRow + " - " + (firstRow + numberOfRows));
            return this; 
        } else {
        	log.debug("getSerializableModel - null");
            return null;
        }
    }
	
	
    /**
     * This is helper method that is called by framework after model update. In must delegate actual
     * database update to Data Provider.
     */
    public void update() {

    }
    
 }
