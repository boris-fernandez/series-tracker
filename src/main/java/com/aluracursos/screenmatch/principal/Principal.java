package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.*;
import com.aluracursos.screenmatch.repository.SerieRepository;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://www.omdbapi.com/?t=";
    //Configura esta parte con tu apikey.
    private String API_KEY = System.getenv("API_KEY");
    private ConvierteDatos conversor = new ConvierteDatos();
    private List<DatosSerie> datosSeries = new ArrayList<>();
    private  SerieRepository repositorio;
    private List<Serie> series;
    private Optional<Serie> serieBuscada;

    public Principal(SerieRepository repository) {
        this.repositorio = repository;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Buscar series 
                    2 - Buscar episodios
                    3 - Mostrar series buscadas
                    4 - Buscar series por titulo
                    5 - Top 5 mejores series
                    6 - Buscar series por categoria 
                    7 - Buscar serie por numero de temporadas y evaluacion              
                    8 - Buscar episodio por titulo
                    9- Top 5 episodios por serie
                    
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    mostrarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriesPorTitulo();
                    break;
                case 5:
                    buscarTop5Series();
                    break;
                case 6:
                    buscarSeriePorCategoria();
                    break;
                case 7:
                    buscarSeriePorNumerotemporadasYEvaluacion();
                case 8:
                    buscarEpisodioPorTitulo();
                case 9:
                    buscarTop5Episodios();
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }

    }

    private DatosSerie getDatosSerie() {
        System.out.println("Escribe el nombre de la serie que deseas buscar");
        var nombreSerie = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + API_KEY);
        System.out.println(json);
        DatosSerie datos = conversor.obtenerDatos(json, DatosSerie.class);
        return datos;
    }
    private void buscarEpisodioPorSerie() {
        mostrarSeriesBuscadas();
        System.out.println("Escribe el nombre de la serie de la cual quieres ver los episodios");
        var nombreSerie = teclado.nextLine();
        List<DatosTemporadas> temporadas = new ArrayList<>();

        Optional<Serie> serie = series.stream()
                .filter(e -> e.getTitulo().toLowerCase().contains(nombreSerie.toLowerCase()))
                .findFirst();

        if (serie.isPresent()){
            var serieEncontrada = serie.get();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumoApi.obtenerDatos(URL_BASE + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DatosTemporadas datosTemporada = conversor.obtenerDatos(json, DatosTemporadas.class);
                temporadas.add(datosTemporada);
            }
            temporadas.forEach(System.out::println);
            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream().map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());
            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);
        }
        else{

        }


    }
    private void buscarSerieWeb() {
        DatosSerie datos = getDatosSerie();
        Serie serie = new Serie(datos);
        repositorio.save(serie);
        //datosSeries.add(datos);
        System.out.println(datos);
    }
    private void mostrarSeriesBuscadas(){
        series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }
    private void buscarSeriesPorTitulo() {
        System.out.println("Escribe el nombre de la serie que deseas buscar");
        String nombreSerie = teclado.nextLine();
        serieBuscada = repositorio.findByTituloContainsIgnoreCase(nombreSerie);
        if(serieBuscada.isPresent()){
            System.out.println("La serie buscada es: " + serieBuscada.get());
        }else {
            System.out.println("Serie no encontrada...");
        }
    }
    private void buscarTop5Series() {
        List<Serie> topSeries = repositorio.findTop5ByOrderByEvaluacionDesc();
        topSeries.forEach(s ->
                System.out.println("Serie: " + s.getTitulo() + ", Evaluacion: " + s.getEvaluacion()));
    }
    private void buscarSeriePorCategoria() {
        System.out.println("Escriba el genero/categoria de la serie que quieres buscar");
        String genero = teclado.nextLine();
        var categoria = Categoria.fromEspanol(genero);
        List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);
        System.out.println("Las series de la categoria " + genero);
        seriesPorCategoria.forEach(System.out::println);
    }

    private void buscarSeriePorNumerotemporadasYEvaluacion() {
        System.out.println("Ingrese el numero de temporadas que tenga la serie");
        int numeroTemporadas = teclado.nextInt();
        System.out.println("Ingresa la evaluacion de la serie q deseas buscar ");
        Double evaluacion = teclado.nextDouble();
        List<Serie> filtroSeries = repositorio.seriesPorTemporadaYEvaluacion(numeroTemporadas, evaluacion);
        System.out.println("*** Series filtradas ***");
        filtroSeries.forEach(s ->
                System.out.println(s.getTitulo() + " - evaluacion: " + s.getEvaluacion() + " - temporadas: " + s.getTotalTemporadas()));


        /*Optional<Serie> seriePorTotalTemporadasYEvaluacion = repositorio.findBytotalTemporadasAndEvaluacion(numeroTemporadas, evaluacion);
        if(seriePorTotalTemporadasYEvaluacion.isPresent()){
            System.out.println("Las series que contienen esa caracteristicas son: \n" +
                    "|Titulo: " + seriePorTotalTemporadasYEvaluacion.get().getTitulo() + "\n" +
                    "|Total de temporadas: " + seriePorTotalTemporadasYEvaluacion.get().getTotalTemporadas() + "\n" +
                    "|Evaluacion: " + seriePorTotalTemporadasYEvaluacion.get().getEvaluacion() + "\n\n");
        }else{
            System.out.println("No se econtro ninguna serie que contenga esas caracteristicas");
        }*/
    }
    public void buscarEpisodioPorTitulo(){
        System.out.println("Escribe el titulo del episodio");
        String nombre = teclado.nextLine();
        List<Episodio> filtroEpisodio = repositorio.episodiosPorNombre(nombre);
        filtroEpisodio.forEach(s ->
                System.out.println("Serie: " + s.getSerie().getTitulo() +
                        ", Temporada: " + s.getTemporada() +
                        ", titulo: " + s.getTitulo()));
    }

    public void buscarTop5Episodios() {
        buscarSeriesPorTitulo();
        if (serieBuscada.isPresent()){
            Serie serie = serieBuscada.get();
            List<Episodio> topEpisodios = repositorio.top5Episodios(serie);
            topEpisodios.forEach(s ->
                    System.out.println("Serie: " + s.getSerie().getTitulo() +
                            ", Temporada: " + s.getTemporada() +
                            ", titulo: " + s.getTitulo() +
                            ", evaluacion: " + s.getEvaluacion()));
        }
    }

}

