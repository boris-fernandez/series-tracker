package com.aluracursos.screenmatch.model;

//s un tipo especial de clase en Java que tiene un número
// fijo de constantes. Utilizar un Enum puede ser muy útil
// en programación para almacenar valores que sabemos que nunca cambiarán,

public enum Categoria {
    ACCION("Action", "Acción"),
    ROMANCE("Romance", "Romance"),
    COMEDIA("Comedy", "Comedia"),
    DRAMA("Drama", "Drama"),
    CRIMEN("Crime", "Crimen");

    private String categoriaOmbd;
    private String CategoriaEspanol;

    Categoria(String categoriaOmbd, String CategoriaEspanol){
        this.categoriaOmbd = categoriaOmbd;
        this.CategoriaEspanol = CategoriaEspanol;
    }

    public static  Categoria fromString(String text){
        for (Categoria categoria: Categoria.values()){
            if(categoria.categoriaOmbd.equalsIgnoreCase(text)){
                return categoria;
            }
        }
        throw  new IllegalArgumentException("Ninguna categoria encontrada: " + text);
    }

    public static  Categoria fromEspanol(String text){
        for (Categoria categoria: Categoria.values()){
            if(categoria.CategoriaEspanol.equalsIgnoreCase(text)){
                return categoria;
            }
        }
        throw  new IllegalArgumentException("Ninguna categoria encontrada: " + text);
    }

}
