package creatingATM;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import com.mysql.cj.xdevapi.Statement;

public class AutomatedTellerMachine {

	 public static String accountNumber="",pin="";
	 public static int choice,amountToWithDraw,typeOfAccount,minBalance=500,mainBalance,balanceChoice,newPin,amountToDeposit;
	 static Scanner obj=new Scanner(System.in);
	
		
	public static void getUserDetails()
	{
		System.out.print("Hello, Enter your Account Number:");
		accountNumber=obj.next();
		System.out.print("Enter your pin number:");
		pin=obj.next();
	}
	public static void services() {
		
		System.out.println("Select the serivce you want to use:");
		System.out.println("1.Cash WithDrawl");
		System.out.println("2.Mini Statement");
		System.out.println("3.Deposit");
		System.out.println("4.Balance Inquiry");
		System.out.println("5.Change Pin");
		System.out.println("6.Exit");
		
		choice=obj.nextInt();
	}
	
	public static void accountType()
	{
		System.out.print("Choose your account Type: ");
		System.out.println("1.Current");
		System.out.println("2.Savings");
		typeOfAccount=obj.nextInt();
		System.out.println("Withdraw the cash is processing please wait...");
		amountCheck();
	}
	
	public static void withDrawl() {
		
		System.out.println("Enter the amount to with draw: ");
		amountToWithDraw=obj.nextInt();
		accountType();
		services();
	}
	public static void amountCheck() {
		
		if(typeOfAccount==2)
		{
			if(amountToWithDraw<=mainBalance)
			{
				System.out.println("Transaction completed.");
				mainBalance-=amountToWithDraw;
				System.out.println("Balance: "+mainBalance);
					
			}
			else
			{
				System.out.println("Sorry transaction cancelled due to insufficent balance in your account.");
			}
		}
		else
		{
			if(amountToWithDraw<=mainBalance) {
				if((mainBalance-amountToWithDraw)>=minBalance)
				{
					System.out.println("Transaction completed.");
					mainBalance-=amountToWithDraw;
					System.out.println("Balance: "+mainBalance);
				}
				else
				{
					System.out.println("Sorry transaction cancelled due to insufficent balance in your account.");
				}
			}
			else
			{
				System.out.println("Transaction not completed due to insufficent balance");
			}
		}
		
	}
	
	public static void balanceInquiry() {
		
		System.out.println(mainBalance);
		services();
		
	}
	
	public static void deposit() {
		
		System.out.print("Enter the amount to deposit: ");
		amountToDeposit=obj.nextInt();
		mainBalance+=amountToDeposit;
		System.out.println("Amount succesfully deposited");
		System.out.println("Balance: "+mainBalance);
		services();
		
	}
	
	public static void changePin() {
		System.out.println("Enter the new pin: ");
		newPin=obj.nextInt();
		System.out.println("Pin changed succesfully");
		services();
	}
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		
		getUserDetails();
		
		
       Class.forName("com.mysql.cj.jdbc.Driver"); 
		
		Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/ATM","root","Mysql@1234");
		boolean accountExists=false;
		
		
		PreparedStatement ps=con.prepareStatement("select * from userInformation where accountNumber=? and pin=?");
		
		ps.setString(1, accountNumber);
		ps.setString(2, pin);
		
		ResultSet rs=ps.executeQuery();
		
		
		while(rs.next())
		{
			mainBalance=rs.getInt(4);
			accountExists=true;
		}
		
		if(accountExists)
		{
			services();
		while(choice!=6)
		{
			switch(choice) {
			  
			case 1:
				withDrawl();
				break;
			case 2:
				PreparedStatement ps3=con.prepareStatement("update transactionHistory set deposit=?,withdraw=? where accountNumber=?");
				ps3.setInt(1, amountToDeposit);
				ps3.setInt(2, amountToWithDraw);
				ps3.setString(3, accountNumber);
				int rs3=ps3.executeUpdate();
				
				PreparedStatement ps4=con.prepareStatement("select * from transactionHistory where accountNumber=?");
				ps4.setString(1, accountNumber);
				
				ResultSet rs1=ps4.executeQuery();
			      while(rs1.next())
			      {
			    	  System.out.println("----------------------------------------------------");
			    	  System.out.println("Deposited amount: "+rs1.getInt(2));
			    	  System.out.println("Amount with Drawed: "+rs1.getInt(3));
			    	  System.out.println("----------------------------------------------------");
			      }
			  
				services();
				break;
			case 3:
				 deposit();
				 break;
			case 4:
				balanceInquiry();
				break;
			case 5:
				changePin();
				PreparedStatement ps2=con.prepareStatement("update userInformation set pin=? where accountNumber=?");
				ps2.setInt(1,newPin);
				ps2.setString(2, accountNumber);
				ps2.executeUpdate();
				
				break;
			default:
				System.out.println("Wrong choice entered");
					
			}
		}
		if(choice==6)
		{
			PreparedStatement ps1=con.prepareStatement("update userInformation set balance=? where accountNumber=?");
			ps1.setInt(1, mainBalance);
			ps1.setString(2, accountNumber);
			
			int rs1=ps1.executeUpdate();
			
			if(rs1>0)
			{ 
				System.out.println("Thanks for visiting");
			}
			else
			{
				System.out.println("Due to some inconvience we can't update your main balnace please contact your bank");
			}	
		}

		}
		else
		{
			System.out.println("Your account number and Pin doesn't match");
		}
	}
	private static PreparedStatement prepareStatement(String string) {
		// TODO Auto-generated method stub
		return null;
	}

}
