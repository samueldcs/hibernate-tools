/*
 * Created on 26-Nov-2004
 *
 */
package org.hibernate.cfg;

import org.dom4j.Element;
import org.hibernate.MappingException;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.internal.MetadataBuilderImpl.MetadataBuildingOptionsImpl;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataBuildingOptions;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.id.factory.IdentifierGeneratorFactory;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author max
 *
 */
public class JDBCMetaDataConfiguration extends Configuration {

    private static final Logger log = LoggerFactory.getLogger(JDBCMetaDataConfiguration.class);
	protected ReverseEngineeringStrategy revEngStrategy = new DefaultReverseEngineeringStrategy();
	protected StandardServiceRegistry serviceRegistry = null;
	
	protected MetadataBuildingOptions metadataBuildingOptions = null;
	
	protected MetadataBuildingOptions getMetadataBuildingOptions() {
		if (metadataBuildingOptions == null) {
			metadataBuildingOptions = 
					new MetadataBuildingOptionsImpl( getServiceRegistry() );
		}
		return metadataBuildingOptions;
	}
	
	public StandardServiceRegistry getServiceRegistry(){
		if(serviceRegistry == null){
			serviceRegistry = new StandardServiceRegistryBuilder()
				.applySettings(getProperties())
				.build();
		}
		return serviceRegistry;
	}
	
	static protected Mapping buildMapping(final Metadata metadata) {
		return new Mapping() {
			/**
			 * Returns the identifier type of a mapped class
			 */
			public Type getIdentifierType(String persistentClass) throws MappingException {
				final PersistentClass pc = metadata.getEntityBinding(persistentClass);
				if (pc==null) throw new MappingException("persistent class not known: " + persistentClass);
				return pc.getIdentifier().getType();
			}

			public String getIdentifierPropertyName(String persistentClass) throws MappingException {
				final PersistentClass pc = metadata.getEntityBinding(persistentClass);
				if (pc==null) throw new MappingException("persistent class not known: " + persistentClass);
				if ( !pc.hasIdentifierProperty() ) return null;
				return pc.getIdentifierProperty().getName();
			}

            public Type getReferencedPropertyType(String persistentClass, String propertyName) throws MappingException
            {
				final PersistentClass pc = metadata.getEntityBinding(persistentClass);
				if (pc==null) throw new MappingException("persistent class not known: " + persistentClass);
				Property prop = pc.getProperty(propertyName);
				if (prop==null)  throw new MappingException("property not known: " + persistentClass + '.' + propertyName);
				return prop.getType();
			}

			public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
				return null;
			}
		};
	}

	
	private boolean ignoreconfigxmlmapppings = true;
	// set to true and fk's that are part of a primary key will just be mapped as the raw value and as a readonly property. if false, it will be <many-to-one-key-property
    private boolean preferBasicCompositeIds = true;
	
    /**
     * If true, compositeid's will not create key-many-to-one and
     * non-updatable/non-insertable many-to-one will be created instead. 
     * @return
     */
    public boolean preferBasicCompositeIds() {
        return preferBasicCompositeIds ;
    }    
   
    public void setPreferBasicCompositeIds(boolean flag) {
        preferBasicCompositeIds = flag;
    }
	    
    protected void parseMappingElement(Element subelement, String name) {
        if(!ignoreconfigxmlmapppings ) {  
        	//FIXME the method is private
           // super.parseMappingElement(subelement, name);
        } 
        else {
            log.info("Ignoring " + name + " mapping");
        }
    }

	public void setReverseEngineeringStrategy(ReverseEngineeringStrategy reverseEngineeringStrategy) {
		this.revEngStrategy = reverseEngineeringStrategy;		
	}
	
	public ReverseEngineeringStrategy getReverseEngineeringStrategy() {
		return revEngStrategy;
	}

}
