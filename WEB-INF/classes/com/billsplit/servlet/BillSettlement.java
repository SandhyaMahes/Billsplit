package com.billsplit.servlet.p1;
//package p1;
public class BillSettlement{
	int num;
	int debtor;
	int creditor;
	float amount;
    public BillSettlement(int num,int debtor,float amount,int creditor){
		this.num=num;
        this.debtor=debtor;
		this.amount = amount;
        this.creditor = creditor;
    }
	public int getnum(){
        return this.num;
    }
	public int getdebtor(){
        return this.debtor;
    }
    public float getamount(){
        return this.amount;
    }
    public int getcreditor(){
        return this.creditor;
    }
}