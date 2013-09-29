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
		
		//��������� ������� ����� � ��������� ������������ ������
		SwingUtilities.invokeLater(new Runnable() {
			public void run() { new ProductsDB().setVisible(true); }
		});
	}
	
	//����������� �� ��������� ������� JFrame
	public ProductsDB() {
		//�������������� JFrame
		super("������� ����� ���������");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(500, 500);
		setLocationRelativeTo(null);
		
		//��������� ���������
		s = Settings.get(this);
		s.save();
		
		//�������������� ��
		db = new Database(this, s.getParam("db.file"));
		if (!db.initialized) {
			log.warning("Database initialization error!");
			System.exit(1);
		}
		
		//������� ������� - View ���������
		CategoryView cw = new CategoryView();
		
		//�������� JTabbedPane ��� ������� � �������� ���� ���� View
		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab("���������", cw);
		
		//��������� �� ������� JFrame ���� ������ �������
		add(tabs, BorderLayout.CENTER);
	}
	
	//---------------������ ������� ������ ������������ ������ View---------------------
	
	//����������, ����� ������ ����� �������� GUI
	public void updateUI(Object source) {
		message("���������� ����� �������� ���������");
	}
	
	//����������, ����� ������ ����� �������� ������
	public void error(Exception e) {
		JOptionPane.showMessageDialog(null, "������ ����������:\n" + e, "������", JOptionPane.ERROR_MESSAGE);
	}
	
	//����������, ����� ������ ��� - �� ����� �����������
	public boolean confirm(String msg) {
		return false;
	}
	
	//����������, ����� ������ ����� �������� ���������
	public void message(String msg){
		JOptionPane.showMessageDialog(null,
			"��������� �� ������/�����������:\n" + msg, "������� ���������", JOptionPane.INFORMATION_MESSAGE);
	}

}