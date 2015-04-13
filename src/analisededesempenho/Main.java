/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analisededesempenho;

import java.util.Random;

/**
 *
 * @author gabrielamaral
 */
public class Main {

    private static Random r;

    private static void iniciarRandom() {
        long semente = System.currentTimeMillis();
        System.out.println("Semente = " + semente);
        r = new Random(semente); // semente aleatoria
    }

    private static double aleatorio() {
        return 1.0 - r.nextDouble();
    }

    private static double min(double num1, double num2) {
        if (num1 < num2) {
            return (num1);
        } else {
            return (num2);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /* 
         * raciocinio para o desenvolvimento:
         * intervalo entre chegadas: media 5.0 segundos
         * tempo de atendimento: media 4.5 segundos
         */

        /**
         * ***************************
         * Declaração das variaveis * ***************************
         */
        double parametroChegadas = 1.0 / 5.0; //parametro Exponencial
        double parametroServico = 1.0 / 4.5; //parametro Exponencial

        double tempoDecorrido; //indica o tempo atual da execucao da simulacao
        double tempoSimulacao = 1_000_000.0; //indica o tempo total (termino) da simulacao

        double chegadaPessoa; //tempo de chegada da proxima pessoa
        double saidaPessoa = 0.0; //tempo de saida da pessoa em atendimento

        double fila = 0.0;
        
        double ocupacao = 0.0;
        
        /**
         * Variaveis para o calculo de medidas E[N] E[W] lambda
         */
        double eNSoma = 0.0;
        double eNTempoAnterior = 0.0;
        int eNNumEventos = 0;

        double eWChegadaSoma = 0.0;
        double eWChegadaTempoAnterior = 0.0;
        int eWChegadaNumEventos = 0;

        double eWSaidaSoma = 0.0;
        double eWSaidaTempoAnterior = 0.0;
        int eWSaidaNumEventos = 0;

        iniciarRandom();

        chegadaPessoa = (-1.0 / parametroChegadas) * Math.log(aleatorio());

        while (true) {
            if (saidaPessoa > 0.0) {
                tempoDecorrido = min(chegadaPessoa, saidaPessoa);
            } else {
                tempoDecorrido = chegadaPessoa;
            }
            
            if (tempoDecorrido > tempoSimulacao) {
                eNSoma += eNNumEventos * (tempoDecorrido - eNTempoAnterior);
                eWSaidaSoma += eWSaidaNumEventos * (tempoDecorrido - eWSaidaTempoAnterior);
                eWChegadaSoma += eWChegadaNumEventos * (tempoDecorrido - eWChegadaTempoAnterior);
                break;
            }

            if (tempoDecorrido == chegadaPessoa) {
			//evento de chegar uma pessoa no sistema
			/*raciocinio
                 * incrementar fila
                 * - caixa livre?
                 *     pessoa entra em atendimento
                 * - caixa ocupado?
                 *     pessoa vai para a fila
                 * 
                 * gero o tempo de chegada da proxima pessoa
                 */
                fila++;
                if (saidaPessoa == 0.0) {
                    //indica caixa livre
                    saidaPessoa = tempoDecorrido + (-1.0 / parametroServico) * Math.log(aleatorio());;
                    ocupacao += (saidaPessoa - tempoDecorrido);
                }

                chegadaPessoa = tempoDecorrido + (-1.0 / parametroChegadas) * Math.log(aleatorio());
                
                eNSoma += eNNumEventos * (tempoDecorrido - eNTempoAnterior);
                eNTempoAnterior = tempoDecorrido;
                eNNumEventos++;

                eWChegadaSoma += eWChegadaNumEventos * (tempoDecorrido - eWChegadaTempoAnterior);
                eWChegadaTempoAnterior = tempoDecorrido;
                eWChegadaNumEventos++;
            } else {
			//evento de sair uma pessoa do caixa
                /*raciocinio
                 * decrementar fila
                 * - existe pessoa na fila?
                 *     gerar o tempo de atendimento para a proxima pessoa
                 * - sem fila?
                 *     ajustar o caixa como livre (saidaPessoa = 0.0)
                 */
                fila--;
                if (fila > 0.0) {
                    saidaPessoa = tempoDecorrido + (-1.0 / parametroServico) * Math.log(aleatorio());
                    ocupacao += (saidaPessoa - tempoDecorrido);
                } else {
                    saidaPessoa = 0.0;
                }
                
                eNSoma += eNNumEventos * (tempoDecorrido - eNTempoAnterior);
                eNTempoAnterior = tempoDecorrido;
                eNNumEventos--;

                eWSaidaSoma += eWSaidaNumEventos * (tempoDecorrido - eWSaidaTempoAnterior);
                eWSaidaTempoAnterior = tempoDecorrido;
                eWSaidaNumEventos++;
            }
        }
        
        double lambda = eWChegadaNumEventos / tempoDecorrido;
        double eW = (eWChegadaSoma - eWSaidaSoma) / eWChegadaNumEventos;
        
        System.out.println("Ocupacao: " + ocupacao / tempoDecorrido);
        System.out.println("Lambda: " + lambda);
        System.out.println("E[W]: " + eW);
        System.out.println("E[N]: " + (eNSoma / tempoDecorrido));
        System.out.printf("Diferença: %.20f\n", Math.abs((eNSoma / tempoDecorrido) - (eW * lambda)));
    }

}
