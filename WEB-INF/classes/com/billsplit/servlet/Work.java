package com.billsplit.servlet.p1;
//package p1;
public class Work
{
    int num;
    String personName;
    float netbalance_value;
    float amtDebt;
	String date;
    public Work(int num, String personName,float netbalance_value){
        this.num = num;
        this.personName = personName;
        this.netbalance_value = netbalance_value;
    }
    public int getnum(){
        return this.num;
    }
    public String getpersonName(){
        return this.personName;
    }
    public float getnetbalance_value(){
        return this.netbalance_value;
    }
}
