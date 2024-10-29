package ru.nic.trajectory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public class FileHandler {
	private String pathToFile; //путь к файлу
	private List<String> allFileStrings; //строки файла
	
	public FileHandler(String pathToFile) { //конструктор класса FileHandler, принимающий путь к файлу в качествет аргумента
		this.pathToFile = pathToFile;
	}
	
	public FileHandler() { //конструктор класса FileHandler, ничего не принимающий при создании экземпляра
		
	}
	
	public String getPathToFile() { //получение пути к файлу
		return pathToFile;
	}
	
	public File getFile() {
		File file = new File(pathToFile);
		return file;
	}

	public void setPathToFile(String pathToFile) { //метод для установки пути к файлу
		this.pathToFile = pathToFile;
	}
	
	public List<String> getAllFileStrings() throws IOException{ //метод для получения всех строк файла		
		this.allFileStrings = Files.readAllLines(Paths.get(pathToFile));
		return allFileStrings;
	}
	
	public void setAllFileStrings(List<String> newFileStrings) throws IOException{ //метод для полного изменения содержимого файла
		Files.write(Paths.get(pathToFile), newFileStrings);
	}
	
	public void addFileString(String newTrajectoryString) throws IOException{ //метод для добавления строки в конец файла
		Files.write(Paths.get(pathToFile), newTrajectoryString.getBytes());
	}
	
	public String getContent() {
		 try (BufferedReader reader = new BufferedReader(new FileReader(pathToFile))) {
             StringBuilder content = new StringBuilder();
             String line;
             while ((line = reader.readLine()) != null) {
                 content.append(line).append("\n");
             }
             return content.toString();
		 } catch (IOException e) {
             return null;
         }
	}

	@Override
	public int hashCode() {
		return Objects.hash(allFileStrings, pathToFile);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileHandler other = (FileHandler) obj;
		return Objects.equals(allFileStrings, other.allFileStrings) && Objects.equals(pathToFile, other.pathToFile);
	}

	@Override
	public String toString() {
		return "FileHandler [pathToFile=" + pathToFile + "]";
	}
	
	
}

