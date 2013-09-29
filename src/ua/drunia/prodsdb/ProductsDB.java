package ua.drunia.prodsdb;

import ua.drunia.prodsdb.logic.*;
import ua.drunia.prodsdb.gui.*;
import ua.drunia.prodsdb.util.*;

import java.util.logging.Logger;
import java.util.logging.Level; 
import java.io.File;
import java.sql.ResultSet;

import javax.swing.*;
import java.awt.*;

public class ProductsDB extends JFrame implements IUserUI {
	private static Logger log = Logger.getLogger(ProductsDB.class.getName());
	public static Database db;
	public static Settings s;

	public static void main(String[] args) {
		log.addHandler(LogUtil.getFileHandler());
		
		//Запускаем главную форму в отдельном граффическом потоке
		SwingUtilities.invokeLater(new Runnable() {
			public void run() { new ProductsDB().setVisible(true); }
		});
	}
	
	//Конструктор по умолчанию главной JFrame
	public ProductsDB() {
		//Инициализируем JFrame
		super("Главная форма программы");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(500, 500);
		setLocationRelativeTo(null);
		
		//Загружаем настройки
		s = Settings.get(this);
		s.save();
		
		//Инициализируем бд
		db = new Database(this, s.getParam("db.file"));
		if (!db.initialized) {
			log.warning("Database initialization error!");
			System.exit(1);
		}
		
		//Создаем вкладку - View Категории
		CategoryView cw = new CategoryView();
		
		//Создадим JTabbedPane для вкладок и поместим туда наши View
		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab("Категории", cw);
		
		//Добавляем на главный JFrame нашу панель вкладок
		add(tabs, BorderLayout.CENTER);
	}
	
	//---------------Методы которые должен обрабатывать каждый View---------------------
	
	//Вызывается, когда модель хочет обновить GUI
	public void updateUI(Object source) {
		message("Контроллер хочет обновить интерфейс");
	}
	
	//Вызывается, когда модель хочет показать ошибку
	public void error(Exception e) {
		JOptionPane.showMessageDialog(null, "Ошибка выполнения:\n" + e, "Ошибка", JOptionPane.ERROR_MESSAGE);
	}
	
	//Вызывается, когда модель что - то хочет подтвердить
	public boolean confirm(String msg) {
		return false;
	}
	
	//Вызывается, когда модель хочет показать сообщение
	public void message(String msg){
		JOptionPane.showMessageDialog(null,
			"Сообщение от модели/контроллера:\n" + msg, "Простое сообщение", JOptionPane.INFORMATION_MESSAGE);
	}

}