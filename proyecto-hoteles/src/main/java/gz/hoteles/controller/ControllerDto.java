package gz.hoteles.controller;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import gz.hoteles.dto.DynamicSearchPaginatorDto;
import gz.hoteles.servicio.DtoService;
import gz.hoteles.support.FrontendMessageException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ControllerDto<T> {

	@Autowired
	protected DtoService<T> dtoService;

	public static final String IMPOSSIBLE_TO_PERFORM_THE_OPERATION = "Imposible realizar la operación.";


	@GetMapping("/{id}")
	public ResponseEntity<?> get(@PathVariable(value = "id") Long id){

		try {
			Optional<T> s = this.dtoService.findById(id);
			if(s.isPresent())
				return new ResponseEntity<T>(s.get(), HttpStatus.OK);
			else return new ResponseEntity<>("No se encuentra el elemento",
					HttpStatus.BAD_REQUEST); 

		}  catch (FrontendMessageException e) {
			return new ResponseEntity<>(e.getMessage(),
					((FrontendMessageException)e).getStatus());
		}  catch (Exception e) {
			return new ResponseEntity<>(IMPOSSIBLE_TO_PERFORM_THE_OPERATION,
					HttpStatus.BAD_REQUEST);
		}
	}


	@GetMapping
	public ResponseEntity<?> getAll(){	

		try {
			return new ResponseEntity<List<T>>(dtoService.findAll(), HttpStatus.OK);
		}  catch (FrontendMessageException e) {

			return new ResponseEntity<>(e.getMessage(),
					e.getStatus());
		}  catch (Exception e) {
			return new ResponseEntity<>(IMPOSSIBLE_TO_PERFORM_THE_OPERATION+e.getMessage(),
					HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping
	public ResponseEntity<?> post(@RequestBody T entity){    

		try {	
			entity = dtoService.save(entity);
			return new ResponseEntity<T>(entity, HttpStatus.CREATED);
		}  catch (FrontendMessageException e) {
			return new ResponseEntity<>(e.getMessage(),
					e.getStatus());
		}  catch (Exception e) {
			return new ResponseEntity<>(IMPOSSIBLE_TO_PERFORM_THE_OPERATION,
					HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> put(@PathVariable Long id, @RequestBody T entity) { 

		try {	
			entity = dtoService.update(entity);
			return new ResponseEntity<T>(entity, HttpStatus.CREATED);
		}  catch (FrontendMessageException e) {
			return new ResponseEntity<>(e.getMessage(),
					e.getStatus());
		}  catch (Exception e) {
			System.out.println(e.getMessage());
			return new ResponseEntity<>(IMPOSSIBLE_TO_PERFORM_THE_OPERATION,
					HttpStatus.BAD_REQUEST);

		}
	}
	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {	        

		try {
			Optional<T> optT = this.dtoService.findById(id);
			if(optT.isPresent())
				this.dtoService.deleteById(id);
			return new ResponseEntity<>(HttpStatus.OK);
		}  catch (FrontendMessageException e) {
			return new ResponseEntity<>(e.getMessage(),
					e.getStatus());
		}  catch (Exception e) {
			return new ResponseEntity<>(IMPOSSIBLE_TO_PERFORM_THE_OPERATION,
					HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * End point usado para la paginacion dinamica
	 * Este sera el unico que usaremos
	 * @param dynamicSearchPaginator
	 * @return
	 */
	@PostMapping("/dynamicSearch")
	public ResponseEntity<?> withDynamicSearchPageAndOrder(@RequestBody DynamicSearchPaginatorDto dynamicSearchPaginator) {

		try {
			return new ResponseEntity<Page<T>>(dtoService.findPageBySearchCriteriaAndOrderCriteria(dynamicSearchPaginator), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(IMPOSSIBLE_TO_PERFORM_THE_OPERATION,
					HttpStatus.BAD_REQUEST);
		}
	}

}
