package com.example.just4fun;

public class Users {
    private String name;
    private boolean study;
    private int age;

    public Users(String name,boolean study,int age){
        this.name=name;
        this.study=study;
        this.age=age;
    }

    public Users(){

    }

    public String getName(){
        return  name;
    }
    public boolean getStudy(){return study;}
    public int getAge(){return age;}

    public  void setName(String name){
        this.name=name;
    }
    public  void setStudy(boolean study){this.study=study; }
    public  void setAge(int age){this.age=age;}

    public String toString(){
        return "Name : "+name+" Study : "+study+" Age : "+age;
    }
}
