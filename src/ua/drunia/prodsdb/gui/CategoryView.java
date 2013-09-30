package ua.drunia.prodsdb.gui;

import ua.drunia.prodsdb.ProductsDB;
import ua.drunia.prodsdb.logic.*;
import ua.drunia.prodsdb.util.*;

import java.util.logging.Logger;
import java.sql.ResultSet;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class CategoryView extends JPanel implements 
	CategoryController.ISqlResultListener, IUserUI {
	
	//Создаем класс для логгирования событий
	private Logger log = Logger.getAnonymousLogger();
	
	//Ссфлка на главный JFrame приложения
	private ProductsDB prodsdb;
	
	//Контроллер для взаимодействия с базой данных
	private CategoryController cc;
	
	//Таблица для вывода категорий
	JTable catTable;
	JScrollPane catScroll;
	
	//Кнопки и т.д.
	JButton addCatBtn, delCatBtn;
	
	//Конструктор панели по умолчанию
	public CategoryView(ProductsDB prodsdb) {
		this.prodsdb = prodsdb;
		
		//Назначаем логгирование в файл
		log.addHandler(LogUtil.getFileHandler());
		//Устанавливаем лайоут - менеджер
		setLayout(new BorderLayout());
		
		//Создаем наш контроллер
		cc = new CategoryController(prodsdb.getDatabase(), this);
		cc.setSqlResultListener(this);
		
		//Создаем кнопки и ложим на панель
		addCatBtn = new JButton("Добавить категорию");
		addCatBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog("Введите название категории");
				String desc = JOptionPane.showInputDialog("Введите описание категории");
				if ((name == null) || (desc == null)) {
					error(new Exception("Нельзя внести пустые значения"));
					return;
				}
				cc.addCategory(0, name, desc);
			}
		});
		delCatBtn = new JButton("Удалить категорию");
		delCatBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (catTable.getSelectedRowCount() == 0) return;
				int catId = Integer.parseInt((String) catTable.getValueAt(catTable.getSelectedRow(), 0));
				cc.removeCategory(catId);
			}
		});
		JPanel btnPanel = new JPanel();
		btnPanel.add(addCatBtn);
		btnPanel.add(delCatBtn);
		add(btnPanel, BorderLayout.PAGE_END);
		
		//Делаем запрос в базу на выборку всех категорий
		cc.getCategories(1);
	}
	
	//Обработчик всех запросов к БД
	public boolean sqlQueryReady(ResultSet rs, int callerId) {
		//Можно заменить на if (callerId == 1) { .... }
		switch (callerId) {
			//Обрабатываю запрос ны выборку категорий
			//1 - это идентификатор запроса на выбор данных из БД
			case 1: 
				try {
					try { remove(catScroll); } catch (Exception e) {} ; 
					//Выбираем данные для таблицы
					Vector<String> cols = new Vector<String>();
					cols.add("Ид. категории");
					cols.add("Родительский ид");
					cols.add("Имя");
					cols.add("Описание");
					
					Vector<Vector> data = new Vector<Vector>();
					while (rs.next()) {
						Vector<String> vv = new Vector<String>(4);
						String[] row = new String[4];
						vv.add(rs.getString(1));
						vv.add(rs.getString(2));
						vv.add(rs.getString(3));
						vv.add(rs.getString(4));
						data.add(vv);
					}
					//Создаем табличку
					catTable = new JTable(data, cols);
					catScroll = new JScrollPane(catTable);
					add(catScroll, BorderLayout.CENTER);
					updateUI();
					//Возвращаем true - типа успешно обработали событие
					return true;
				} catch (java.sql.SQLException e) {
					log.info("callerId = " + callerId + e.toString());
				}
			break;
		}
		
		//Возвращается true в случае успешной обработки события
		return false;
	}
	
	
	//---------------Методы которые должен обрабатывать каждый View---------------------
	
	//Вызывается, когда модель хочет обновить GUI
	public void updateUI(Object source) {
		log.info("requested UpdateUI()");
		cc.getCategories(1);
	}
	
	//Вызывается, когда модель хочет показать ошибку
	public void error(Exception e) {
		prodsdb.error(e);
	}
	
	//Вызывается, когда модель что - то хочет подтвердить
	public boolean confirm(String msg) {
		return false;
	}
	
	//Вызывается, когда модель хочет показать сообщение
	public void message(String msg){
		prodsdb.message(msg);
	}
	
}