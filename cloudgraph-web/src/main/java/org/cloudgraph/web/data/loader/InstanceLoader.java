package org.cloudgraph.web.data.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.plasma.sdo.PlasmaChangeSummary;
import org.plasma.sdo.access.client.SDODataAccessClient;
import org.plasma.sdo.helper.PlasmaQueryHelper;
import org.plasma.sdo.helper.PlasmaXMLHelper;
import org.plasma.sdo.xml.DefaultOptions;

import org.cloudgraph.web.export.instance.InstanceSpecification;
import org.cloudgraph.web.export.instance.Package;
import org.cloudgraph.web.export.instance.query.QPackage;
import org.cloudgraph.web.export.instance.Clazz;
import org.cloudgraph.web.export.instance.query.QClazz;

import commonj.sdo.DataGraph;
import commonj.sdo.DataObject;
import commonj.sdo.helper.XMLDocument;

public class InstanceLoader extends AbstractLoader 
    implements Loader
{
    private static Log log = LogFactory.getLog(InstanceLoader.class);
	protected SDODataAccessClient service = new SDODataAccessClient();
	
    @Override
	public void define(File queryFile) {
    	
	    log.info("defining Query so new export-specific types are known");
	    InputStream stream;
		try {
			stream = new FileInputStream(queryFile);
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	    PlasmaQueryHelper.INSTANCE.define(stream, 
        		"http://apls/export/datatype",
        		"http://org.cloudgraph/web/meta");
	}
	
	@Override
	public void load(File file) {
        log.info("loading data");
        
        DefaultOptions options = new DefaultOptions("");
        options.setRootNamespacePrefix("xyz");
        options.setValidate(false);
        options.setFailOnValidationError(false);
        
        InputStream xmlloadis;
		try {
			xmlloadis = new FileInputStream(file);
			XMLDocument doc = PlasmaXMLHelper.INSTANCE.load(xmlloadis, 
					null, options);
			
			InstanceSpecification inst = (InstanceSpecification)doc.getRootObject();
			PlasmaChangeSummary changeSummary = (PlasmaChangeSummary)inst.getDataGraph().getChangeSummary();
            Package pkg = inst.getPackageableType().get_package();
            changeSummary.clear(pkg);
            pkg = this.fetchPackage(pkg.getExternalId());
            inst.getPackageableType().set_package(pkg);

			Clazz clazz = inst.getClazz();
			changeSummary.clear(clazz); // remove created info from change summary
			clazz = fetchClazz(clazz.getExternalId());			
			inst.unsetClazz();
			inst.setClazz(clazz);
            
            SDODataAccessClient service = new SDODataAccessClient();
			service.commit(doc.getRootObject().getDataGraph(), 
					"dataloader");
			
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} 

        //doc.setSchemaLocation(options.getRootElementNamespaceURI()
        //		+ " " + prefix + ".xsd");
	}

    protected Package fetchPackage(String uuid) {
		
		QPackage query = QPackage.newQuery();
		query.select(query.seqId());
		query.where(query.externalId().eq(uuid));
		
		DataGraph[] results = service.find(query);
		Package result = (Package)results[0].getRootObject();
		result.setDataGraph(null); // so can re parent
		return result;
	}
    
    protected Clazz fetchClazz(String uuid) {
		
		QClazz query = QClazz.newQuery();
		query.select(query.seqId());
		query.where(query.externalId().eq(uuid));
		
		DataGraph[] results = service.find(query);
		Clazz result = (Clazz)results[0].getRootObject();
		result.setDataGraph(null); // so can re parent
		return result;
	}
}