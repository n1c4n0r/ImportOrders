package es.nmc.espublico.importorders.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
    private static final Logger logger = LoggerFactory.getLogger(Util.class);
    private Util() {
        // Constructor privado para evitar instanciación
    }
    public static Date convertStringDDMMYYYYtoDate(String dateString){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date date = new Date();
        try {
             date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            logger.error("Error al convertir la fecha: {}", e.getMessage());
        }
        return date;
    }

    public static String formatDateToString(Date date) {
        if (date == null) {
            return "N/A"; // O cualquier valor por defecto que desees
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(date);
    }
}
