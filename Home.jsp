<%@ page language="java" import="java.sql.*"%>
<html>
<head>
 <title>Bill Splitting</title>
 <script>
    var alerted = localStorage.getItem('alerted') || '';
    if (alerted != 'yes') {
     alert("Successful Login");
     localStorage.setItem('alerted','yes');
    }
 </script>
</head>
<body>
<center>BILLSPLIT</center>
<%
int person_id=(int)request.getAttribute("person_id");
%>
<script>
var person_id="<%=person_id%>";
</script>
<a href="Add_Roommate.html"><input type="button" value="Add roommate"><br> </a>
<a href="Remove_roommate.html"><input type ="button" value="Remove roommate"><br> </a>
<form action="http://localhost:8080/billsplit/Add_Bill.jsp" method="get">
    <input type ="submit" value="Add bill details"><br>
</form>
<form action="http://localhost:8080/billsplit/view" method="get">
    <input type="hidden" name="person_id" value="<%=person_id%>">
    <input type ="submit" value="View Your Bills"><br>
</form>
<form action="http://localhost:8080/billsplit/bills" method="get">
    <input type="hidden" name="person_id" value="<%=person_id%>">
    <input type ="submit" value="Settle Your Bills"><br>
</form>
<a href="User_Login.html">Logout</a>
</body>
</html>