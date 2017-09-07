/*
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.tool.hbm2x.hbm2hbmxml.BackrefTest;

import java.io.File;
import java.util.Iterator;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.mapping.Backref;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tools.test.util.HibernateUtil;
import org.hibernate.tools.test.util.JUnitUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Dmitry Geraskov
 * @author koen
 */
public class TestCase {

	private static final String[] HBM_XML_FILES = new String[] {
			"Car.hbm.xml"
	};
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	private Metadata metadata = null;
	private File outputDir = null;
	private File resourcesDir = null;
	private Exporter hbmexporter = null;

	@Before
	public void setUp() throws Exception {
		outputDir = new File(temporaryFolder.getRoot(), "output");
		outputDir.mkdir();
		resourcesDir = new File(temporaryFolder.getRoot(), "resources");
		resourcesDir.mkdir();
		metadata = 
				HibernateUtil.initializeMetadata(this, HBM_XML_FILES, resourcesDir);
		hbmexporter = new HibernateMappingExporter();
		hbmexporter.setMetadata(metadata);
		hbmexporter.setOutputDirectory(outputDir);
		hbmexporter.start();
	}

	@Test
	public void testAllFilesExistence() {
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir,  
				"org/hibernate/tool/hbm2x/hbm2hbmxml/BackrefTest/Car.hbm.xml") );
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir,  
				"org/hibernate/tool/hbm2x/hbm2hbmxml/BackrefTest/CarPart.hbm.xml") );
	}
	
	@Test
	public void testBackrefPresent() {
		PersistentClass pc = metadata.getEntityBinding("org.hibernate.tool.hbm2x.hbm2hbmxml.BackrefTest.CarPart");
		Iterator<?> iterator = pc.getPropertyIterator();
		boolean hasBackrefs = false;
		while (iterator.hasNext() && !hasBackrefs) {
			hasBackrefs = (iterator.next() instanceof Backref);			
		}
		Assert.assertTrue("Class mapping should create Backref for this testcase", hasBackrefs);
	}
	
	@Test
	public void testReadable() {
		StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder();
		ssrb.applySetting(AvailableSettings.DIALECT, HibernateUtil.Dialect.class.getName());
        MetadataSources metadataSources = new MetadataSources(ssrb.build());
        metadataSources.addFile(new File(
        		outputDir, 
        		"org/hibernate/tool/hbm2x/hbm2hbmxml/BackrefTest/Car.hbm.xml"));
        metadataSources.addFile(new File(
        		outputDir, 
        		"org/hibernate/tool/hbm2x/hbm2hbmxml/BackrefTest/CarPart.hbm.xml"));
        Assert.assertNotNull(metadataSources.buildMetadata());
    }

}
