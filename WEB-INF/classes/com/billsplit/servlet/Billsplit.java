package com.billsplit.servlet;
import com.billsplit.servlet.p1.Work;
import com.billsplit.servlet.p1.BillSettlement;
import com.billsplit.servlet.p1.NetBalance;
import com.billsplit.servlet.p1.BillDetails;
import java.io.*;
import java.util.*;
import java.math.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
public class Billsplit extends HttpServlet
{
	public static int flag=0;
	public static HashMap<Integer,Work> map= new HashMap<Integer,Work>();
	public static HashMap<Integer,BillSettlement> billsettlement=new HashMap<Integer,BillSettlement>();
  	public static HashMap<Integer,BillDetails> billdetails= new HashMap<Integer,BillDetails>();
	public static HashMap<Integer,NetBalance> netbalance=new HashMap<Integer,NetBalance>();
    public static Connection getConnection(){
       Connection con=null;
       try{  
          Class.forName("com.mysql.jdbc.Driver");  
          con=DriverManager.getConnection("jdbc:mysql://localhost:3306/newdb?useSSL=false","root","root");   
          }
       catch(Exception e){ System.out.println(e);}  
      return con;
    }
	public static void displaymap(HttpServletResponse res,HashMap<Integer,Work> map) throws IOException,ServletException
   {
	    PrintWriter out=res.getWriter();
        for(Integer i : map.keySet()){
            Work b= map.get(i);
            out.println(b.getnum()+"     "+b.getpersonName()+"     "+b.getnetbalance_value());
        }
   }
    public static void updateBillSettlements(int person_id,float value)
   {
	   Connection con = getConnection();
	   String query = "UPDATE BILLSPLIT SET NETBALANCE=? WHERE PERSON_ID=?";
       try {
          PreparedStatement preparedStmt = con.prepareStatement(query);
		  preparedStmt.setFloat(1,value);
          preparedStmt.setInt(2,person_id);
          preparedStmt.executeUpdate();
        }catch(Exception ex){
            ex.printStackTrace();
        }
		map=executeQuery("SELECT * FROM BILLSPLIT");
		//displaymap(map);
   }
	public static HashMap<Integer,Work> executeQuery(String query){
        Statement st = null;
        ResultSet rs = null;
        Connection con = getConnection();
        Work bs;
        try{
            st = con.createStatement();
            rs = st.executeQuery(query);
            while(rs.next()){
                Integer num = rs.getInt("PERSON_ID");
                String name = rs.getString("NAME");
                float netbalance_value=rs.getFloat("NETBALANCE");
                bs = new Work(num,name,netbalance_value);
                map.put(num,bs);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        } 
        return map;
   }
   public static HashMap<Integer,BillDetails> executeBillQuery(String query){
        Statement st = null;
        ResultSet rs = null;
        Connection con = getConnection();
        BillDetails bd;
        try{
            st = con.createStatement();
            rs = st.executeQuery(query);
            while(rs.next()){
                Integer num = rs.getInt("BILLNUMBER");
                String name = rs.getString("BILLLABEL");
                float amount=rs.getFloat("BILLAMOUNT");
				String date = rs.getString("BILLDATE");
                String time = rs.getString("BILLTIME");
                int creditor=rs.getInt("CREDITOR");
				String beneficiaries=rs.getString("BENEFICIARIES");
                bd = new BillDetails(num,name,amount,date,time,creditor,beneficiaries);
                billdetails.put(num,bd);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        } 
        return billdetails;
   }
   public static HashMap<Integer,NetBalance> executeNetBalanceQuery(String query){
        Statement st = null;
        ResultSet rs = null;
        Connection con = getConnection();
        NetBalance nb;;
        try{
            st = con.createStatement();
            rs = st.executeQuery(query);
            while(rs.next()){
                Integer num = rs.getInt("PERSON_ID");
                float netbalance_value=rs.getFloat("NETBALANCE");
                nb = new NetBalance(num,netbalance_value);
                netbalance.put(num,nb);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        } 
        return netbalance;
   }
   
   public static void executeUpdate(String query,float amountTobePaid,float prevAmtDebt,int benificiaryIndividual){
		amountTobePaid+=prevAmtDebt;
        Connection con = getConnection();
        Work bs;
        try {
          PreparedStatement preparedStmt = con.prepareStatement(query);
          preparedStmt.setFloat(1,amountTobePaid);
          preparedStmt.setInt(2,benificiaryIndividual);
          preparedStmt.executeUpdate();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        map=executeQuery("SELECT * FROM BILLSPLIT"); 		
   }
   public static float getPrevNetBalance_Value(int person_id)
   {
	   Connection con=getConnection();
	   Statement st = null;
	   ResultSet rs = null;
	   float netbalance_value=0;
       try {
		   st = con.createStatement();
           String query="SELECT NETBALANCE FROM BILLSPLIT WHERE PERSON_ID="+person_id;
		   rs = st.executeQuery(query);
           while(rs.next()){
                netbalance_value= rs.getFloat("NETBALANCE");
            }
        }catch(Exception ex){
            ex.printStackTrace();
        } 
		return netbalance_value;
   }
    
    public void doGet(HttpServletRequest req,HttpServletResponse res) throws IOException,ServletException
    {
		Work bs;
		map=executeQuery("SELECT * FROM BILLSPLIT");
    }
}	   
     