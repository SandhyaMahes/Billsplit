package com.billsplit.servlet.p1;
//package p1;
public class BillDetails{
	int billnum;
	String billlabel;
	float amount;
    String billdate;
    String billtime;
    int creditor;
    String beneficiaries;
    public BillDetails(int billnum,String billlabel,float amount,String billdate,String billtime,int creditor,String beneficiaries)
    {
        this.billnum=billnum;
        this.billlabel=billlabel;
		this.amount = amount;
        this.billdate=billdate;
        this.billtime=billtime;
        this.creditor=creditor;
        this.beneficiaries=beneficiaries;
    }
	public int getbillnum(){
        return this.billnum;
    }
    public String getbilllabel(){
        return this.billlabel;
    }
    public float getamount(){
        return this.amount;
    }
    public String getbilldate(){
        return this.billdate;
    }
    public String getbilltime(){
        return this.billtime;
    }
    public int getcreditor(){
        return this.creditor;
    }
    public String getbeneficiaries(){
        return this.beneficiaries;
    }
}