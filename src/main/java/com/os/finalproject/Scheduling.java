/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.os.finalproject;

/**
 *
 * @author rafael
 */
public class Scheduling {
    //Atributos de los procesos
    private int id;
    private String name;
    private int arrivalTime;
    private int burstTime;
    private int waitingTime;
    private int turnaroundTime;
    private int countPages;
    private boolean flag;
    private boolean complete;

    public Scheduling(int id, String name, int arrivalTime, int burstTime, int waitingTime, int turnaroundTime) {
        this.id = id;
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.waitingTime = waitingTime;
        this.turnaroundTime = turnaroundTime;
    }

    public Scheduling(int id, String name, int arrivalTime, int burstTime) {
        this.id = id;
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
    }

    public Scheduling(int id, String name, int arrivalTime, int burstTime, int countPages) {
        this.id = id;
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.countPages = countPages;
    }
    

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
    
 
    
    public int getcountPages() {
        return countPages;
    }

    public void setcountPages(int countPages) {
        this.countPages = countPages;
    }
    

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public int getTurnaroundTime() {
        return turnaroundTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public void setBurstTime(int burstTime) {
        this.burstTime = burstTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public void setTurnaroundTime(int turnaroundTime) {
        this.turnaroundTime = turnaroundTime;
    }
    
    
    
}
