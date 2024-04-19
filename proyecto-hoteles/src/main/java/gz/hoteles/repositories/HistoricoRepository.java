package gz.hoteles.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import gz.hoteles.entities.Historico;

public interface HistoricoRepository extends JpaRepository<Historico, Integer> {
    
}
