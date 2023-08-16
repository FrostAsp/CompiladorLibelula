package libelula;

import java.io.*;
import java.util.LinkedList;
import java.util.StringTokenizer;

public class LIBELULA {

    public static void main(String[] args) throws Exception {

        String archivo;

        if (args.length > 0) {

            archivo = args[0];

            if (archivo != null) {

                if (verificacionFormato(archivo)) {

                    analisisArchivo(archivo);
                }
            }

        } else {

            System.out.println("El arreglo args se encuentra vacio o no existe");
        }
    }

    public static boolean verificacionFormato(String archivo) {

        try {

            if (archivo.matches("(\\s|^)[A-Za-z]{1}[A-Za-z0-9]{0,19}\\.LID")) {

                return true;

            } else {

                System.out.println("El nombre del archivo es invalido");
            }

        } catch (Exception e) {

            System.out.println("Hubo un problema al intentar abrir el archivo :" + e);
        }

        return false;

    }

    public static void analisisArchivo(String archivo) {

        int um = 0, c = 0, d = 0, u = 0, n = 0, contadorBegin = 0, contadorEnd = 0, contadorVar = 0, contadorModule = 0, contadorUntil = 0, contadorIF = 0, contadorElse = 0, contadorComandosDentroDeIF = 0;

        String numeroLineaIF = null;

        boolean begin = false, module = false, end = false, error = false, comandoIf = false, comandoEnd = false, comandoElse = false, var = false, replace = false, variablesUntil = false, comanodModule2 = false, comandoLibelula = false, variableDeclarada = false, errorDefinicionComandos = false;

        try {

            FileReader fr = new FileReader(archivo);
            BufferedReader bf = new BufferedReader(fr);
            String lectura;

            String[] nombreArchivoOriginal = archivo.split("\\.");

            String nuevaExtension = nombreArchivoOriginal[0] + "-errores";
            PrintWriter nuevoArchivo = new PrintWriter(nuevaExtension + ".txt");

            String tokensAnalisar;
            String lineaSinEspacios;

            boolean comentarioApertura = false;
            boolean comentarioCierrre = false;
            boolean lineaComentarioApertura = false;
            boolean lineaComentarioCierre = false;

            int lineasComentario = 0;

            String[] palabrasReservadasModule2 = palabrasReservadasAnexo1();
            String[] palabrasReservadasLibelula = palabrasReservadasAnexo2();

            String nombreProyecto = null;

            LinkedList<String> variablesUsadas = new LinkedList<String>();

            while ((lectura = bf.readLine()) != null) {

                nuevoArchivo.println(um + "" + c + "" + d + "" + u + " " + lectura);

                u++;

                lineaSinEspacios = lectura.replaceAll("\\s", "");

                if (lectura.length() > 100) {

                    nuevoArchivo.print("        ERROR 001: Error de Extension, Maximo 100 Caracteres por Linea" + "\n");
                }

                try {

                    if (comentarioApertura == false && !lineaSinEspacios.contains(":=")) {

                        if ((lectura.trim().contains("(*"))) {

                            if (!(lectura.trim().contains("*)"))) {

                                if (!(lectura.trim().equals("(*"))) {

                                    nuevoArchivo.print("        ERROR 002: No se Pueden Escribir Palabras Antes o Despues del Comentario de Apertura" + "\n");
                                    error = true;

                                } else {

                                    error = false;
                                }

                                if (error == false) {

                                    comentarioApertura = true;
                                    lineaComentarioApertura = true;
                                    lineasComentario = 0;
                                }
                            }
                        }

                        if (comentarioApertura == false && lectura.trim().equals("*)")) {

                            nuevoArchivo.print("        ERROR 300: No fue encontrado el comentario de apertura [ (* ]" + "\n");
                            error = true;
                        }

                    } else if (comentarioApertura && !lineaSinEspacios.contains(":=")) {

                        if (comentarioCierrre == false) {

                            if (lineasComentario == 8) {

                                if (lectura.trim().equals("*)") == false) {

                                    nuevoArchivo.print("          ERROR 003: Se Supero la Extension Maxima de los Comentarios, no fue encontrado comentario de cierre" + "\n");
                                    comentarioApertura = false;
                                    lineaComentarioApertura = false;
                                    lineaComentarioCierre = false;
                                    lineasComentario = 0;
                                }
                            }
                        }

                        if ((lectura.trim().contains("*)"))) {

                            if (!(lectura.trim().contains("(*"))) {

                                if (!(lectura.trim().equals("*)"))) {

                                    nuevoArchivo.print("        ERROR 002: No se Pueden Escribir Palabras antes o Despues del Comentario de Apertura" + "\n");
                                    error = true;
                                    comentarioApertura = false;
                                    lineaComentarioApertura = false;
                                    lineaComentarioCierre = false;
                                }
                                comentarioCierrre = true;
                                lineaComentarioApertura = false;
                                lineaComentarioCierre = false;
                            }

                        }

                        lineasComentario++;
                    }

                } catch (Exception e) {

                    System.out.println("Hubo un problema al realizar la lectura Multilinea: " + e);
                }

                try {
                    int indice = 0;

                    if (lineaSinEspacios.contains(":=")) {

                        indice = lineaSinEspacios.indexOf(":=");
                    }
                    if (!lineaSinEspacios.contains(":=") && lineaSinEspacios.substring(0, indice).length() > 0 && indice != -1) {

                        if (!variablesUsadas.contains("real/" + lineaSinEspacios.substring(0, indice)) && !variablesUsadas.contains("integer/" + lineaSinEspacios.substring(0, indice)) && !variablesUsadas.contains("char/" + lineaSinEspacios.substring(0, indice))) {

                            if (lectura.contains("(") && lectura.contains(")") && !lineaSinEspacios.contains("Read") && !lineaSinEspacios.contains("Write")) {

                                String comentariounilinea = lectura.trim().replaceAll("\\s+", " ");

                                if (!(comentariounilinea.contains("(*")) || !(comentariounilinea.contains("*)"))) {

                                    nuevoArchivo.print("        ERROR 004: Formato Errone en Comentario, espacios entre parentesis y asterico o asterisco no existe" + "\n");
                                    error = true;

                                } else if (!comentariounilinea.endsWith(")")) {

                                    nuevoArchivo.print("           ERROR 005: No se puede Escribir Ningun Comando o Palabra Despues del Comentario de Cierre  " + "\n");
                                    error = true;

                                } else if (comentariounilinea.charAt(0) != '(') {

                                    nuevoArchivo.print("           ERROR 005: No se puede Escribir Ningun Comando o Palabra Antes del Comentario de Apertura  " + "\n");
                                    error = true;
                                }
                            }

                        }
                    }

                } catch (Exception e) {

                    System.out.println("Hubo un problema al realizar la lectura unilineas: " + e);
                }

                String[] cadenas = lineaSinEspacios.split("[,:]");
                String[] definicionTipoVariable = lineaSinEspacios.split("[:;]");
                String[] primerLinea = lectura.trim().split("\\s+");

                if (lineaComentarioApertura == false && lineaComentarioCierre == false) {

                    if (module) {

                        if (begin == false) {

                            try {

                                if (!primerLinea[0].contains("(*") && !primerLinea[0].contains("*)")) {

                                    if (!lectura.contains("INTEGER") && !lectura.contains("CHAR") && !lectura.contains("REAL")) {

                                        int numeroModule = 0;

                                        for (int i = 0; i < palabrasReservadasModule2.length; i++) {

                                            if (primerLinea[0].length() != 0 && primerLinea[0].equals(palabrasReservadasModule2[i])) {

                                                comanodModule2 = true;
                                                error = true;
                                                break;
                                            }
                                        }

                                        if (primerLinea[0].equals("BEGIN")) {

                                            begin = true;
                                        }

//                                        System.out.println(primerLinea[0]);
                                        for (int i = 0; i < palabrasReservadasLibelula.length; i++) {

                                            if (primerLinea[0].length() != 0 && primerLinea[0].equals(palabrasReservadasLibelula[i]) && !primerLinea[0].equals("MODULE")) {

                                                comandoLibelula = true;
                                                error = true;
                                                break;
                                            }
                                        }
                                        if (primerLinea[0].length() != 0 && comandoLibelula == false && comanodModule2 == false && !primerLinea[0].equals("VAR") && !primerLinea[0].equals("BEGIN") && !primerLinea[0].contains("RETURN") && !primerLinea[0].equals("MODULE")) {

                                            nuevoArchivo.print("     Error 550: No es un Comando Valido de LIBELULA ni de MODULE2 " + "[" + primerLinea[0] + "]" + "\n");
                                            error = true;
                                        }

                                        if (primerLinea[0].length() != 0 && comandoLibelula == true && comanodModule2 == false && !primerLinea[0].equals("VAR") && !primerLinea[0].equals("BEGIN")) {

                                            nuevoArchivo.print("     Error 551: Error no se permiten Comandos de LIBELULA " + "[" + primerLinea[0] + "]" + "\n");
                                            error = true;
                                        }
                                        if (primerLinea[0].length() != 0 && comandoLibelula == false && comanodModule2 == true && !primerLinea[0].equals("MODULE")) {

                                            nuevoArchivo.print("     Advertencia Comando no soportado en esta version " + "\n");
                                            error = true;
                                        }

                                        comanodModule2 = false;
                                        comandoLibelula = false;

                                    }

                                }

                            } catch (Exception e) {

                                System.out.println("Hubo un Problema al Realizar la Comprobacion de Palabras Reservadas antes de BEGIN: " + e);

                            }

                            if (var) {

                                try {

                                    if (lineaSinEspacios.contains("REAL") || lineaSinEspacios.contains("CHAR") || lineaSinEspacios.contains("INTEGER")) {

                                        String[] variablesError = lineaSinEspacios.substring(0, lineaSinEspacios.indexOf(";") + 1).split("[,:;]");
                                        String[] lexemas = lectura.trim().split("[,:;]");
                                        String[] comas = lectura.trim().split("[,:;]");

                                        if (lineaSinEspacios.contains(",")) {

                                            for (int i = 0; i < comas.length; i++) {

                                                if (comas[i].length() < 1) {

//                                                    System.out.println(comas[i].trim());
                                                    nuevoArchivo.println("        ERROR 532: Cada variable debe dividirse por una unica coma" + "\n");
                                                    error = true;
                                                    errorDefinicionComandos = true;
                                                    break;

                                                }
                                            }

                                        } else {

                                            for (int i = 0; i < lexemas.length; i++) {

//                                                System.out.println(lexemas[i]);
                                                if (!lexemas[i].trim().equals("INTEGER") && !lexemas[i].trim().equals("CHAR") && !lexemas[i].trim().equals("REAL")) {

                                                    if (!lexemas[i].trim().contains("INTEGER") && !lexemas[i].trim().contains("CHAR") && !lexemas[i].trim().contains("REAL")) {

                                                        if (lexemas[i].trim().contains(" ")) {

                                                            nuevoArchivo.println("        ERROR 451: Cada variable debe dividirse por coma" + "[" + lexemas[i].trim() + "]" + "\n");
                                                            error = true;
                                                            errorDefinicionComandos = true;
                                                            break;
                                                        }

                                                        if (lexemas[i].trim().length() > 20) {

                                                            nuevoArchivo.println("        ERROR 008: La Extension de Cada Variable no Puede Superar los 20 Caracteres" + "[" + lexemas[i].trim() + "]" + "\n");
                                                            error = true;
                                                            errorDefinicionComandos = true;
                                                            break;
                                                        }

                                                        if (Character.isDigit(lexemas[i].charAt(0))) {

                                                            nuevoArchivo.println("        ERROR 009: Las Variables no Puede Comenzar con Numeros" + "[" + lexemas[i].trim() + "]" + "\n");
                                                            error = true;
                                                            errorDefinicionComandos = true;
                                                            break;
                                                        }
                                                    } else {

                                                        nuevoArchivo.println("        ERROR 450: No se reconoce el tipo de Dato en la definicion de Variable, falta [ ; ] o [ : ] " + "[" + lexemas[i].trim() + "]" + "\n");
                                                        error = true;
                                                        errorDefinicionComandos = true;
                                                        break;
                                                    }

                                                }

                                            }
                                            if (!lineaSinEspacios.contains(":REAL") && !lineaSinEspacios.contains(":CHAR") && !lineaSinEspacios.contains(":INTEGER")) {

                                                nuevoArchivo.print("                ERROR 011: Antes del tipo de Variable debe Haber dos Puntos y debe Contener el tipo de dato" + "\n");
                                                error = true;
                                                errorDefinicionComandos = true;

                                            } else if (lineaSinEspacios.endsWith(";")) {

                                                String[] tipoVariable = lectura.trim().split("[:;]");

                                                Boolean tipoComandoValido = false;

                                                if (tipoVariable[1].trim().equals("REAL") || tipoVariable[1].trim().equals("CHAR") || tipoVariable[1].trim().equals("INTEGER")) {

                                                    tipoComandoValido = true;

                                                }

                                                if (tipoComandoValido == false) {

                                                    nuevoArchivo.print("             ERROR 086: NO se Reconoce el Tipo de Dato como un Comando Valido para la Definicion de Variables " + "[" + tipoVariable[1] + "]" + "\n");
                                                    errorDefinicionComandos = true;
                                                    error = true;
                                                }

                                                for (int i = 0; i < variablesError.length; i++) {

                                                    if (!variablesError[i].trim().matches("[A-Za-z0-9]{0,19}") && !variablesError[i].contains(" ")) {

                                                        nuevoArchivo.print("     ERROR 010: Las Variables no Pueden Contener Caracteres Especiales" + "[" + cadenas[i] + "]" + "\n");
                                                        error = true;
                                                        errorDefinicionComandos = true;
                                                        break;

                                                    }
                                                }

                                                for (int i = 0; i < lexemas.length; i++) {

                                                    for (int j = 0; j < palabrasReservadasLibelula.length; j++) {

                                                        if (lexemas[i].trim().equals(palabrasReservadasLibelula[j]) && !lexemas[i].trim().equals("REAL") && !lexemas[i].trim().equals("CHAR") && !lexemas[i].trim().equals("INTEGER")) {

                                                            nuevoArchivo.print("     ERROR 013: No se Pueden Definir Palabras Reservadas de LIBELULA como Nombres de Variables " + "[" + lexemas[i].trim() + "]" + "\n");
                                                            error = true;
                                                            errorDefinicionComandos = true;
                                                            break;
                                                        }
                                                    }

                                                }

                                                for (int i = 0; i < lexemas.length; i++) {

                                                    for (int j = 0; j < palabrasReservadasModule2.length; j++) {

                                                        if (lexemas[i].trim().equals(palabrasReservadasModule2[j]) && !lexemas[i].trim().equals("REAL") && !lexemas[i].trim().equals("CHAR") && !lexemas[i].trim().equals("INTEGER")) {

                                                            nuevoArchivo.print("     ERROR 014: No se Pueden Definir Palabras Reservadas de MODULE2 como Nombres de Variables " + "[" + lexemas[i].trim() + "]" + "\n");
                                                            error = true;
                                                            errorDefinicionComandos = true;
                                                            break;
                                                        }
                                                    }

                                                }

                                            }

                                            int primercoma = lineaSinEspacios.indexOf(",");

                                            if (primercoma == 0) {

                                                nuevoArchivo.print("     ERROR 015: La Definicion de Variables no Puede Comenzar con coma" + "\n");
                                                error = true;
                                            }

                                        }
                                    }

                                } catch (Exception e) {

                                    System.out.println("Hubo un Problema al Realizar la Comprobacion de Variables entre MODULE Y BEGIN: " + e);
                                }

                                try {

                                    if (lineaSinEspacios.contains("REAL") || lineaSinEspacios.contains("CHAR") || lineaSinEspacios.contains("INTEGER")) {

                                        String[] lexemas = lectura.trim().split("[,:;]");
                                        String[] variable = lectura.trim().split("[;,:]");

                                        if (!lineaSinEspacios.endsWith(";")) {

                                            nuevoArchivo.print("     ERROR 764: La Definicion de Variables debe finalizar con punto y coma " + "\n");
                                            error = true;
                                            errorDefinicionComandos = true;
                                        }

                                        if (errorDefinicionComandos == false) {

                                            for (int i = 0; i < variable.length; i++) {

                                                if (!lexemas[i].contains("REAL") && !lexemas[i].contains("CHAR") && !lexemas[i].contains("INTEGER")) {

                                                    if (lineaSinEspacios.contains("REAL")) {

                                                        if (variablesUsadas.contains("real/" + variable[i].trim()) || variablesUsadas.contains("char/" + variable[i].trim()) || variablesUsadas.contains("integer/" + variable[i].trim())) {

                                                            nuevoArchivo.print("     ERROR 200: Ya Existe este Nombre Definido para otra Variable, no Pueden Repetirse Nombres de Variables" + "[" + variable[i].trim() + "]" + "\n");
                                                            error = true;

                                                        } else {

//                                                            System.out.println(variable[i].trim());
                                                            variablesUsadas.add("real/" + variable[i].trim());

                                                        }
                                                    }

                                                    if (lineaSinEspacios.contains("CHAR")) {

                                                        if (variablesUsadas.contains("char/" + variable[i].trim()) || variablesUsadas.contains("real/" + variable[i].trim()) || variablesUsadas.contains("integer/" + variable[i].trim())) {

                                                            nuevoArchivo.print("     ERROR 2010: Ya Existe este Nombre Definido para otra Variable, no Pueden Repetirse Nombres de Variables" + "[" + variable[i].trim() + "]" + "\n");
                                                            error = true;

                                                        } else {

//                                                            System.out.println(variable[i].trim());
                                                            variablesUsadas.add("char/" + variable[i].trim());

                                                        }
                                                    }

                                                    if (lineaSinEspacios.contains("INTEGER")) {

//                                                        System.out.println( lexemas[i]);
                                                        if (variablesUsadas.contains("char/" + variable[i].trim()) || variablesUsadas.contains("real/" + variable[i].trim()) || variablesUsadas.contains("integer/" + variable[i].trim())) {

                                                            nuevoArchivo.print("     ERROR 2011: Ya Existe este Nombre Definido para otra Variable, no Pueden Repetirse Nombres de Variables" + "[" + variable[i].trim() + "]" + "\n");
                                                            error = true;

                                                        } else {

                                                            variablesUsadas.add("integer/" + variable[i].trim());

                                                        }
                                                    }

//                                                    System.out.println(variablesUsadas);
                                                }

                                            }

                                        }

                                        errorDefinicionComandos = false;

                                    }

                                } catch (Exception e) {

                                    System.out.println("Hubo un Problema al Realizar la Comprobacion de las variables definidas despues de VAR" + e);
                                }

                            } else {

                                try {

                                    if (lineaSinEspacios.contains("REAL") || lineaSinEspacios.contains("CHAR") || lineaSinEspacios.contains("INTEGER")) {

                                        if (lineaSinEspacios.contains(":")) {

                                            nuevoArchivo.println("        ERROR 019: Las Definiciones de Variables Requieren de la Definicion del Comando VAR" + " [" + lineaSinEspacios + "] " + "\n");
                                            error = true;
                                        }

                                    }

                                } catch (Exception e) {

                                    System.out.println("Hubo un Problema al Realizar la Comprobacion de las variables definidas sin existir el Comando VAR " + e);
                                }

                            }
                        }

                        if (begin == true && end == false) {

                            try {

                                if (!primerLinea[0].contains("(*") && !primerLinea[0].contains("*)")) {

                                    for (int i = 0; i < palabrasReservadasModule2.length; i++) {

                                        if (primerLinea[0].length() != 0 && primerLinea[0].equals(palabrasReservadasModule2[i])) {

                                            comanodModule2 = true;
                                            error = true;
                                            break;
                                        }
                                    }

                                    if (primerLinea[0].equals("MODULE")) {

                                        module = true;
                                    }

                                    for (int i = 0; i < palabrasReservadasLibelula.length; i++) {

                                        if (primerLinea[0].length() != 0 && !lineaSinEspacios.contains(":=") && primerLinea[0].equals(palabrasReservadasLibelula[i]) && !primerLinea[0].equals("MODULE")) {

                                            comandoLibelula = true;
                                            error = true;
                                            break;
                                        }
                                    }

                                    if (variablesUsadas.contains("char/" + primerLinea[0].trim()) || variablesUsadas.contains("integer/" + primerLinea[0].trim()) || variablesUsadas.contains("real/" + primerLinea[0].trim())) {

                                        variableDeclarada = true;

                                    }

                                    if (primerLinea[0].length() != 0 && variableDeclarada == false && comandoLibelula == false && comanodModule2 == false && !primerLinea[0].equals("MODULE") && !primerLinea[0].contains("RETURN") && !lineaSinEspacios.contains("UNTIL") && !lineaSinEspacios.contains("REPEAT") && !lineaSinEspacios.contains("IF") && !lineaSinEspacios.contains("END;") && !lineaSinEspacios.contains(":=")) {

                                        nuevoArchivo.print("     Error 468: No es un Comando Valido de LIBELULA ni de MODULE2 " + "[" + primerLinea[0] + "]" + "\n");
                                        error = true;
                                    }
                                    if (primerLinea[0].length() != 0 && variableDeclarada == false && comandoLibelula == false && comanodModule2 == true && !primerLinea[0].equals("MODULE") && !primerLinea[0].contains("RETURN") && !lineaSinEspacios.contains("UNTIL") && !lineaSinEspacios.contains("REPEAT") && !lineaSinEspacios.contains("IF") && !lineaSinEspacios.contains("END;") && !lineaSinEspacios.contains(":=")) {

                                        nuevoArchivo.print("     Advertencia comando no soportado en esta version" + "\n");
                                        error = true;
                                    }

                                    comanodModule2 = false;
                                    comandoLibelula = false;
                                    variableDeclarada = false;
                                }

                                if (lineaSinEspacios.contains(":=")) {

                                    String[] primerLexema = lineaSinEspacios.trim().split("[-:(<>=;*+)]");

                                    for (int i = 0; i < primerLexema.length; i++) {

                                        if (!esNumero(primerLexema[i].replaceAll("[^A-Za-z0-9]", "").trim()) && primerLexema[i].replaceAll("[^A-Za-z0-9]", "").trim().length() > 0) {

                                            if (!variablesUsadas.contains("char/" + primerLexema[i].replaceAll("[^A-Za-z0-9]", "").trim()) && !variablesUsadas.contains("integer/" + primerLexema[i].replaceAll("[^A-Za-z0-9]", "").trim()) && !variablesUsadas.contains("real/" + primerLexema[i].replaceAll("[^A-Za-z0-9]", "").trim())) {

                                                for (int j = 0; j < palabrasReservadasModule2.length; j++) {

                                                    if (primerLexema[i].replaceAll("[^A-Za-z0-9]", "").trim().equals(palabrasReservadasModule2[j])) {

                                                        comanodModule2 = true;
                                                        error = true;
                                                        break;
                                                    }
                                                }

                                                for (int s = 0; s < palabrasReservadasLibelula.length; s++) {

                                                    if (primerLexema[i].replaceAll("[^A-Za-z0-9]", "").trim().equals(palabrasReservadasLibelula[s]) && !primerLexema[i].replaceAll("[^A-Za-z0-9]", "").trim().equals("MODULE")) {

                                                        comandoLibelula = true;
                                                        error = true;
                                                        break;
                                                    }
                                                }

                                                if (comanodModule2 == false && comandoLibelula == false && !lineaSinEspacios.contains("'")) {

                                                    nuevoArchivo.print("     Error 433: No se reconoce como una variable declarada, ni un comando de MODULE2 ni LIBELULA " + "[" + primerLexema[i].replaceAll("[^A-Za-z0-9]", "").trim() + "]" + "\n");
                                                    error = true;
                                                }
                                                if (comanodModule2 == true && comandoLibelula == false && !lineaSinEspacios.contains("'")) {

                                                    nuevoArchivo.print("     Advertencia Comando no soportado en esta version " + "\n");
                                                    error = true;
                                                }
                                                if (comanodModule2 == false && comandoLibelula == true && !lineaSinEspacios.contains("'")) {

                                                    nuevoArchivo.print("     Error 434: No se pueden emplear comandos de LIbelula " + "[" + primerLexema[i].replaceAll("[^A-Za-z0-9]", "").trim() + "]" + "\n");
                                                    error = true;
                                                }
                                            }

                                        }
                                    }

                                    comanodModule2 = false;
                                    comandoLibelula = false;

                                }

                                if (lineaSinEspacios.contains("WriteLn")) {

                                    if (!lineaSinEspacios.endsWith(";")) {

                                        nuevoArchivo.print("     Error 100: El Comando WriteLn debe Finalizar con punto y coma " + "\n");
                                    }

                                    if (lineaSinEspacios.substring(0, lineaSinEspacios.length() - 1).length() > 7) {

                                        nuevoArchivo.print("     Error 101: No se pueden escribir mas comandos en la misma linea que WriteLn" + "\n");
                                    }
                                }

                                if (lineaSinEspacios.contains("Read")) {

                                    String[] comandos = lineaSinEspacios.split("[()]");

                                    if (lineaSinEspacios.contains("(") && lineaSinEspacios.contains(")")) {

                                        if (comandos[0].equals("Read")) {

                                            if (!variablesUsadas.contains("char/" + comandos[1]) && comandos[1].length() > 0) {

                                                nuevoArchivo.print("        ERROR 200: Variable no definida o no corresponde al Tipo de dato CHAR" + "[" + comandos[1] + "]" + "\n");
                                                error = true;

                                            } else if (comandos[1].length() == 0) {

                                                nuevoArchivo.print("        ERROR 201: Espacios Vacio, se requiere Variable" + "[" + comandos[1] + "]" + "\n");
                                                error = true;

                                            }

                                        } else if (comandos[0].equals("ReadInt")) {

                                            if (!variablesUsadas.contains("integer/" + comandos[1]) && comandos[1].length() > 0) {

                                                nuevoArchivo.print("        ERROR 202: Variable no definida o no corresponde al Tipo de dato INTEGER" + "[" + comandos[1] + "]" + "\n");
                                                error = true;

                                            } else if (comandos[1].length() == 0) {

                                                nuevoArchivo.print("        ERROR 203: Espacios Vacio, se requiere Variable" + "[" + comandos[1] + "]" + "\n");
                                                error = true;

                                            }

                                        } else if (comandos[0].equals("ReadReal")) {

                                            if (!variablesUsadas.contains("real/" + comandos[1]) && comandos[1].length() > 0) {

                                                nuevoArchivo.print("        ERROR 204: Variable no definida o no corresponde al Tipo de dato REAL" + "[" + comandos[1] + "]" + "\n");
                                                error = true;

                                            } else if (comandos[1].length() == 0) {

                                                nuevoArchivo.print("        ERROR 205: Espacios Vacio, se requiere Variable" + "[" + comandos[1] + "]" + "\n");
                                                error = true;

                                            }
                                        }

                                        if (!lineaSinEspacios.endsWith(";")) {

                                            nuevoArchivo.print("        ERROR 206: Se debe finalizar la Linea con punto y coma " + "\n");
                                            error = true;
                                        }

                                    } else {

                                        nuevoArchivo.print("        ERROR 207: Faltan parentesis " + "\n");
                                        error = true;
                                    }

                                }

                                if (lineaSinEspacios.contains("Write") && !lineaSinEspacios.contains("WriteLn")) {

                                    if (!lineaSinEspacios.contains("(")) {

                                        nuevoArchivo.print("        ERROR 208: Falta parentesis de apertura" + "\n");
                                        error = true;

                                    } else if (!lineaSinEspacios.contains(")")) {

                                        nuevoArchivo.print("        ERROR 209: Falta parentesis de cierre" + "\n");
                                        error = true;
                                    }
                                    if (!lineaSinEspacios.endsWith(";")) {

                                        nuevoArchivo.print("        ERROR 2010: El comando debe finalizar en punto y coma" + "\n");
                                        error = true;
                                    }

                                    if (lineaSinEspacios.contains("(") && lineaSinEspacios.contains(")")) {

                                        String[] comandos = lineaSinEspacios.split("[()]");

                                        if (comandos[0].equals("Write")) {

                                            if (!variablesUsadas.contains("char/" + comandos[1]) && comandos[1].length() > 0) {

                                                nuevoArchivo.print("        ERROR 2011: Variable no definida o no corresponde al Tipo de dato CHAR" + "[" + comandos[1] + "]" + "\n");
                                                error = true;

                                            } else if (comandos[1].length() == 0) {

                                                nuevoArchivo.print("        ERROR 2013: Comando con Variable en Blanco" + "[" + comandos[1] + "]" + "\n");
                                                error = true;

                                            }

                                        } else if (comandos[0].equals("WriteInt")) {

                                            if (comandos[1].length() == 0) {

                                                nuevoArchivo.print("        ERROR 2014: No se cumple con el Formato necesario del Comando" + "[" + comandos[1] + "]" + "\n");
                                                error = true;

                                            } else {

                                                if (!comandos[1].contains(",")) {

                                                    nuevoArchivo.print("        ERROR 2015: La varible y su tamaño deben dividirse por una sola coma" + "[" + comandos[1] + "]" + "\n");
                                                    error = true;

                                                } else if (!variablesUsadas.contains("integer/" + comandos[1].substring(0, comandos[1].indexOf(",")))) {

                                                    nuevoArchivo.print("        ERROR 2016: Variable no definida o no corresponde al Tipo de dato INTEGER" + "[" + comandos[1].substring(0, comandos[1].indexOf(",")) + "]" + "\n");
                                                    error = true;

                                                }

                                                if (!esNumero(comandos[1].substring(comandos[1].indexOf(",") + 1, comandos[1].length()))) {

                                                    nuevoArchivo.print("        ERROR 2017: El tamaño solo puede ser una variable numerica" + "[" + comandos[1].substring(comandos[1].indexOf(",") + 1, comandos[1].length()) + "]" + "\n");
                                                    error = true;

                                                } else {

                                                    int tamaño = Integer.parseInt(comandos[1].substring(comandos[1].indexOf(",") + 1, comandos[1].length()));

                                                    if (tamaño < 0 || tamaño > 20) {

                                                        nuevoArchivo.print("        ERROR 2018: El tamaño de la variable debe deber de 0 a 20" + "[" + comandos[1].substring(comandos[1].indexOf(",") + 1, comandos[1].length()) + "]" + "\n");
                                                        error = true;

                                                    }

                                                }

                                            }

                                        } else if (comandos[0].equals("WriteReal")) {

                                            if (comandos[1].length() == 0) {

                                                nuevoArchivo.print("        ERROR 2019: No se cumple con el Formato necesario del Comando" + "[" + comandos[1] + "]" + "\n");

                                            } else {

                                                if (!comandos[1].contains(",")) {

                                                    nuevoArchivo.print("        ERROR 2020: La varible y su tamaño deben dividirse por una sola coma" + "[" + comandos[1] + "]" + "\n");

                                                } else if (!variablesUsadas.contains("real/" + comandos[1].substring(0, comandos[1].indexOf(",")))) {

                                                    nuevoArchivo.print("        ERROR 2021: Variable no definida o no corresponde al Tipo de dato INTEGER" + "[" + comandos[1].substring(0, comandos[1].indexOf(",")) + "]" + "\n");

                                                }
                                                if (!esNumero(comandos[1].substring(comandos[1].indexOf(",") + 1, comandos[1].length()))) {

                                                    nuevoArchivo.print("        ERROR 2023: El tamaño solo puede ser una variable numerica" + "[" + comandos[1].substring(comandos[1].indexOf(",") + 1, comandos[1].length()) + "]" + "\n");

                                                } else {

                                                    int tamaño = Integer.parseInt(comandos[1].substring(comandos[1].indexOf(",") + 1, comandos[1].length()));

                                                    if (tamaño < 0 || tamaño > 20) {

                                                        nuevoArchivo.print("        ERROR 2024: El tamaño de la variable debe deber de 0 a 20" + "[" + comandos[1].substring(comandos[1].indexOf(",") + 1, comandos[1].length()) + "]" + "\n");

                                                    }

                                                }

                                            }

                                        } else if (comandos[0].equals("WriteString")) {

                                            if (comandos[1].length() == 0) {

                                                nuevoArchivo.print("        ERROR 2025: No se pueden imprimir espacios en blanco sin comillas simples" + "\n");

                                            } else {

                                                int posicion, contador = 0;

                                                posicion = comandos[1].indexOf("'");

                                                while (posicion != -1) {

                                                    contador++;

                                                    posicion = comandos[1].indexOf("'", posicion + 1);

                                                }

                                                if (contador == 1) {

                                                    nuevoArchivo.print("        ERROR 2030: Falta comilla simple de cierre" + "[" + comandos[1] + "]" + "\n");

                                                } else if (posicion == -1 && contador == 0) {

                                                    nuevoArchivo.print("        ERROR 2031: No se cumple con el Formato necesario del Comando [' ']" + "[" + comandos[1] + "]" + "\n");

                                                } else {

                                                    String[] tamañoWriteStirng = lectura.split("[']");

                                                    if (tamañoWriteStirng[1].length() > 60) {

                                                        nuevoArchivo.print("        ERROR 2032: Tamaño exedido para una impresion de cadena, 60 caracteres maximo  " + "[" + comandos[1] + "]" + "\n");

                                                    }
                                                }

                                            }

                                        }

                                    }

                                }

                            } catch (Exception e) {

                                System.out.println("Hubo un Problema al Realizar la Comprobacion de las variables despues de BEGIN " + e);
                            }

                            try {

                                if (lineaSinEspacios.contains("IF")) {

                                    boolean existeThen = false;

                                    String[] comandosIF = lectura.split("[(=><);]");

                                    if (!comandosIF[0].trim().equals("IF")) {

                                        nuevoArchivo.print("        ERROR 385: comando IF, no es correcto" + "[" + comandosIF[0].trim() + "]" + "\n");
                                        error = true;
                                        comandoIf = false;

                                    } else if (comandosIF[0].trim().equals("IF")) {

                                        comandoIf = true;
                                    }

                                    for (int i = 0; i < comandosIF.length; i++) {

                                        if (comandosIF[i].trim().contains("THEN")) {

                                            if (!comandosIF[i].trim().equals("THEN")) {

                                                nuevoArchivo.print("        ERROR 756: El Comando THEN contiene errores " + "[" + comandosIF[i].trim() + "]" + "\n");
                                                error = true;
                                                comandoIf = false;
                                            }
                                        }
                                    }

                                    if (comandoIf && !lineaSinEspacios.trim().contains("(") || !lineaSinEspacios.trim().contains(")")) {

                                        nuevoArchivo.print("        ERROR 379: falta parentesis de apertura o de cierre" + "\n");
                                        error = true;
                                        comandoIf = false;
                                    }

                                    if (comandoIf && lineaSinEspacios.trim().contains("(") && lineaSinEspacios.trim().contains(")")) {

                                        boolean comandoModule2 = false, variableNoDefinida = false;

                                        lectura = lectura.replaceAll("\\s+", " ");

                                        String[] condicionIf = lectura.substring(lectura.indexOf("("), lectura.indexOf(")") + 1).split("[(=><);]");

                                        if (lectura.contains("< =")) {

                                            nuevoArchivo.print("        ERROR 240: operadores comparativos con espacios [< =]" + "\n");
                                            error = true;
                                            comandoIf = false;

                                        }
                                        if (lectura.contains("> =")) {

                                            nuevoArchivo.print("        ERROR 241: operadores comparativos con espacios [> =]" + "\n");
                                            error = true;
                                            comandoIf = false;

                                        }

                                        for (int i = 0; i < condicionIf.length; i++) {

                                            if (!esNumero(condicionIf[i].replaceAll("[^A-Za-z0-9]", ""))) {

                                                for (int j = 0; j < palabrasReservadasModule2.length; j++) {

                                                    if (palabrasReservadasModule2[j].equals(condicionIf[i].trim().replaceAll("[^A-Za-z0-9]", ""))) {

                                                        comandoModule2 = true;
                                                        comandoIf = true;
                                                        break;
                                                    }
                                                }

                                                if (!variablesUsadas.contains("real/" + condicionIf[i].replaceAll("[^A-Za-z0-9]", ""))) {

                                                    if (!variablesUsadas.contains("integer/" + condicionIf[i].replaceAll("[^A-Za-z0-9]", ""))) {

                                                        if (!variablesUsadas.contains("char/" + condicionIf[i].replaceAll("[^A-Za-z0-9]", ""))) {

                                                            if (!condicionIf[i].trim().equals("IF") && !condicionIf[i].trim().equals("THEN") && !condicionIf[i].trim().equals("RETURN;") && condicionIf[i].trim().length() > 0) {

                                                                if (!comandoModule2) {

                                                                    nuevoArchivo.print("        ERROR 381: No se reconoce como una variable declarada, ni un comando de MODULE2 " + "[" + condicionIf[i].trim() + "]" + "\n");
                                                                    comandoIf = false;
                                                                    error = true;
                                                                }
                                                            }

                                                        }
                                                    }
                                                }

                                            }

                                        }

                                    }

                                    if (lectura.endsWith(";")) {

                                        nuevoArchivo.print("        ERROR 242: El Comando IF no requiere de punto y coma" + "[" + lectura + "]" + "\n");
                                        error = true;
                                        comandoIf = false;
                                    }

                                }
                                if (comandoIf && lineaSinEspacios.contains("IF")) {

                                    numeroLineaIF = um + "" + c + "" + d + "" + u;

                                }

                                if (comandoIf && comandoEnd == false) {

                                    if (lineaSinEspacios.equals("ELSE")) {

                                        contadorElse++;

                                        if (contadorElse > 1) {

                                            nuevoArchivo.print("        ERROR 751: Existe mas de una definicion de ELSE" + "\n");
                                            error = true;

                                        } else {

                                            comandoElse = true;
                                        }

                                    }
                                    if (lineaSinEspacios.equals("END;")) {

                                        comandoEnd = true;
                                        contadorEnd++;

                                    }

                                }

                                if (comandoIf && comandoElse == false && !lineaSinEspacios.contains("IF")) {

                                    String[] comandosDentroIF = lineaSinEspacios.split("[(:=>+-<)+;]");

                                    for (int i = 0; i < comandosDentroIF.length; i++) {

                                        if (comandosDentroIF[i].length() > 0) {

                                            for (int j = 0; j < palabrasReservadasModule2.length; j++) {

                                                if (palabrasReservadasModule2[j].equals(comandosDentroIF[i])) {

                                                    contadorComandosDentroDeIF++;

                                                }
                                            }
                                            for (int x = 0; x < palabrasReservadasLibelula.length; x++) {

                                                if (palabrasReservadasLibelula[x].equals(comandosDentroIF[i])) {

                                                    contadorComandosDentroDeIF++;

                                                }
                                            }

                                        }
                                    }

                                    if (contadorComandosDentroDeIF == 0) {

                                        nuevoArchivo.print("        ERROR 452: Dentro del comando IF, se requiere de al menos un definicion valida de un comando de MODULE2 o LIBELULA" + "\n");
                                        error = true;
                                    }

                                } else {

                                    if (comandoIf && comandoElse == false && lineaSinEspacios.length() > 0) {

                                        nuevoArchivo.print("        ERROR 452: Dentro del comando IF, se requiere de al menos un definicion valida de un comando de MODULE2 o LIBELULA" + "\n");
                                        error = true;
                                    }
                                }

                                if (comandoIf && comandoElse && comandoEnd == false && !lineaSinEspacios.contains("ELSE")) {

                                    String[] comandosDentroIF = lineaSinEspacios.split("[(:=>+-<)+;]");

                                    for (int i = 0; i < comandosDentroIF.length; i++) {

                                        if (comandosDentroIF[i].length() > 0) {

                                            for (int j = 0; j < palabrasReservadasModule2.length; j++) {

                                                if (palabrasReservadasModule2[j].equals(comandosDentroIF[i])) {

                                                    contadorComandosDentroDeIF++;
                                                }
                                            }
                                            for (int x = 0; x < palabrasReservadasLibelula.length; x++) {

                                                if (palabrasReservadasLibelula[x].equals(comandosDentroIF[i])) {

                                                    contadorComandosDentroDeIF++;

                                                }
                                            }
                                        }
                                    }

                                    if (contadorComandosDentroDeIF == 0) {

                                        nuevoArchivo.print("        ERROR 453: Dentro del comando ELSE, se requiere de al menos un definicion valida de un comando de MODULE2 o variable definida" + "\n");
                                        error = true;
                                    }

                                } else {

                                    if (comandoIf && comandoElse && comandoEnd == false && lineaSinEspacios.length() > 0) {

                                        nuevoArchivo.print("        ERROR 453: Dentro del comando ELSE, se requiere de al menos un definicion valida de un comando de MODULE2 o variable definida" + "\n");
                                        error = true;
                                    }
                                }

                            } catch (Exception e) {

                                System.out.println("Hubo un Problema al Realizar la Comprobacion del comando IF" + e);
                            }

                            try {

                                if (lectura.contains(": =")) {

                                    nuevoArchivo.print("     ERROR 2012: espacios en [: =]" + "\n");
                                    error = true;
                                    break;
                                }

                                String[] lexemasVariables = lineaSinEspacios.split(":=");

                                if (variablesUsadas.contains("real/" + lexemasVariables[0]) || variablesUsadas.contains("integer/" + lexemasVariables[0]) || variablesUsadas.contains("char/" + lexemasVariables[0])) {

                                    boolean variableChar = false, variableInteger = false, variableReal = false, module2Comando = false;

                                    if (variablesUsadas.contains("real/" + lexemasVariables[0])) {

                                        String[] variables = lineaSinEspacios.split("[:=;)(*+-]");

                                        for (int i = 0; i < variables.length; i++) {

                                            if (variables[i].length() > 0 && !esNumero(variables[i].replaceAll("[^A-Za-z0-9]", ""))) {

                                                if (variablesUsadas.contains("char/" + variables[i].trim())) {

                                                    nuevoArchivo.print("     ERROR 234: No se pueden emplear Variables de tipo CHAR, en asignaciones de tipo REAL (todas las definiciones deben sen ser mismo tipo)" + "[" + variables[i].trim() + "]" + "\n");
                                                    variableChar = true;
                                                }

                                            }

                                            if (variables[i].length() > 0 && variables[i].contains(".")) {

                                                double valorVariable = Double.parseDouble(variables[i]);

                                                if (valorVariable < -32768.00 || valorVariable > 32767.99) {

                                                    nuevoArchivo.print("        ERROR 238: El valor asignado fuera del rango soportado para datos de tipo REAL " + "[" + valorVariable + "]" + "\n");
                                                }

                                            } else if (variables[i].length() > 0 && !variables[i].contains(".") && esNumero(variables[i])) {

                                                int valorVariable = Integer.parseInt(variables[i]);

                                                if (valorVariable < -32768 || valorVariable > 32767) {

                                                    nuevoArchivo.print("         ERROR 239: El valor asignado fuera del rango soportado para datos de tipo INTEGER " + "[" + valorVariable + "]" + "\n");

                                                }

                                            }

                                        }

                                        variableChar = false;

                                    }

                                    if (variablesUsadas.contains("integer/" + lexemasVariables[0])) {

                                        String[] variables = lineaSinEspacios.split("[:=;)(*+-]");

                                        for (int i = 0; i < variables.length; i++) {

                                            if (variables[i].length() > 0 && !esNumero(variables[i].replaceAll("[^A-Za-z0-9]", ""))) {

                                                if (variablesUsadas.contains("char/" + variables[i].trim())) {

                                                    nuevoArchivo.print("     ERROR 234: No se pueden emplear Variables de tipo CHAR, en asignaciones de tipo REAL (todas las definiciones deben sen ser mismo tipo)" + "[" + variables[i].trim() + "]" + "\n");
                                                    variableChar = true;
                                                }
                                            }

                                            if (variables[i].length() > 0 && variables[i].contains(".")) {

                                                double valorVariable = Double.parseDouble(variables[i]);

                                                if (valorVariable < -32768.00 || valorVariable > 32767.99) {

                                                    nuevoArchivo.print("        ERROR 238: El valor asignado fuera del rango soportado para datos de tipo REAL " + "[" + valorVariable + "]" + "\n");

                                                }

                                            } else if (variables[i].length() > 0 && !variables[i].contains(".") && esNumero(variables[i])) {

                                                int valorVariable = Integer.parseInt(variables[i]);

                                                if (valorVariable < -32768 || valorVariable > 32767) {

                                                    nuevoArchivo.print("         ERROR 239: El valor asignado fuera del rango soportado para datos de tipo INTEGER " + "[" + valorVariable + "]" + "\n");

                                                }

                                            }

                                        }

                                        variableChar = false;

                                    }

                                    if (variablesUsadas.contains("char/" + lexemasVariables[0])) {

                                        String[] variables = lineaSinEspacios.split("[:=;)(*+-]");

                                        boolean numero = false;

                                        for (int i = 0; i < variables.length; i++) {

                                            if (esNumero(variables[i]) && variables[i].length() > 0) {

                                                numero = true;
                                            }

                                        }

                                        if (numero) {

                                            nuevoArchivo.print("         ERROR 218: No se pueden utilizar expresiones numericas REAL o INTEGER en ecuaciones de tipo CHAR" + "\n");
                                            break;
                                        }

                                        for (int i = 0; i < variables.length; i++) {

                                            if (variables[i].length() > 0 && !esNumero(variables[i].replaceAll("[^A-Za-z0-9]", ""))) {

                                                if (variablesUsadas.contains("integer/" + variables[i].trim())) {

                                                    nuevoArchivo.print("     ERROR 237: No se pueden emplear Variables de tipo Integer en definiciones de tipo Real (todas las definiciones deben sen ser mismo tipo)" + "[" + variables[i].trim() + "]" + "\n");
                                                }
                                                if (variablesUsadas.contains("real/" + variables[i].trim())) {

                                                    nuevoArchivo.print("     ERROR 237: No se pueden emplear Variables de tipo Integer en definiciones de tipo Real (todas las definiciones deben sen ser mismo tipo)" + "[" + variables[i].trim() + "]" + "\n");
                                                }

                                            }

                                            if (variables[i].contains("'")) {

                                                int posicion, contador = 0;

                                                posicion = variables[i].indexOf("'");

                                                while (posicion != -1) {

                                                    contador++;

                                                    posicion = variables[i].indexOf("'", posicion + 1);

                                                }

                                                if (contador == 1) {

                                                    nuevoArchivo.print("        ERROR 231: Falta comilla simple de cierre" + "[" + variables[i] + "]" + "\n");

                                                } else if (posicion == -1 && contador == 0) {

                                                    nuevoArchivo.print("        ERROR 232: No se cumple con el Formato necesario del Comando [' ']" + "[" + variables[i] + "]" + "\n");

                                                } else {

                                                    String[] tamaño = lineaSinEspacios.split("[']");

                                                    if (tamaño[1].length() > 60) {

                                                        nuevoArchivo.print("        ERROR 223: Tamaño exedido para una impresion de cadena, 60 caracteres maximo  " + "[" + variables[i] + "]" + "\n");

                                                    }
                                                }
                                            }

                                        }

                                        variableChar = false;
                                    }

                                }

                            } catch (Exception e) {

                                System.out.println("Hubo un Problema al Realizar la comprobacion de ecuaciones " + e);
                            }

                        }

                    } else {

                        try {

                            if (!primerLinea[0].contains("(*") && !primerLinea[0].contains("*)")) {

                                for (int i = 0; i < palabrasReservadasModule2.length; i++) {

                                    if (primerLinea[0].length() != 0 && primerLinea[0].equals(palabrasReservadasModule2[i])) {

                                        comanodModule2 = true;
                                        error = true;
                                        break;
                                    }
                                }

                                if (primerLinea[0].equals("MODULE")) {

                                    module = true;
                                }

                                for (int i = 0; i < palabrasReservadasLibelula.length; i++) {

                                    if (primerLinea[0].length() != 0 && primerLinea[0].equals(palabrasReservadasLibelula[i]) && !primerLinea[0].equals("MODULE")) {

                                        comandoLibelula = true;
                                        error = true;
                                        break;
                                    }
                                }
                                if (primerLinea[0].length() != 0 && comandoLibelula == false && comanodModule2 == false && !primerLinea[0].equals("MODULE") && !primerLinea[0].contains("RETURN")) {

                                    nuevoArchivo.print("     Error 2036: No es un Comando Valido de LIBELULA ni de MODULE2 " + "[" + primerLinea[0] + "]" + "\n");
                                    error = true;
                                }

                                if (primerLinea[0].length() != 0 && comandoLibelula == true && comanodModule2 == false && !primerLinea[0].equals("MODULE")) {

                                    nuevoArchivo.print("     Error 2037: No se pueden definir comandos de LIBELULA antes de MODULE " + "[" + primerLinea[0] + "]" + "\n");
                                    error = true;
                                }

                                comanodModule2 = false;
                                comandoLibelula = false;
                            }

                        } catch (Exception e) {

                            System.out.println("Hubo un Problema al Realizar la Comprobacion de los comandos Antes de MODULE" + e);
                        }

                    }

                }

                StringTokenizer tokens = new StringTokenizer(lectura);

                while (tokens.hasMoreTokens()) {

                    tokensAnalisar = tokens.nextToken();

                    ///////// REPARAR
//                    if (replace && until == false) {
                    if (begin && end == false) {

                        boolean comandoModule2 = false;

                        lectura = lectura.replaceAll("\\s+", " ");

                        String[] lexemas = lectura.trim().split(" ");

                        if (lectura.contains(": =")) {

                            nuevoArchivo.print("     ERROR 2012: espacios en [: =]" + "\n");
                            error = true;
                            break;
                        }

                        String[] lexemasVariables = lineaSinEspacios.split(":=");

                        if (variablesUsadas.contains("real/" + lexemasVariables[0]) || variablesUsadas.contains("integer/" + lexemasVariables[0]) || variablesUsadas.contains("char/" + lexemasVariables[0])) {

//                            System.out.println(lexemasVariables[0]);
                            for (int i = 0; i < palabrasReservadasModule2.length; i++) {

                                if (lexemasVariables[0].length() != 0 && lexemasVariables[0].equals(palabrasReservadasModule2[i])) {

                                    comandoModule2 = true;
                                    error = true;
                                    break;
                                }
                            }

                            if (comandoModule2 == true) {

                                nuevoArchivo.print("        Advertencia Comando no soportado en esta version " + "[" + tokensAnalisar.replaceAll("[^A-Za-z0-9]", "") + "]" + "\n");
                                error = true;
                            }

                            if (!lineaSinEspacios.endsWith(";")) {

                                nuevoArchivo.print("        ERROR 026: Se debe finalizar con punto y coma" + "\n");
                                error = true;
                            }

                        }

                    }

                    try {

                        if (tokensAnalisar.equals("MODULE")) {

                            boolean errorModule = false;
                            String[] numeroModule = lectura.trim().split("\\s+");

                            if (lineaSinEspacios.length() > 6) {

                                if (!lineaSinEspacios.substring(6, lineaSinEspacios.length() - 1).matches("[A-Za-z0-9]{0,19}")) {

                                    nuevoArchivo.print("        ERROR 027: El Nombre del Proyecto no puede Contener Caracteres Especiales " + "\n");
                                    error = true;
                                    errorModule = true;
                                }

                                if (lineaSinEspacios.substring(6, lineaSinEspacios.length() - 1).trim().length() > 20) {

                                    nuevoArchivo.print("        ERROR 028: El Nombre del Proyecto solo Puede Contener 20 Caracteres " + "\n");
                                    error = true;
                                    errorModule = true;
                                }
                                if (!lineaSinEspacios.endsWith(";")) {

                                    nuevoArchivo.print("        ERROR 029: El Nombre del Proyecto debe Finalizar en Punto y Coma " + "\n");
                                    error = true;
                                    errorModule = true;
                                }

                                if (Character.isDigit(lineaSinEspacios.substring(6).charAt(0))) {

                                    nuevoArchivo.print("         ERROR 030: El Nombre del Proyecto no Puede Comenzar con Numeros " + "\n");
                                    error = true;
                                    errorModule = true;
                                }

                                for (int i = 0; i < palabrasReservadasLibelula.length; i++) {

                                    if (palabrasReservadasLibelula[i].equals(lineaSinEspacios.trim().substring(6, lineaSinEspacios.length() - 1))) {

                                        nuevoArchivo.print("     ERROR 031: El Nombre del Proyecto no Puede ser una Palabra Reservada de Libelula " + "[" + palabrasReservadasLibelula[i] + "]" + "\n");
                                        error = true;
                                        errorModule = true;
                                    }

                                }

                                for (int i = 0; i < palabrasReservadasModule2.length; i++) {

                                    if (palabrasReservadasModule2[i].equals(lineaSinEspacios.trim().substring(6, lineaSinEspacios.length() - 1))) {

                                        nuevoArchivo.print("     ERROR 032: El Nombre del Proyecto no Puede ser una Palabra Reservada de Module2 " + "[" + palabrasReservadasModule2[i] + "]" + "\n");
                                        error = true;
                                        errorModule = true;
                                    }

                                }

                                if (errorModule == false) {

                                    nombreProyecto = lineaSinEspacios.trim().substring(6, lineaSinEspacios.length() - 1);
                                    contadorModule++;
                                    module = true;
                                }

                                if (contadorModule > 1) {

                                    nuevoArchivo.print("         ERROR 260: Existe mas de una definicion de MODULE" + "\n");
                                    error = true;
                                    errorModule = true;
                                }

                            }
                            if (lineaSinEspacios.equals("MODULE")) {

                                nuevoArchivo.print("         ERROR 267: Falta Nombre del Proyecto" + "\n");
                                error = true;
                            }

                        }

                    } catch (Exception e) {

                        System.out.println("Hubo un Problema al Realizar la Comprobacion del nombre del proyecto junto a MODULE" + e);
                    }

                    try {

                        if (tokensAnalisar.contains("VAR")) {

                            if (!(lineaSinEspacios.length() == 3)) {

                                nuevoArchivo.print("         ERROR 034: No Pueden Escribirse Comandos antes ni Despues de VAR, en su misma linea" + "[" + lineaSinEspacios + "]" + "\n");
                                error = true;

                            }

                            if (lineaSinEspacios.endsWith(";")) {

                                nuevoArchivo.print("        ERROR 035: El Comando VAR no Requiere de punto y coma" + "[" + lineaSinEspacios + "]" + "\n");
                                error = true;
                            }

                            if (lineaSinEspacios.equals("VAR")) {

                                contadorVar++;
                                var = true;

                            }

                            if (contadorVar > 1) {

                                nuevoArchivo.print("        ERROR 036: VAR no Puede Declararse mas de una vez" + "\n");
                                error = true;
                            }

                        }

                    } catch (Exception e) {

                        System.out.println("Hubo un Problema al Realizar la Comprobacion del comando VAR" + e);
                    }

                    try {

                        if (tokensAnalisar.contains("BEGIN")) {

                            if (!(lineaSinEspacios.length() == 5)) {

                                nuevoArchivo.print("         ERROR 037: No Pueden Escribirse Comandos antes ni Despues de BEGIN, en su misma Linea" + "[" + lineaSinEspacios + "]" + "\n");
                                error = true;

                            }

                            if (lineaSinEspacios.endsWith(";")) {

                                nuevoArchivo.print("         ERROR 038: El Comando BEGIN no Requiere de punto y coma" + "[" + lineaSinEspacios + "]" + "\n");
                                error = true;
                            }

                            if (lineaSinEspacios.equals("BEGIN")) {

                                contadorBegin++;

                                if (contadorBegin > 1) {

                                    begin = true;
                                    nuevoArchivo.print("     ERROR 039: BEGIN Fue Declarado mas de una vez" + "\n");
                                    error = true;
                                }
                            }

                        }

                    } catch (Exception e) {

                        System.out.println("Hubo un Problema al Realizar la Comprobacion del comando BEGIN" + e);
                    }

                    try {

                        if (tokensAnalisar.contains("REPEAT")) {

//                            System.out.println(variablesUsadas);
                            if (!(lineaSinEspacios.length() == 6)) {

                                nuevoArchivo.print("        ERROR 040: No pueden escribirse comandos antes ni despues de REPEAT, en su misma Linea" + "[" + lineaSinEspacios + "]" + "\n");
                                error = true;

                            }

                            if (lineaSinEspacios.endsWith(";")) {

                                nuevoArchivo.print("        ERROR 041: El comando REPEAT no Requiere de punto y coma" + "[" + lineaSinEspacios + "]" + "\n");
                                error = true;
                            }
                            if (lineaSinEspacios.equals("REPEAT")) {

                                replace = true;

                            }

                        }

                        if (tokensAnalisar.contains("UNTIL")) {

                            contadorUntil++;

                            if (contadorUntil > 1) {

                                nuevoArchivo.print("        ERROR 551: Existe mas de una declaracion de Until" + "[" + lineaSinEspacios + "]" + "\n");
                                error = true;

                            } else {

                                lectura = lectura.replaceAll("\\s+", " ");

                                String[] comandosUntil = lectura.trim().split(" ");

                                for (int i = 0; i < comandosUntil.length; i++) {

                                    if (comandosUntil[i].replaceAll("[^A-Za-z0-9]", "").length() > 0 && !comandosUntil[i].equals("UNTIL")) {

                                        if (!variablesUsadas.contains("real/" + comandosUntil[i].replaceAll("[^A-Za-z0-9]", ""))) {

                                            if (!variablesUsadas.contains("char/" + comandosUntil[i].replaceAll("[^A-Za-z0-9]", ""))) {

                                                if (!variablesUsadas.contains("integer/" + comandosUntil[i].replaceAll("[^A-Za-z0-9]", ""))) {

                                                    variablesUntil = true;
                                                    nuevoArchivo.print("        ERROR 553: Una de las variables no se reconoce como una variable declarada" + "[" + lineaSinEspacios + "]" + "\n");
                                                    error = true;

                                                }

                                            }

                                        }

                                    }

                                }
                                lectura = lectura.replaceAll("\\s+", " ");

                                if (lectura.contains("< =")) {

                                    nuevoArchivo.print("        ERROR 240: operadores comparativos con espacios [< =]" + "\n");
                                    error = true;

                                } else if (lectura.contains("> =")) {

                                    nuevoArchivo.print("        ERROR 241: operadores comparativos con espacios [> =]" + "\n");
                                    error = true;

                                }

                                if (!lineaSinEspacios.endsWith(";")) {

                                    nuevoArchivo.print("        ERROR 552: El comando Until debe finalizar en punto y coma" + "[" + lineaSinEspacios + "]" + "\n");
                                    error = true;
                                }
                                variablesUntil = false;
                            }
                        }

                    } catch (Exception e) {

                        System.out.println("Hubo un Problema al Realizar la Comprobacion del comando REPEAT" + e);
                    }

                    try {

                        if (tokensAnalisar.contains("ELSE")) {

                            if (!(lineaSinEspacios.length() == 4)) {

                                nuevoArchivo.print("        ERROR 244: No Pueden Escribirse Comandos antes ni Despues de ELSE, en su misma Linea" + "\n");
                                error = true;

                            }

                            if (lineaSinEspacios.endsWith(";")) {

                                nuevoArchivo.print("        ERROR 245: El Comando ELSE no requiere de punto y coma" + "[" + lineaSinEspacios + "]" + "\n");
                                error = true;
                            }

                        }

                    } catch (Exception e) {

                        System.out.println("Hubo un Problema al Realizar la Comprobacion del comando ELSE" + e);
                    }

                    try {

                        boolean errorEND = false;
//
                        if (tokensAnalisar.contains("END")) {

                            String[] lineaEnd = lectura.trim().split("\\s");

                            if (lineaSinEspacios.contains("END;")) {

                                if (!lineaSinEspacios.endsWith(";")) {

                                    nuevoArchivo.println("        ERROR 246: El comando debe finalizar con punto y coma" + "\n");
                                    errorEND = true;
                                    error = true;
                                }
                                if (lineaSinEspacios.length() > 4) {
                                    nuevoArchivo.println("        ERROR 247: No se pueden escribir mas comandos en la misma linea logica que END;" + "\n");
                                    errorEND = true;
                                    error = true;
                                }

                            } else {

                                try {

                                    String nombreEnd = lineaEnd[1].substring(0, lineaEnd[1].length() - 1);

                                } catch (Exception e) {

                                    nuevoArchivo.println("        ERROR 046: Falta nombre del proyecto" + "\n");
                                    errorEND = true;
                                    error = true;
                                }

//                                System.out.println(lineaSinEspacios);
                                if (errorEND == false) {

                                    if (!lineaEnd[1].substring(0, lineaEnd[1].length() - 1).equals(nombreProyecto)) {

                                        nuevoArchivo.println("        ERROR 046: El Nombre Asignado en END no Corresponde al Mismo Nombre del Proyecto Asignado en MODULE" + " [" + lineaEnd[1].substring(0, lineaEnd[1].length() - 1) + "] " + "\n");
                                        error = true;
                                        errorEND = true;
                                    }
                                    if (lineaSinEspacios.endsWith(";")) {

                                        nuevoArchivo.print("        ERROR 048: El comando END no requiere de punto y coma" + "[" + lineaSinEspacios + "]" + "\n");
                                        error = true;
                                        errorEND = true;
                                    }
                                    if (!lineaSinEspacios.endsWith(".")) {

                                        nuevoArchivo.print("        ERROR 049: El comando END debe finalizar con ." + "[" + lineaSinEspacios + "]" + "\n");
                                        error = true;
                                        errorEND = true;
                                    }

                                    if (errorEND == false && tokensAnalisar.equals("END") && lineaEnd[1].substring(0, lineaEnd[1].length() - 1).equals(nombreProyecto)) {

                                        contadorEnd++;
                                        end = true;

                                        if (contadorEnd > 1) {

                                            nuevoArchivo.print("     ERROR 050: END fue Declarado mas de una vez" + "\n");
                                            error = true;
                                        }
                                    }

                                }
                            }

                        }
                        if (end && contadorEnd > 1) {

                            if (!primerLinea[0].contains("(*") && !primerLinea[0].contains("*)") && primerLinea[0].length() > 0) {

                                nuevoArchivo.print("        ERROR 075: Despues del Comando END solo pueden Agregarse Comentarios y Espacios en Blanco" + "\n");
                                error = true;
                            }

                        }

                    } catch (Exception e) {

                        System.out.println("Hubo un Problema al Realizar la Comprobacion del comando END" + e);
                    }

                    try {

                        if (end == false) {

                            if (!lineaSinEspacios.contains("REAL") && !lineaSinEspacios.contains("INTEGER") && !lineaSinEspacios.contains("CHAR")) {

                                if (lineaSinEspacios.contains("RETURN")) {

                                    if (!lineaSinEspacios.endsWith(";")) {

                                        nuevoArchivo.print("    ERROR 560: El comando RETURN debe finalizar con punto y coma" + "\n");
                                        error = true;
                                    }
                                    if (module == false) {

                                        if (lineaSinEspacios.equals("RETURN;")) {

                                            nuevoArchivo.print("     Error 561: No se pueden definir comandos de LIBELULA antes de MODULE " + "[" + primerLinea[0] + "]" + "\n");
                                            error = true;
                                        }
                                    } else if (module && begin == false) {

                                        nuevoArchivo.print("     Error 562: No se pueden definir comandos de LIBELULA antes de MODULE " + "[" + primerLinea[0] + "]" + "\n");
                                        error = true;
                                    }

                                }

                            }
                        }

                    } catch (Exception e) {

                        System.out.println("Hubo un Problema al Realizar la Comprobacion del comando RETURN" + e);
                    }

                }

                if (um > 9) {

                    um = 0;

                } else if (c > 9) {

                    c = 0;
                    um++;

                } else if (d > 9) {

                    d = 0;
                    c++;

                } else if (u > 9) {

                    u = 0;
                    d++;

                }

            }
            if (comandoEnd == false) {

                nuevoArchivo.print("        ERROR 973: No se encontro comando de cierre del IF en la linea " + numeroLineaIF + "\n");
                error = true;
            }

            if (module == false) {

                nuevoArchivo.print("        ERROR 051: La Declaracion de MODULE no fue Encontrado" + "\n");
                error = true;
            }

            if (begin == false) {

                nuevoArchivo.print("        ERROR 052: La Declaracion de BEGIN no fue Encontrado" + "\n");
                error = true;
            }

            nuevoArchivo.close();
            bf.close();

            if (error) {

                System.out.println("Error Existen Problemas de Compilacion en :" + nombreArchivoOriginal[0]);
            } else {

                System.out.println("Se realizo la Compilacion Satisfactoriamente en :" + nombreArchivoOriginal[0]);
            }

        } catch (Exception e) {

            System.out.println("Hubo un problema al realizar la lectura del archivo :" + e);
        }

    }

    public static String[] palabrasReservadasAnexo1() {

        String anexo1[] = {"ABS", "ABSTRACT", "AND", "ARRAY", "AS", "BITSET", "BOOLEAN", "BY", "CAP", "CARDINAL", "CASE",
            "CHR", "CLASS", "CMPLX", "COMPLEX", "CONST", "DEC", "DEFINITION", "DISPOSE", "DIV", "DO", "ELSIF",
            "EXCEPT", "EXCL", "EXIT", "EXPORT", "FALSE ", "FINALLY", "FLOAT", "FOR", "FORWARD", "FROM", "GENERIC", "GUARD", "HALT",
            "HIGH", "IM", "IMPLEMENTATION", "IMPORT", "IN", "INC", "INCL", "INHERIT", "INT", "INTERRUPTIBLE", "LENGTH",
            "LFLOAT", "LONGCOMPLEX", "LONGREAL", "LOOP", "MAX", "MIN", "MOD", "NEW", "NIL", "NOT", "ODD", "OF", "OR", "ORD", "OVERRIDE",
            "PACKEDSET", "POINTER", "PROC", "PROCEDURE", "PROTECTION", "QUALIFIED", "RE", "READONLY", "RECORD", "REM", "RETRY",
            "REVEAL", "SET", "SIZE", "TO", "TRACED", "TRUE", "TRUNC", "TYPE", "UNINTERRUPTIBLE", "UNSAFEGUARDED", "VAL",
            "WHILE", "WITH"};

        return anexo1;
    }

    public static String[] palabrasReservadasAnexo2() {

        String anexo2[] = {"BEGIN", "CHAR", "ELSE", "END", "IF", "INTEGER", "Read", "ReadInt", "ReadReal", "REAL", "REPEAT",
            "RETURN", "THEN", "UNTIL", "VAR", "Write", "WriteInt", "WriteLn", "WriteReal", "WriteString"};

        return anexo2;
    }

    public static boolean esNumero(String cadena) {
        try {

            Integer.parseInt(cadena);
            return true;

        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public static boolean esChar(String cadena) {

        String cadenaSinComasSimples;
        cadenaSinComasSimples = cadena.replaceAll("'", "");

        if (cadenaSinComasSimples.length() == 1) {

            return true;

        } else {

            return false;
        }
    }

}
