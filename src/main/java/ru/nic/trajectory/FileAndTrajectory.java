package ru.nic.trajectory;

import java.util.Objects;

public class FileAndTrajectory {
		private FileHandler fileHandler;
		private Trajectory trajectory;
		private String name;
		
		public FileAndTrajectory(String name, FileHandler fileHandler, Trajectory trajectory) {
			this.fileHandler = fileHandler;
			this.trajectory = trajectory;
			this.name = name;
		}

		public FileHandler getFileHandler() {
			return fileHandler;
		}

		public void setFileHandler(FileHandler fileHandler) {
			this.fileHandler = fileHandler;
		}

		public Trajectory getTrajectory() {
			return trajectory;
		}

		public void setTrajectory(Trajectory trajectory) {
			this.trajectory = trajectory;
		}
		
		public String getName() {
			return this.name;
		}
		
		public void setName(String name) {
			this.name = name;
		}

		@Override
		public int hashCode() {
			return Objects.hash(fileHandler, trajectory);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			FileAndTrajectory other = (FileAndTrajectory) obj;
			return Objects.equals(fileHandler, other.fileHandler) && Objects.equals(trajectory, other.trajectory);
		}
		
		@Override
		public String toString() {
			return name;
		}
}
