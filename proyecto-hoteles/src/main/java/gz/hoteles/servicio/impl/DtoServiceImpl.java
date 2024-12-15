package gz.hoteles.servicio.impl;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import gz.hoteles.dto.DtoGeneral;
import gz.hoteles.entities.EntityGeneral;
import gz.hoteles.servicio.DtoService;

/**
 * 
 *
 * @param <T> Representa el objeto DTO.
 * @param <A> Representa el objeto entidad.
 */
public abstract class DtoServiceImpl<T extends DtoGeneral,A extends EntityGeneral> implements DtoService<T> {

	@Autowired
	protected JpaRepository<A,Long> jpaRepository;
	
	@Autowired
	protected JpaSpecificationExecutor<A> jpaSpecRepository;

	protected abstract T parseDto(A entity);
	
	protected Optional<T> parseOptionalDto(Optional<A> optionalEntity) {
		return optionalEntity.flatMap(e -> Optional.of(parseDto(e)));
	}
	
	protected List<T> parseListDto(List<A> listEntity) {
		return  listEntity.stream().map(e -> parseDto(e)).collect(Collectors.toList());
	}

	protected abstract A parseEntity(T dto) throws Exception;
		
	@Override
	public List<T> findAll()  throws Exception {
		return this.parseListDto(this.jpaRepository.findAll());
	}

	@Override
	public T save(T dto) throws Exception {
		A e = this.parseEntity(dto);		

		try {
			return this.parseDto(this.jpaRepository.save(e));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	@Override
	public T update(T dto) throws Exception {
		A e = this.parseEntity(dto);

		return this.parseDto(this.jpaRepository.save(e));	
	}

	@Override
	public Optional<T> findById(Long id)  throws Exception {
		return this.parseOptionalDto(this.jpaRepository.findById(id));
	}

	@Override
	public void deleteById(Long id)  throws Exception {		
		this.jpaRepository.deleteById(id);
		
	}

	@Override
	public void delete(T dto)  throws Exception {
		A entity = this.parseEntity(dto);
		this.jpaRepository.delete(entity);
	}

	@Override
	public Page<T> findAll(String filter, String sortOrder, Integer pageIndex, Integer pageSize, String sortBy) throws Exception {
	
		Pageable paging = null;
		
	     
		if(!sortBy.equals(""))
		{
			String[] sortColumnsArray = sortBy.split(";");
			paging = PageRequest.of(pageIndex, pageSize, Sort.by(sortColumnsArray));
		} else paging = PageRequest.of(pageIndex, pageSize);
	     
	        Page<A> pagedResult = this.jpaRepository.findAll(paging);
	         
	        Page<T> dtoPage = pagedResult.map(new Function<A, T>() {        
	        	@Override
	            public T apply(A entity) {
	                return parseDto(entity);
	            }
	        });
	        
	      return dtoPage;	    
	}
	
}

