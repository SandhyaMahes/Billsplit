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
public class ViewBills extends Billsplit
{
	public static HashMap<Integer,BillSettlement> displaybills=new HashMap<Integer,BillSettlement>();
    private static int flag=0;
	
	 public static void calculateNetbalance(int person_id,HttpServletRequest req,HttpServletResponse res) throws IOException,ServletException
    {
	   netbalance=executeNetBalanceQuery("SELECT * FROM BILLSPLIT");
       NetBalance nb;
       PrintWriter out=res.getWriter();
	   out.print("<html>");
	   //displayNetbalance(netbalance,res);
	   out.println("****Simpified Bill Details for PERSON_ID "+person_id+"  *****");
	   out.print("<br>");
	   simplifyUserBill(netbalance,person_id,req,res);
	   out.print("<html>");
   }
	public static int simplifyUserSpecificBills(int person_id,HttpServletRequest req,HttpServletResponse res) throws IOException,ServletException
	{
		int billsettled_flag=0;
        PrintWriter out=res.getWriter();
		out.print("<html>");
		map=executeQuery("SELECT * FROM BILLSPLIT");
		for(Integer i : map.keySet()){
            Work b= map.get(i);
			if(b.getnum()==person_id)
            {
				if(b.getnetbalance_value()==0)
				{
					out.println("You have no bills to be settled.");
					out.print("<br>");
					billsettled_flag=1;
			        break;
				}
				else if(b.getnetbalance_value()>0)
					out.println("You have money to be redeemed.");
				else if(b.getnetbalance_value()<0)
					out.println("You have debts to pay.");
				out.print("<br>");
				calculateNetbalance(person_id,req,res);
			}
        }
		out.print("</html>");
		return billsettled_flag;
	}
	public static void simplifyUserBill(HashMap<Integer,NetBalance> netbalance,int person_id,HttpServletRequest req,HttpServletResponse res) throws IOException,ServletException
    {
        PrintWriter out=res.getWriter();
		out.print("<html>");
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
					out.print("No more Bills to be settled");
					out.print("<br>");
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
		LABEL:
		{if(min_key==0 ||  max_key==0)
			{
		    out.print("<br>");
			break LABEL;}
		else{
			if (max_Value != min_Value) {
				flag=1;
                float result = max_Value + min_Value;
				if(result == 0 )
				{
		            out.println("Person_id "+min_key + " needs to pay " + Math.abs(min_Value) + " to " +"Person_id "+max_key );
					out.print("<br>");
					break LABEL;
				}
			    else if (result > 0) {
					out.println("Person_id "+min_key + " needs to pay " + Math.abs(min_Value) + " to " +"Person_id "+max_key );
                    out.print("<br>");
					netbalance.remove(max_key);
                    netbalance.remove(min_key);
				    nb=new NetBalance(max_key,result);
                    netbalance.put(max_key, nb);
				    nb=new NetBalance(min_key,0);
                    netbalance.put(min_key, nb);
						key_value=displaybills.size()+1;
						BillSettlement bs = new BillSettlement(key_value,min_key,Math.abs(min_Value),max_key);
                        displaybills.put(key_value,bs);						
                }
				else {
                    out.println("Person_id "+min_key + " needs to pay " +Math.abs(max_Value)  + " to " + "Person_id "+max_key );
                    out.print("<br>");
					netbalance.remove(max_key);
                    netbalance.remove(min_key);
			    	nb=new NetBalance(min_key,result);
                    netbalance.put(min_key, nb);
				    nb=new NetBalance(max_key,0);
                    netbalance.put(max_key, nb);
						key_value=displaybills.size()+1;
						BillSettlement bs=new BillSettlement(key_value,min_key,Math.abs(max_Value),max_key);
                        displaybills.put(key_value,bs); 				
				}				
            }
          simplifyUserBill(netbalance,person_id,req,res);
		}}
        req.setAttribute("displaybills",displaybills);		
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
	public static void displayUserspecificBills(int person_id,HttpServletRequest req,HttpServletResponse res) throws IOException,ServletException
	{
		billdetails.clear();
        PrintWriter out=res.getWriter();
		out.print("<html>");
		Connection con = getConnection();
		String query="SELECT * FROM BILLDETAILS WHERE CREDITOR="+person_id +" OR BENEFICIARIES LIKE ?";
		Statement st = null;
		ResultSet rs = null;
        try{
			out.print("<h3>");
			out.print("Your Bill Details");
			out.print("</h3>");
			out.print("<style>");
			out.print("table, th, td {");
            out.print("border: 1px solid black;");
            out.print("border-collapse: collapse;");
            out.print("text-align: center;");
            out.print("}");
			out.println("</style>");
			out.println("<table>");
			out.print("<tr>");
			out.print("<th>"+"BILLNUMBER"+"</th>");
			out.print("<th>"+"BILLLABEL"+"</th>");
			out.print("<th>"+"BILLAMOUNT"+"</th>");
			out.print("<th>"+"BILLDATE"+"</th>");
			out.print("<th>"+"BILLTIME"+"</th>");
			out.print("<th>"+"CREDITOR"+"</th>");
			out.print("<th>"+"BENEFICIARIES"+"</th>");
			out.print("</tr>");
			out.print("<br>");
			PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setString(1,String.valueOf("%"+Integer.toString(person_id)+"%"));
			rs=preparedStmt.executeQuery();
            while(rs.next()){
                int billnum = rs.getInt("BILLNUMBER");
				String bill_label = rs.getString("BILLLABEL");
				float billamount = rs.getFloat("BILLAMOUNT");
				String billdate = rs.getString("BILLDATE");
				String billtime = rs.getString("BILLTIME");
				int creditor = rs.getInt("CREDITOR");
				String beneficiaries=rs.getString("BENEFICIARIES");
			    beneficiaries=beneficiaries.replace("[","");
				String beneficiariesStr=beneficiaries.replace("]","");
				String[] beneficiariesArray = beneficiariesStr.split(",");
				StringBuilder sb1 = new StringBuilder(""); 
				String names=null;
				for(String str:beneficiariesArray)
				{
					str=str.replaceAll("\\s+","");
					names=getNamefromID(Integer.parseInt(str));
					sb1.append(names+",");
				}	
                String beneficiariesName=sb1.toString();
				beneficiariesName=beneficiariesName.substring(0,beneficiariesName.length()-1);
                BillDetails bd=new BillDetails(billnum,bill_label,billamount,billdate,billtime,creditor,beneficiariesName);
                billdetails.put(billnum,bd);
                req.setAttribute("billdetails", billdetails);
				out.print("<tr>");
				out.print("<td>");
				out.print(billnum+"</td>");
				out.print("<td>"+bill_label+"</td>");
				out.print("<td>"+billamount+"</td>");
				out.print("<td>"+billdate+"</td>");
				out.print("<td>"+billtime+"</td>");
				out.print("<td>"+creditor+"</td>");
				out.print("<td>"+beneficiariesName+"</td>");
					out.println("</tr>");
				out.print("<br>");
            }
        }catch(Exception ex){
            ex.printStackTrace();
        } 
			out.println("</table>");
		    out.print("</html>");
	}
	public void doGet(HttpServletRequest req,HttpServletResponse res) throws IOException,ServletException
    {
		res.setContentType("text/html");
		PrintWriter out=res.getWriter();
        out.println("<html>");
		int person_id=Integer.parseInt(req.getParameter("person_id"));
		out.println("Logged in as Person_id "+person_id);
			displaybills.clear();
			displayUserspecificBills(person_id,req,res);
            simplifyUserSpecificBills(person_id,req,res);
	    out.println("</html>");
		out.println("</html>");
	}
}