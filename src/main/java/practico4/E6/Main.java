package practico4.E6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author santiago
 */
public class Main {
	
    //Estadisticas
    private static int countGanoX = 0;
    private static int countGanoO = 0;
    private static int countEmpate = 0;
    private static float mu = 0.01f;
    private static float STEP_MU = 0.00001f;
    public static double gamma = 0.8;
    public static int SIZE = 3;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try{
            float MIN_MU = 0.0000001f;
            int MAX_IT = 200;     
            //declaro los jugadores, el jugador1 va a tener coeficientes predefinidos por los resultados del practico 1
            JugadorP1 jugador1 = new JugadorP1(null, Tablero.Marca.X);
            jugador1.coeficientes.w0 = 2.9353569f;
            jugador1.coeficientes.w1 = -0.6056756f;
            jugador1.coeficientes.w2 = 1.9616599f;
            jugador1.coeficientes.w3 = 1.2146791f;
            jugador1.coeficientes.w4 = -1.3071471f;
            jugador1.coeficientes.w5 = -4.862419f;
            jugador1.coeficientes.indep = 0.46656638f;
            JugadorRefuerzo jugador2 = new JugadorRefuerzo(null, Tablero.Marca.O, 0.8);
            //Partidas
            int cantIteraciones = 0;
            int entrenamiento = 0;            
            for(int i=0;i<entrenamiento;i++)
            {
            	List<Tablero> tablerosEnt = generarTableros(jugador1, jugador2);
            	System.out.println("Vop del Jugador antes de entrenar");
                for (Tablero tablero : tablerosEnt) {
    				jugador1.tablero = tablero;
    				jugador2.tablero = tablero;
    	            imprimirVop(jugador1,jugador2);
    			}
                System.out.println("Comienza a entrenar");
                for (Tablero tablero : tablerosEnt) {
    				jugador1.tablero = tablero;
    				jugador2.tablero = tablero;
    	            tablero.imprimir();
    	            imprimirVop(jugador1,jugador2);
    	            jugar(tablero, jugador1, jugador2);
    			}
                System.out.println("Vop del Jugador luego de entrenar");
                List<Tablero> tablerosEnt2 = generarTableros(jugador1, jugador2);
                for (Tablero tablero : tablerosEnt2) {
    				jugador1.tablero = tablero;
    				jugador2.tablero = tablero;
    	            //tablero.imprimir();
    	            imprimirVop(jugador1,jugador2);
    			}
            }
            
            while (mu > MIN_MU && cantIteraciones < MAX_IT) {

                //Inicializo
                Tablero tablero = new Tablero(SIZE);
                jugador1.tablero = tablero;
                jugador2.tablero = tablero;                
                jugar(tablero,jugador1,jugador2); 
                //imprimirVop(jugador1, jugador2);
                cantIteraciones++;

            }
            /*List<Tablero> tablerosEnt2 = generarTableros(jugador1, jugador2);
            for (Tablero tablero : tablerosEnt2) {
				jugador1.tablero = tablero;
				jugador2.tablero = tablero;
	            //tablero.imprimir();
	            imprimirVop(jugador1,jugador2);
			}*/
            System.out.println("########################");
            int cant = countGanoO + countGanoX + countEmpate;
            System.out.println("Cantidad de juegos: " + cant);
            System.out.println("El jugador del practico 1 gano " + countGanoX + " veces.");
            System.out.println("El jugador del practico 4 gano " + countGanoO + " veces.");
            System.out.println("Empataron " + countEmpate + " veces.");
            System.out.println("Coeficientes del jugador del practico 1: ");
            jugador1.coeficientes.imprimir();
            System.out.println("########################");
            //System.out.println(cant+","+ countGanoX +","+ countGanoO +","+ countEmpate);


        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void jugar(Tablero tablero, JugadorP1 jugador1,JugadorRefuerzo jugador2)
    {
    try{
    	EstadoTablero estadoTablero = tablero.getEstadoTablero(Tablero.Marca.X, jugador1.coeficientes);
        EstadoTablero estadoTableroPrueba;
        List<List<Double>> ejemplos = new ArrayList();
        List<Double> ejemplosVop = new ArrayList();

        //Movimientos
        while (!estadoTablero.finalizado) {


            /**
             * JUGADOR PRACTICO 1
             */
            //System.out.println("TURNO DE: " + jugador);


            //Mejor pos.
            double mejorVop = -1;
            int mejori = -1;
            int mejorj = -1;


            //2- Calcular posicion para movida probando.
            for (int i = 0; i < tablero.SIZE; i++) {
                for (int j = 0; j < tablero.SIZE; j++) {
                    try {
                        estadoTableroPrueba = jugador1.setMarca(i, j, Tablero.Marca.X, true, true);
                        if (mejorVop == -1 || estadoTableroPrueba.VOp > mejorVop) {
                            mejorVop = estadoTableroPrueba.VOp;
                            mejori = i;
                            mejorj = j;
                        }
                    } catch (Exception jugadaProhibidaIgnored) {
                    }
                }
            }

            //3- Mover
            try{
                jugador1.setMarca(mejori, mejorj, Tablero.Marca.X, false, false);
                estadoTablero = tablero.getEstadoTablero(Tablero.Marca.X, jugador1.coeficientes);
            } catch(Exception jugadaProhibidaIgnore) {
                jugadaProhibidaIgnore.printStackTrace();
            }

            //imprimir ta-te-ti
            //tablero.imprimir();

            //Guardo un ejemplo para que el jugador con red neuronal entrene al terminar la partida
            List<Double> inputs = new ArrayList();
            inputs.add((double) jugador1.tablero.cantFichasO);
            inputs.add((double) jugador1.tablero.cantFichasX);
            inputs.add((double) jugador1.tablero.cantLineasInutilesParaO);
            inputs.add((double) jugador1.tablero.cantLineasInutilesParaX);
            inputs.add((double) jugador1.tablero.cantMinimaRestanteParaGanarO);
            inputs.add((double) jugador1.tablero.cantMinimaRestanteParaGanarX);
            
            ejemplos.add(inputs);
            ejemplosVop.add(estadoTablero.VOp);


            if (!estadoTablero.finalizado) {

                /**
                 * JUGADOR PRACTICO 4
                 */
                //System.out.println("TURNO DE: " + oponente);

                //2- Calculo la probabilidad ACUMULADA de realizar cada movida probando
                Map<Integer, Double> probabilidades = new HashMap();
                probabilidades.put(0, 0d);
                Map<Integer, Integer> posicion = new HashMap();
                posicion.put(0, 0);
                
                double total = 0;
                int contador = 1;
                for(int i=0; i<tablero.SIZE; i++){
                    for(int j=0; j<tablero.SIZE; j++){
                        if(tablero.grilla[i][j] == Tablero.Marca.N){
                            double aux = jugador2.setMarca(i, j,jugador2.marca, true, true);
                            probabilidades.put(contador, probabilidades.get(contador-1) + Math.exp(aux));
                            total=probabilidades.get(contador);
                            posicion.put(contador, i*10 + j);
                            contador++;
                        }
                    }
                }
                

                //3- Muevo con cierto margen de azar para balancear explotacion y exploracion
                Random r = new Random();
                double p = r.nextDouble();
                boolean bandera = false;
                int contador2 = -1;
                while((!bandera) && (probabilidades.get(contador2+1)!=null)){
                    contador2++;
                    bandera = p<(probabilidades.get(contador2))/total;           
                }
                /*           
                System.out.println("contador2: "+contador2);
                double pos= posicion.get(contador2);
                System.out.println("posicion: "+pos); 
                System.out.println(String.format("%10s", p));
                System.out.println(String.format("%10s", contador2));
                System.out.println(String.format("%10s", posicion.size()));
                System.out.println(String.format("%10s", probabilidades.size()));*/
                double posicionJ2 = posicion.get(contador2)%10d;
                int posicionJ = (int) posicionJ2;
                double posicionI2 = posicion.get(contador2)/10d;
                int posicionI = (int) posicionI2;
                if(contador2 != 0){
                    jugador2.setMarca(posicionI, posicionJ, jugador2.marca, false, false);
                    estadoTablero = tablero.getEstadoTablero(Tablero.Marca.O, jugador1.coeficientes);
                }

                //imprimir ta-te-ti
               // tablero.imprimir();


            }


            if (estadoTablero.finalizado && !estadoTablero.empate) {
                if (estadoTablero.ganador == Tablero.Marca.X) {
                    countGanoX++;
                    //tablero.imprimir();
                } else {
                    countGanoO++;
                    //tablero.imprimir();
                }
                System.out.println("GANO: " + estadoTablero.ganador);
            } else if (estadoTablero.empate) {
                countEmpate++;
                System.out.println("EMPATE!!! ");
                //tablero.imprimir();
            } else {
                //Calculo VEnt desde el punto de vista de X usando el Vop del ultimo turno.
                EstadoTablero trucho = tablero.getEstadoTablero(Tablero.Marca.X, jugador1.coeficientes);
            }                    
            
            //imprimo datos de jugada.
            //coeficientes.imprimir();
            //Actualizar MU
            mu -= STEP_MU;

        }
        //Actualizo los coeficientes de la red neuronal con backpropagation
        jugador2.red.backpropagation(ejemplos, ejemplosVop);
    }catch(Exception e){
        e.printStackTrace();
    }
    }
    
    public static void imprimirVop(JugadorP1 jugador1, JugadorRefuerzo jugador2)
    {        
        double VopJ1 = jugador1.tablero.getEstadoTablero(Tablero.Marca.X, jugador1.coeficientes).VOp;
        //Estimo el valor de Vjugador1
        double VJ2 = jugador2.V().get(0);
        //Determino la recompensa                
        double recompensa = jugador2.recompensa(Tablero.Marca.O);
        double VopJ2 = recompensa+gamma*VJ2;//Math.pow(gamma, jugador2.tablero.cantFichasO+jugador2.tablero.cantFichasX)*VJ2;	         
        System.out.println(VopJ2);
    }   
    
    public static List<Tablero> generarTableros(JugadorP1 jugador1, JugadorRefuerzo jugador2)
    {

        List<Tablero> tablerosEnt= new ArrayList<Tablero>();
    	try{
        //Tablero1
        Tablero tablero1 = new Tablero(SIZE);
        jugador1.tablero = tablero1;
        jugador2.tablero = tablero1;
        jugador2.setMarca(0, 0, Tablero.Marca.O, false, false);
        jugador1.setMarca(1, 0, Tablero.Marca.X, false, false);
        jugador2.setMarca(0, 2, Tablero.Marca.O, false, false);
        jugador1.setMarca(0, 1, Tablero.Marca.X, false, false);
        jugador2.setMarca(1, 1, Tablero.Marca.O, false, false);
        tablerosEnt.add(tablero1);
        
        //Tablero2
        Tablero tablero2 = new Tablero(SIZE);
        jugador1.tablero = tablero2;
        jugador2.tablero = tablero2;
        jugador2.setMarca(0, 0, Tablero.Marca.O, false, false);
        jugador1.setMarca(1, 1, Tablero.Marca.X, false, false);
        jugador2.setMarca(2, 1, Tablero.Marca.O, false, false);
        jugador1.setMarca(0, 2, Tablero.Marca.X, false, false);
        jugador2.setMarca(2, 0, Tablero.Marca.O, false, false);
        tablerosEnt.add(tablero2);
        
        //Tablero3
        Tablero tablero3 = new Tablero(SIZE);
        jugador1.tablero = tablero3;
        jugador2.tablero = tablero3;
        jugador1.setMarca(1, 1, Tablero.Marca.X, false, false);
        jugador2.setMarca(2, 1, Tablero.Marca.O, false, false);
        jugador1.setMarca(0, 1, Tablero.Marca.X, false, false);
        tablerosEnt.add(tablero3);
        
        //Tablero4
        Tablero tablero4 = new Tablero(SIZE);
        jugador1.tablero = tablero4;
        jugador2.tablero = tablero4;
        jugador2.setMarca(0, 0, Tablero.Marca.O, false, false);
        jugador1.setMarca(1, 0, Tablero.Marca.X, false, false);
        jugador2.setMarca(0, 2, Tablero.Marca.O, false, false);
        tablerosEnt.add(tablero4);
        
        //Tablero5
        Tablero tablero5 = new Tablero(SIZE);
        jugador1.tablero = tablero5;
        jugador2.tablero = tablero5;
        jugador1.setMarca(1, 1, Tablero.Marca.X, false, false);
        jugador2.setMarca(2, 1, Tablero.Marca.O, false, false);
        jugador1.setMarca(0, 0, Tablero.Marca.X, false, false);
        jugador2.setMarca(0, 2, Tablero.Marca.O, false, false);
        tablerosEnt.add(tablero5);
        
        //Tablero6
        Tablero tablero6 = new Tablero(SIZE);
        jugador1.tablero = tablero6;
        jugador2.tablero = tablero6;
        jugador1.setMarca(0, 1, Tablero.Marca.X, false, false);
        jugador2.setMarca(1, 2, Tablero.Marca.O, false, false);
        jugador1.setMarca(1, 0, Tablero.Marca.X, false, false);
        jugador2.setMarca(2, 1, Tablero.Marca.O, false, false);
        tablerosEnt.add(tablero6);
        
        //Tablero7
        Tablero tablero7 = new Tablero(SIZE);
        jugador1.tablero = tablero7;
        jugador2.tablero = tablero7;
        jugador1.setMarca(1, 1, Tablero.Marca.X, false, false);
        jugador2.setMarca(2, 1, Tablero.Marca.O, false, false);
        tablerosEnt.add(tablero7);
        
        //Tablero8
        Tablero tablero8 = new Tablero(SIZE);
        jugador1.tablero = tablero8;
        jugador2.tablero = tablero8;
        jugador1.setMarca(0, 1, Tablero.Marca.X, false, false);
        jugador2.setMarca(1, 2, Tablero.Marca.O, false, false);
        tablerosEnt.add(tablero8);
        
        //Tablero9
        Tablero tablero9 = new Tablero(SIZE);
        jugador1.tablero = tablero9;
        jugador2.tablero = tablero9;
        jugador1.setMarca(0, 0, Tablero.Marca.X, false, false);
        jugador2.setMarca(0, 2, Tablero.Marca.O, false, false);
        tablerosEnt.add(tablero9);
        
        //Tablero10
        Tablero tablero10 = new Tablero(SIZE);
        jugador1.tablero = tablero10;
        jugador2.tablero = tablero10;
        jugador1.setMarca(0, 0, Tablero.Marca.X, false, false);
        jugador2.setMarca(1, 0, Tablero.Marca.O, false, false);
        tablerosEnt.add(tablero10);
               
        }catch(Exception e){
            e.printStackTrace();
        }
    	return tablerosEnt; 
    }
       
}
