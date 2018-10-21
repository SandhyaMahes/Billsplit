package com.billsplit.servlet.p1;
//package p1;
public class NetBalance{
	int person_id;
	String name;
	float amount;
    public NetBalance(int person_id,float amount){
        this.person_id=person_id;
		this.amount = amount;
    }
	public int getperson_id(){
        return this.person_id;
    }
    public float getamount(){
        return this.amount;
    }
}