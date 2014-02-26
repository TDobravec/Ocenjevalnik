/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Ana
 */
public class DN3 {
    public static void main(String args[]) {
        
              int funkcija = Integer.parseInt(args[0]);
              float x = Float.parseFloat(args[1]);
              float y = Float.parseFloat(args[2]);
              
              if(args.length == 3){
    
                 if (funkcija==1){
                    System.out.printf("%.1f + %.1f = %.1f\n", x, y, x+y);
                 }
                 if (funkcija ==2){
                     System.out.printf("%.1f - %.1f = %.1f\n", x, y, x-y);
                 }
    
                if (funkcija ==3){
                      System.out.printf("%.1f * %.1f = %.1f\n", x, y, x*y);
                 }
    
                 if (funkcija ==4){
                       System.out.printf("%.1f / %.1f = %.1f\n", x, y, x/y);
                 }
              }
              
              if(args.length>2)
                    if(funkcija ==5){
                        float min = Float.parseFloat(args[1]);
                        System.out.printf("Minimum stevil:");
                          for(int i=1; i<=args.length-1;i++){
                              System.out.printf("%5.1f", Float.parseFloat(args[i]));
                              if(Float.parseFloat(args[i])<min){
                                min = Float.parseFloat(args[i]);
                                i = i+1;
                              }
                          }
                          System.out.printf(" je %3.1f\n", min);                         
                    }
    }                  
    
}
