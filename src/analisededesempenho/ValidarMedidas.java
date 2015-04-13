package analisededesempenho;

import java.util.Random;

/**
 *
 * @author gabrielamaral
 */
public class ValidarMedidas {
    
    private static Random r;

    private static void iniciarRandom() {
        long semente = System.currentTimeMillis();
        System.out.println("Semente = " + semente);
        r = new Random(semente); // semente aleatoria
    }
    
    private static double aleatorio() {
        return 1.0 - r.nextDouble();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       
        double parametroChegadas = 1.0 / 5.0; //parametro Exponencial
        double parametroServico = 1.0 / 4.5; //parametro Exponencial
        
        iniciarRandom();
        
        double soma = 0.0;
        for (int i = 0; i < 1_000_000; i++) {
            soma += (-1.0 / parametroChegadas) * Math.log(aleatorio());
        }
        System.out.println("Media = " + soma/1_000_000.0);
        
        soma = 0.0;
        for (int i = 0; i < 1_000_000; i++) {
            soma += (-1.0 / parametroServico) * Math.log(aleatorio());
        }
        System.out.println("Media = " + soma/1_000_000.0);
    }
    
}
