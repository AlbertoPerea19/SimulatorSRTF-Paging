/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.os.finalproject;

import UI.FrameSrtf;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.os.finalproject.Scheduling;
import java.util.concurrent.TimeUnit;


/**
 *
 * @author rafael
 */
public class ShortestRemaining {

    //contadores de las frames/pages
    int cont=0;
    int contQueue=0;
    
    private float totalwt;
    private float totaltat;
    
    List<Scheduling> list= new ArrayList<>();
    String frames[]=new String[16];
    Scheduling queue[]= new Scheduling[5];
    
    
    //Enlistar procesos
    public void implementation(List<Scheduling> lis) throws InterruptedException{
        list.addAll(lis);
        Scheduling sc[]= new Scheduling[list.size()];
        
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
            //Si no existe proceso que cumpla, realizar recursion
            if(!flag){
                second++;
                continue;
            }
            //Validar si tiene paginación
            if(sc[shortest].getCountFrames()==0){
                valida=insertionFrame(sc[shortest]);
                //En caso que el proceso esté en cola, encontrar proceso con menor tiempo restante
                if(!valida){
                    int index2=0;
                    int menor=sc[0].getBurstTime();
                    for(int i=0; i<shortest;i++){
                        if(sc[i].getBurstTime()<menor && sc[i].isFlag() && !sc[i].isComplete()){
                            menor=sc[i].getBurstTime();
                            index2++;
                        }
                    }
                    shortest=index2;
                    flag=true;
                                    
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
                    //reiniciar contador
                    cont=cont-sc[shortest].getCountFrames();
                    if(cont<0){
                        cont=0;
                    }
                    //Eliminar proceso de la RAM
                    for(int i=0;i<frames.length;i++){
                        if(frames[i]!=null)
                            if(frames[i].equals(sc[shortest].getName()))
                                frames[i]=null;
                    }
                        int menor=0;
                        int index=0;
                        //Encontrar elemento más pequeño de la cola
                        for(int j=0;j<queue.length;j++){
                            if(queue[j]!=null && queue[j].getBurstTime()<menor){
                                menor=queue[j].getBurstTime();
                                index++;
                            }
                           
                        }
                        //Asignar proceso de la cola a la RAM
                        if(queue[index]!=null && queue[index].getCountFrames()<=frames.length-cont){
                            int queue_frames=queue[index].getCountFrames();
                            
                            while(queue_frames!=0){
                                frames[cont]=queue[index].getName();
                                cont++;
                                queue_frames--;
                            }
                            queue[index].setFlag(true);
                            queue[index]=null;
                            
                            contQueue--;  
                        } 
                        //En caso de no cumplir con requisitos, asignar anterior proceso de la cola
                        if(index!=0){
                                if(queue[index-1]!=null && queue[index-1].getCountFrames()<=frames.length-cont){
                                    int queue_frames=queue[index-1].getCountFrames();

                                    while(queue_frames!=0){
                                        frames[cont]=queue[index-1].getName();
                                        cont++;
                                        queue_frames--;
                                    }
                                    queue[index-1].setFlag(true);
                                    queue[index-1]=null;
                                }
                            }
                        //En caso de no cumplir con requisitos, asignar siguiente proceso de la cola
                        if(queue[index+1]!=null && queue[index+1].getCountFrames()<=frames.length-cont){
                            int queue_frames=queue[index+1].getCountFrames();
                            
                            while(queue_frames!=0){
                                frames[cont]=queue[index+1].getName();
                                cont++;
                                queue_frames--;
                            }
                            queue[index+1].setFlag(true);
                            queue[index+1]=null;
                            
                        }
                    
                    System.out.println("en el tiempo: "+second+" el proceso "+sc[shortest].getName()+" ha finalizado");
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
            //print tabla
            if(max==1)
                printTable();
            
            second++;
            TimeUnit.SECONDS.sleep(1);
        }
        System.out.println("en el tiempo: "+second+" todos los procesos finalizaron");
    }
    
    public void printTable(){
        //imprimir frames
      /*  System.out.println("EN RAM");
         for(int i=0;i<frames.length;i++){
             System.out.println(frames[i]);
         }

         //imprimir pages
         System.out.println("EN COLA");
         for(int i=0;i<queue.length;i++){
             if(queue[i]!=null)
             System.out.println(queue[i].getName());
             else
                 System.out.println(queue[i]);
         }*/
        System.out.print("RAM   ");
        System.out.println("EN COLA");
        for(int i=0;i<frames.length;i++){
            if(i<queue.length){
                System.out.print(frames[i]);
                System.out.print("    ");
                if(queue[i]!=null)
                 System.out.println(queue[i].getName());
                 else
                     System.out.println(queue[i]);
            }else{
                System.out.println(frames[i]);

            }
        }

    }
    
    
    public void turnAroundTime(Scheduling sc[],int n, int wait[], int turn[]){
        for (int i = 0; i < n; i++) {
            //asignacion tiempo de entrega 
            turn[i] = sc[i].getBurstTime()+ wait[i]; 
            sc[i].setTurnaroundTime(turn[i]);
        }
    }
    
    //insertar procesos en los frames
    public Boolean insertionFrame(Scheduling sc){
        
        //asignacion valor random
        Random r= new Random();      
        int maxValue=16;
        int randomNumber = 0;
        do {
          randomNumber = r.nextInt(maxValue);
        } while(randomNumber == 0);
        
       System.out.println("Frames asignados a "+sc.getName()+" es: "+randomNumber);
        
       //Validar si entra en RAM
        if((maxValue-cont)>=randomNumber ){
            sc.setCountFrames(randomNumber);
            
            while(randomNumber!=0){
                frames[cont]=sc.getName();
                cont++;
                randomNumber--;
            }
        //Meter en cola    
        }else{
            sc.setCountFrames(randomNumber);
            sc.setFlag(false);
            queue[contQueue]=sc;
            contQueue++;
            System.out.println("No es posible agregarse a RAM, agregando a la memoria virtual...");
            }
        return sc.isFlag();
    }

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

        System.out.println(p1.getCountFrames());
        System.out.println(p2.getCountFrames());
        System.out.println(p3.getCountFrames());
        System.out.println(p4.getCountFrames());

        for(int i=0;i<sr.frames.length;i++){
            System.out.println(sr.frames[i]);
        }
        
       
    }
}
