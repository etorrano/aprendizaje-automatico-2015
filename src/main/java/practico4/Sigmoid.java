package practico4;

import java.util.List;

/**
 * Created by raul on 29/09/15.
 */
public class Sigmoid extends Neurona {

    public Sigmoid(Integer id, Integer cantEntradas, TipoNeurona tipo) {
        super(id, cantEntradas, tipo);
    }

    @Override
    public double getSalida(List<Double> entradas) {
        return 1 / (1 + Math.exp(-getSumaConPesos(entradas)));
    }

    public double getError(Double salidaReal, Double termino) {
        double error = 0d;
        if (tipo == TipoNeurona.HIDDEN)
            error = salidaReal * (1 - salidaReal) * termino;
        else if (tipo == TipoNeurona.OUTPUT)
            error = salidaReal * (1 - salidaReal) * (termino - salidaReal);
        return error;

    }
}
