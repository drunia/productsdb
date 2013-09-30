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
	
	//������� ����� ��� ������������ �������
	private Logger log = Logger.getAnonymousLogger();
	
	//������ �� ������� JFrame ����������
	private ProductsDB prodsdb;
	
	//���������� ��� �������������� � ����� ������
	private CategoryController cc;
	
	//������� ��� ������ ���������
	JTable catTable;
	JScrollPane catScroll;
	
	//������ � �.�.
	JButton addCatBtn, delCatBtn;
	
	//����������� ������ �� ���������
	public CategoryView(ProductsDB prodsdb) {
		this.prodsdb = prodsdb;
		
		//��������� ������������ � ����
		log.addHandler(LogUtil.getFileHandler());
		//������������� ������ - ��������
		setLayout(new BorderLayout());
		
		//������� ��� ����������
		cc = new CategoryController(prodsdb.getDatabase(), this);
		cc.setSqlResultListener(this);
		
		//������� ������ � ����� �� ������
		addCatBtn = new JButton("�������� ���������");
		addCatBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog("������� �������� ���������");
				String desc = JOptionPane.showInputDialog("������� �������� ���������");
				if ((name == null) || (desc == null)) {
					error(new Exception("������ ������ ������ ��������"));
					return;
				}
				cc.addCategory(0, name, desc);
			}
		});
		delCatBtn = new JButton("������� ���������");
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
		
		//������ ������ � ���� �� ������� ���� ���������
		cc.getCategories(1);
	}
	
	//���������� ���� �������� � ��
	public boolean sqlQueryReady(ResultSet rs, int callerId) {
		//����� �������� �� if (callerId == 1) { .... }
		switch (callerId) {
			//����������� ������ �� ������� ���������
			//1 - ��� ������������� ������� �� ����� ������ �� ��
			case 1: 
				try {
					try { remove(catScroll); } catch (Exception e) {} ; 
					//�������� ������ ��� �������
					Vector<String> cols = new Vector<String>();
					cols.add("��. ���������");
					cols.add("������������ ��");
					cols.add("���");
					cols.add("��������");
					
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
					//������� ��������
					catTable = new JTable(data, cols);
					catScroll = new JScrollPane(catTable);
					add(catScroll, BorderLayout.CENTER);
					updateUI();
					//���������� true - ���� ������� ���������� �������
					return true;
				} catch (java.sql.SQLException e) {
					log.info("callerId = " + callerId + e.toString());
				}
			break;
		}
		
		//������������ true � ������ �������� ��������� �������
		return false;
	}
	
	
	//---------------������ ������� ������ ������������ ������ View---------------------
	
	//����������, ����� ������ ����� �������� GUI
	public void updateUI(Object source) {
		log.info("requested UpdateUI()");
		cc.getCategories(1);
	}
	
	//����������, ����� ������ ����� �������� ������
	public void error(Exception e) {
		prodsdb.error(e);
	}
	
	//����������, ����� ������ ��� - �� ����� �����������
	public boolean confirm(String msg) {
		return false;
	}
	
	//����������, ����� ������ ����� �������� ���������
	public void message(String msg){
		prodsdb.message(msg);
	}
	
}