package gz.hoteles.servicio.impl;

import java.util.ArrayList;
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
import gz.hoteles.dto.DynamicSearchPaginatorDto;
import gz.hoteles.entities.EntityGeneral;
import gz.hoteles.servicio.DtoService;
import gz.hoteles.support.GenericSpecification;
import gz.hoteles.support.OrderCriteria;
import gz.hoteles.support.PageDto;
import gz.hoteles.support.SearchCriteria;

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

		return this.parseDto(this.jpaRepository.save(e));
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
	
	
	@Override
	public boolean isMine(Long id) throws Exception {
		
		return true;
	}


	/**
	 * Realiza una búsqueda paginada y ordenada de objetos DTO utilizando criterios específicos.
	 * @param dynamicSearchPaginator Objeto que contiene criterios de búsqueda y paginación.
	 * @return Página de resultados de objetos DTO.
	 * @throws Exception Sí ocurre un error durante la operación.
	 */
	@Override
	public Page<T> findPageBySearchCriteriaAndOrderCriteria(DynamicSearchPaginatorDto dynamicSearchPaginator)  throws Exception {

		PageDto pageDto = dynamicSearchPaginator.getPage();
		List<OrderCriteria> ocl = dynamicSearchPaginator.getListOrderCriteria();
		List<SearchCriteria> scl = dynamicSearchPaginator.getListSearchCriteria();

		GenericSpecification<A> genericSpesification = new GenericSpecification<A>();

		if(null != scl)
			genericSpesification.setSearch(scl);

		Pageable paging = PageRequest.of(pageDto.getPageIndex(), pageDto.getPageSize(), Sort.by(this.getOrderList(ocl)));

		Page<A> pagedResult = jpaSpecRepository.findAll(genericSpesification, paging);

		Page<T> dtoPage = pagedResult.map(new Function<A, T>() {
			@Override
			public T apply(A entity) {
				try {
					return parseDto(entity);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});

		return dtoPage;

	}
	/**
	 * Obtiene una lista de órdenes a partir de una lista de criterios de ordenación.
	 * @param ocl Lista de criterios de ordenación.
	 * @return Lista de órdenes para la consulta paginada.
	 */
	protected List<Sort.Order> getOrderList(List<OrderCriteria> ocl)
	{
		List<Sort.Order> orders = new ArrayList<Sort.Order>();
		if(null != ocl)
		{
			ocl.forEach(
					oc -> {
						Sort.Order order = new Sort.Order(Sort.Direction.fromString(oc.getValueSortOrder()), oc.getSortBy());
						orders.add(order);
					}
			);
		}
		return orders;
	}

}

