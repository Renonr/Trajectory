package ru.nic.trajectory;

import org.junit.Assert;
import org.junit.Test;


public class AppTest 
{
	private String testSet1 = "C:\\Users\\ikovalev\\SwingTest\\diagrams\\src\\test\\java\\ru\\test\\diagrams\\testSet1.txt";
	
    @Test
    public void testStatistics() {
    	MockTable mockTable = new MockTable(testSet1);
    	
    	Double delta = 0.05;
    	
    	Double expectedXColumnMean = 5.0;
    	Double actualXColumnMean = mockTable.getColumnMean(1);
    	
    	Double expectedVxColumnDisperion = 2.0;
    	Double actualVxCoulumnDispersion = mockTable.getDispersion(4);
    	
    	Double expectedVzColumnSecondMoment = 272.8;
    	Double actualVzColumnSecondMoment = mockTable.getSecondMoment(6);
    	
    	Double expectedVyColumnThirdMoment = 117.025;
    	Double actualVyColumnThirdMoment = mockTable.getThirdMoment(5);
    	
    	Assert.assertEquals(expectedXColumnMean, actualXColumnMean, delta);
    	Assert.assertEquals(expectedVxColumnDisperion, actualVxCoulumnDispersion);
    	Assert.assertEquals(expectedVzColumnSecondMoment, actualVzColumnSecondMoment);
    	Assert.assertEquals(expectedVyColumnThirdMoment, actualVyColumnThirdMoment);
    }
    
    @Test
    public void testRowCountAfterInsert() {
    	MockTable mockTable = new MockTable(testSet1);
    	
    	int expectedRowCount = 9;
    	
    	mockTable.insertRow(0, true);
    	mockTable.insertRow(4, false);
    	mockTable.insertRow(2, true);
    	mockTable.insertRow(2, false);
    	
    	int actualRowCount = mockTable.getRowCount();
    	
    	Assert.assertEquals(expectedRowCount, actualRowCount);
    }
    
    @Test
    public void testSort() {
    	MockTable mockTable = new MockTable(testSet1);
    	
    	int startRowCount = mockTable.getRowCount();
    	
    	mockTable.insertRow(2, false);
    	mockTable.insertRow(2, true);
    	
    	for (int i = 1; i <= startRowCount; i++) {   		
    		Assert.assertTrue(mockTable.getTime(i) > mockTable.getTime(i-1));
    	}
    }
    
    @Test
    public void testInsertFirstRow() {
    	MockTable mockTable = new MockTable(testSet1);
    	
    	int rowCount = mockTable.getRowCount();
    	
    	mockTable.insertRow(0, true);
    	
    	Double[] expectedRows = {1.0, 2.0, 6.0};
    	Double[] actualRows = {mockTable.getTime(0), mockTable.getTime(1), mockTable.getTime(rowCount)};
    	
    	Assert.assertArrayEquals(expectedRows, actualRows);
    }
    
    @Test
    public void testInsertLastRow() {
    	MockTable mockTable = new MockTable(testSet1);
    	
    	int rowCount = mockTable.getRowCount();
    	
    	mockTable.insertRow(rowCount-1, false);
    	
    	Double[] expectedRows = {5.0, 6.0};
    	Double[] actualRows = {mockTable.getTime(rowCount-1), mockTable.getTime(rowCount)};
    	
    	Assert.assertArrayEquals(expectedRows, actualRows);
    }
    
    @Test
    public void testNewTime() {
    	MockTable mockTable = new MockTable(testSet1);
    	
    	Double expectedAbove = 3.5;
    	Double expectedBelow = 1.5;
    	
    	mockTable.insertRow(2, false);
    	mockTable.insertRow(1, true);
    	
    	Double actualAbove = mockTable.getTime(4);
    	Double actualBelow = mockTable.getTime(1);
    	
    	Assert.assertEquals(expectedAbove, actualAbove);
    	Assert.assertEquals(expectedBelow, actualBelow);
    }
}
