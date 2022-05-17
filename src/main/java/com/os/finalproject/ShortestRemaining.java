/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.os.finalproject;

import UI.FrameSrtf;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import com.os.finalproject.Scheduling;
import java.util.concurrent.TimeUnit;


/**
 *
 * @author rafael
 */
public class ShortestRemaining {

    //contadores de las frames/pages
    
    int contQueue=0;
    
    private float totalwt;
    private float totaltat;
    
    List<Scheduling> list= new ArrayList<>();
    String frames[][]=new String[16][2];
    Scheduling queue[]= new Scheduling[5];
    
    
    //Enlistar procesos
    public void implementation(List<Scheduling> lis) throws InterruptedException{
        //Recibir datos ingresados
        list.addAll(lis);
        Scheduling sc[]= new Scheduling[list.size()];
        //Cargar datos
        list.toArray(sc);
        findTheBest(sc, sc.length);
        
    }
    
    //Sacando los valores 
    public void findTheBest(Scheduling sc[], int n) throws InterruptedException{
        int wait[]=new int[n], turn[]=new int[n];
        int totalWaitingTime=0, totalTurnAroundTime=0;
        
        //Sacando tiempo de espera
        waitingTime(sc,n,wait,queue);
        
        //Sacando tiempo de entrega
       turnAroundTime(sc,n,wait,turn);
       
       //Calculando promedios
       for (int i = 0; i < n; i++) { 
            totalWaitingTime = totalWaitingTime + wait[i]; 
            totalTurnAroundTime = totalTurnAroundTime + turn[i]; 
        } 
         totalwt=(float)totalWaitingTime /(float)n;
         totaltat=(float)totalTurnAroundTime/(float)n;
        
              
    }
    
    //Calcular tiempos de espera
    public void waitingTime(Scheduling sc[], int n, int wt[], Scheduling queue[]) throws InterruptedException{
        //asignación de valores
        int remaining[]= new int[n];
        Boolean valida=true;
        int complete=0, second=0, max=Integer.MAX_VALUE, shortest=0, finishTime;
        
        

        for(int i=0;i<n;i++){
            remaining[i]=sc[i].getBurstTime();
            sc[i].setFlag(true);
            sc[i].setComplete(false);
        }
        
        
        while(complete !=n){
            boolean flag=false;
            for(int i=0;i<n;i++){
   
                //Encontrar proceso con minimo restante
                if(sc[i].getArrivalTime()<=second && remaining[i]<=max && remaining[i]>0 && sc[i].isFlag()){
                    max= remaining[i];
                    shortest=i;
                    flag=true;
                }
            }
            //Si no existe proceso que cumpla, regresar al inicio
            if(!flag){
                second++;
                continue;
            }

            //Validar no ha sido asignado una pagina
            if(sc[shortest].getcountPages()==0){
                //Generarle tamaño de pagina
                insertionPage(sc[shortest]);
                
                //Generar frames disponibles
                int numberRandom=generateRandom();
                System.out.println("Frames disponibles : "+numberRandom);

                //En caso que solo existe un proceso, lo meterá a ejecución
                if(complete==n-1){
                    int numr=0;
                    do{
                        numr=generateRandom();
                    }while(numr<sc[shortest].getcountPages());
                }
                //Enlistar a cola, en caso de que no exista frames suficientes
                if(numberRandom<sc[shortest].getcountPages()){
                    
                    sc[shortest].setFlag(false);
                    queue[contQueue]=sc[shortest];
                    contQueue++;
                    System.out.println("NO es posible ingresarlo, agregando a la cola...");
                    System.out.println("");
                    max=Integer.MAX_VALUE;
                    second++;
                    continue;
                    
                }
            }


            //reducir tiempo restante en uno
            if(flag){
            remaining[shortest]--;
            //actualizar maximo
            max=remaining[shortest];
            System.out.println("en el tiempo: "+second+" el proceso "+sc[shortest].getName()+" está corriendo...");
            }
            
            //Al finalizar proceso, restaurar nuevamente el máximo
            if(max<=0){
                max=Integer.MAX_VALUE;
                if(remaining[shortest]==0){                   
                    complete++;
                    sc[shortest].setComplete(true);
                    flag=false;
                    
                    System.out.println("en el tiempo: "+second+" el proceso "+sc[shortest].getName()+" ha finalizado");
                    printTablePages(sc[shortest]);

                    //Validando si la cola no está vacia
                    if(!queueIsEmpty()){
                        int menor=0;
                        int index=0;
                        //Encontrar elemento más pequeño de la cola
                        for(int j=0;j<queue.length;j++){
                            if(queue[j]!=null && queue[j].getBurstTime()<menor){
                                menor=queue[j].getBurstTime();
                                index++;
                            }
                           
                        }
                        //regresar proceso a ejecucion
                        returnFrames(index,sc[shortest]);
                    }

                    finishTime=second+1;
                    //calcular tiempo de espera
                    wt[shortest]=finishTime-sc[shortest].getArrivalTime()-sc[shortest].getBurstTime();
                    
                    if(wt[shortest]<0){
                        wt[shortest]=0;
                    }
                    //asignar tiempo de espera al proceso
                    sc[shortest].setWaitingTime(wt[shortest]);
                }
   
            }
            //print cola
            if(max==1)
                printQueue(sc[shortest]);
            
            second++;
            TimeUnit.SECONDS.sleep(1);
        }
        System.out.println("en el tiempo: "+second+" todos los procesos finalizaron");
        
        
    }
    
    //Asignar proceso de la cola a los frames
    public void returnFrames(int index, Scheduling sc){
        
        int newFrame = generateRandom();
        if(queue[index]!=null && queue[index].getcountPages()<=newFrame){
            
            System.out.println("***Regresando de cola a ejecución***");
            System.out.println("Paginas de "+queue[index].getName()+" es: "+queue[index].getcountPages());
            System.out.println("Frames disponibles: "+ newFrame);
            System.out.println("");            
            
            queue[index].setFlag(true);
            queue[index]=null;
            contQueue--;  
        }else{
            //En caso de no cumplir con requisitos, asignar otro proceso de la cola
            if(queue[index+1]!=null){
                index++;
                returnFrames(index,sc);
            }else{
                if(index!=0 && queue[index-1]!=null){
                    index--;
                    returnFrames(index,sc);
                }else{
                    returnFrames(index, sc);
                }
            }
        } 
        
        
    }

    //Imprimir tabla de paginas
    public void printTablePages(Scheduling sc){
        int pos;
        int nCartas = sc.getcountPages();
        Stack < Integer > pCartas = new Stack < Integer > ();
        for (int i = 0; i < nCartas ; i++) {
            pos = (int) Math.floor(Math.random() * nCartas );
            while (pCartas.contains(pos)) {
                pos = (int) Math.floor(Math.random() * nCartas );
            }
        pCartas.push(pos);
        }
        System.out.println("");
        System.out.println("TABLA DE PAGINA");
        System.out.println("");
        System.out.println("----------------");
        System.out.println(sc.getName());
        System.out.println("----------------");
        System.out.println("Frames      Paginas");
        for(int i=0;i<nCartas;i++){
            System.out.print("|");
            System.out.print(pCartas.get(i).toString());
            System.out.println("|             "+i+" |");
        }
        System.out.println("");
    }

    //Impresion de la cola
    public void printQueue(Scheduling sc){
        System.out.println("--------------");
        System.out.println(" En ejecución");
        System.out.println("--------------");
        System.out.println(sc.getName());
        System.out.println("--------------");
        System.out.println(" En cola");
        System.out.println("--------------");
        for(int k=0;k<queue.length;k++){
            if(queue[k]!=null)
                System.out.println(queue[k].getName());
        }
        System.out.println("");        

    }
    
    //Calcular tiempos de salidas
    public void turnAroundTime(Scheduling sc[],int n, int wait[], int turn[]){
        for (int i = 0; i < n; i++) {
            //asignacion tiempo de entrega 
            turn[i] = sc[i].getBurstTime()+ wait[i]; 
            sc[i].setTurnaroundTime(turn[i]);
        }
    }
    
    //Asignar paginas
    public void insertionPage(Scheduling sc){
        
        //generar numero random
        int randomNumber= generateRandom();
        
       System.out.println("Paginas asignadas a "+sc.getName()+" es: "+randomNumber);

        //asignar paginas al proceso
       sc.setcountPages(randomNumber);
    }
    
    //generar valor random
    public int generateRandom(){
        
        Random r= new Random();  
        //Valor maximo del frame    
        int maxValue=16;
        int randomNumber = 0;
        //validar que el numero random sea diferente de 0
        do {
          randomNumber = r.nextInt(maxValue);
        } while(randomNumber == 0);

        

        return randomNumber;
    }

    //Validar si cola está vacia
    public boolean queueIsEmpty(){
        boolean isEmpty=true;
        for(int i=0;i<queue.length;i++){
            if(queue[i]!=null){
                isEmpty=false;
            }
        }
        return isEmpty;
    }
   
    //Getter y setters
    public float getTotalwt() {
        return totalwt;
    }

    public float getTotaltat() {
        return totaltat;
    }

    public void setTotalwt(float totalwt) {
        this.totalwt = totalwt;
    }

    public void setTotaltat(float totaltat) {
        this.totaltat = totaltat;
    }

    
    
    /*
    public static void main(String[] args) throws InterruptedException {
        
        Scheduling p1= new Scheduling(1,"p1",0,9);
        Scheduling p2= new Scheduling(2,"p2",1,5);
        Scheduling p3= new Scheduling(3,"p3",2,3);
        Scheduling p4= new Scheduling(4,"p4",3,7);
        ShortestRemaining sr= new ShortestRemaining();
        
        List<Scheduling> list= new ArrayList<>();
        list.add(p1);
        list.add(p2);
        list.add(p3);
        list.add(p4);
        sr.implementation(list);

        System.out.println(p1.getcountPages());
        System.out.println(p2.getcountPages());
        System.out.println(p3.getcountPages());
        System.out.println(p4.getcountPages());

        for(int i=0;i<sr.frames.length;i++){
            System.out.println(sr.frames[i]);
        }
        
       
    }
    */
}
