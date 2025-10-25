package com.smartroute.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.smartroute.model.Motorista;

@Repository
public interface MotoristaRepository extends JpaRepository<Motorista, Long> {

	Optional<Motorista> findByCpf(String cpf);

	Optional<Motorista> findByEmail(String email);

	@Query("SELECT m FROM Motorista m WHERE m.nome LIKE %:nome%")
	List<Motorista> findByNomeContaining(@Param("nome") String nome);

	List<Motorista> findByIdadeGreaterThan(Integer idade);
}