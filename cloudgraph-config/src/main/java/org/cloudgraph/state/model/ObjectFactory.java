//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.11.12 at 10:29:26 AM EST 
//


package org.cloudgraph.state.model;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.cloudgraph.state package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.cloudgraph.state
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link URIMap }
     * 
     */
    public URIMap createURIMap() {
        return new URIMap();
    }

    /**
     * Create an instance of {@link StateModel }
     * 
     */
    public StateModel createCloudGraphState() {
        return new StateModel();
    }

    /**
     * Create an instance of {@link TypeMapEntry }
     * 
     */
    public TypeMapEntry createTypeMapEntry() {
        return new TypeMapEntry();
    }

    /**
     * Create an instance of {@link TypeMap }
     * 
     */
    public TypeMap createTypeMap() {
        return new TypeMap();
    }

    /**
     * Create an instance of {@link SequenceMap }
     * 
     */
    public SequenceMap createSequenceMap() {
        return new SequenceMap();
    }

    /**
     * Create an instance of {@link RowKeyMap }
     * 
     */
    public RowKeyMap createRowKeyMap() {
        return new RowKeyMap();
    }

    /**
     * Create an instance of {@link RowKeyMapEntry }
     * 
     */
    public RowKeyMapEntry createRowKeyMapEntry() {
        return new RowKeyMapEntry();
    }

}
