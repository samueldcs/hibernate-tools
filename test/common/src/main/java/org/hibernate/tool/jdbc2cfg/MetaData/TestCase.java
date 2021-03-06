/*
 * Created on 2004-11-23
 *
 */
package org.hibernate.tool.jdbc2cfg.MetaData;

import org.hibernate.boot.Metadata;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tools.test.util.HibernateUtil;
import org.hibernate.tools.test.util.JUnitUtil;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author max
 * @author koen
 */
public class TestCase {

	private Metadata metadata = null;

	@Before
	public void setUp() {
		JdbcUtil.createDatabase(this);
		metadata = MetadataDescriptorFactory
				.createReverseEngineeringDescriptor(null, null)
				.createMetadata();
	}

	@After
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}

	@Test
	public void testBasic() {
		JUnitUtil.assertIteratorContainsExactly(
				"There should be three tables!", 
				metadata.getEntityBindings().iterator(),
				3);
		Table table = HibernateUtil.getTable(
				metadata, 
				JdbcUtil.toIdentifier(this, "BASIC" ) );
		Assert.assertEquals( 
				JdbcUtil.toIdentifier(this, "BASIC"), 
				JdbcUtil.toIdentifier(this, table.getName()));
		Assert.assertEquals( 2, table.getColumnSpan() );
		Column basicColumn = table.getColumn( 0 );
		Assert.assertEquals( 
				JdbcUtil.toIdentifier(this, "A"), 
				JdbcUtil.toIdentifier(this, basicColumn.getName()));
		PrimaryKey key = table.getPrimaryKey();
		Assert.assertNotNull( "There should be a primary key!", key );
		Assert.assertEquals( key.getColumnSpan(), 1 );
		Column column = key.getColumn( 0 );
		Assert.assertTrue( column.isUnique() );
		Assert.assertSame( basicColumn, column );
	}

	@Test
	public void testScalePrecisionLength() {
		Table table = HibernateUtil.getTable(
				metadata, 
				JdbcUtil.toIdentifier(this, "BASIC"));
		Column nameCol = table.getColumn(new Column( JdbcUtil.toIdentifier(this, "NAME" ) ) );
		Assert.assertEquals( nameCol.getLength().intValue(), 20 );
		Assert.assertEquals( nameCol.getPrecision(), null );
		Assert.assertEquals( nameCol.getScale(), null );
	}

	@Test
	public void testCompositeKeys() {
		Table table = HibernateUtil.getTable(
				metadata, 
				JdbcUtil.toIdentifier(this, "MULTIKEYED"));
		PrimaryKey primaryKey = table.getPrimaryKey();
		Assert.assertEquals( 2, primaryKey.getColumnSpan() );
	}

}
