package com.smartroute.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.smartroute.model.Corrida;
import com.smartroute.model.Recomendacao;

@Repository
public interface CorridaRepository extends JpaRepository<Corrida, Long> {

	List<Corrida> findByMotoristaId(Long motoristaId);

	List<Corrida> findByRecomendacao(Recomendacao recomendacao);

	@Query("SELECT c FROM Corrida c WHERE c.lucroLiquido > :lucroMinimo")
	List<Corrida> findByLucroMaiorQue(@Param("lucroMinimo") Double lucroMinimo);

	@Query("SELECT c FROM Corrida c WHERE c.valorCorrida BETWEEN :valorMin AND :valorMax")
	List<Corrida> findByValorCorridaBetween(@Param("valorMin") Double valorMin, @Param("valorMax") Double valorMax);
}