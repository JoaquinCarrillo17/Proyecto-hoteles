package gz.hoteles.servicio;



import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;


@Transactional
public interface DtoService<T> {
	
List<T> findAll() throws Exception;
	
	T save(T dto) throws Exception;
	
	T update(T dto) throws Exception;

	
	Optional<T> findById(Long id) throws Exception;
	
	//Example<S> findByOne()
	
	Page<T> findAll(String filter, String sortOrder, Integer pageIndex, 
            				Integer pageSize,String sortBypageable)  throws Exception;
	
	void deleteById(Long id) throws Exception;
	
	void delete(T dto) throws Exception;
	
//	List<T> findBySearchCriteria(List<SearchCriteria> scl) throws Exception;
	
//	Page<T> findPageBySearchCriteria(List<SearchCriteria> scl, PageDto pageDto) throws Exception;

}