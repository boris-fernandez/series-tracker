package com.aluracursos.screenmatch.model;

//s un tipo especial de clase en Java que tiene un número
// fijo de constantes. Utilizar un Enum puede ser muy útil
// en programación para almacenar valores que sabemos que nunca cambiarán,

public enum Categoria {
    ACCION("Action"),
    ROMANCE("Romance"),
    COMEDIA("Comedy"),
    DRAMA("Drama"),
    CRIMEN("Crime");

    private String categoriaOmbd;

    Categoria(String categoriaOmbd){
        this.categoriaOmbd = categoriaOmbd;
    }

    public static  Categoria fromString(String text){
        for (Categoria categoria: Categoria.values()){
            if(categoria.categoriaOmbd.equalsIgnoreCase(text)){
                return categoria;
            }
        }
        throw  new IllegalArgumentException("Ninguna categoria encontrada: " + text);
    }

}
