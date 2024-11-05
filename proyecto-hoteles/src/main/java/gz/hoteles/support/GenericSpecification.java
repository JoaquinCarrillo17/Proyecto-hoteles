package gz.hoteles.support;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lombok.Getter;
import lombok.Setter;

public class GenericSpecification<T> implements Specification<T> {

	private static final long serialVersionUID = 1900581010229669687L;

	@Getter
	@Setter
	private List<SearchCriteria> search;


	public GenericSpecification() {
		this.search = new ArrayList<>();
	}

	public void addSearchCriteria(SearchCriteria criteria) {
		search.add(criteria);
	}

	@Override
	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

		//create a new predicate list
		List<Predicate> predicates = new ArrayList<>();

		for (SearchCriteria criteria : search) {
			addConditions(root, query, builder, predicates, criteria);
		}

		return builder.and(predicates.toArray(new Predicate[0]));

	}

	protected void addConditions(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder,
								 List<Predicate> predicates, SearchCriteria criteria)

	{
		String arr[] = criteria.getKey().split("\\.");
		Path<?> path = root;
		for (String str : arr) {
			path = path.get(str);
		}
		addCondition(builder, predicates, criteria, path);
	}

	@SuppressWarnings("unchecked")
	private void addCondition(CriteriaBuilder builder, List<Predicate> predicates, SearchCriteria criteria, Path path) {
		switch (SearchOperation.valueOf(criteria.getOperation())) {

			case GREATER_THAN:
				predicates.add(builder.greaterThan(path, criteria.getValue()));
				break;

			case LESS_THAN:
				predicates.add(builder.lessThan(path, criteria.getValue()));
				break;

			case GREATER_THAN_EQUAL:
				predicates.add(builder.greaterThanOrEqualTo(path, criteria.getValue()));
				break;

			case LESS_THAN_EQUAL:
				predicates.add(builder.lessThanOrEqualTo(path, criteria.getValue()));
				break;

			case NOT_EQUAL:
				predicates.add(builder.notEqual(path, criteria.getValue()));
				break;

			case EQUAL:
				predicates.add(builder.equal(path, criteria.getValue()));
				break;

			case EQUAL_BOOLEAN:
				predicates.add(builder.equal(path, Boolean.valueOf(criteria.getValue())));
				break;
			case MATCH:
				predicates.add(builder.like(builder.lower(path), "%" + criteria.getValue().toString().toLowerCase() + "%"));
				break;

			case MATCH_END:
				predicates.add(builder.like(builder.lower(path), criteria.getValue().toString().toLowerCase() + "%"));
				break;

			case EQUAL_DATE:

				Predicate predicateDateIni = null, predicateDateEnd = null;

				ZonedDateTime zdtIni = null, zdtEnd = null;
				if(path.getJavaType().getSimpleName().equals("ZonedDateTime"))
				{
					zdtIni = convertStringToZonedDateTime(criteria.getValue());
					zdtEnd = convertStringToZonedDateTime(criteria.getValue()).plusDays(1);

					predicateDateIni = (builder.greaterThanOrEqualTo(path,zdtIni));
					predicateDateEnd = (builder.lessThan(path,zdtEnd));

				}
				else if(path.getJavaType().getSimpleName().equals("Date"))
				{
					try {
						Date dateIni = convertStringToDate(criteria.getValue());
						Date dateEnd = addDaysToDate(dateIni, 1);

						predicateDateIni = (builder.greaterThanOrEqualTo(path,dateIni));
						predicateDateEnd = (builder.lessThan(path,dateEnd));

					} catch (ParseException e) {
						e.printStackTrace();
					}
				}

				predicates.add(builder.and(predicateDateIni,predicateDateEnd));

				break;

			case GREATER_THAN_DATE:

				if(path.getJavaType().getSimpleName().equals("ZonedDateTime"))
				{
					ZonedDateTime zdt = convertStringToZonedDateTime(criteria.getValue());
					predicates.add(builder.greaterThanOrEqualTo(path,zdt));
				}else if(path.getJavaType().getSimpleName().equals("Date")) {
					Date date = null;
					try {
						date = convertStringToDate(criteria.getValue());
						predicates.add(builder.greaterThanOrEqualTo(path,date));
					} catch (ParseException e) {
						e.printStackTrace();
					}
				} else if (path.getJavaType().getSimpleName().equals("LocalDate")) {
					LocalDate localDate = convertStringToLocalDate(criteria.getValue());
					predicates.add(builder.greaterThanOrEqualTo(path,localDate));
				}else if (path.getJavaType().getSimpleName().equals("LocalDateTime")) {
					LocalDateTime localDatetime = convertStringToLocalDateTime(criteria.getValue());
					predicates.add(builder.greaterThanOrEqualTo(path,localDatetime));
				}
				break;

			case LESS_THAN_DATE:

				if(path.getJavaType().getSimpleName().equals("ZonedDateTime"))
				{
					ZonedDateTime zdt = convertStringToZonedDateTime(criteria.getValue());
					predicates.add(builder.lessThanOrEqualTo(path,zdt));
				}else if(path.getJavaType().getSimpleName().equals("Date")) {
					Date date = null;
					try {
						date = convertStringToDate(criteria.getValue());
						predicates.add(builder.lessThanOrEqualTo(path,date));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (path.getJavaType().getSimpleName().equals("LocalDate")) {
					LocalDate localDate = convertStringToLocalDate(criteria.getValue());
					predicates.add(builder.lessThanOrEqualTo(path,localDate));
				}else if (path.getJavaType().getSimpleName().equals("LocalDateTime")) {
					LocalDateTime localDatetime = convertStringToLocalDateTime(criteria.getValue());
					predicates.add(builder.lessThanOrEqualTo(path,localDatetime));
				}
				break;

			case IN:
				String[] values = criteria.getValue().split(",");
				List<Object> list = new LinkedList<>();

				Arrays.asList(values).stream().forEach(value -> {
					if(path.getJavaType().getSimpleName().equals("Long")
							|| path.getJavaType().getSimpleName().equals("Integer"))
						list.add(Long.parseLong(value));
					else if(path.getJavaType().getSimpleName().equals("String"))
						list.add(String.valueOf(value));
				});

				predicates.add(path.in(list));
				break;

			default:
				break;

		}
	}

	private ZonedDateTime convertStringToZonedDateTime(String value) {
//		String dateFormatValue = DateTimeFormatter.ISO_LOCAL_DATE.toString() + " " + DateTimeFormatter.ISO_LOCAL_TIME.toString();
		String dateFormatValue = "yyyy-MM-dd HH:mm:SS";
		ZoneId zone = ZoneId.of("Europe/Madrid");
		LocalDateTime ldt =
				LocalDateTime.parse(value,
						DateTimeFormatter.ofPattern(
								dateFormatValue));

		return ZonedDateTime.of(ldt, zone);
	}

	private Date convertStringToDate(String value) throws ParseException {
//		String dateFormatValue = DateTimeFormatter.ISO_LOCAL_DATE.toString() + " " + DateTimeFormatter.ISO_LOCAL_TIME.toString();
		String dateFormatValue = "yyyy-MM-dd HH:mm:SS";
		return new SimpleDateFormat(dateFormatValue).parse(value);
	}

	private LocalDate convertStringToLocalDate(String value){
		String dateFormatValue = "yyyy-MM-dd"; // Formato de fecha
		return LocalDate.parse(value, DateTimeFormatter.ofPattern(dateFormatValue));
	}
	private LocalDateTime convertStringToLocalDateTime(String value){
		String dateTimeFormatValue = "yyyy-MM-dd HH:mm:ss"; // Formato de fecha
		return LocalDateTime.parse(value, DateTimeFormatter.ofPattern(dateTimeFormatValue));
	}

	private Date addDaysToDate(Date date, int plusDays) {
		ZoneId zone = ZoneId.of("Europe/Madrid");
		LocalDateTime ldtEnd = LocalDateTime.ofInstant(
				date.toInstant(), zone);
		Date newDate = Date.from(ldtEnd.atZone(zone).toInstant());

		return newDate;
	}
}
