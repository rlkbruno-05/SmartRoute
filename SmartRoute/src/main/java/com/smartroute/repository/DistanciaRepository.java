package com.smartroute.repository;

import com.smartroute.model.Distancia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DistanciaRepository extends JpaRepository<Distancia, Long> {

	@Query("SELECT d FROM Distancia d WHERE d.origem = :origem AND d.destino = :destino")
	Optional<Distancia> findByParEnderecos(@Param("origem") String origem, @Param("destino") String destino);

	void deleteByOrigemAndDestino(String origem, String destino);
}