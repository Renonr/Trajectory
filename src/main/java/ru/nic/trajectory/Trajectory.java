package ru.nic.trajectory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Trajectory 
{	
	private HashMap<Double, List<Double>> trajectoryNormalData = new HashMap<>(); //хранит данные в формате секунда: данные траектории
	private List<String> trajectoryData = new ArrayList<>(); //хранит данные в виде массива строк
	
	public void addNormalTrajectoryData(Double second, List<Double> values) //добавляет строку в hashmap
	{
		this.trajectoryNormalData.put(second, values);
	}
	
	public void setTrajectoryNormalData(HashMap<Double, List<Double>> trajectoryData)  // задаёт новый hashmap
	{
		this.trajectoryNormalData = trajectoryData;
	}
	
	public HashMap<Double, List<Double>> getNormalTrajectoryData(){ //Получает hashmap с данными о траектории
		return this.trajectoryNormalData;
	}
	
	public void setAllTrajectoryData(List<String> trajectoryData) { //Метод, который присваивает значения траектории в массив строк
		this.setTrajectoryData(trajectoryData);						// И преобразует его в удобный формат данных (hashmap)
		
		Double second;		
		
		for (String dataString: trajectoryData) 
		{
			List<Double> values = new ArrayList<>();
			
			second = Double.parseDouble(dataString.split(" ")[0]);		
			Arrays.asList(dataString.split(" ")).stream()
												.filter(x -> !x.isEmpty())
											    .forEach(n -> values.add(Double.parseDouble(n)));
			
			this.trajectoryNormalData.put(second, values.subList(1, values.size()));
		}
	}

	public List<String> getTrajectoryData() { //Метод возвращающий данные о траектории в формате строкового массива
		this.trajectoryData.clear();
		
		for (Map.Entry<Double, List<Double>> entry : this.trajectoryNormalData.entrySet()) { //данный цикл нужен для того, чтобы
			String dataRow = entry.getKey().toString();										 //преобразовать данные из hashmap в List<Sting>,
			for (Double value : entry.getValue()) {											 //т.к. если надо будет перезаписать файл, то 
				dataRow += (" " + value.toString());										 //при получении List<String> из экземпляра класс
			}																				 //траектории, в него сохранятся внесённые изменения
			this.trajectoryData.add(dataRow);
		}
				
		return trajectoryData;
	}

	public void setTrajectoryData(List<String> trajectoryData) { //Устанавливает новые данные в строковой массив
		this.trajectoryData = trajectoryData;
	}
	
	//Функции для получения данных о траектории в определённую секунду
	public double getX(double second) {
		return this.trajectoryNormalData.get(second).get(0);
	}
	
	public double getY(double second) {
		return this.trajectoryNormalData.get(second).get(1);
	}
	
	public double getZ(double second) {
		return this.trajectoryNormalData.get(second).get(2);
	}
	
	public double getSpeedX(double second) {
		return this.trajectoryNormalData.get(second).get(3);
	}
	
	public double getSpeedY(double second) {
		return this.trajectoryNormalData.get(second).get(4);
	}
	
	public double getSpeedZ(double second) {
		return this.trajectoryNormalData.get(second).get(5);
	}
	
	public List<Double> getDataOnSecond(double second){
		return this.trajectoryNormalData.get(second);
	}
	
	//Функции для изменения данных о траектории в определённую секунду
	public void setX(double second, double newX) {
		this.trajectoryNormalData.get(second).set(0, newX);
	}
	
	public void setY(double second, double newY) {
		this.trajectoryNormalData.get(second).set(1, newY);
	}
	
	public void setZ(double second, double newZ) {
		this.trajectoryNormalData.get(second).set(2, newZ);
	}
	
	public void setSpeedX(double second, double speedX) {
		this.trajectoryNormalData.get(second).set(3, speedX);
	}
	
	public void setSpeedY(double second, double speedY) {
		this.trajectoryNormalData.get(second).set(4, speedY);
	}
	
	public void setSpeedZ(double second, double speedZ) {
		this.trajectoryNormalData.get(second).set(5, speedZ);
	}
	
	public void setDataOnSecond(double second, List<Double> newData){
		this.trajectoryNormalData.put(second, newData);
	}

	@Override
	public int hashCode() {
		return Objects.hash(trajectoryData, trajectoryNormalData);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Trajectory other = (Trajectory) obj;
		return Objects.equals(trajectoryData, other.trajectoryData)
				&& Objects.equals(trajectoryNormalData, other.trajectoryNormalData);
	}
	
	
}

