package ua.drunia.prodsdb.logic;

interface IController {
	void setDatabase(Database db);
	void setView(IUserUI ui);
}