package org.cloudgraph.config;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.plasma.sdo.core.CoreConstants;
import org.plasma.sdo.helper.PlasmaTypeHelper;

import org.cloudgraph.config.ColumnKeyToken;
import org.cloudgraph.config.DataGraph;
import org.cloudgraph.config.PreDefinedTokenName;
import org.cloudgraph.config.RowKeyToken;
import org.cloudgraph.config.UserDefinedToken;

import commonj.sdo.Type;

public class DataGraphConfig {
    private DataGraph graph;
    private Map<PreDefinedTokenName, RowKeyToken> preDefinedRowKeyTokens = new HashMap<PreDefinedTokenName, RowKeyToken>(); 
    private Map<PreDefinedTokenName, ColumnKeyToken> preDefinedColumnKeyTokens = new HashMap<PreDefinedTokenName, ColumnKeyToken>(); 
	private List<UserDefinedTokenConfig> userDefinedRowKeyTokens = new ArrayList<UserDefinedTokenConfig>();
	private Map<String, UserDefinedTokenConfig> pathTouserDefinedRowKeyMap = new HashMap<String, UserDefinedTokenConfig>();
	private byte[] rowKeyFieldDelimiterBytes;
	private byte[] rowKeySectionDelimiterBytes;
	private byte[] columnKeyFieldDelimiterBytes;
	private byte[] columnKeySectionDelimiterBytes;
    
    @SuppressWarnings("unused")
	private DataGraphConfig() {}
	public DataGraphConfig(DataGraph graph) {
		super();
		this.graph = graph;
		for (RowKeyToken token : this.graph.getRowKeyModel().getRowKeyTokens())
			preDefinedRowKeyTokens.put(token.getName(), token);
		
		for (ColumnKeyToken ctoken : this.graph.getColumnKeyModel().getColumnKeyTokens())
			preDefinedColumnKeyTokens.put(ctoken.getName(), ctoken);
		
	    for (UserDefinedToken token : this.graph.getRowKeyModel().getUserDefinedTokens())
	    {
	    	UserDefinedTokenConfig tokenConfig = new UserDefinedTokenConfig(this, token);
	    	userDefinedRowKeyTokens.add(tokenConfig);
	    	if (this.pathTouserDefinedRowKeyMap.get(tokenConfig.getPropertyPath()) != null) 
	    		throw new CloudGraphConfigurationException("a user defined token path '" 
	    				+ tokenConfig.getPathExpression()
	    				+ "' already exists with property path '"
	    				+ tokenConfig.getPropertyPath()
	    				+ "' for data graph of type, "
	    				+ this.graph.getUri() + "#" + this.graph.getType());
	    	this.pathTouserDefinedRowKeyMap.put(tokenConfig.getPropertyPath(), tokenConfig);
	    }
	}

	public DataGraph getGraph() {
		return this.graph;
	}
	
	public Type getRootType() {
		return PlasmaTypeHelper.INSTANCE.getType(
				this.graph.getUri(), this.graph.getType());
	}
	
	public List<RowKeyToken> getPreDefinedRowKeyTokens()
	{
		return graph.getRowKeyModel().getRowKeyTokens();
	}
	
	public RowKeyToken getRowKeyToken(PreDefinedTokenName name) {
		return preDefinedRowKeyTokens.get(name);
	}
	
	public String getRowKeyFieldDelimiter() {
        return this.graph.getRowKeyModel().getFieldDelimiter();		
	}
	
	public String getRowKeySectionDelimiter() {
        return this.graph.getRowKeyModel().getSectionDelimiter();		
	}
	
	public byte[] getRowKeyFieldDelimiterBytes() {
		if (rowKeyFieldDelimiterBytes == null) {
			this.rowKeyFieldDelimiterBytes = this.graph.getRowKeyModel().getFieldDelimiter().getBytes(
	        		Charset.forName( CoreConstants.UTF8_ENCODING ));
		}
        return rowKeyFieldDelimiterBytes;		
	}
	
	public byte[] getRowKeySectionDelimiterBytes() {
		if (rowKeySectionDelimiterBytes == null) {
			this.rowKeySectionDelimiterBytes = this.graph.getRowKeyModel().getSectionDelimiter().getBytes(
	        		Charset.forName( CoreConstants.UTF8_ENCODING ));
		}
        return rowKeySectionDelimiterBytes;		
	}

	public boolean hasUserDefinedRowKeyTokens() {
		return this.graph.getRowKeyModel().getUserDefinedTokens().size() > 0;
	}
	
	public List<UserDefinedTokenConfig> getUserDefinedRowKeyTokens() {
		return userDefinedRowKeyTokens;
	}
	
	public UserDefinedTokenConfig getUserDefinedRowKeyToken(String path) {
		return this.pathTouserDefinedRowKeyMap.get(path);
	}

	public ColumnKeyToken getColumnKeyToken(PreDefinedTokenName name) {
		return preDefinedColumnKeyTokens.get(name);
	}
	
	public String getColumnKeyFieldDelimiter() {
        return this.graph.getColumnKeyModel().getFieldDelimiter();		
	}
	
	public String getColumnKeySectionDelimiter() {
        return this.graph.getColumnKeyModel().getSectionDelimiter();		
	}
	
	public byte[] getColumnKeyFieldDelimiterBytes() {
		if (columnKeyFieldDelimiterBytes == null) {
			this.columnKeyFieldDelimiterBytes = this.graph.getColumnKeyModel().getFieldDelimiter().getBytes(
	        		Charset.forName( CoreConstants.UTF8_ENCODING ));
		}
        return columnKeyFieldDelimiterBytes;		
	}

	public byte[] getColumnKeySectionDelimiterBytes() {
		if (columnKeySectionDelimiterBytes == null) {
			this.columnKeySectionDelimiterBytes = this.graph.getColumnKeyModel().getSectionDelimiter().getBytes(
	        		Charset.forName( CoreConstants.UTF8_ENCODING ));
		}
        return columnKeySectionDelimiterBytes;		
	}
}