package com.billsplit.servlet;
import java.io.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
public class PasswordAuthentication extends Billsplit
{
	public void doPost(HttpServletRequest req,HttpServletResponse res) throws IOException,ServletException
    {
		PrintWriter out=res.getWriter();
		Connection con = getConnection();
		int person_id=Integer.parseInt(req.getParameter("person_id"));
        req.setAttribute("person_id",person_id);
		String password=req.getParameter("password");
		String query="SELECT PASSWORD FROM USERLOGIN WHERE PERSON_ID="+person_id;
		Statement st = null;
		ResultSet rs = null;
		String pwd=null;
        try{
            st = con.createStatement();
            rs = st.executeQuery(query);
            while(rs.next()){
                pwd= rs.getString("PASSWORD");
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
		if(password.equals(pwd))
		{
            req.setAttribute("message", "Successful Login");
			String fwd="Home.jsp";
            RequestDispatcher rd = req.getRequestDispatcher(fwd);
            rd.forward(req,res);
		}
		else
		{   req.setAttribute("message", "Login Failed..Incorrect Password");
	        RequestDispatcher rd = req.getRequestDispatcher("User_Login.html");
            rd.forward(req,res);
		}
		out.print("hii");
	}
}