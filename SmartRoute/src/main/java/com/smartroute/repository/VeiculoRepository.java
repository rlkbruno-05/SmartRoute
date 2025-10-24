package com.smartroute.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.smartroute.model.TipoCombustivel;
import com.smartroute.model.Veiculo;

@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {

	Optional<Veiculo> findByPlaca(String placa);

	List<Veiculo> findByMotoristaId(Long motoristaId);

	List<Veiculo> findByTipoCombustivel(TipoCombustivel tipoCombustivel);

	@Query("SELECT v FROM Veiculo v WHERE v.consumoMedio > :consumoMinimo")
	List<Veiculo> findByConsumoMaiorQue(@Param("consumoMinimo") Double consumoMinimo);
}