package com.aluracursos.screenmatch.repository;

import com.aluracursos.screenmatch.model.Categoria;
import com.aluracursos.screenmatch.model.Episodio;
import com.aluracursos.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.security.spec.ECPoint;
import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    Optional<Serie> findByTituloContainsIgnoreCase(String nombreSerie);

    List<Serie> findTop5ByOrderByEvaluacionDesc();

    List<Serie> findByGenero(Categoria categoria);

    //Optional<Serie> findBytotalTemporadasLessThanEqualAndEvaluacionGreaterThanEqual(int numeroTemporadas, double evaluacion);

    @Query("select s from Serie as s where s.totalTemporadas <= :numeroTemporadas and s.evaluacion >= :evaluacion")
    List<Serie> seriesPorTemporadaYEvaluacion(int numeroTemporadas, double evaluacion);

    @Query("select e from Serie as s join s.episodios as e where e.titulo ilike %:nombreEpisodio%")
    List<Episodio> episodiosPorNombre(String nombreEpisodio);

    @Query("select e from Serie as s join s.episodios as e where s = :serie order by e.evaluacion desc limit 5")
    List<Episodio> top5Episodios(Serie serie);
}
