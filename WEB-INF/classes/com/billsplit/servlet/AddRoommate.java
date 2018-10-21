package com.billsplit.servlet;
import com.billsplit.servlet.p1.Work;
import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
public class AddRoommate extends Billsplit
{
	public static void updateUserLogin(int person_id,String name,String password)
	{
		Connection con = getConnection();
		String query="INSERT INTO USERLOGIN (PERSON_ID,NAME,PASSWORD) VALUES(?,?,?)";
		Statement st = null;
		try {
          PreparedStatement preparedStmt = con.prepareStatement(query);
          preparedStmt.setInt(1,person_id);
          preparedStmt.setString(2,String.valueOf(name));
		  preparedStmt.setString(3,String.valueOf(password));
          preparedStmt.executeUpdate();
        }catch(Exception ex){
            ex.printStackTrace();
        }
	}
	public void doPost(HttpServletRequest req,HttpServletResponse res) throws IOException,ServletException
    {
		PrintWriter out=res.getWriter();
		map=executeQuery("SELECT * FROM BILLSPLIT");
		int person_id=map.size()+1;
		String name = req.getParameter("name");
		String password=req.getParameter("psw");
		String query="INSERT INTO BILLSPLIT (PERSON_ID,NAME,NETBALANCE) VALUES (?,?,0.0)";
		updateUserLogin(person_id,name,password);
		Connection con = getConnection();
        Work bs;
        try {
          PreparedStatement preparedStmt = con.prepareStatement(query);
          preparedStmt.setInt(1,person_id);
          preparedStmt.setString(2,String.valueOf(name));
          preparedStmt.executeUpdate();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        map=executeQuery("SELECT * FROM BILLSPLIT");
		RequestDispatcher rd=req.getRequestDispatcher("Add_roommate.html");  
        rd.forward(req, res);	
	}
}