package net.stevenuray.walletexplorer.conversion.currency.test;

import net.stevenuray.walletexplorer.conversion.currency.CoindeskBpiConverter;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import junit.framework.TestCase;

public class CoindeskBpiConverterTest extends TestCase {

	//Integration test!
	@Test
	public void testCsvFileDownload(){
		@SuppressWarnings("unused")
		CoindeskBpiConverter converter = null;
		try{
			converter = new CoindeskBpiConverter();
		} catch(IllegalStateException e){
			e.printStackTrace();
			fail("Could not successfully download a csv file");
		}
	}
}
