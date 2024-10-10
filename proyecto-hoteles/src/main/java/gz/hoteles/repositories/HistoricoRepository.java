package gz.hoteles.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import gz.hoteles.entities.Historico;

public interface HistoricoRepository extends JpaRepository<Historico, Integer> {

    Optional<Historico> findByFecha(LocalDate today);

    Optional<Historico> findByFechaAndIdUsuario(LocalDate today, int idUsuario);

    List<Historico> findByIdUsuario(int idUsuario);
    
}
