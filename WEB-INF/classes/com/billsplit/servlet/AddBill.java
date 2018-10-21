package com.billsplit.servlet;
import com.billsplit.servlet.p1.Work;
import com.billsplit.servlet.p1.BillSettlement;
import com.billsplit.servlet.p1.BillDetails;
import java.util.*;
import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
public class AddBill extends Billsplit
{
	List<Integer> billshareID=new ArrayList<Integer>();
	public static boolean checkIfPayerIsBenificiary(int person_id,List<Integer> beneficiary) {
        boolean IfPayerIsBenificiary = false;
        if (beneficiary.contains(person_id)) {
            return true;
        }
        return IfPayerIsBenificiary;
    }
	public static int checkprevBillSettlements(int beneficiaryIndividual,float amountToBePaid,int person_id)
   {
	   BillSettlement bs;
	   float prevdebt_value=0;
	   int prevbillsettledflag=0;
	   int debtor=0;
	   int creditor=0;
	   int removeflag=0;
	   int key_value=0;
	   
	   for(Integer i: billsettlement.keySet()){
		   bs = billsettlement.get(i);
		   key_value=bs.getnum();
		   debtor=bs.getdebtor();
		   creditor=bs.getcreditor();
		   prevdebt_value=bs.getamount();
		   if(debtor==beneficiaryIndividual && creditor==person_id)
		   {
			   amountToBePaid+=prevdebt_value;
			   bs = new BillSettlement(key_value,debtor,amountToBePaid,creditor);
               billsettlement.put(key_value,bs);
			   prevbillsettledflag=1;
			   break;
		   }
		   else if(debtor==person_id && creditor==beneficiaryIndividual)
		   {
			   if(amountToBePaid==prevdebt_value)
				   billsettlement.remove(key_value);
			   else if(amountToBePaid<prevdebt_value)
			   {
				   amountToBePaid=prevdebt_value-amountToBePaid;
				   bs = new BillSettlement(key_value,debtor,amountToBePaid,creditor);
                   billsettlement.put(key_value,bs);
			   }
			   else if(amountToBePaid>prevdebt_value)
			   {
				   amountToBePaid=amountToBePaid-prevdebt_value;
				   bs = new BillSettlement(key_value,creditor,amountToBePaid,debtor);
                   billsettlement.put(key_value,bs);
			   }
			   prevbillsettledflag=1;
			   break;
		   }
	   }
	   return prevbillsettledflag;
   }
   
	public void doGet(HttpServletRequest req,HttpServletResponse res) throws IOException,ServletException
    {
		float prevAmtBalancebeneficiary=0;
		Connection con=getConnection();
		PrintWriter out=res.getWriter();
		String[] bill=req.getParameterValues("mynumber[]");
		int person_id=Integer.parseInt(req.getParameter("person_id"));
		float amount=Float.parseFloat(req.getParameter("amount"));
		billdetails=executeBillQuery("SELECT * FROM BILLDETAILS");
		int billnum=billdetails.size()+1;
		String date=req.getParameter("billdate");
		String time=req.getParameter("billtime");
		String label=req.getParameter("billlabel");
		for(int i=0;i<bill.length;i++)
        {
			billshareID.add(Integer.parseInt(bill[i]));
        }
		int dividableCount = billshareID.size();
        float amountToBePaid = 0;
		float amountToBeCredited = 0;
		int flag=0;
		int key_value=0;
		int prevbillsettledflag=0;
		amountToBePaid = amount / dividableCount;
		boolean IfPayerIsBenificiary = checkIfPayerIsBenificiary(person_id,billshareID);
        if (IfPayerIsBenificiary) 
			amountToBeCredited=amount-amountToBePaid;
		else
			amountToBeCredited=amount;
        for (Integer beneficiaryIndividual : billshareID) {
            if(beneficiaryIndividual!=person_id)
            {
				out.println("<html>");
               out.println("Person_ID "+beneficiaryIndividual + " should be paying "+ amountToBePaid + " to " +"Person_ID "+person_id);
			   out.println("<br>");
			   prevbillsettledflag=checkprevBillSettlements(beneficiaryIndividual,amountToBePaid,person_id);
			   if(prevbillsettledflag==0)
			   {
				   key_value=billsettlement.size()+1;
				   BillSettlement bs=new BillSettlement(key_value,beneficiaryIndividual,amountToBePaid,person_id);
				   billsettlement.put(key_value,bs);
			   }
			   try {
					prevAmtBalancebeneficiary=getPrevNetBalance_Value(beneficiaryIndividual);
					String query = "UPDATE BILLSPLIT SET NETBALANCE =? where PERSON_ID=? ";
                    executeUpdate(query,-amountToBePaid,prevAmtBalancebeneficiary,beneficiaryIndividual);
               }
			   catch(Exception ex){
                   ex.printStackTrace();
               }  
            }
			if(beneficiaryIndividual==person_id)
            {
               try {
					 prevAmtBalancebeneficiary=getPrevNetBalance_Value(person_id);
				     String query = "UPDATE BILLSPLIT SET NETBALANCE =? where PERSON_ID=? ";
			         executeUpdate(query,amountToBeCredited,prevAmtBalancebeneficiary,person_id);
               }
			   catch(Exception ex){
                   ex.printStackTrace();
               } 
		       HashMap<Integer,Work> map=executeQuery("SELECT * FROM BILLSPLIT");
			}
        }
		String query="INSERT INTO BILLDETAILS (BILLNUMBER,BILLLABEL,BILLAMOUNT,BILLDATE,BILLTIME,CREDITOR,BENEFICIARIES) VALUES (?,?,?,?,?,?,?)";
		Statement st = null;
        ResultSet rs = null;
        Work bs;
        try {
                PreparedStatement preparedStmt = con.prepareStatement(query);
                preparedStmt.setInt(1,billnum);
                preparedStmt.setString(2,String.valueOf(label));
                preparedStmt.setFloat(3,amount);
		        preparedStmt.setString(4,String.valueOf(date));
		        preparedStmt.setString(5,String.valueOf(time));
		        preparedStmt.setInt(6,person_id);
                preparedStmt.setString(7,String.valueOf(billshareID));
                preparedStmt.executeUpdate();
				String beneficiaryString=billshareID.toString();
				BillDetails bill_detail=new BillDetails(billnum,label,amount,date,time,person_id,beneficiaryString);
				billdetails.put(billnum,bill_detail);
				flag=1;
        }catch(Exception ex){
            ex.printStackTrace();
        }
	    billshareID.removeAll(billshareID);

		out.println("<button id=\"back\" onclick=\"goBack()\">Go to Profile</button>");
		out.print("<script>");
        out.print("function goBack() {");
         out.print("window.history.back();");
       out.print("}");
        out.print("</script>");
	}
	
}