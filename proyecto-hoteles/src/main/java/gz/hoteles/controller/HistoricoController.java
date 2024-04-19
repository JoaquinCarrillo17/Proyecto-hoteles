package gz.hoteles.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import gz.hoteles.entities.Historico;
import gz.hoteles.repositories.HistoricoRepository;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/historicos")
public class HistoricoController {

    @Autowired
    HistoricoRepository historicoRepository;

    @GetMapping()
    public ResponseEntity<?> list() {
        List<Historico> historicos = historicoRepository.findAll();
        if (historicos.size() > 0) {
            return ResponseEntity.ok(historicos);
        } else
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No se ha encontrado ningún historico");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable(name = "id") int id) {
        if (id <= 0  || Integer.valueOf(id) == null) {
            throw new IllegalArgumentException("El ID debe ser un número entero positivo");
        }
        Historico historico = historicoRepository.findById(id).orElse(null);
        if (historico == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontró ningún historico con el ID proporcionado");
        } else
            return ResponseEntity.ok(historico);
    }

    @PostMapping
    public ResponseEntity<?> post(@RequestBody Historico input) {
        Historico save = historicoRepository.save(input);
        return ResponseEntity.ok(save);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") int id) {
        if (id <= 0  || Integer.valueOf(id) == null) {
            throw new IllegalArgumentException("El ID debe ser un número entero positivo");
        }
        Historico findById = historicoRepository.findById(id).orElse(null);
        if (findById != null) {
            historicoRepository.delete(findById);
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontró ningun historico por el ID proporcionado");
        return ResponseEntity.ok().build();
    }
    

    
}
