package com.billsplit.servlet;
import com.billsplit.servlet.p1.Work;
import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
public class RemoveRoommate extends Billsplit
{
	public void doGet(HttpServletRequest req,HttpServletResponse res) throws IOException,ServletException
    {
		PrintWriter out=res.getWriter();
		Connection con = getConnection();
		int person_id=Integer.parseInt(req.getParameter("person_id"));
		Work bs;
            try {
				String query="DELETE FROM BILLSPLIT WHERE PERSON_ID=?";
			    PreparedStatement preparedStmt = con.prepareStatement(query);
                preparedStmt.setInt(1,person_id);
                preparedStmt.execute();
			    System.out.println("ROOMMATE_"+person_id+" removed");
            }catch(Exception ex){
                ex.printStackTrace();
            }
            String query="DELETE FROM USERLOGIN WHERE PERSON_ID=?";
            try {
			    PreparedStatement preparedStmt = con.prepareStatement(query);
                preparedStmt.setInt(1,person_id);
                preparedStmt.execute();
            }catch(Exception ex){
                ex.printStackTrace();
            }
			map.clear();
			map=executeQuery("SELECT * FROM BILLSPLIT");
			try {
				for(Integer i : map.keySet()){
					Work b= map.get(i);
			        if(b.getnum()>person_id)
			        {
						query="UPDATE BILLSPLIT SET PERSON_ID=? WHERE PERSON_ID = ?";
				        PreparedStatement prepared = con.prepareStatement(query);
                        prepared.setInt(1,(b.getnum()-1));
			            prepared.setInt(2,b.getnum());
                        prepared.execute();					
						query="UPDATE USERLOGIN SET PERSON_ID=? WHERE PERSON_ID = ?";
						PreparedStatement ps = con.prepareStatement(query);
                        ps.setInt(1,(b.getnum()-1));
			            ps.setInt(2,b.getnum());
                        ps.execute();
			        }
                }
            }catch(Exception ex){
            ex.printStackTrace();
            }
			map.clear();
			map=executeQuery("SELECT * FROM BILLSPLIT");
		    RequestDispatcher rd=req.getRequestDispatcher("Remove_Roommate.html");  
            rd.forward(req, res);
	}
}