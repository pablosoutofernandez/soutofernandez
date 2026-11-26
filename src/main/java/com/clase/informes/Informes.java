package com.clase.informes;

import java.io.InputStream;
import java.sql.Connection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.clase.persistencia.ConexionMySQL;

import javafx.scene.control.Alert;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

/**
 * Utilidad para TODOS los informes.
 * Los .jrxml van en src/main/resources/informes/
 */
public class Informes {

    // ---------- 1) Informes con consulta SQL dentro del .jrxml ----------

    // Muestra el informe en una ventana (visor de Jasper con botones de imprimir y guardar)
    public static void mostrar(String jrxml, Map<String, Object> parametros) {
        try {
            JasperPrint print = generar(jrxml, parametros);
            // false = al cerrar el visor NO se cierra la aplicación (con true se cerraría todo)
            JasperViewer.viewReport(print, false);
        } catch (Exception e) {
            e.printStackTrace();
            error(e);
        }
    }

    // Guarda el informe directamente en un PDF
    public static void exportarPdf(String jrxml, Map<String, Object> parametros, String rutaPdf) {
        try {
            JasperPrint print = generar(jrxml, parametros);
            JasperExportManager.exportReportToPdfFile(print, rutaPdf);
        } catch (Exception e) {
            e.printStackTrace();
            error(e);
        }
    }

    // Compila el .jrxml y lo rellena con los datos de la BD
    public static JasperPrint generar(String jrxml, Map<String, Object> parametros) throws Exception {
        JasperReport jasperReport = compilar(jrxml);
        Map<String, Object> params = (parametros == null) ? new HashMap<>() : parametros;
        try (Connection conexion = ConexionMySQL.getConexion()) {
            return JasperFillManager.fillReport(jasperReport, params, conexion);
        }
    }

    // ---------- 2) Informes con una LISTA de objetos (sin SQL en el .jrxml) ----------

    // Ej: Informes.mostrarLista("tabladoctores.jrxml", params, tablaDoctores.getItems());
    public static void mostrarLista(String jrxml, Map<String, Object> parametros, Collection<?> datos) {
        try {
            JasperReport jasperReport = compilar(jrxml);
            Map<String, Object> params = (parametros == null) ? new HashMap<>() : parametros;
            JasperPrint print = JasperFillManager.fillReport(jasperReport, params,
                    new JRBeanCollectionDataSource(datos));
            JasperViewer.viewReport(print, false);
        } catch (Exception e) {
            e.printStackTrace();
            error(e);
        }
    }

    // ---------- Utilidades ----------

    // Compila un .jrxml de resources/informes (sirve también para pasar SUBINFORMES como parámetro)
    public static JasperReport compilar(String jrxml) throws Exception {
        InputStream informe = Informes.class.getResourceAsStream("/informes/" + jrxml);
        if (informe == null) {
            throw new Exception("No se encuentra /informes/" + jrxml + " en resources.");
        }
        return JasperCompileManager.compileReport(informe);
    }

    private static void error(Exception e) {
        try {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error al generar el informe");
            alert.setHeaderText("No se ha podido generar el informe");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        } catch (IllegalStateException fueraDeJavaFX) {
            // si se llama fuera de la aplicación JavaFX (pruebas) solo se imprime el error
            System.err.println(e.getMessage());
        }
    }
}
