package ru.nic.trajectory;


import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {	
		private static final Logger logger = Logger.getLogger(MainFrame.class.getName()); //Объявление логгера
	
    	private JPanel chartPanel = new JPanel(new SpringLayout());   //Панель для графика
    	private ArrayList<XYSeries> cache = new ArrayList<XYSeries>(); //Коллекция для хранения данных графика
    	
    	private JPanel textPanel = new JPanel(new SpringLayout()); //Панель для текстового представления файла
    	private JTextArea fileTextArea = new JTextArea(); //Элемент формы с текстовым отображением файла
    	private JTextField filePath = new JTextField(); //Текстовая строка с полным названием файла
        
        private MyTableModel model = new MyTableModel(); //Модель таблицы
        private TableRowSorter<MyTableModel> trajectoryTableSorter = new TableRowSorter<MyTableModel>();//Сортировщик для таблицы
        private JTable trajectoryTable = new JTable(model); //Таблица с данными файла
        private JPanel tablePanel = new JPanel(new SpringLayout()); //Панель с таблицей
        
        private JPopupMenu tableMenu = new JPopupMenu(); //Контекстное меню компоненты "Таблица"
        private JMenuItem insertAboveItem = new JMenuItem("Вставить строку выше");//Вставка новой строки выше выбранной строки
        private JMenuItem insertBelowItem = new JMenuItem("Вставить строку ниже");//Вставка новой строки ниже выбранной строки
        private JMenuItem removeRowItem = new JMenuItem("Удалить строку");//Удаление выбранной строки
        private JMenuItem statisticsItem = new JMenuItem("Отобразить статистику");// Среднее, дисперсия, начальные выборочные моменты
        
        private JPanel listPanel = new JPanel(new SpringLayout()); //Панель для компоненты "каталог"
        private DefaultListModel<FileAndTrajectory> listModel = new DefaultListModel<FileAndTrajectory>(); //Модель данных для JList
        private JList<FileAndTrajectory> trajectoryList = new JList<FileAndTrajectory>(listModel);//Список каталога, который хранит экземпляры класса с тракторией и файлом
        
        private JPopupMenu popupMenu = new JPopupMenu(); //Контекстное меню компоненты "каталог"
        private JMenuItem closeItem = new JMenuItem("Закрыть"); //Закрытие выбранной траектории
        private JMenuItem openItem = new JMenuItem("Открыть"); //Открыть новый файл с траекторией
        private JMenuItem saveItem = new JMenuItem("Сохранить"); //Сохранение изменений
        private JMenuItem saveAsItem = new JMenuItem("Сохранить как"); //Сохранение изменений данных и файла с траекторией в новом месте/с новым названием

	    public MainFrame() {
	    	//Настройка логгера
	    	LoggerConfig.setup();
	    	
	    	//Настройка главной формы
	    	getContentPane().setBackground(new Color(255, 255, 255));
	    	setBackground(new Color(0, 0, 255));
	    	setTitle("НИЦ Траектория");
	        setSize(1000, 700);
	        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        setLocationRelativeTo(null);
	        setResizable(false);
	                
	        //Кнопка выбора файла по которому будет строиться график
	        JButton btnNewButton = new JButton("ФАЙЛ");
	        btnNewButton.addActionListener(new ActionListener() {
	        	public void actionPerformed(ActionEvent e) {
	        		FileHandler fileHandler = openFile();
	        		String name = "Траектория " + (listModel.getSize() + 1);
	        		
	        		createComponents(name, fileHandler);
	        	}
	        });
	        	        	  
	        //Создание слушателей и назначение действий для кнопок контекстного меню компоненты "Каталог"
	        openItem.addActionListener(new ActionListener() {
	        	@Override
	        	public void actionPerformed(ActionEvent e) {
	        		openAll();
	        	}
	        });
	        
	        closeItem.addActionListener(new ActionListener() {
	        	@Override
	        	public void actionPerformed(ActionEvent e) {
	        		closeAll();
	        	}
	        });
	        
	        saveAsItem.addActionListener(new ActionListener() {
	        	@Override
	        	public void actionPerformed(ActionEvent e) {	        		
	        		try {
						saveFileAs();
					} catch (IOException e1) {
						logger.log(Level.WARNING, "Не удалось сохранить файл!");
					}        		
	        	}
	        });
	        
	        saveItem.addActionListener(new ActionListener() {
	        	@Override
	        	public void actionPerformed(ActionEvent e) {
	        		try {
						saveThisData();
					} catch (IOException e1) {
						logger.log(Level.WARNING, "Не удалось сохранить файл!");
					}
	        	}
	        });
	        
	        //Добавление кнопок в контекстное меню компоненты "каталог"
	        popupMenu.add(openItem);
	        popupMenu.add(closeItem);
	        popupMenu.add(saveItem);
	        popupMenu.add(saveAsItem);
	        
	        //Добавление слушателя нажатий мыши для контекстного меню компоненты "каталог"
	        trajectoryList.addMouseListener(new MouseAdapter() {
	            @Override
	            public void mousePressed(MouseEvent e) {
	                if (e.isPopupTrigger()) {
	                    showPopup(e);
	                }
	            }

	            @Override
	            public void mouseReleased(MouseEvent e) {
	                if (e.isPopupTrigger()) {
	                    showPopup(e);
	                }
	            }

	            private void showPopup(MouseEvent e) {
	                int index = trajectoryList.locationToIndex(e.getPoint());
	                if (index != -1) {
	                	trajectoryList.setSelectedIndex(index);
	                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
	                }
	            }
	        });
	        
	        //Создание слушателей и назначение действий для кнопок контекстного меню компоненты "Таблица"
	        insertAboveItem.addActionListener(e -> insertRow(trajectoryTable.getSelectedRow(), true));
	        insertBelowItem.addActionListener(e -> insertRow(trajectoryTable.getSelectedRow(), false));	        
	        removeRowItem.addActionListener(e ->  removeRow(trajectoryTable.getSelectedRow()));
	        statisticsItem.addActionListener(e -> new StatisticsDialog(MainFrame.this).setVisible(true));

	        //Добавление кнопок в контекстное меню компоненты "Таблица"
	        tableMenu.add(insertAboveItem);
	        tableMenu.add(insertBelowItem);
	        tableMenu.add(removeRowItem);
	        tableMenu.add(statisticsItem);
	        
	        //Добавление слушателя нажатий мыши для контекстного меню компоненты "Таблица"
	        trajectoryTable.addMouseListener(new MouseAdapter() {
	            @Override
	            public void mousePressed(MouseEvent e) {
	                if (e.isPopupTrigger()) {
	                    int row = trajectoryTable.rowAtPoint(e.getPoint());
	                    if (row != -1) {
	                    	trajectoryTable.setRowSelectionInterval(row, row);
	                    	tableMenu.show(e.getComponent(), e.getX(), e.getY());
	                    }
	                }
	            }

	            @Override
	            public void mouseReleased(MouseEvent e) {
	                if (e.isPopupTrigger()) {
	                    int row = trajectoryTable.rowAtPoint(e.getPoint());
	                    if (row != -1) {
	                    	trajectoryTable.setRowSelectionInterval(row, row);
	                    	tableMenu.show(e.getComponent(), e.getX(), e.getY());
	                    }
	                }
	            }
	        });
	        
	        //Компоновка элементов формы
	        SpringLayout springLayout = new SpringLayout();
	        
	        springLayout.putConstraint(SpringLayout.NORTH, listPanel, 50, SpringLayout.NORTH, getContentPane());
	        springLayout.putConstraint(SpringLayout.WEST, listPanel, 0, SpringLayout.WEST, btnNewButton);
	        springLayout.putConstraint(SpringLayout.SOUTH, listPanel, 0, SpringLayout.SOUTH, tablePanel);
	        springLayout.putConstraint(SpringLayout.EAST, listPanel, -587, SpringLayout.EAST, getContentPane());
	        
	        springLayout.putConstraint(SpringLayout.NORTH, textPanel, 0, SpringLayout.NORTH, chartPanel);
	        springLayout.putConstraint(SpringLayout.WEST, textPanel, 0, SpringLayout.WEST, btnNewButton);
	        springLayout.putConstraint(SpringLayout.SOUTH, textPanel, 0, SpringLayout.SOUTH, getContentPane());
	        springLayout.putConstraint(SpringLayout.EAST, textPanel, 0, SpringLayout.WEST, chartPanel);
	        
	        springLayout.putConstraint(SpringLayout.NORTH, tablePanel, 50, SpringLayout.NORTH, getContentPane());
	        springLayout.putConstraint(SpringLayout.SOUTH, tablePanel, -1, SpringLayout.NORTH, chartPanel);
	        springLayout.putConstraint(SpringLayout.WEST, tablePanel, 322, SpringLayout.EAST, btnNewButton);
	        springLayout.putConstraint(SpringLayout.WEST, tablePanel, 397, SpringLayout.WEST, getContentPane());
	        springLayout.putConstraint(SpringLayout.EAST, tablePanel, 0, SpringLayout.EAST, getContentPane());
	        
	        springLayout.putConstraint(SpringLayout.NORTH, btnNewButton, 0, SpringLayout.NORTH, getContentPane());
	        springLayout.putConstraint(SpringLayout.WEST, btnNewButton, 0, SpringLayout.WEST, getContentPane());
	        
	        springLayout.putConstraint(SpringLayout.NORTH, chartPanel, 400, SpringLayout.NORTH, getContentPane());
	        springLayout.putConstraint(SpringLayout.SOUTH, chartPanel, 0, SpringLayout.SOUTH, getContentPane());
	        springLayout.putConstraint(SpringLayout.EAST, chartPanel, 0, SpringLayout.EAST, getContentPane());	
	        springLayout.putConstraint(SpringLayout.WEST, chartPanel, 397, SpringLayout.WEST, getContentPane());
	        
	        getContentPane().setLayout(springLayout);
	        getContentPane().add(btnNewButton);
	        getContentPane().add(chartPanel);	        	        	        	        	        
	        getContentPane().add(textPanel);
	        getContentPane().add(tablePanel);
	        getContentPane().add(listPanel);
	        
	        //Компоновка текстового поля	        
	        JScrollPane scrollPane = new JScrollPane(fileTextArea);
	        SpringLayout textLayout = (SpringLayout) textPanel.getLayout();
	        
	        textLayout.putConstraint(SpringLayout.NORTH, scrollPane, 30, SpringLayout.NORTH, textPanel);
	        textLayout.putConstraint(SpringLayout.WEST, scrollPane, 0, SpringLayout.WEST, textPanel);
	        textLayout.putConstraint(SpringLayout.SOUTH, scrollPane, 0, SpringLayout.SOUTH, textPanel);
	        textLayout.putConstraint(SpringLayout.EAST, scrollPane, 0, SpringLayout.EAST, textPanel);
	        
	        textLayout.putConstraint(SpringLayout.NORTH, filePath, -20, SpringLayout.NORTH, scrollPane);
	        textLayout.putConstraint(SpringLayout.WEST, filePath, 90, SpringLayout.WEST, scrollPane);
	        
	        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	        
	        filePath.setHorizontalAlignment(SwingConstants.CENTER);
	        filePath.setEditable(false);
	        filePath.setBorder(new EmptyBorder(0, 0, 0, 0));
	        
	        textPanel.add(scrollPane);
	        textPanel.add(filePath);
	        
	        //Компоновка Таблицы	        
	        JScrollPane tableScrollPane = new JScrollPane(trajectoryTable);
	        SpringLayout tableLayout = (SpringLayout) tablePanel.getLayout();
	        
	        tableLayout.putConstraint(SpringLayout.NORTH, tableScrollPane, 0, SpringLayout.NORTH, tablePanel);
	        tableLayout.putConstraint(SpringLayout.WEST, tableScrollPane, -587, SpringLayout.EAST, tablePanel);
	        tableLayout.putConstraint(SpringLayout.SOUTH, tableScrollPane, 0, SpringLayout.SOUTH, tablePanel);
	        tableLayout.putConstraint(SpringLayout.EAST, tableScrollPane, 0, SpringLayout.EAST, tablePanel);
	 
    		tableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    		tableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    		tableScrollPane.setColumnHeaderView(trajectoryTable.getTableHeader());
    		
	        tablePanel.add(tableScrollPane);
	        
	        //Компоновка каталога
	        JScrollPane listScrollPane = new JScrollPane(trajectoryList);	        
	        SpringLayout listLayout = (SpringLayout) listPanel.getLayout();

	        listLayout.putConstraint(SpringLayout.NORTH, listScrollPane, 0, SpringLayout.NORTH, listPanel);
	        listLayout.putConstraint(SpringLayout.SOUTH, listScrollPane, 0, SpringLayout.SOUTH, listPanel);
	        listLayout.putConstraint(SpringLayout.WEST, listScrollPane, 0, SpringLayout.WEST, listPanel);
	        listLayout.putConstraint(SpringLayout.EAST, listScrollPane, 0, SpringLayout.EAST, listPanel);
	        
	        listPanel.add(listScrollPane);        	        
	    }
	    	    	
	    //Функция создания графика
	    private void paintGraphic(Trajectory trajectory) {
	    	Set<Entry<Double, List<Double>>> entryData = trajectory.getNormalTrajectoryData().entrySet();
	    	
	    	//Отчистка коллекции с данными графика
	    	cache.clear();
	    	
        	//Создаем набор данных
	        XYSeries series1 = new XYSeries("Speed X");
	        XYSeries series2 = new XYSeries("Speed Y");
	        XYSeries series3 = new XYSeries("Speed Z");
	        
	        XYSeries series4 = new XYSeries("X");
	        XYSeries series5 = new XYSeries("Y");
	        XYSeries series6 = new XYSeries("Z");
	        
	        //Добавление новых данных в коллекцию
	        cache.add(series1);
	        cache.add(series2);
	        cache.add(series3);
	        
	        cache.add(series4);
	        cache.add(series5);
	        cache.add(series6);
	        
	        //Заполнение набора данных
	        for (Entry<Double, List<Double>> entry : entryData) {
	        	double second = entry.getKey();
	        	series1.add(second, trajectory.getSpeedX(second));
	        	series2.add(second, trajectory.getSpeedY(second));
	        	series3.add(second, trajectory.getSpeedZ(second));
	        	
	        	series4.add(second, trajectory.getX(second));
	        	series5.add(second, trajectory.getY(second));
	        	series6.add(second, trajectory.getZ(second));
	        }
	        
	        XYSeriesCollection dataset1 = new XYSeriesCollection(series1);
	        XYSeriesCollection dataset2 = new XYSeriesCollection(series2);
	        XYSeriesCollection dataset3 = new XYSeriesCollection(series3);
	        
	        XYSeriesCollection dataset4 = new XYSeriesCollection(series4);
	        XYSeriesCollection dataset5 = new XYSeriesCollection(series5);
	        XYSeriesCollection dataset6 = new XYSeriesCollection(series6);
	        
	        //Комбинированный набор данных для одновременного отображения нескольких граффиков данных
	        XYSeriesCollection combinedDataset = new XYSeriesCollection();
	        combinedDataset.addSeries(dataset1.getSeries(0));
	        combinedDataset.addSeries(dataset2.getSeries(0));
	        combinedDataset.addSeries(dataset3.getSeries(0));
	        
	        combinedDataset.addSeries(dataset4.getSeries(0));
	        combinedDataset.addSeries(dataset5.getSeries(0));
	        combinedDataset.addSeries(dataset6.getSeries(0));

	        //Создаем график
	        JFreeChart chart = ChartFactory.createXYLineChart(
	                "Динамика скорости", // Заголовок графика
	                "Время",       // Подпись оси X
	                "Скорость",       // Подпись оси Y
	                combinedDataset,        // Данные
	                PlotOrientation.VERTICAL,
	                false,           // Включить легенду
	                true,           // Включить подсказки
	                false           // Не создавать URL
	        );

	        ChartPanel newChartPanel = new ChartPanel(chart);
	        
	        //Компоновка и обновление компоненты "График"
	        SpringLayout chartLayout = (SpringLayout) chartPanel.getLayout();
	        chartLayout.putConstraint(SpringLayout.NORTH, newChartPanel, 30, SpringLayout.NORTH, chartPanel);
	        chartLayout.putConstraint(SpringLayout.WEST, newChartPanel, 0, SpringLayout.WEST, chartPanel);
	        chartLayout.putConstraint(SpringLayout.SOUTH, newChartPanel, 0, SpringLayout.SOUTH, chartPanel);
	        chartLayout.putConstraint(SpringLayout.EAST, newChartPanel, 0, SpringLayout.EAST, chartPanel);
	        
	        chartPanel.removeAll();	  
	        chartPanel.add(newChartPanel);        
	        chartPanel.revalidate();
	        chartPanel.repaint();
	        
	        //Вызов функции для создания чек-боксов над граффиком 
	        createCheckBoxes(chart, chartPanel, chartLayout);	        
        }
	    
	    //Функция, создающая чек-боксы над графиком, которые отвечают за их отображение
	    private void createCheckBoxes(JFreeChart chart, JPanel chartPanel, SpringLayout chartLayout) {
	    	//Создание подписи для чек-боксов
	    	JLabel textLabel = new JLabel("Проекции скорости: ");
	    	chartLayout.putConstraint(SpringLayout.NORTH, textLabel, 10, SpringLayout.NORTH, chartPanel);
	    	chartLayout.putConstraint(SpringLayout.WEST, textLabel, 270, SpringLayout.WEST, chartPanel);
	        chartPanel.add(textLabel);
	        
	        //Создание 6 чекбоксов, каждый отвечает за свой график
	        JCheckBox chckbxVx = new JCheckBox("Vx,м/с");
	        chckbxVx.setSelected(true);
	        chartLayout.putConstraint(SpringLayout.NORTH, chckbxVx, -3, SpringLayout.NORTH, textLabel);
	        chartLayout.putConstraint(SpringLayout.WEST, chckbxVx, 5, SpringLayout.EAST, textLabel);
	        chartPanel.add(chckbxVx);
	        
	        JCheckBox chckbxVy = new JCheckBox("Vy,м/с");
	        chckbxVy.setSelected(true);
	        chartLayout.putConstraint(SpringLayout.NORTH, chckbxVy, 0, SpringLayout.NORTH, chckbxVx);
	        chartLayout.putConstraint(SpringLayout.WEST, chckbxVy, 5, SpringLayout.EAST, chckbxVx);
	        chartPanel.add(chckbxVy);
	        
	        JCheckBox chckbxVz = new JCheckBox("Vz,м/с");
	        chckbxVz.setSelected(true);
	        chartLayout.putConstraint(SpringLayout.WEST, chckbxVz, 5, SpringLayout.EAST, chckbxVy);
	        chartLayout.putConstraint(SpringLayout.SOUTH, chckbxVz, 0, SpringLayout.SOUTH, chckbxVx);
	        chartPanel.add(chckbxVz);
	        
	        JLabel textLabel2 = new JLabel("Координаты: ");
	        chartLayout.putConstraint(SpringLayout.NORTH, textLabel2, 10, SpringLayout.NORTH, chartPanel);
	        chartLayout.putConstraint(SpringLayout.WEST, textLabel2, 0, SpringLayout.WEST, chartPanel);
	        chartPanel.add(textLabel2);
	        
	        JCheckBox chckbxX = new JCheckBox("X,м");
	        chckbxX.setSelected(false);
	        chartLayout.putConstraint(SpringLayout.NORTH, chckbxX, -3, SpringLayout.NORTH, textLabel2);
	        chartLayout.putConstraint(SpringLayout.WEST, chckbxX, 5, SpringLayout.EAST, textLabel2);
	        chartPanel.add(chckbxX);
	        
	        JCheckBox chckbxY = new JCheckBox("Y,м");
	        chckbxY.setSelected(false);
	        chartLayout.putConstraint(SpringLayout.NORTH, chckbxY, 0, SpringLayout.NORTH, chckbxX);
	        chartLayout.putConstraint(SpringLayout.WEST, chckbxY, 5, SpringLayout.EAST, chckbxX);
	        chartPanel.add(chckbxY);
	        
	        JCheckBox chckbxZ = new JCheckBox("Z,м");
	        chckbxZ.setSelected(false);
	        chartLayout.putConstraint(SpringLayout.WEST, chckbxZ, 5, SpringLayout.EAST, chckbxY);
	        chartLayout.putConstraint(SpringLayout.SOUTH, chckbxZ, 0, SpringLayout.SOUTH, chckbxX);
	        chartPanel.add(chckbxZ);
	        
	        //Настройка координатной плоскости, на которой будут графики
	        XYPlot plot = (XYPlot) chart.getPlot();
	        plot.setBackgroundPaint(Color.WHITE);
	        plot.setDomainGridlinePaint(Color.BLACK);
	        plot.setRangeGridlinePaint(Color.BLACK);
	        
	        //При открытии файла отображаются только графики скорости
	        plot.getRenderer().setSeriesVisible(3, false);
	        plot.getRenderer().setSeriesVisible(4, false);
	        plot.getRenderer().setSeriesVisible(5, false);
	        
	        //Установка тёмно-синего, тёмно-зелёного и бордового цветов для графиков координат
	        plot.getRenderer().setSeriesPaint(3, new Color(0, 0, 128));
	        plot.getRenderer().setSeriesPaint(4, new Color(0, 100, 0));
	        plot.getRenderer().setSeriesPaint(5, new Color(128, 0, 0));
	        
	        //Добавление слушателей для активации/деактивации чек-боксов
	        chckbxVx.addItemListener(new ItemListener() {
	            @Override
	            public void itemStateChanged(ItemEvent e) {
	                plot.getRenderer().setSeriesVisible(0, e.getStateChange() == ItemEvent.SELECTED);
	            }
	        });
	        
	        chckbxVy.addItemListener(new ItemListener() {
	            @Override
	            public void itemStateChanged(ItemEvent e) {
	                plot.getRenderer().setSeriesVisible(1, e.getStateChange() == ItemEvent.SELECTED);
	            }
	        });
	        
	        chckbxVz.addItemListener(new ItemListener() {
	            @Override
	            public void itemStateChanged(ItemEvent e) {
	                plot.getRenderer().setSeriesVisible(2, e.getStateChange() == ItemEvent.SELECTED);
	            }
	        });
	        
	        chckbxX.addItemListener(new ItemListener() {
	            @Override
	            public void itemStateChanged(ItemEvent e) {
	                plot.getRenderer().setSeriesVisible(3, e.getStateChange() == ItemEvent.SELECTED);
	            }
	        });
	        
	        chckbxY.addItemListener(new ItemListener() {
	            @Override
	            public void itemStateChanged(ItemEvent e) {
	                plot.getRenderer().setSeriesVisible(4, e.getStateChange() == ItemEvent.SELECTED);
	            }
	        });
	        
	        chckbxZ.addItemListener(new ItemListener() {
	            @Override
	            public void itemStateChanged(ItemEvent e) {
	                plot.getRenderer().setSeriesVisible(5, e.getStateChange() == ItemEvent.SELECTED);
	            }
	        });
	    }
	    
	  //Функция для создания таблицы
	    private void createTable(Trajectory trajectory) {
	        model.setRowCount(0); 
	    	
	        //Добавление данных в Таблицу
	    	Set<Entry<Double, List<Double>>> entryData = trajectory.getNormalTrajectoryData().entrySet();
    		for (Entry<Double, List<Double>> entry : entryData) {
	        	double second = entry.getKey();
	        	model.addRow(new Object[] {
	        			second,
	        			trajectory.getX(second),
	        			trajectory.getY(second),
	        			trajectory.getZ(second),
	        			trajectory.getSpeedX(second),
	        			trajectory.getSpeedY(second),
	        			trajectory.getSpeedZ(second)
	        	});
    		}
    		
    		//Отключение ручной сортировки у всех столбцов
    		trajectoryTableSorter.setModel(model);
    		trajectoryTableSorter.setSortKeys(java.util.Arrays.asList(new RowSorter.SortKey(0, SortOrder.ASCENDING)));
    		
    		trajectoryTableSorter.setSortable(0, false);
    		trajectoryTableSorter.setSortable(1, false);
    		trajectoryTableSorter.setSortable(2, false);
    		trajectoryTableSorter.setSortable(3, false);
    		trajectoryTableSorter.setSortable(4, false);
    		trajectoryTableSorter.setSortable(5, false);
    		trajectoryTableSorter.setSortable(6, false);
    		
    		trajectoryTable.setRowSorter(trajectoryTableSorter);
    		
    		//Установка собственных рендера и эдитора для таблица
            trajectoryTable.setDefaultRenderer(Double.class, new MyTableCellRenderer());
            trajectoryTable.setDefaultEditor(Double.class, new MyTableCellEditor(new JTextField()));
            
            //Блокировка возможности перемещения столбцов
            trajectoryTable.getTableHeader().setReorderingAllowed(false);           
	    }
	    
	    //Создание собственной модели данных для таблицы
	    class MyTableModel extends DefaultTableModel {
	        public MyTableModel() {
	            super(new Object[]{"Секунда", "X", "Y", "Z", "Vx", "Vy", "Vz"}, 0);            
	        }      
	        
	        @Override
	        public boolean isCellEditable(int row, int column) {
	            return true;
	        }

	        @Override
            public Class<?> getColumnClass(int columnIndex) {
	        	return Double.class;
            }        
	    }

	    //Создание собственного рендера для таблицы
	    class MyTableCellRenderer extends DefaultTableCellRenderer {
	    	//Метод getTableCellRendererComponent делает все значения в ячейках по центру
	    	@Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(CENTER);
                
                return c;
            }       
	    }

	    //Создание собственного эдитора для таблицы
	    class MyTableCellEditor extends DefaultCellEditor {       
	    	int column;
	    	
	        public MyTableCellEditor(JTextField textField) {
	            super(textField);	            
	            
	            //Переопределённые ниже методы нужны для подсвечивания красным ячейки, в которую вписывают некорректные данные 
	            textField.getDocument().addDocumentListener(new DocumentListener() {
	            	@Override
	            	public void insertUpdate(DocumentEvent e) {         
	            		String val = String.valueOf(getCellEditorValue());
	    	            // Устанавливаем цвет фона в зависимости от состояния ячейки
	    	            if (!val.matches("-?\\d+(\\.\\d+)?") && !val.matches("^[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?$")) {
	    	                textField.setBackground(Color.RED);
	    	            }
	    	            else {
	    	            	textField.setBackground(Color.WHITE);
	    	            }
	            	}

					@Override
					public void removeUpdate(DocumentEvent e) {						
						String val = String.valueOf(getCellEditorValue());
						if (!val.matches("-?\\d+(\\.\\d+)?") && !val.matches("^[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?$")) {
	    	                textField.setBackground(Color.RED);
	    	            }
	    	            else {
	    	            	textField.setBackground(Color.WHITE);
	    	            }	
					}

					@Override
					public void changedUpdate(DocumentEvent e) {
						String val = String.valueOf(getCellEditorValue());
						if (!val.matches("-?\\d+(\\.\\d+)?") && !val.matches("^[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?$")) {
	    	                textField.setBackground(Color.RED);
	    	            }
	    	            else {
	    	            	textField.setBackground(Color.WHITE);
	    	            }
					}
	            });	 
	            
	            //Слушатель addTableModelListener нужен для автоматизированный сортировки в порядке возрастания в случае изменения времени
	            model.addTableModelListener(new TableModelListener() {
	                @Override
	                public void tableChanged(TableModelEvent e) {
	                    if (e.getType() == TableModelEvent.UPDATE) {
	                        if (e.getColumn() == 0) {
	                            trajectoryTableSorter.sort();
	                            trajectoryTable.repaint(); // Перерисовываем таблицу
	                        }
	                        UpdateTextAndChart();
	                    }
	                    
	                    int row = trajectoryTable.getEditingRow();
		            	int column = trajectoryTable.getEditingColumn();
		            	
		            	//Проверка на то, сменило ли число своё представление (с обычного на экспоненциальную)
		            	if (row != -1 && column != -1) {
		            		if ((Double) trajectoryTable.getValueAt(row, column) > 9999999) {
		            			logger.log(Level.WARNING, "Значение ячейки в строке " + (row + 1)
		            					+ ", в колонке " + (column + 1)
		            					+ " записалось в экспоненциальной форме, т.к. оно слишком велико!");
		            		}
		            	}
	                }
	            });
	        }
	        
	        //Метод stopCellEditing нужен для отмены редактирования, если в ячейку введены некорретные данные
	        @Override
	        public boolean stopCellEditing() {
	            String value = String.valueOf(getCellEditorValue());

	            if (!value.matches("-?\\d+(\\.\\d+)?") && !value.matches("^[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?$")) {
	                cancelCellEditing();
	                
	                logger.log(Level.WARNING, "Попытка записать некорректные данные в таблицу, редактирование отменено!");
	            }else {
	            	//В блоке else добавляется звёздочка к названию в компоненте "Каталог", если значения траектории поменялись
	            	int elementIndex = trajectoryList.getSelectedIndex();
	            	String oldName = listModel.get(elementIndex).getName();
	            	
	            	if (oldName.charAt(oldName.length()-1) != '*') {
	            		oldName += "*";
	            		
	            		trajectoryList.getSelectedValue().setName(oldName);
	            		listModel.set(elementIndex, trajectoryList.getSelectedValue());
	            	}
	            	
	            }

	            return super.stopCellEditing();
	        }

	        @Override
	        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
	        	this.column = column;
	            Component c = super.getTableCellEditorComponent(table, value, isSelected, row, column);
	            return c;
	        }

	        //Метод getCellEditorValue возвращает значение, которое находится в редактируемой ячейке
	        @Override
	        public Object getCellEditorValue() {
	            String value = String.valueOf(super.getCellEditorValue());
	            
	            // Если значение некорректно, возвращаем предыдущее значение
	            try {
	            	return Double.parseDouble(value);
	            }catch (NumberFormatException e) {
	            	return value;
	            }	
	        }	    
	    }
	    
	    //Метод insertRow служит для вставки строки либо сверху, либо снизу (зависит от флага above)
	    private void insertRow(int selectedRow, boolean above) {
	        if (selectedRow != -1) {
	        	int insertIndex = above ? selectedRow : selectedRow + 1;
	        	if (selectedRow > 0 && selectedRow < trajectoryTable.getRowCount() - 1) {
	            	Double newSecond = 0.0;
	            	if (above) {
	            		newSecond = ((Double) trajectoryTable.getValueAt(selectedRow, 0) +
	            				(Double) trajectoryTable.getValueAt(selectedRow-1, 0)) / 2;
	            	}
	            	else {
	            		newSecond = ((Double) trajectoryTable.getValueAt(selectedRow, 0) +
	            				(Double) trajectoryTable.getValueAt(selectedRow+1, 0)) / 2;
	            	}        	
	            	model.insertRow(insertIndex, new Object[]{newSecond, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0});
	        	}
	        	else if (selectedRow == 0) {
	        		model.insertRow(insertIndex, new Object[]{(Double) trajectoryTable.getValueAt(selectedRow, 0), 0.0, 0.0, 0.0, 0.0, 0.0, 0.0});
	        		for (int i = 1; i < trajectoryTable.getRowCount(); i++) {
	        			trajectoryTable.setValueAt((Double) trajectoryTable.getValueAt(i, 0) + 1, i, 0);
	        		}
	        	}
	        	else if (selectedRow == trajectoryTable.getRowCount() - 1) {
	        		model.insertRow(insertIndex, new Object[]{(Double) trajectoryTable.getValueAt(selectedRow, 0) + 1, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0});
	        	}
	        	UpdateTextAndChart();
	        }
	    }
	    
	    //Метод для удаления строки из таблицы
	    public void removeRow(int selectedRow) {
	    	if (selectedRow != -1) {
	    		model.removeRow(selectedRow);
	    		UpdateTextAndChart();
	    	}
	    }
	    
	    //Метод UpdateTextAndChart обновляет компоненту отображения данных и график при изменении таблицы
	    public void UpdateTextAndChart() {
	    	fileTextArea.setText("");
	    	
	    	cache.get(0).clear();
	    	cache.get(1).clear();
	    	cache.get(2).clear();
	    	
	    	cache.get(3).clear();
	    	cache.get(4).clear();
	    	cache.get(5).clear();
	    	
	        for (int row = 0; row < trajectoryTable.getRowCount(); row++) {
	        	fileTextArea.append(
	        			trajectoryTable.getValueAt(row, 0) + "  " +
	        		    trajectoryTable.getValueAt(row, 1) + "  " +
	        		    trajectoryTable.getValueAt(row, 2) + "  " +
	        		    trajectoryTable.getValueAt(row, 3) + "  " +
	        		    trajectoryTable.getValueAt(row, 4) + "  " +
	        		    trajectoryTable.getValueAt(row, 5) + "  " +
	        		    trajectoryTable.getValueAt(row, 6) + "\n");
	        	
	        	cache.get(0).addOrUpdate((Double) trajectoryTable.getValueAt(row, 0), (Double) trajectoryTable.getValueAt(row, 4));
	        	cache.get(1).addOrUpdate((Double) trajectoryTable.getValueAt(row, 0), (Double) trajectoryTable.getValueAt(row, 5));
	        	cache.get(2).addOrUpdate((Double) trajectoryTable.getValueAt(row, 0), (Double) trajectoryTable.getValueAt(row, 6));
	        	
	        	cache.get(3).addOrUpdate((Double) trajectoryTable.getValueAt(row, 0), (Double) trajectoryTable.getValueAt(row, 1));
	        	cache.get(4).addOrUpdate((Double) trajectoryTable.getValueAt(row, 0), (Double) trajectoryTable.getValueAt(row, 2));
	        	cache.get(5).addOrUpdate((Double) trajectoryTable.getValueAt(row, 0), (Double) trajectoryTable.getValueAt(row, 3));
	        }
	        
	        chartPanel.repaint();
	    }
	    
	    //Метод для расчёта среднего арифметического 
	    public Double getColumnMean(int column) {
	    	Double mean = null;
	    	
	    	if (column > 0 && column < 7) {
	    		mean = 0.0;
	    		for (int i = 0; i < trajectoryTable.getRowCount(); i++) {
	    			mean += (Double) trajectoryTable.getValueAt(i, column);
	    		}
	    		mean = mean / trajectoryTable.getRowCount();
	    	}
	    	
	    	return mean;
	    }
	    
	    //Метод для расчёта дисперсии
	    public Double getDispersion(int column) {
	    	Double dispersion = null;
	    	Double mean = getColumnMean(column);
	    	
	    	if (column > 0 && column < 7) {
	    		dispersion = 0.0;
	    		for (int i = 0; i < trajectoryTable.getRowCount(); i++) {
	    			dispersion += Math.pow(((Double) trajectoryTable.getValueAt(i, column) - mean), 2);
	    		}
	    		dispersion = dispersion / trajectoryTable.getRowCount();
	    	}
	    	
	    	return dispersion;
	    }
	    
	    //Метод для расчёта начального выборочного момента 2 порядка
	    public Double getSecondMoment(int column) {
	    	Double secondMomen = null;
	    	
	    	if (column > 0 && column < 7) {
	    		secondMomen = 0.0;
	    		for (int i = 0; i < trajectoryTable.getRowCount(); i++) {
	    			secondMomen += Math.pow((Double) trajectoryTable.getValueAt(i, column), 2);
	    		}
	    		secondMomen = secondMomen / trajectoryTable.getRowCount();
	    	}
	    	
	    	return secondMomen;
	    }
	    
	  //Метод для расчёта начального выборочного момента 3 порядка
	    public Double getThirdMoment(int column) {
	    	Double thirdMomen = null;
	    	
	    	if (column > 0 && column < 7) {
	    		thirdMomen = 0.0;
	    		for (int i = 0; i < trajectoryTable.getRowCount(); i++) {
	    			thirdMomen += Math.pow((Double) trajectoryTable.getValueAt(i, column), 3);
	    		}
	    		thirdMomen = thirdMomen / trajectoryTable.getRowCount();
	    	}
	    	
	    	return thirdMomen;
	    }
	    
	    //Диалог для отображение статистической информации по траектории
	    public class StatisticsDialog extends JDialog{
	    	private String[] properties = {
	    			"Среднее арифметическое",
	    			"Дисперсия",
	    			"Выборочная второго порядка",
	    			"Выборочная третьего порядка"
	    			};
	    	
	    	public StatisticsDialog(JFrame parent) {
	    		super(parent, "Statistics Dialog", true);
	    		setSize(970, 125);
	    		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    		setLocationRelativeTo(parent);
	    		
	    		SpringLayout statisticsLayout = new SpringLayout();
	    		
	    		JPanel statisticsPanel = new JPanel(statisticsLayout);
	    		DefaultTableModel statisticsModel = new DefaultTableModel();
	    		JTable statisticsTable = new JTable(statisticsModel);
	    		JScrollPane statisticsScroll = new JScrollPane(statisticsTable);
	    		
	    		statisticsTable.setSize(950, 125);
	    		statisticsTable.setPreferredScrollableViewportSize(statisticsTable.getSize());
	    		statisticsTable.getTableHeader().setReorderingAllowed(false);
	    		statisticsTable.setDefaultEditor(Object.class, null);
	    		
	    		statisticsLayout.putConstraint(SpringLayout.NORTH, statisticsScroll, 0, SpringLayout.NORTH, statisticsPanel);
	    		
	    		statisticsModel.addColumn("Свойство", properties);
	    		
	    		statisticsModel.addColumn("X", new String[] {convertToNormalNumber(getColumnMean(1)), convertToNormalNumber(getDispersion(1)),
	    				convertToNormalNumber(getSecondMoment(1)), convertToNormalNumber(getThirdMoment(1))});
	    		
	    		statisticsModel.addColumn("Y", new String[] {convertToNormalNumber(getColumnMean(2)), convertToNormalNumber(getDispersion(2)),
	    				convertToNormalNumber(getSecondMoment(2)), convertToNormalNumber(getThirdMoment(2))});
	    		
	    		statisticsModel.addColumn("Z", new String[] {convertToNormalNumber(getColumnMean(3)), convertToNormalNumber(getDispersion(3)),
	    				convertToNormalNumber(getSecondMoment(3)), convertToNormalNumber(getThirdMoment(3))});
	    		
	    		statisticsModel.addColumn("Vx", new String[] {convertToNormalNumber(getColumnMean(4)), convertToNormalNumber(getDispersion(4)),
	    				convertToNormalNumber(getSecondMoment(4)), convertToNormalNumber(getThirdMoment(4))});
	    		
	    		statisticsModel.addColumn("Vy", new String[] {convertToNormalNumber(getColumnMean(5)), convertToNormalNumber(getDispersion(5)),
	    				convertToNormalNumber(getSecondMoment(5)), convertToNormalNumber(getThirdMoment(5))});
	    		
	    		statisticsModel.addColumn("Vz", new String[] {convertToNormalNumber(getColumnMean(6)), convertToNormalNumber(getDispersion(6)),
	    				convertToNormalNumber(getSecondMoment(6)), convertToNormalNumber(getThirdMoment(6))});
	    		
	    		//Установка размеров колонок
	    		statisticsTable.getColumnModel().getColumn(0).setMinWidth(200);
	    		statisticsTable.getColumnModel().getColumn(1).setMinWidth(150);
	    		statisticsTable.getColumnModel().getColumn(2).setMinWidth(150);
	    		statisticsTable.getColumnModel().getColumn(3).setMinWidth(150);
	    		statisticsTable.getColumnModel().getColumn(4).setMinWidth(100);
	    		statisticsTable.getColumnModel().getColumn(5).setMinWidth(100);
	    		statisticsTable.getColumnModel().getColumn(6).setMinWidth(100);
	    		
	    		statisticsPanel.add(statisticsScroll);
	    		
	    		add(statisticsPanel);
	    	}
	    }
	    
	    //Функция, изменяющая представление числа с эспоненциального на нормальное
	    public String convertToNormalNumber(Double number) {
	    	DecimalFormat df = new DecimalFormat("#.######");

	        // Устанавливаем символы форматирования, чтобы избежать локализации
	        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
	        df.setDecimalFormatSymbols(symbols);

	        // Форматируем число
	        String formattedNumber = df.format(number);

	        // Преобразуем строку обратно в Double
	        return formattedNumber;
	    }
	    
	    //Функции для открытия и закрытия траекторий, которые используются компонентой "Каталог"
	    public void openAll() {
        	paintGraphic(trajectoryList.getSelectedValue().getTrajectory());
    		createTable(trajectoryList.getSelectedValue().getTrajectory());
    			        		
    		fileTextArea.setText(trajectoryList.getSelectedValue().getFileHandler().getContent());	        		
    		filePath.setText(trajectoryList.getSelectedValue().getFileHandler().getPathToFile());
        }
	    
	    public void closeAll() {
	        chartPanel.removeAll(); 
	        chartPanel.repaint(); 
	        
	        model.setRowCount(0);
	        
	        fileTextArea.setText("");
	        filePath.setText("");
	        
	        int selectedIndex = trajectoryList.getSelectedIndex();
	        if (selectedIndex != -1) {
	        	listModel.remove(selectedIndex);
	        }
	    }
	    
	    //Функция открытия файла (возможно, функцию открытия/сохранения-как с помощью JFileChooser лучше реализовать в FileHandler, но не уверен)
	    private FileHandler openFile() {
	        JFileChooser fileChooser = new JFileChooser();	        
	        int returnValue = fileChooser.showOpenDialog(null);        
	        
	        if (returnValue == JFileChooser.APPROVE_OPTION) {
	        	File selectedFile = fileChooser.getSelectedFile();
	        	FileHandler fileHandler = new FileHandler(selectedFile.getAbsolutePath());
	        	
	        	String errorRows;
	        	
				errorRows = checkFileRows(fileHandler);
				if (errorRows != "") {
		        	JOptionPane.showMessageDialog(null,
		        			"Не удалось открыть файл!\nНекорректные строки: " + errorRows.substring(0, errorRows.length()-1),
		        			"Предупреждение",
		        			JOptionPane.WARNING_MESSAGE);
		        	
		        	logger.log(Level.WARNING, "Попытка открыть файл, в котором строки записаны в неверном формате!");
		        	return null;
		        }
	        	
	        	return fileHandler;
	        }
	        return null;	        
	    }
	    
	    //Функция, заполняющая все компоненты при открытии файла
	    public void createComponents(String name, FileHandler fileHandler){
	    	try {	        			
    			Trajectory trajectory = new Trajectory();
    			
    			FileAndTrajectory fileAndTrajectory = new FileAndTrajectory(name, fileHandler, trajectory);

        		try {
					fileAndTrajectory.getTrajectory().setAllTrajectoryData(fileHandler.getAllFileStrings());
					
					if (checkTrajectoryInProgram(fileAndTrajectory)) {
						JFrame frame = new JFrame("Данный файл уже открыт");
				        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				        frame.setSize(400, 200);
				        
				        int confirmResult = JOptionPane.showConfirmDialog(frame,
		    					"Файл уже открыть. Хотите открыть его и потерять изменения?",
	                            "Подтверждение перезаписи",
	                            JOptionPane.YES_NO_OPTION);
		    			
		    			if (confirmResult == JOptionPane.NO_OPTION) {
	                        return;
	                    }
		    			closeAll();	
		    			
		    			name = name.substring(0, name.length()-1) + (listModel.getSize() + 1);
					}
					
					InputNameDialog nameDialog = new InputNameDialog(MainFrame.this, name);
					nameDialog.setVisible(true); 
        			name = nameDialog.getName();
        			fileAndTrajectory.setName(name);
					
					paintGraphic(fileAndTrajectory.getTrajectory());
	        		createTable(fileAndTrajectory.getTrajectory());
	        			        		
	        		fileTextArea.setText(fileAndTrajectory.getFileHandler().getContent());	        		
	        		filePath.setText(fileAndTrajectory.getFileHandler().getPathToFile());
	        		
	        		listModel.addElement(fileAndTrajectory);
				} catch (IOException e1) {
					logger.log(Level.SEVERE, "Не удалось открыть файл!");
				}
    		}
    		catch(NullPointerException e1) {
    			logger.log(Level.WARNING, "При открытии файл не был выбран!");
    		}
	    	
	    	//Отображение файла, который только что открыли
	    	int lastIndex = listModel.size() - 1;
    		if (lastIndex != -1) {
    			trajectoryList.setSelectedIndex(lastIndex);
    		}
	    }
	    
	    //Диалог для назначения траектории имени
	    public class InputNameDialog extends JDialog{
	    	private String name = new String();
	    	
	    	public InputNameDialog(JFrame parent, String defaultName) {
	    		super(parent, "Name Dialog", true);
	    		setSize(240, 160);
	    		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    		setLocationRelativeTo(parent);
	    		
	    		name = defaultName;
	    		
	    		SpringLayout nameDialogLayout = new SpringLayout();
	    		
	    		JPanel dialogPanel = new JPanel(nameDialogLayout);
	    		JLabel label = new JLabel("Введите название траектории: ");
	    		JTextField trajectoryName = new JTextField(20);
	    		JButton okButton = new JButton("ОК");
	    		
	    		okButton.addActionListener(new ActionListener(){
	    			
	    			@Override
	    			public void actionPerformed(ActionEvent e) {
	    				name = trajectoryName.getText();
	    				dispose();  				
	    			}
	    			
	    		});
	    		
	    		trajectoryName.setText(name);
	    		
	    		nameDialogLayout.putConstraint(SpringLayout.NORTH, label, 30, SpringLayout.NORTH, dialogPanel);
	    		nameDialogLayout.putConstraint(SpringLayout.NORTH, trajectoryName, 50, SpringLayout.NORTH, dialogPanel);
	    		nameDialogLayout.putConstraint(SpringLayout.SOUTH, okButton, -15, SpringLayout.SOUTH, dialogPanel);
	    		nameDialogLayout.putConstraint(SpringLayout.WEST, okButton, 80, SpringLayout.WEST, dialogPanel);
	    		
	    		dialogPanel.add(label);
	    		dialogPanel.add(trajectoryName);
	    		dialogPanel.add(okButton);
	    		
	    		add(dialogPanel);
	    	}
	    	
	    	public String getName() {
    			return name;
    		}
	    }
	    
	    //Функция для сохранения данных в файл
	    public void saveThisData() throws IOException {
        	String thisFileText = fileTextArea.getText();
        	String thisFilePath = filePath.getText();
        	
        	List<String> newFileStrings = new ArrayList<String>();
        	
        	for(String fileString : thisFileText.split("\n")) {
        		newFileStrings.add(fileString);
        	}
        	
        	FileHandler saveFileHandler = new FileHandler(thisFilePath);
        	saveFileHandler.setAllFileStrings(newFileStrings);
        	
        	String name = trajectoryList.getSelectedValue().getName();
        	
        	if (name.charAt(name.length()-1) == '*') {
        		trajectoryList.getSelectedValue().setName(trajectoryList.getSelectedValue().getName().substring(0, name.length()-1));
        		listModel.set(trajectoryList.getSelectedIndex(), trajectoryList.getSelectedValue());
        	}
        }
	    
	    //Функция для сохранения файла с траекторией под новым именем/в другой директории
	    public void saveFileAs() throws IOException {
	    	saveThisData();
	    	
	    	JFrame frame = new JFrame("Сохранить файл в другой директории");
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        frame.setSize(400, 200);
	    	
	    	JFileChooser fileChooser = new JFileChooser();
	    	fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	    	int result = fileChooser.showSaveDialog(frame);
	    	
	    	if (result == JFileChooser.APPROVE_OPTION) {
	    		File selectedFile = fileChooser.getSelectedFile();
	    		
	    		if (selectedFile.exists()) {
	    			int confirmResult = JOptionPane.showConfirmDialog(frame,
	    					"Файл уже существует. Хотите перезаписать его?",
                            "Подтверждение перезаписи",
                            JOptionPane.YES_NO_OPTION);
	    			
	    			if (confirmResult != JOptionPane.YES_OPTION) {
                        return;
                    }
	    		}
	    	
	    		File sourceFile = trajectoryList.getSelectedValue().getFileHandler().getFile();
	    		
	    		try {
                    // Копируем содержимое исходного файла в новый файл
                    copyFile(sourceFile, selectedFile);
                    
                    String oldName = trajectoryList.getSelectedValue().getName();
                    FileHandler oldFileHandler = trajectoryList.getSelectedValue().getFileHandler();
                    oldFileHandler.setPathToFile(selectedFile.getAbsolutePath());
                    
                    closeAll();
                    
                    createComponents(oldName, oldFileHandler);
                    
                    if (sourceFile.delete()) {
                        JOptionPane.showMessageDialog(frame, "Файл успешно сохранен: " + selectedFile.getAbsolutePath());
                    } else {
                        JOptionPane.showMessageDialog(frame, "Ошибка при удалении старого файла.");
                        logger.log(Level.SEVERE, "Не удалось удалить файл при перезаписи");
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Ошибка при сохранении файла: " + ex.getMessage());
                    logger.log(Level.SEVERE, "Не удалось сохранить файл");
                }
	    		
	    	}
	    }
	    
	    //Функция для копирования содержимого файла
	    private static void copyFile(File source, File dest) throws IOException {
	        try (FileInputStream is = new FileInputStream(source);
	             FileOutputStream os = new FileOutputStream(dest)) {
	            byte[] buffer = new byte[1024];
	            int length;
	            while ((length = is.read(buffer)) > 0) {
	                os.write(buffer, 0, length);
	            }
	        }
	    }
	    
	    //Функция для проверки строк файла с траекторией на корректность 
	    public String checkFileRows(FileHandler fileHandler) {
	    	String errorRows = "";
	    	String correctRowRegEx = "^\\d+(\\.\\d+)?\\s{2}\\d+(\\.\\d+)?\\s{2}\\d+(\\.\\d+)?\\s{2}\\d+(\\.\\d+)?\\s{2}\\d+(\\.\\d+)?\\s{2}\\d+(\\.\\d+)?\\s{2}\\d+(\\.\\d+)?\\s*$";
	    	
	    	int rowNumber = 1;
	    	
	    	try {
				for(String row : fileHandler.getAllFileStrings()) {
					if (!row.matches(correctRowRegEx)) {
						errorRows += String.valueOf(rowNumber) + ", ";
					}
					rowNumber++;
				}
				return errorRows.strip();
			} catch (IOException e) {
				return errorRows;
			}
	    }
	    
	    //Функция, проверяющая, открыт ли файл в программе
	    public boolean checkTrajectoryInProgram(FileAndTrajectory fileAndTrajectory) {
	    	for (int i = 0; i < listModel.size(); i++) {
	    		if (listModel.getElementAt(i).equals(fileAndTrajectory)) {
	    			return true;
	    		}
	    	}
	    	return false;
	    }
}