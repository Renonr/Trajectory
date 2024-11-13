package ru.nic.trajectory;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class MockTable {
	
	private DefaultTableModel mockModel = new DefaultTableModel(new Object[]{"Секунда", "X", "Y", "Z", "Vx", "Vy", "Vz"}, 0);
	private JTable mockTable = new JTable(mockModel);
	private TableRowSorter<DefaultTableModel> mockTableSorter = new TableRowSorter<DefaultTableModel>();
	
	MockTable(String pathToDataFile){
		FileHandler fileHandler = new FileHandler(pathToDataFile);
		
		Trajectory trajectory = new Trajectory();
		try {
			trajectory.setAllTrajectoryData(fileHandler.getAllFileStrings());
			Set<Entry<Double, List<Double>>> entryData = trajectory.getNormalTrajectoryData().entrySet();
			for (Entry<Double, List<Double>> entry : entryData) {
	        	double second = entry.getKey();
	        	mockModel.addRow(new Object[] {
	        			second,
	        			trajectory.getX(second),
	        			trajectory.getY(second),
	        			trajectory.getZ(second),
	        			trajectory.getSpeedX(second),
	        			trajectory.getSpeedY(second),
	        			trajectory.getSpeedZ(second)
	        	});
			}
		} catch (IOException e) {
			// TODO Автоматически созданный блок catch
		}
		
		mockModel.setRowCount(0);
		
		Set<Entry<Double, List<Double>>> entryData = trajectory.getNormalTrajectoryData().entrySet();
		for (Entry<Double, List<Double>> entry : entryData) {
        	double second = entry.getKey();
        	mockModel.addRow(new Object[] {
        			second,
        			trajectory.getX(second),
        			trajectory.getY(second),
        			trajectory.getZ(second),
        			trajectory.getSpeedX(second),
        			trajectory.getSpeedY(second),
        			trajectory.getSpeedZ(second)
        	});
		}
		
		mockTableSorter.setModel(mockModel);
		mockTableSorter.setSortKeys(java.util.Arrays.asList(new RowSorter.SortKey(0, SortOrder.ASCENDING)));
		
		mockTableSorter.setSortable(0, false);
		mockTableSorter.setSortable(1, false);
		mockTableSorter.setSortable(2, false);
		mockTableSorter.setSortable(3, false);
		mockTableSorter.setSortable(4, false);
		mockTableSorter.setSortable(5, false);
		mockTableSorter.setSortable(6, false);
		
		mockTable.setRowSorter(mockTableSorter);
	}
	
	public void insertRow(int selectedRow, boolean above) {
        if (selectedRow != -1) {
        	int insertIndex = above ? selectedRow : selectedRow + 1;
        	if (selectedRow > 0 && selectedRow < mockTable.getRowCount() - 1) {
            	Double newSecond = 0.0;
            	if (above) {
            		newSecond = ((Double) mockTable.getValueAt(selectedRow, 0) +
            				(Double) mockTable.getValueAt(selectedRow-1, 0)) / 2;
            	}
            	else {
            		newSecond = ((Double) mockTable.getValueAt(selectedRow, 0) +
            				(Double) mockTable.getValueAt(selectedRow+1, 0)) / 2;
            	}        
            	
            	mockModel.insertRow(insertIndex, new Object[]{newSecond, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0});
        	}
        	else if (selectedRow == 0) {
        		mockModel.insertRow(insertIndex, new Object[]{(Double) mockTable.getValueAt(selectedRow, 0), 0.0, 0.0, 0.0, 0.0, 0.0, 0.0});
        		for (int i = 1; i < mockTable.getRowCount(); i++) {
        			mockTable.setValueAt((Double) mockTable.getValueAt(i, 0) + 1, i, 0);
        		}
        		
        	}
        	else if (selectedRow == mockTable.getRowCount() - 1) {
        		mockModel.insertRow(insertIndex, new Object[]{(Double) mockTable.getValueAt(selectedRow, 0) + 1, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0});
        	}
        	mockTableSorter.sort();
        }
    }
	
	public Double getTime(int rowIndex) {
		return (Double) mockTable.getValueAt(rowIndex, 0);
	}
	
	public int getRowCount() {
		return mockTable.getRowCount();
	}
	
	public Double getColumnMean(int column) {
    	Double mean = null;
    	
    	if (column > 0 && column < 7) {
    		mean = 0.0;
    		for (int i = 0; i < mockTable.getRowCount(); i++) {
    			mean += (Double) mockTable.getValueAt(i, column);
    		}
    		mean = mean / mockTable.getRowCount();
    	}
    	
    	return mean;
    }
    
    public Double getDispersion(int column) {
    	Double dispersion = null;
    	Double mean = getColumnMean(column);
    	
    	if (column > 0 && column < 7) {
    		dispersion = 0.0;
    		for (int i = 0; i < mockTable.getRowCount(); i++) {
    			dispersion += Math.pow(((Double) mockTable.getValueAt(i, column) - mean), 2);
    		}
    		dispersion = dispersion / mockTable.getRowCount();
    	}
    	
    	return dispersion;
    }
    
    public Double getSecondMoment(int column) {
    	Double secondMomen = null;
    	
    	if (column > 0 && column < 7) {
    		secondMomen = 0.0;
    		for (int i = 0; i < mockTable.getRowCount(); i++) {
    			secondMomen += Math.pow((Double) mockTable.getValueAt(i, column), 2);
    		}
    		secondMomen = secondMomen / mockTable.getRowCount();
    	}
    	
    	return secondMomen;
    }
    
    public Double getThirdMoment(int column) {
    	Double thirdMomen = null;
    	
    	if (column > 0 && column < 7) {
    		thirdMomen = 0.0;
    		for (int i = 0; i < mockTable.getRowCount(); i++) {
    			thirdMomen += Math.pow((Double) mockTable.getValueAt(i, column), 3);
    		}
    		thirdMomen = thirdMomen / mockTable.getRowCount();
    	}
    	
    	return thirdMomen;
    }
}
