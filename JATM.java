import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.io.*;

public class JATM {
	
	static HashMap<String, String> dataBase = new HashMap<>(); 
	static HashMap<String, ArrayList<String>> MainDataBase = new HashMap<>();
	static HashMap<String, ArrayList<String>> financialInformation = new HashMap<>();
	static HashMap<String, ArrayList<String>> message = new HashMap<>();
	static HashMap<String, Integer> newMessage = new HashMap<>();
	
	//static int id = 1;
	
	public static void main(String[] args) {
		login();
	}
	
	public static void login() {
		InformationAboutUser user = new InformationAboutUser();
		while(true) {
			System.out.print("Input your Login: ");
			Scanner log = new Scanner(System.in);
			String userName = log.nextLine();
			String userPassword;
			
			if (dataBase.containsKey(userName)) {
				System.out.println("Your login is contained in our database");
			
				while (true) {
					System.out.print("'if you want to go back enter 1'\nInput your password: ");
					Scanner pass = new Scanner(System.in);
					userPassword = pass.nextLine();
					if (userPassword.equals("1")) {
						login();
					}
					if (dataBase.get(userName).equals(userPassword)) {
						System.out.println("\nWelcome!");
						user.login = userName;
						
						ArrayList<String> exchanger = new ArrayList<>();
						exchanger = MainDataBase.get(userName);
						user.password = exchanger.get(0);
						user.name = exchanger.get(1);
						user.money = Long.parseLong(exchanger.get(2));
						
						break;
					}
					else {
						System.out.println("The password is incorrect");
					}
				}
			}
			
			else {
				System.out.println("Registration");
				System.out.println("if you want to go back enter 1, if you want to generate a password enter 'auto' or '2'");
				System.out.print("Input your password : ");
				Scanner pass = new Scanner(System.in);
				userPassword = pass.next();
				
				if (userPassword.equals("1")) {
					continue;
				}
				else if (userPassword.equals("2") || userPassword.equals("auto")) {
					GeniratePassword p = new GeniratePassword();
					userPassword = p.mainGeneration();
				}
				else if (userPassword.length() < 5) {
					userPassword = checkPassword(userPassword);
				}
				dataBase.put(userName, userPassword);
				System.out.println("Your data is entered into the program\nWelcome");
				user.login = userName;
				user.password = userPassword;
				
				addRegistationToDataBase(userName);
			}
			break;
		}
		myCabinet(user);
	}
	
	private static void addRegistationToDataBase(String userName) {
		ArrayList<String> exchanger = new ArrayList<>();
		message.put(userName, exchanger);
		
		ArrayList<String> exchanger2 = new ArrayList<>();
		financialInformation.put(userName, exchanger2);
		
		newMessage.put(userName, 0);
	}
	
	public static void myCabinet(InformationAboutUser user) {
		System.out.println("\n$My cabinet");
		System.out.print("functions: \n1.Зачислить деньги на карту \n2.Перевести деньги на другую карту \n3.Посмотреть личные данные \n4.Посмотреть статистику финансов \n5.Посмотреть сообщения (" + newMessage.get(user.login) + " new messages) \n6.Игра - лотерея  \n7.Log out \n8.Завершить сессию \nEnter number: ");
		Scanner scan = new Scanner(System.in);
		int choose = scan.nextInt();
		
		System.out.println();
		addDataBase(user);
		
		Commands command = new Commands();
		
		switch(choose) {
		case 1: 
			command.depositMoney(user);
			break;
		case 2: 
			command.transferMoney(user);
			break;
		case 3: 
			command.personalData(user);
			break;
		case 4:
			command.finInformation(user);
			break;
		case 5:
			command.userMessage(user);
			break;
		case 6: 	
			command.lottery(user);
			break;
		case 7: 
			System.out.println("Log out");
			login();
			break;
		case 8:
			break;
		default: 
			System.out.println("Error, unknown command");
			myCabinet(user);
			break;
		}
	}
	
	private static void addDataBase(InformationAboutUser user) {
		ArrayList<String> exchanger = new ArrayList<>();
		exchanger.add(user.password);
		exchanger.add(user.name);
		exchanger.add(Long.toString(user.money));
	
		MainDataBase.put(user.login, exchanger);
	}
	
	private static String checkPassword(String password) {
		while (password.length() < 5) {
			System.out.println("Your password must be longer than five characters, enter your password again");
			Scanner scan = new Scanner(System.in);
			password = scan.nextLine();
		}
		return password;
	}

	public static String printNumber(long number) {
		if (number > 999) {
			ArrayList<Character> numbersList = new ArrayList<>();
			while(number > 0) {
				numbersList.add(0, (char)((number % 10) + '0'));
				number /= 10;
			}

			for (int i = numbersList.size() - 3; i > 0; i -= 3) {
				numbersList.add(i, '.');
			}

			StringBuilder result = new StringBuilder();
			for (char s : numbersList) {
				result.append(s);
			}

			return result.toString();
		}
		else {
			return Long.toString(number);
		}
	}
}

class InformationAboutUser {
	public String login;
	public String password;
	public String name = "null";
	public long money = 0L;
}

class Commands {
	public void depositMoney(InformationAboutUser user) {
		System.out.println("Deposit money");
		System.out.print("You have: " + JATM.printNumber(user.money) + "$" + "\nenrollment: ");
		long enroll = 0L; 
		try {
			enroll = moneyExctends(enroll);
		} catch (IOException e) {
			System.out.println("Error, try again");
			depositMoney(user);
		}
		
		user.money += enroll;
		System.out.println("Account has: " + JATM.printNumber(user.money) + "$");
		
		FinancialInformation addDeposit = new FinancialInformation();
		addDeposit.addInformation(user.login, enroll, 1, "");
		
		System.out.println("functions: \n1.add more \n2.return");
		Scanner chscan = new Scanner(System.in);
		int choose = chscan.nextInt();
		switch(choose) {
		case 1:
			depositMoney(user);
			break;
		default:
			JATM.myCabinet(user);
			break;
		}
	}

	private long moneyExctends(long number) throws IOException {
		Scanner scan = new Scanner(System.in);
		try {
			number = scan.nextLong();
		} catch (InputMismatchException e) {
			throw new IOException("Error");
		}
		if (number < 0) {
			throw new IOException("Your number is less then zero");
		}
		return number;
	}

	public void transferMoney(InformationAboutUser user) {
		System.out.print("Transfer money\n Enter the person's login: ");
		Scanner firstScan = new Scanner(System.in);
		String personLogin = firstScan.nextLine();
		if (JATM.dataBase.get(personLogin) == null) {
			System.out.println("Error, there is no such login in out database");
			transferMoneyMethod(user);
		}
		else if (personLogin.equals(user.login)) {
			System.out.println("Error, you cannot transfer money to yourself");
			transferMoneyMethod(user);
		}
		
		System.out.print("You have: " + JATM.printNumber(user.money) + "$" + "\ntransfer: ");
		Scanner scan = new Scanner(System.in);
		long transfer = 0L;
		
		try {
			transfer = moneyExctends(transfer);
		} catch(IOException e) {
			System.out.println("Error, try again");
			transferMoney(user);
		}
		if (transfer > user.money) {
			System.out.println("Error, you don't have that much money");
			transferMoney(user);
		}
		user.money -= transfer;
		
		addToDataBase(user, transfer, personLogin);

		System.out.println("Account has: " + JATM.printNumber(user.money) + "$");
		
		transferMoneyMethod(user);
	}
	
	private void addToDataBase(InformationAboutUser user, long transfer, String personLogin) {
		ArrayList<String> exchanger = new ArrayList<>();
		exchanger = JATM.MainDataBase.get(personLogin);
		exchanger.set(2, Long.toString(Long.parseLong(exchanger.get(2)) + transfer));
		JATM.MainDataBase.put(personLogin, exchanger);
		
		ArrayList<String> MsExchanger = new ArrayList<>();
		MsExchanger = JATM.message.get(personLogin);
		
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		
		MsExchanger.add("date: " + formater.format(date) + " -->  User " + user.login + " send you " + JATM.printNumber(transfer) + "$");
		JATM.message.put(personLogin, MsExchanger);
		
		int newMessage = JATM.newMessage.get(personLogin);
		newMessage++;
		JATM.newMessage.put(personLogin, newMessage);
		
		FinancialInformation addTransfer = new FinancialInformation();
		addTransfer.addInformation(user.login, transfer, 2, "");
		addTransfer.addInformation(personLogin, transfer, 3, user.login);
	}

	private void transferMoneyMethod(InformationAboutUser user) {
		System.out.println("functions: \n1.transfer money \n2.return");
		Scanner chscan = new Scanner(System.in);
		int choose = chscan.nextInt();
		switch(choose) {
		case 1:
			transferMoney(user);
			break;
		default:
			JATM.myCabinet(user);
			break;
		}
	}
	
	public void personalData(InformationAboutUser user) {
		System.out.println("Personal data");
		
		String Hpassword = "";
		for (int i = 0; i < user.password.length(); i++) {
			Hpassword += "*";
		}
		
		System.out.println("login: " + user.login + "\npassword: " + Hpassword + " \nName: " + user.name + "\nmoney: " + JATM.printNumber(user.money) + "$");
		
		System.out.println("\nfunctions: \n1. Show password \n2.Change name \n3.Return");
		Scanner scan = new Scanner(System.in);
		int choose = scan.nextInt();
		
		switch(choose) {
		case 1:
			System.out.println("Password: " + user.password + "\n");
			break;
		case 2:
			System.out.print("Your name: ");
			Scanner nscan = new Scanner(System.in);
			user.name = nscan.nextLine();
			break;
		case 3:
			System.out.println("return");
			JATM.myCabinet(user);
			break;
		default:
			System.out.println("Error, unknown command");
		}
		personalData(user);
	}
	
	public void finInformation(InformationAboutUser user) {
		FinancialInformation helper = new FinancialInformation();
		helper.printMessages(user);
	}
	
	public void userMessage(InformationAboutUser user) {
		ArrayList<String> exchanger = new ArrayList<>();
		exchanger = JATM.message.get(user.login);
		if (exchanger.isEmpty()) {
			System.out.println("no messages");
			JATM.myCabinet(user);
		}
		
		for (String i : exchanger) {
			System.out.println(i);
		}
		
		JATM.newMessage.put(user.login, 0);
		JATM.myCabinet(user);
	}
	
	public void lottery(InformationAboutUser user) {
		Lottery lot = new Lottery();
		lot.informationAboutLottery(user);
	}
}

class GeniratePassword {
	public String mainGeneration() {
		System.out.print("Enter the number of characters (minimal quantity 5): ");
		Scanner scan = new Scanner(System.in);
		int numberOfSymbols = scan.nextInt();
		
		if (numberOfSymbols < 5 || numberOfSymbols > 100) {
			System.out.println("Error, try again");
			mainGeneration();
		}
		
		String allSymbols = "0123456789-.qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
		String password = "";
		for (int i = 0; i < numberOfSymbols; i++) {
			password += allSymbols.charAt((int)(Math.random() * allSymbols.length()));
		}
		
		password = checkPasswordPrivate(password);
		return choose(password);
	}
	
	private String choose(String password) {
		System.out.println("Your passwoord: " + password + "\n1.password regeneration or 2.further");
		Scanner scan = new Scanner(System.in);
		int chs = scan.nextInt();
		
		if (chs == 1) {
			password = mainGeneration();
		}
		else if (chs != 2) {
			System.out.println("Error, try again");
			choose(password);
		}
		
		return password;
	}
	
	private String checkPasswordPrivate(String password) {
		if (password.charAt(0) == '.' || password.charAt(0) == '-') {
			password = mainGeneration();
		}
		return password;
	}
}

class FinancialInformation {
	public void printMessages(InformationAboutUser user) {
		System.out.println("Your financial history: ");
		
		ArrayList<String> exchanger = new ArrayList<>();
		exchanger = JATM.financialInformation.get(user.login);
		
		if (!exchanger.isEmpty()) {
			for (String i : exchanger) {
				System.out.println(i);
			}
		} else {
			System.out.println("You don't have financial history");
		}
		
		JATM.myCabinet(user);
	}

	public void addInformation(String userName, long money, int function, String transferName) {
		ArrayList<String> message = new ArrayList<>();
		message = JATM.financialInformation.get(userName);
		
		String startWord;
		if (function == 1) {
			startWord = "Your deposit ";
		} 
		else if (function == 2) {
			startWord = "Your transfer ";
			money *= -1;
		}
		else {
			startWord = "from " + transferName + " ";
		}
		
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		
		message.add(startWord + JATM.printNumber(money) + "$ date: " + formater.format(date));
		JATM.financialInformation.put(userName, message);
	}
}

class Lottery {
	public void informationAboutLottery(InformationAboutUser user) {
		System.out.print("Лотерея \nИгры: 1.классическая лотерея; 2.угадай число; \nВаш выбор - ");
		Scanner scan = new Scanner(System.in);
		String choose = scan.nextLine();
		
		if (choose.equals("1")) {
			classicGame(user);
		}
		else if (choose.equals("2")) {
			guessNumberGame(user);
		}
		else {
			System.out.println("Error, try again");
			informationAboutLottery(user);
		}
	}

	private void classicGame(InformationAboutUser user) {
		Long bet = yourBet(user);
		
		System.out.print("1 игра - вероятность выигрыша 49% - ваша ставка увеличется в 1.5 раза \n2 игра - вероятность выигрыша 30% - ваша ставка увеличется в 2 раза \n3 игра - вероятность выигрыша 10% - ваша ставка увеличется в 3 раза \nВаш выбор - ");
		Scanner scan = new Scanner(System.in);
		String choose = scan.nextLine();
		
		if (choose.equals("1")) {
			int rand = randomAtHundred();
			if (rand >= 49) {
				bet = (long) (bet * 1.5);
				System.out.println("Поздравляем, вы выиграли - " + JATM.printNumber(bet) + "$");
			} else {
				System.out.println("Проигрыш, вы проиграли - " + JATM.printNumber(bet) + "$");
				bet *= -1;
			}
		}
		else if (choose.equals("2")) {
			int rand = randomAtHundred();
			if (rand >= 30) {
				bet *= 2;
				System.out.println("Поздравляем, вы выиграли - " + JATM.printNumber(bet) + "$");
			} else {
				System.out.println("Проигрыш, вы проиграли - " + JATM.printNumber(bet) + "$");
				bet *= -1;
			}
		}
		else if (choose.equals("3")) {
			int rand = randomAtHundred();
			if (rand >= 10) {
				bet *= 3;
				System.out.println("Поздравляем, вы выиграли - " + JATM.printNumber(bet) + "$");
			} else {
				System.out.println("Проигрыш, вы проиграли - " + JATM.printNumber(bet) + "$");
				bet *= -1;
			}
		}
		else {
			System.out.println("Error, try again");
			classicGame(user);
		}
		
		addWinnigsToDataBase(user, bet);
		System.out.println("You have - " + JATM.printNumber(user.money) + "$");
		continueGame(user);
	}
	
	private void guessNumberGame(InformationAboutUser user) {
		Long bet = yourBet(user);
		
		System.out.print("1 игра - угадайте число 0 или 1 - ваша ставка увеличивается в 1.5 раза\n2 игра - угадайте число от 1 до 3 - ваша ставка увеличется в 2 раза \n3 игра - угадайте число от 1 до 10 - ваша ставка увеличется в 3 раза \n4 игра - угадайте число от 1 до 100 - ваш ставка увеличется в 10 раз \nВаш выбор - ");
		Scanner scan = new Scanner(System.in);
		String choose = scan.nextLine();
		
		System.out.print("Enter your number - ");
		Scanner scan2 = new Scanner(System.in);
		int number = scan2.nextInt();
		
		if (choose.equals("1")) {
			int rand = (int)Math.floor(Math.random() * (1 - 0 + 1) + 0);
			if (number == rand) {
				bet = (long) (bet * 1.5);
				System.out.println("Поздравляем, вы выиграли - " + bet + "$");
			} else {
				System.out.println("Проигрыш, вы проиграли - " + bet + "$ правильное число - " + rand);
				bet *= -1;
			}
		}
		else if (choose.equals("2")) {
			int rand = (int) (Math.random() * 3 + 1);
			if (number == rand) {
				bet *= 2;
				System.out.println("Поздравляем, вы выиграли - " + bet + "$");
			} else {
				System.out.println("Проигрыш, вы проиграли - " + bet + "$ правильное число - " + rand);
				bet *= -1;
			}
		}
		else if (choose.equals("3")) {
			int rand = (int) (Math.random() * 10 + 1);
			if (number == rand) {
				bet *= 3;
				System.out.println("Поздравляем, вы выиграли - " + bet + "$");
			} else {
				System.out.println("Проигрыш, вы проиграли - " + bet + "$ правильное число - " + rand);
				bet *= -1;
			}
		}
		else if (choose.equals("4")) {
			int rand = (int) (Math.random() * 100 + 1);
			if (number == rand) {
				bet *= 10;
				System.out.println("Поздравляем, вы выиграли - " + bet + "$");
			} else {
				System.out.println("Проигрыш, вы проиграли - " + bet + "$ правильное число - " + rand);
				bet *= -1;
			}
		}
		else {
			System.out.println("Error, try again");
			classicGame(user);
		}
		
		addWinnigsToDataBase(user, bet);
		System.out.println("You have - " + user.money + "$");
		continueGame(user);
 	}
	
	private long yourBet(InformationAboutUser user) {
		System.out.print("На вашем счету - " + JATM.printNumber(user.money) + "$ \nВаша ставка - ");
		Scanner scan = new Scanner(System.in);
		String bid = scan.nextLine();
		if (Long.parseLong(bid) > user.money) {
			System.out.println("Error, you don't have so match money. Try again");
			classicGame(user);
		}
		Long bet = Long.parseLong(bid);
		
		return bet;
	}
	
	private int randomAtHundred() {
		int rand = (int) (Math.random() * 100 + 1);
		return rand;
	}
	
	private void addWinnigsToDataBase(InformationAboutUser user, long total) {
		user.money += total;
		ArrayList<String> exchanger = new ArrayList<>();
		exchanger = JATM.financialInformation.get(user.login);
		
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		
		exchanger.add("*Lottery*  =>" + total + "$ data - " + formater.format(date));
		JATM.financialInformation.put(user.login, exchanger);
	}
	
	private void continueGame(InformationAboutUser user) {
		System.out.println("1.Продолжить игру; 2.Вернуться в личный кабинет");
		Scanner scan = new Scanner(System.in);
		String choose = scan.nextLine();
		if (choose.equals("1")) {
			informationAboutLottery(user);
		}
		else if (choose.equals("2")) {
			JATM.myCabinet(user);
		}
		else {
			System.out.println("Error, try again");
			continueGame(user);
		}
	}
}