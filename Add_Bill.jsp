
<%@ page language="java" import="java.sql.*"%>
<html>
<head>
<title>Add Bills</title>
<script src="jquery-3.3.1.js"></script>
</head>
<body>
<center>Add Bills</center><br><br>
<center>ROOMMATE DETAILS
<table>
<tr> <th> PERSON_ID </th>
     <th> NAME  </th>
	 <th> NETBALANCE </th>
</tr>	 
	   <%
        int noOfRows=0;
        try
        {
			ResultSet rs = null;
            Class.forName("com.mysql.jdbc.Driver");  
            Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/newdb?useSSL=false","root","root");  
		    Statement s=con.createStatement();
            rs=s.executeQuery("SELECT * FROM BILLSPLIT");
            while(rs.next()) 
            {
				noOfRows++;
				int person_id=rs.getInt("PERSON_ID");
				String name=rs.getString("NAME");
				float netbalance=rs.getFloat("NETBALANCE");
				%>
				<tr> <td><%=person_id %> </td>
                     <td><%=name %> </td>
					 <td><%=netbalance %> </td>
				</tr>	 
					 <%
            }
        }
        catch(Exception e){out.println(e);}
		%>
		</table>
        <script>
                $(document).ready(function() {
                 var max_fields = '<%= noOfRows%>';
                 var wrapper = $(".input_fields_wrap"); 
                 var add_button = $(".add_field_button"); 
                 var x = 1; 
                 $(add_button).click(function(event){ 
                     event.preventDefault();
                     if(x < max_fields){ 
                         x++; 
                         $(wrapper).append('<div><input type="number" name="mynumber[]"/><a href="#" class="remove_field">Remove</a></div>'); 
                     }
                     else
                     alert("No more Roommates to be added!");
                 });
                 
                 $(wrapper).on("click",".remove_field", function(event){ 
                     event.preventDefault(); $(this).parent('div').remove(); x--;
                 })
               });
               </script>
<form name="bill" action="http://localhost:8080/billsplit/addbill" method="get">
Enter Payer's PERSON_ID:<br>
  <input type="number" name="person_id"><br><br>
Bill amount<br>
  <input type="number" name="amount"><br><br>
<div class="input_fields_wrap">
    <button class="add_field_button">Add PERSON_ID of Beneficiary</button><br>
    <div><input type="number" name="mynumber[]"></div>
</div><br><br>
Bill Description:
<input type="text" name="billlabel"><br><br>
Bill Date:
<input type = "date" name = "billdate"><br><br>
Bill Time:
<input type = "time" name="billtime"><br><br>
<input type ="submit" value="Add Bill">
</form>
<a href="User_Login.html">Logout</a>
</center>
</body>
</html>
