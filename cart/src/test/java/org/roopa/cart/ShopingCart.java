package org.roopa.cart;

/************************************************************************************************
 * Project: Selenium WebDriver Automation Assessment 
 * Version: v1.0 
 * Author:Roopa Patil
 * Date:1/22/2017
 * Description:This is a test script to add three items in to the cart ,then checkout and verify
 * CA Tax And Grand Total.
 * *
 **************************************************************************************************/

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.opencsv.CSVReader;

public class ShopingCart {

	private WebDriver driver;
	private Properties prop;
	
	/**
	 * Loads the properties from cart.properties file 
	 * */
	
	public void loadProperties(){
		try{
			
		prop = new Properties();
		
		File f=new File("cart.properties");
		InputStream stream= new FileInputStream(f);
		prop.load(stream);	
	} catch (IOException io){
		System.out.println("File not exists...");
	}
		
	}
	
	/**
	 * Data provider: Created and Provides the order data (List of products ,unique web element identifier 
	 * and quantity).
	 * */	

	@DataProvider(name="orderlist")
	public Object[][] getOrderData(){

		return new Object[][]{
			{"Kohler K66266U","a.swatch:nth-child(14)","1"},
			{"Kohler K66266U","a.swatch:nth-child(7)","1"},
			{"Kohler K-5180-ST","","2"}
		};
	}

	/**
	 * Data provider: Reads the order data (List of products ,unique web element identifier 
	 * and quantity) from csv file 
	 * */	

	@DataProvider(name="orderlistcsv")
	public Object[][] getOrderDatacsv(){
		Object[][] csvObject = null ;
		try {
			CSVReader reader = new CSVReader(new FileReader("orderlist.csv"));
			List<String[]> list = reader.readAll();
			csvObject=new Object[list.size()][];
			csvObject=list.toArray(csvObject);
			reader.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found"+e);
		} catch(IOException e){
			System.out.println("IO Exception"+e);
		}
		
		return csvObject;	
	}
	
	/**
	 *   Starting the home page of Build.com
	 */


	@BeforeTest
	public void getUrl() {
		//System.out.println("Running --->:["+this.getClass().getName()+"]");
		loadProperties();
		if(prop.getProperty("browser").equalsIgnoreCase("FireFox")){
			driver = new FirefoxDriver();
		}else if(prop.getProperty("browser").equalsIgnoreCase("Chrome")){
			//TO DO
		}else if(prop.getProperty("browser").equalsIgnoreCase("IE")){
			//TO DO
		} else {
			System.out.println("Driver not found");
		}
		
		driver.get(prop.getProperty("url"));
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.MINUTES);
	}

	/**
	 * Searching the items and adding them to the cart.
	 * @param ProductId
	 * @param subProdId
	 * @param Qty
	 */


	@Test(dataProvider="orderlistcsv" )
	public void searchAndAdd(String ProductId,String subProdId,String Qty){	
		getElementById(prop.getProperty("searchbar")).sendKeys(ProductId);
		getElementById(prop.getProperty("searchbar")).submit();
		if(Integer.parseInt(Qty)>1){
			getElementByCss(prop.getProperty("inputqty")).clear();
			getElementByCss(prop.getProperty("inputqty")).click();
		}

		if(subProdId.isEmpty()){
			getElementByCss(prop.getProperty("addtocart")).click();
		} else {
			getElementByCss(subProdId).click();
			getElementByCss(prop.getProperty("addtocart2")).click();
		}
	}

	/**
	 * Checkout flow.
	 * Adding shipping address,phone number ,email and payment information
	 */

	@Test(dependsOnMethods={"searchAndAdd"})
	public void checkOut(){

		getElementByCss(prop.getProperty("btncheckout")).click();
		getElementByCss(prop.getProperty("btnguestlogin")).click();

		/**
		 * Entering Shipping address information
		 */
		getElementById(prop.getProperty("shipfname")).sendKeys(prop.getProperty("shipfnameval"));
		getElementById(prop.getProperty("shipfname")).submit();
		getElementById(prop.getProperty("shiplname")).sendKeys(prop.getProperty("shiplnameval"));
		getElementById(prop.getProperty("shiplname")).submit();	
		getElementById(prop.getProperty("shipadd1")).sendKeys(prop.getProperty("shipadd1val"));
		getElementById(prop.getProperty("shipadd1")).submit();
		getElementById(prop.getProperty("shipzip")).sendKeys(prop.getProperty("shipzipval"));
		getElementById(prop.getProperty("shipzip")).submit();	
		getElementById(prop.getProperty("shipcity")).sendKeys(prop.getProperty("shipcityval"));
		getElementById(prop.getProperty("shipcity")).submit();
		Select state = new Select(getElementById(prop.getProperty("shipstate")));
		state.selectByVisibleText(prop.getProperty("shipstateval"));
		getElementById(prop.getProperty("shipstate")).submit();		


		/**
		 * Entering phone no
		 */
		String val = prop.getProperty("shipphoneval"); 
		WebElement element = getElementById(prop.getProperty("shipphone"));
		
		element.clear();
		for (int i = 0; i < val.length(); i++){
			char c = val.charAt(i);
			String s = new StringBuilder().append(c).toString();
			element.sendKeys(s);
		} 
		getElementById(prop.getProperty("shipphone")).submit();

		/**
		 * Entering email 
		 */
		getElementById(prop.getProperty("email")).sendKeys(prop.getProperty("emailval"));
		getElementById(prop.getProperty("email")).submit();
		
		/**
		 * Entering Payment information
		 */
	
		getElementById(prop.getProperty("creditcardno")).sendKeys(prop.getProperty("creditcardval"));
		getElementById(prop.getProperty("creditcardno")).submit();
		Select month = new Select(getElementById(prop.getProperty("creditmonth")));
		month.selectByVisibleText(prop.getProperty("creditmonthval"));
		getElementById(prop.getProperty("creditmonth")).submit();
		Select year = new Select(getElementById(prop.getProperty("credityear")));
		year.selectByVisibleText(prop.getProperty("credityearval"));
		getElementById(prop.getProperty("creditname")).sendKeys(prop.getProperty("creditnameval"));
		getElementById(prop.getProperty("creditname")).submit();
		getElementById(prop.getProperty("creditcvv")).sendKeys(prop.getProperty("creditcvvval"));
		getElementByCss(prop.getProperty("btncheckoutreview")).submit();
		
	}

	/**
	 * Fetching SubTotal value from the order review page
	 * @return
	 */

	public float getSubTotal(){
		String subTotal=getElementById(prop.getProperty("subamt")).getAttribute(prop.getProperty("subamtval"));
		float subTotalValue=Float.parseFloat(subTotal);
		return subTotalValue;
	}

	/**
	 * Fetching tax value from the order review page
	 * @return
	 */

	public float getTax(){
		String tax = getElementById(prop.getProperty("taxamt")).getAttribute(prop.getProperty("taxamtval"));
		float taxValue=Float.parseFloat(tax);
		return taxValue;
	}

	/**
	 * Fetching Grand total value from the order review page
	 * @return
	 */

	public float getGrandTotal(){ 
		String grandTotal = getElementById(prop.getProperty("grandtot")).getText().toString();
		grandTotal=grandTotal.substring(1,grandTotal.length()).replace(",", "");
		float grandTotalValue =Float.parseFloat(grandTotal);
		return grandTotalValue;
	}

	/**
	 * Fetching the Shipping value from the order review page
	 * @return
	 */

	public float getShipping(){
		String shipping = getElementById(prop.getProperty("shipamt")).getText().toString();
		float shippingCost=0;

		if(!(shipping.contains(prop.getProperty("freeship")))){
			shipping = shipping.substring(1, shipping.length()).replace(",", "");
			shippingCost = Float.parseFloat(shipping);
		}
		return shippingCost;
	}

	/**
	 * Assertion of the CA Tax
	 */


	@Test(dependsOnMethods={"checkOut"},priority=1)
	public void assertionTax(){
		try{
			Assert.assertEquals(getTax(),(getSubTotal()*0.0725), 0.2);
		}
		catch(AssertionError ae)
		{
			Assert.fail("Assertion of CA Tax failed"+getTax());
		}

	}

	/**
	 * Assertion of GrandTotal
	 */

	@Test(dependsOnMethods={"checkOut"},priority=2)
	public void assertionGrandTotal(){
		try{
			Assert.assertEquals(getGrandTotal(),(getSubTotal()+getShipping()+getTax()), 0.2);
		}
		catch (AssertionError ae) {
			Assert.fail("GrandTotal Assertion Failed GradTotal: "+getGrandTotal());
		}

	}

	/**
	 * 	Negative-Test Case 
	 */ 

	/*  @Test(dependsOnMethods={"checkOut"},priority=3)
	public void assertionTaxFailed(){
		try{
			Assert.assertEquals(getTax(),(getSubTotal()*0.9), 0.2);
		}
		catch (AssertionError ae) {
			Assert.fail("Assert Tax Failed : "+getTax());
		}
	}*/


	/**
	 * Ending the test, housekeeping tasks.
	 */
	@AfterTest
	public void completeTest(){
		driver.close();
	}
	
	/**
	 * Search and provide webelement for given id.
	 */
	public WebElement getElementById(String s){
		WebElement wElement =null;
		try{
			wElement=driver.findElement(By.id(s));
		} catch(AssertionError ae){
			Assert.fail("WebElemnt not found :"+s);
		}
		return wElement;
	}
	
	/**
	 * Search and provide webelement for given css selector.
	 */
	
	public WebElement getElementByCss(String s){
		WebElement wElement =null;
		try{
			wElement=driver.findElement(By.cssSelector(s));
		} catch(AssertionError ae){
			Assert.fail("WebElemnt not found :"+s);
		}
		return wElement;
	}
	
	
	
}



