package com.billsplit.servlet;
import com.billsplit.servlet.p1.Work;
import com.billsplit.servlet.p1.NetBalance;
import com.billsplit.servlet.p1.BillDetails;
import com.billsplit.servlet.p1.BillSettlement;
import java.io.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
public class UserBills extends Billsplit
{
	public static HashMap<Integer,BillSettlement> displaybills=new HashMap<Integer,BillSettlement>();
    private static int flag=0;
	private static int updateflag=0;
   public static void calculateNetbalance(int person_id,HttpServletRequest req,HttpServletResponse res) throws IOException,ServletException
    {
	   netbalance=executeNetBalanceQuery("SELECT * FROM BILLSPLIT");
       NetBalance nb;
       PrintWriter out=res.getWriter();
	   simplifyUserBill(netbalance,person_id,req,res);
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
	   
	   for(Integer i: displaybills.keySet()){
		   bs = displaybills.get(i);
		   key_value=bs.getnum();
		   debtor=bs.getdebtor();
		   creditor=bs.getcreditor();
		   prevdebt_value=bs.getamount();
		   if(debtor==beneficiaryIndividual && creditor==person_id)
		   {
			   amountToBePaid+=prevdebt_value;
			   bs = new BillSettlement(key_value,debtor,amountToBePaid,creditor);
               displaybills.put(key_value,bs);
			   prevbillsettledflag=1;
			   break;
		   }
		   else if(debtor==person_id && creditor==beneficiaryIndividual)
		   {
			   if(amountToBePaid==prevdebt_value)
				   displaybills.remove(key_value);
			   else if(amountToBePaid<prevdebt_value)
			   {
				   amountToBePaid=prevdebt_value-amountToBePaid;
				   bs = new BillSettlement(key_value,debtor,amountToBePaid,creditor);
                   displaybills.put(key_value,bs);
			   }
			   else if(amountToBePaid>prevdebt_value)
			   {
				   amountToBePaid=amountToBePaid-prevdebt_value;
				   bs = new BillSettlement(key_value,creditor,amountToBePaid,debtor);
                   displaybills.put(key_value,bs);
			   }
			   prevbillsettledflag=1;
			   break;
		   }
	   }
	   return prevbillsettledflag;
   }
     public static void simplifyUserBill(HashMap<Integer,NetBalance> netbalance,int person_id,HttpServletRequest req,HttpServletResponse res) throws IOException,ServletException
    {
        PrintWriter out=res.getWriter();
		//out.print("<html>");
	    float max_Value=0;
		float min_Value=0;
		int max_key=0;
		int min_key=0;
		int min_keyFlag=0;
		int max_keyFlag=0;
		float prevAmtBalance=0;
		int prevbillsettledflag=0;
		int key_value=0;
		NetBalance nb;
		//netbalance=executeNetBalanceQuery("select * from billsplit");
		for(Integer i: netbalance.keySet()){
			nb=netbalance.get(i);
			if(nb.getperson_id()== person_id)
			{
				if(nb.getamount()<0)
				{
					min_Value=nb.getamount();
					min_key=nb.getperson_id();
					min_keyFlag=1;
					break;
				}
                else if(nb.getamount()>0)
				{
					max_Value=nb.getamount();
					max_key=nb.getperson_id();
					max_keyFlag=1;
					break;
				}	
				else if(nb.getamount()==0)
				{
					max_keyFlag=1;
					min_keyFlag=1;
				}
			}	
		}
		for(Integer i: netbalance.keySet()){
			nb=netbalance.get(i);
			if(min_keyFlag==0)
			{
				if(nb.getamount()<min_Value)
			    {
					min_Value=nb.getamount();
				    min_key=nb.getperson_id();
			    }
			}
			if(max_keyFlag==0)
			{
				if(nb.getamount()>max_Value)
			    {
					max_Value=nb.getamount();
				    max_key=nb.getperson_id();
			    }
			}
		}
		//out.println("min key"+min_key+"  max_key"+max_key);
		//out.print("min value"+min_Value+"  max_Value"+max_Value);
		LABEL:
		{if(min_key==0 ||  max_key==0)
			{out.println("Bills settled for PERSON_ID "+person_id);
		    out.print("<br>");}
		else{
			if (max_Value != min_Value) {
				flag=1;
                float result = max_Value + min_Value;
				if(result == 0 )
				{
					updateBillSettlements(min_key,0);
					updateBillSettlements(max_key,0);
		            //out.println("Person_id "+min_key + " needs to pay " + Math.abs(min_Value) + " to " +"Person_id "+max_key );
					break LABEL;
				}
			    else if (result > 0) {
					out.println("Person_id "+min_key + " needs to pay " + Math.abs(min_Value) + " to " +"Person_id "+max_key );
                    out.print("<br>");
					netbalance.remove(max_key);
                    netbalance.remove(min_key);
				    nb=new NetBalance(max_key,result);
                    netbalance.put(max_key, nb);
					prevAmtBalance=getPrevNetBalance_Value(max_key);
					updateBillSettlements(max_key,prevAmtBalance+min_Value);
				    nb=new NetBalance(min_key,0);
                    netbalance.put(min_key, nb);
					prevAmtBalance=getPrevNetBalance_Value(min_key);
					updateBillSettlements(min_key,prevAmtBalance-min_Value);
					prevbillsettledflag=checkprevBillSettlements(min_key,min_Value,max_key);
			        if(prevbillsettledflag==0)
					{
						key_value=displaybills.size()+1;
						BillSettlement bs = new BillSettlement(key_value,min_key,Math.abs(min_Value),max_key);
                        displaybills.put(key_value,bs);		
					}						
                }
				else {
                    out.println("Person_id "+min_key + " needs to pay " +Math.abs(max_Value)  + " to " + "Person_id "+max_key );
                    out.print("<br>");
					netbalance.remove(max_key);
                    netbalance.remove(min_key);
			    	nb=new NetBalance(min_key,result);
                    netbalance.put(min_key, nb);
					prevAmtBalance=getPrevNetBalance_Value(min_key);
			        updateBillSettlements(min_key,prevAmtBalance+max_Value);
				    nb=new NetBalance(max_key,0);
                    netbalance.put(max_key, nb);
					prevAmtBalance=getPrevNetBalance_Value(max_key);
					updateBillSettlements(max_key,prevAmtBalance-max_Value);
					prevbillsettledflag=checkprevBillSettlements(min_key,max_Value,max_key);
			        if(prevbillsettledflag==0)
					{
						key_value=displaybills.size()+1;
						BillSettlement bs=new BillSettlement(key_value,min_key,Math.abs(max_Value),max_key);
                        displaybills.put(key_value,bs); 
					}							
				}
							
            }
          simplifyUserBill(netbalance,person_id,req,res);
		}}
		if(flag==0)
		{	out.println("No bills to be settled");
		out.print("<br>");}
        //req.setAttribute("displaybills",displaybills);		
        out.print("</html>");		
   }
    public static String getNamefromID(int person_id)
   {
	   Connection con=getConnection();
	   Statement st = null;
	   ResultSet rs = null;
	   String name=null;
       try {
		   st = con.createStatement();
           String query="SELECT NAME FROM BILLSPLIT WHERE PERSON_ID="+person_id;
		   rs = st.executeQuery(query);
           while(rs.next()){
                name= rs.getString("NAME");
            }
        }catch(Exception ex){
            ex.printStackTrace();
        } 
		return name;
   }
public void doGet(HttpServletRequest req,HttpServletResponse res) throws IOException,ServletException
    {
		res.setContentType("text/html");
		PrintWriter out=res.getWriter();
        out.println("<html>");
		int person_id=Integer.parseInt(req.getParameter("person_id"));
		out.println("Logged in as Person_id "+person_id);
			displaybills.clear();
            calculateNetbalance(person_id,req,res);
	    out.println("</html>");
		out.println("</html>");
	}
}